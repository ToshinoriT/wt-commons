/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.helpers.impl;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.condition.eclipse.FileExistsCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.swt.util.WaitForIdle;
import junit.framework.TestCase;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.wtc.eclipse.core.util.Timestamp;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.conditions.ProjectOpenCondition;
import org.wtc.eclipse.platform.conditions.TreeItemExistsCondition;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IProjectHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.helpers.adapters.ProjectHelperImplAdapter;
import org.wtc.eclipse.platform.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Helper manipulating IProject elements in the workspace.
 */
public class ProjectHelperImpl extends ProjectHelperImplAdapter implements IProjectHelper {
    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#addProjectNatureViaAPI(com.windowtester.runtime.IUIContext,
     *       java.lang.String, java.lang.String)
     */
    public void addProjectNatureViaAPI(IUIContext ui,
                                       String projectName,
                                       String natureID) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectName);
        TestCase.assertNotNull(natureID);

        logEntry2(projectName,
                  natureID);

        IProject project = getProjectForName(projectName);

        try {
            IProjectDescription description = project.getDescription();
            String[] natures = description.getNatureIds();
            String[] newNatures = new String[natures.length + 1];
            System.arraycopy(natures, 0, newNatures, 0, natures.length);
            newNatures[newNatures.length - 1] = natureID;
            description.setNatureIds(newNatures);
            project.setDescription(description, null);
        } catch (CoreException ce) {
            PlatformActivator.logException(ce);
            TestCase.fail(ce.getLocalizedMessage());
        }

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#closeProject(com.windowtester.runtime.IUIContext,
     *       java.lang.String)
     */
    public void closeProject(IUIContext ui, String projectName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectName);

        logEntry2(projectName);
        openProjectInternal(ui, projectName, false);
        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#exportProjectsToArchive(com.windowtester.runtime.IUIContext,
     *       java.lang.String[], java.lang.String)
     */
    public IPath exportProjectsToArchive(IUIContext ui,
                                         String[] projectNames,
                                         String archiveName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectNames);
        TestCase.assertTrue(projectNames.length > 0);
        TestCase.assertNotNull(archiveName);
        TestCase.assertTrue(archiveName.length() > 0);

        logEntry2(getDisplayValue(projectNames), archiveName);

        IPath archivePath = null;

        for (String nextProjectName : projectNames) {
            waitForProjectExists(ui, nextProjectName, true);
        }

        try {
            selectFileMenuItem(ui, "Exp&ort...*"); //$NON-NLS-1$
            ui.wait(new ShellShowingCondition("Export")); //$NON-NLS-1$

            ui.click(new FilteredTreeItemLocator("General/Archive File")); //$NON-NLS-1$
            clickNext(ui);

            // Let the tree repaint
            new WaitForIdle().waitForIdle();

            ui.click(new ButtonLocator("&Deselect All")); //$NON-NLS-1$

            // Let the tree repaint
            new WaitForIdle().waitForIdle();

            for (String nextProjectName : projectNames) {
                ui.click(1, new TreeItemLocator(nextProjectName), SWT.BUTTON1 | SWT.CHECK);
                new WaitForIdle().waitForIdle();
            }

            IPath workspaceLocationMinus = ResourcesPlugin.getWorkspace().getRoot().getLocation();
            IPath archiveRootPath = workspaceLocationMinus.removeLastSegments(1).append(new Path("test-exported-archives")); //$NON-NLS-1$
            File archiveRootFile = archiveRootPath.toFile();

            archiveRootFile.mkdirs();

            Timestamp now = new Timestamp();
            String finalArchiveName = archiveName + now.toString();
            archivePath = archiveRootPath.append(new Path(finalArchiveName));

            if (!"zip".equalsIgnoreCase(archivePath.getFileExtension())) //$NON-NLS-1$
            {
                archivePath = archivePath.addFileExtension("zip"); //$NON-NLS-1$
            }

            IWidgetLocator comboLocator = new SWTWidgetLocator(Combo.class);
            safeEnterText(ui, comboLocator, archivePath.toPortableString());
            clickFinish(ui);

            ui.wait(new ShellDisposedCondition("Export")); //$NON-NLS-1$

            IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
            workbench.waitNoJobs(ui);

            File archiveAsFile = archivePath.toFile();
            ui.wait(new FileExistsCondition(archiveAsFile, true));
        } catch (WidgetSearchException wse) {
            PlatformActivator.logException(wse);
            TestCase.fail(wse.getMessage());
        }

        logExit2();

        return archivePath;

    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#getAllWorkspaceProjects(com.windowtester.runtime.IUIContext)
     */
    public List<IProject> getAllWorkspaceProjects(IUIContext ui) {
        TestCase.assertNotNull(ui);

        logEntry2();

        List<IProject> allProjects = new ArrayList<IProject>();

        IProject[] projectArray = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        Collections.addAll(allProjects, projectArray);

        logExit2(getDisplayValue(allProjects));

        return allProjects;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#getProjectForName(java.lang.String)
     */
    public IProject getProjectForName(String projectName) {
        TestCase.assertNotNull(projectName);

        logEntry2(projectName);

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        logExit2();

        return project;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#getWorkspaceProjectNames()
     */
    public Collection<String> getWorkspaceProjectNames() {
        logEntry2();

        List<String> projectNames = new ArrayList<String>();

        IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

        for (IProject nextProject : allProjects) {
            projectNames.add(nextProject.getName());
        }

        logExit2(projectNames.toString());

        return projectNames;
    }

    /**
     * @see  IProjectHelper#importExistingProjectFromArchive(IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       java.lang.String)
     * @see  IProjectHelper#importExistingProjectFromArchive(IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       java.lang.String, long timeout)
     * @see  IProjectHelper#importExistingProjectFromSource(IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath)
     * @see  IProjectHelper#importExistingProjectFromSource(IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath, long
     *       timeout)
     */
    private void importExistingProject(IUIContext ui,
                                       Plugin sourcePlugin,
                                       IPath importPath,
                                       String projectName,
                                       boolean isArchive,
                                       long timeout) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(importPath);
        TestCase.assertFalse(importPath.isEmpty());

        if (!isArchive) {
            TestCase.assertNotNull(projectName);
            TestCase.assertFalse(projectName.length() == 0);
        }

        TestCase.assertTrue("Timeout value must be larger than or equal to the default value of " + WT.getDefaultWaitTimeOut(), timeout >= WT.getDefaultWaitTimeOut()); //$NON-NLS-1$

        try {
            selectFileMenuItem(ui, "&Import..."); //$NON-NLS-1$
            ui.wait(new ShellShowingCondition("Import")); //$NON-NLS-1$

            ui.click(new FilteredTreeItemLocator("General/Existing Projects into Workspace")); //$NON-NLS-1$
            clickNext(ui);

            IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
            File importSource = resources.getFileFromPlugin(ui,
                                                            sourcePlugin,
                                                            importPath);

            if (isArchive) {
                ui.click(new ButtonLocator("Select &archive file:")); //$NON-NLS-1$

                PlatformActivator.logDebug("Entering text, import archive : " + importSource.getAbsolutePath()); //$NON-NLS-1$

                com.windowtester.swt.IUIContext ui_old = getUIContext(ui);
                Text archiveText = (Text) ui_old.find(new com.windowtester.swt.WidgetLocator(Text.class, 1));

                WidgetReference archiveTextLocator = new WidgetReference(archiveText);
                safeEnterText(ui, archiveTextLocator, importSource.getAbsolutePath());
            } else {
                TestCase.assertEquals("THE IMPORTED PROJECT SOURCE FOLDER <" //$NON-NLS-1$
                                      + importSource.getName() + "> IS NOT THE SAME AS THE EXPECTED PROEJCT NAME <" //$NON-NLS-1$
                                      + projectName + ">", //$NON-NLS-1$
                                      projectName,
                                      importSource.getName());

                // First, we need to copy the files into a good location
                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                IPath rootPath = root.getLocation();
                IPath projectPath = rootPath.append(new Path(projectName));

                File projectFile = projectPath.toFile();

                try {
                    FileUtil.copyFiles(importSource, projectFile);
                } catch (IOException ioe) {
                    PlatformActivator.logException(ioe);
                    TestCase.fail(ioe.getLocalizedMessage());
                }

                ui.click(new ButtonLocator("Select roo&t directory:")); //$NON-NLS-1$
                PlatformActivator.logDebug("Entering text, import source : " + projectPath.toPortableString()); //$NON-NLS-1$
//                IWidgetLocator sourceTextLocator = new TextByIndexLocator(0);

                com.windowtester.swt.IUIContext ui_old = getUIContext(ui);
                Text sourceText = (Text) ui_old.find(new com.windowtester.swt.WidgetLocator(Text.class, 0));

                WidgetReference sourceTextLocator = new WidgetReference(sourceText);
                safeEnterText(ui, sourceTextLocator, projectPath.toPortableString());
            }

            // Click the tree to trigger the tree refresh
            new WaitForIdle().waitForIdle();

            IWidgetLocator treeRef = ui.find(new SWTWidgetLocator(Tree.class));
            TestCase.assertTrue(treeRef instanceof WidgetReference);

            ui.click(treeRef);
            new WaitForIdle().waitForIdle();

            if (projectName != null) {
                // Let's wait for the tree to be populated
                Tree tree = (Tree) ((WidgetReference) treeRef).getWidget();
                ui.wait(new TreeItemExistsCondition(tree, projectName, true));

                ui.click(new ButtonLocator("&Deselect All")); //$NON-NLS-1$
                new WaitForIdle().waitForIdle();

                ui.click(1, new TreeItemLocator(projectName), SWT.BUTTON1 | SWT.CHECK);
                new WaitForIdle().waitForIdle();
            }

            clickFinish(ui);
            ui.wait(new ShellDisposedCondition("Import"), timeout); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getMessage());
        }

        if (projectName != null) {
            waitForProjectExists(ui, projectName, true);
        }

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui, timeout, WT.getDefaultWaitInterval());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#importExistingProjectFromArchive(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       java.lang.String)
     */
    public void importExistingProjectFromArchive(IUIContext ui,
                                                 Plugin sourcePlugin,
                                                 IPath archivePath,
                                                 String projectName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(archivePath);
        TestCase.assertFalse(archivePath.isEmpty());
        TestCase.assertNotNull(projectName);
        TestCase.assertFalse(projectName.length() == 0);

        logEntry2(sourcePlugin.getBundle().getSymbolicName(), archivePath.toPortableString(), projectName);

        importExistingProject(ui, sourcePlugin, archivePath, projectName, true, WT.getDefaultWaitTimeOut());

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#importExistingProjectFromArchive(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       java.lang.String, long)
     */
    public void importExistingProjectFromArchive(IUIContext ui,
                                                 Plugin sourcePlugin,
                                                 IPath archivePath,
                                                 String projectName,
                                                 long timeout) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(archivePath);
        TestCase.assertFalse(archivePath.isEmpty());
        TestCase.assertNotNull(projectName);
        TestCase.assertFalse(projectName.length() == 0);

        logEntry2(sourcePlugin.getBundle().getSymbolicName(), archivePath.toPortableString(), projectName);

        importExistingProject(ui,
                              sourcePlugin,
                              archivePath,
                              projectName,
                              true,
                              timeout);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#importExistingProjectFromSource(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       java.lang.String)
     */
    public void importExistingProjectFromSource(IUIContext ui,
                                                Plugin sourcePlugin,
                                                IPath projectRootPath,
                                                String projectName) {
        logEntry2(sourcePlugin.getBundle().getSymbolicName(), projectRootPath.toPortableString(), projectName);

        importExistingProject(ui, sourcePlugin, projectRootPath, projectName, false, WT.getDefaultWaitTimeOut());

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#importExistingProjectFromSource(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       java.lang.String, long)
     */
    public void importExistingProjectFromSource(IUIContext ui,
                                                Plugin sourcePlugin,
                                                IPath projectRootPath,
                                                String projectName,
                                                long timeout) {
        logEntry2(sourcePlugin.getBundle().getSymbolicName(), projectRootPath.toPortableString(), projectName, "" + timeout); //$NON-NLS-1$

        importExistingProject(ui,
                              sourcePlugin,
                              projectRootPath,
                              projectName,
                              false,
                              timeout);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#importExistingProjectsFromArchive(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath)
     */
    public void importExistingProjectsFromArchive(IUIContext ui,
                                                  Plugin sourcePlugin,
                                                  IPath archivePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(archivePath);
        TestCase.assertFalse(archivePath.isEmpty());

        logEntry2(sourcePlugin.getBundle().getSymbolicName(), archivePath.toPortableString());

        importExistingProject(ui, sourcePlugin, archivePath, null, true, WT.getDefaultWaitTimeOut());

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#importExistingProjectsFromArchive(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath, long)
     */
    public void importExistingProjectsFromArchive(IUIContext ui,
                                                  Plugin sourcePlugin,
                                                  IPath archivePath,
                                                  long timeout) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(archivePath);
        TestCase.assertFalse(archivePath.isEmpty());

        logEntry2(sourcePlugin.getBundle().getSymbolicName(), archivePath.toPortableString());

        importExistingProject(ui,
                              sourcePlugin,
                              archivePath,
                              null,
                              true,
                              timeout);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#openProject(com.windowtester.runtime.IUIContext,
     *       java.lang.String)
     */
    public void openProject(IUIContext ui, String projectName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectName);

        logEntry2(projectName);
        openProjectInternal(ui, projectName, true);
        logExit2();
    }

    /**
     * @see  IProjectHelper#closeProject(IUIContext, java.lang.String)
     * @see  IProjectHelper#openProject(IUIContext, java.lang.String)
     */
    private void openProjectInternal(IUIContext ui, String projectName, final boolean open) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectName);

        final IProject project = getProjectForName(projectName);
        TestCase.assertTrue("THE PROJECT <" + project + "> DOES NOT EXIST", project.exists()); //$NON-NLS-1$ //$NON-NLS-2$

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        WorkspaceModifyOperation wmo = new WorkspaceModifyOperation(root) {
                @Override
                protected void execute(IProgressMonitor monitor)
                                throws CoreException, InvocationTargetException,
                                       InterruptedException {
                    if (project.isOpen() != open) {
                        if (open) {
                            project.open(monitor);
                        } else {
                            project.close(monitor);
                        }
                    }
                }
            };

        try {
            wmo.run(null);
        } catch (InterruptedException ie) {
            PlatformActivator.logException(ie);
            TestCase.fail(ie.getLocalizedMessage());
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            PlatformActivator.logException(cause);
            TestCase.fail(cause.getLocalizedMessage());
        }

        waitForProjectOpen(ui, projectName, open);

        // Closing / opening can cause rebuilds to happen
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IProjectHelper#waitForProjectOpen(com.windowtester.runtime.IUIContext,
     *       java.lang.String, boolean)
     */
    public void waitForProjectOpen(IUIContext ui,
                                   String projectName,
                                   boolean open) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectName);

        logEntry2(projectName, Boolean.toString(open));

        waitForProjectExists(ui, projectName, true);

        ui.wait(new ProjectOpenCondition(projectName, open), 30000, 1000);

        logExit2();
    }
}
