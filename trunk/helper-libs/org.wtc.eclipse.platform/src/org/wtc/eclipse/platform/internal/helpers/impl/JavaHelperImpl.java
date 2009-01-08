/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.helpers.impl;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.condition.eclipse.FileExistsCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.LabeledLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TabItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import junit.framework.TestCase;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.exceptions.SourceFolderCreationError;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IHelperConstants;
import org.wtc.eclipse.platform.helpers.IJavaHelper;
import org.wtc.eclipse.platform.helpers.IProjectHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.helpers.adapters.HelperImplAdapter;
import org.wtc.eclipse.platform.shellhandlers.SourceFolderAddedShellHandler;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Helper for java-specific actions.
 */
public class JavaHelperImpl extends HelperImplAdapter implements IJavaHelper {
    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#addClassFolder(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void addClassFolder(IUIContext ui, IPath classFolder) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(classFolder);
        TestCase.assertFalse(classFolder.isEmpty());

        logEntry2(classFolder.toPortableString());

        // The project name is always the first segment in a full path
        String projectName = classFolder.segment(0);
        IProjectHelper projectHelper = EclipseHelperFactory.getProjectHelper();
        IProject project = projectHelper.getProjectForName(projectName);
        IJavaProject javaProject = JavaCore.create(project);

        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IFolder folder = workspaceRoot.getFolder(classFolder);

        if (!folder.exists()) {
            IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
            resources.createFolder(ui, classFolder);
        }

        try {
            projectHelper.invokeProjectPropertiesDialog(ui, projectName);

            String title = "Properties for " + projectName; //$NON-NLS-1$
            ui.wait(new ShellShowingCondition(title));

            ui.click(new FilteredTreeItemLocator("Java Build Path")); //$NON-NLS-1$

            ui.click(new TabItemLocator("&Libraries")); //$NON-NLS-1$

            ui.click(new ButtonLocator("Add &Class Folder...")); //$NON-NLS-1$

            String nestedTitle = "Class Folder Selection"; //$NON-NLS-1$
            ui.wait(new ShellShowingCondition(nestedTitle));

            WidgetLocator itemLoc = new TreeItemLocator(classFolder.makeRelative().toPortableString(), new LabeledLocator(Tree.class, "&Choose class folders to be added to the build path:")); //$NON-NLS-1$
            ui.click(1, itemLoc, SWT.BUTTON1 | SWT.CHECK);

            clickOK(ui);
            ui.wait(new ShellDisposedCondition(nestedTitle));

            clickOK(ui);
            ui.wait(new ShellDisposedCondition(title));
        } catch (WidgetSearchException wse) {
            TestCase.fail(wse.getLocalizedMessage());
        }

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        try {
            IPackageFragmentRoot root = javaProject.findPackageFragmentRoot(classFolder.makeAbsolute());
            TestCase.assertNotNull("Package fragment root for: " + classFolder + " not found.", root); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (JavaModelException jme) {
            TestCase.fail(jme.getLocalizedMessage());
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#addExternalJARToClasspathViaAPI(com.windowtester.runtime.IUIContext,
     *       java.lang.String, org.eclipse.core.runtime.IPath, boolean)
     */
    public void addExternalJARToClasspathViaAPI(IUIContext ui,
                                                String javaProjectName,
                                                IPath jarPath,
                                                boolean isExported) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(javaProjectName);
        TestCase.assertTrue(javaProjectName.length() > 0);
        TestCase.assertNotNull(jarPath);
        TestCase.assertFalse(jarPath.isEmpty());

        logEntry2(javaProjectName, jarPath.toPortableString(), Boolean.toString(isExported));

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        IProjectHelper projectHelper = EclipseHelperFactory.getProjectHelper();

        IProject project = projectHelper.getProjectForName(javaProjectName);
        TestCase.assertNotNull(project);
        TestCase.assertTrue(project.isAccessible());

        IJavaProject javaProject = JavaCore.create(project);
        TestCase.assertNotNull(javaProject);

        workbench.listenForDialogSettingBuildPath(ui);

        IClasspathEntry entry = JavaCore.newLibraryEntry(jarPath, null, null, isExported);

        addToClasspath(javaProject, entry);

        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#addProjectFolderToClasspathViaAPI(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void addProjectFolderToClasspathViaAPI(IUIContext ui,
                                                  IPath folderPath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(folderPath);
        TestCase.assertFalse(folderPath.isEmpty());

        logEntry2(folderPath.toPortableString());

        // The project name is always the first segment in a full path
        String projectName = folderPath.segment(0);

        IProjectHelper project = EclipseHelperFactory.getProjectHelper();
        IProject iproject = project.getProjectForName(projectName);
        TestCase.assertNotNull(iproject);

        IJavaProject javaProject = JavaCore.create(iproject);
        IClasspathEntry libFolderEntry = JavaCore.newLibraryEntry(folderPath.makeAbsolute(), null, null);
        addToClasspath(javaProject, libFolderEntry);

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#addProjectJARToClasspath(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void addProjectJARToClasspath(IUIContext ui, IPath jarPath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(jarPath);
        TestCase.assertTrue(jarPath.segmentCount() > 1);

        logEntry2(jarPath.toPortableString());

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.verifyFileExists(ui, jarPath, true);

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.listenForDialogSettingBuildPath(ui);

        // The project name is always the first segment in a full path
        String sourceProject = jarPath.segment(0);

        try {
            IProjectHelper projectHelper = EclipseHelperFactory.getProjectHelper();
            projectHelper.invokeProjectPropertiesDialog(ui, sourceProject);

            String title = "Properties for " + sourceProject; //$NON-NLS-1$
            ui.wait(new ShellShowingCondition(title));

            ui.click(new FilteredTreeItemLocator("Java Build Path")); //$NON-NLS-1$

            ui.click(new TabItemLocator("&Libraries")); //$NON-NLS-1$

            ui.click(new ButtonLocator("Add &JARs...")); //$NON-NLS-1$

            String nestedTitle = "JAR Selection"; //$NON-NLS-1$
            ui.wait(new ShellShowingCondition(nestedTitle));

            ui.click(new TreeItemLocator(jarPath.makeRelative().toPortableString(), new LabeledLocator(Tree.class, "&Choose jar archives to be added to the build path:"))); //$NON-NLS-1$

            clickOK(ui);
            ui.wait(new ShellDisposedCondition(nestedTitle));

            clickOK(ui);
            ui.wait(new ShellDisposedCondition(title));

        } catch (WidgetSearchException wse) {
            PlatformActivator.logException(wse);
            TestCase.fail(wse.getLocalizedMessage());
        }

        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#addProjectJARToClasspathViaAPI(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, boolean)
     */
    public void addProjectJARToClasspathViaAPI(IUIContext ui,
                                               IPath jarPath,
                                               boolean isExported) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(jarPath);
        TestCase.assertFalse(jarPath.isEmpty());

        logEntry2(jarPath.toPortableString(), Boolean.toString(isExported));

        // The project name is always the first segment in a full path
        String projectName = jarPath.segment(0);

        IProjectHelper project = EclipseHelperFactory.getProjectHelper();
        IProject iproject = project.getProjectForName(projectName);
        TestCase.assertNotNull(iproject);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        IFile jarFile = resources.getFileFromWorkspace(ui, jarPath);
        TestCase.assertNotNull(jarFile);

        IJavaProject javaProject = JavaCore.create(iproject);
        IClasspathEntry entry = JavaCore.newLibraryEntry(jarFile.getLocation(), null, null, isExported);

        addToClasspath(javaProject, entry);

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#addSourceFolder(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void addSourceFolder(IUIContext ui, IPath sourceFolder)
                         throws SourceFolderCreationError {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourceFolder);
        TestCase.assertFalse(sourceFolder.isEmpty());

        logEntry2(sourceFolder.toPortableString());

        // The project name is always the first segment in a full path
        String sourceProject = sourceFolder.segment(0);
        IProjectHelper projectHelper = EclipseHelperFactory.getProjectHelper();
        IProject project = projectHelper.getProjectForName(sourceProject);
        IJavaProject javaProject = JavaCore.create(project);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.createFolder(ui, sourceFolder);

        try {
            projectHelper.invokeProjectPropertiesDialog(ui, sourceProject);

            String title = "Properties for " + sourceProject; //$NON-NLS-1$
            ui.wait(new ShellShowingCondition(title));

            ui.click(new FilteredTreeItemLocator("Java Build Path")); //$NON-NLS-1$
            ui.click(new TabItemLocator("&Source")); //$NON-NLS-1$

            ui.click(new ButtonLocator("&Add Folder...")); //$NON-NLS-1$

            String nestedTitle = "Source Folder Selection"; //$NON-NLS-1$
            ui.wait(new ShellShowingCondition(nestedTitle));

            ui.click(1,
                     new TreeItemLocator(sourceFolder.makeRelative().toPortableString(), new SWTWidgetLocator(Tree.class)),
                     SWT.BUTTON1 | SWT.CHECK);

            clickOK(ui);
            ui.wait(new ShellDisposedCondition(nestedTitle));

            listenForDialog(ui, new SourceFolderAddedShellHandler(ui));

            clickOK(ui);
            ui.wait(new ShellDisposedCondition(title));
        } catch (WidgetSearchException wse) {
            throw new SourceFolderCreationError(sourceFolder, wse);
        }

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        try {
            IPackageFragmentRoot root = javaProject.findPackageFragmentRoot(sourceFolder.makeAbsolute());

            if (root == null) {
                throw new SourceFolderCreationError(sourceFolder);
            }
        } catch (JavaModelException jme) {
            throw new SourceFolderCreationError(sourceFolder, jme);
        }

        logExit2();
    }

    /**
     * Utility for API-only methods to add classpath entries.
     */
    void addToClasspath(IJavaProject javaProject, IClasspathEntry entry) {
        TestCase.assertNotNull(javaProject);
        TestCase.assertNotNull(entry);

        try {
            if (!inClasspath(javaProject, entry)) {
                IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
                IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
                System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
                newEntries[oldEntries.length] = entry;
                javaProject.setRawClasspath(newEntries, null);
            }
        } catch (JavaModelException jme) {
            PlatformActivator.logException(jme);
            TestCase.fail(jme.getLocalizedMessage());
        }
    }

    /**
     * @param   projectName
     * @param   fullyQualifiedName
     * @param   exists
     * @return
     */
    private IType checkForJavaType(String projectName,
                                   String fullyQualifiedName,
                                   boolean exists) {
        TestCase.assertNotNull("Must specify a non-null fully qualified Java type", //$NON-NLS-1$
                               fullyQualifiedName);

        IProjectHelper projects = EclipseHelperFactory.getProjectHelper();
        final IProject project = projects.getProjectForName(projectName);

        logEntry2(projectName, fullyQualifiedName);

        IType type = null;

        try {
            final IJavaProject jProject = JavaCore.create(project);
            TestCase.assertNotNull(jProject);
            type = jProject.findType(fullyQualifiedName);
            final boolean typeExists = type != null;

            if (exists) {
                TestCase.assertTrue("Unable to resolve type " //$NON-NLS-1$
                                    + fullyQualifiedName,
                                    typeExists);
                PlatformActivator.logDebug("Found Java class: " + fullyQualifiedName); //$NON-NLS-1$
            } else {
                TestCase.assertFalse("Should not be able to resolve type" //$NON-NLS-1$
                                     + fullyQualifiedName,
                                     typeExists);
            }
        } catch (CoreException ce) {
            PlatformActivator.logException(ce);
            TestCase.fail(ce.getLocalizedMessage());
        }

        logExit2();

        return type;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#createClass(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String, java.lang.String)
     */
    public IPath createClass(IUIContext ui,
                             IPath outputPath,
                             String packageName,
                             String className) {
        return createClass(ui,
                           outputPath,
                           packageName,
                           className,
                           null);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#createClass(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String, java.lang.String,
     *       java.lang.String)
     */
    public IPath createClass(IUIContext ui,
                             IPath outputPath,
                             String packageName,
                             String className,
                             String superTypeClassName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(outputPath);
        TestCase.assertFalse(outputPath.isEmpty());
        TestCase.assertNotNull(packageName);
        TestCase.assertNotNull(className);
        TestCase.assertFalse(className.length() == 0);

        logEntry2(outputPath.toPortableString(), packageName, className, getDisplayValue(superTypeClassName));

        try {
            selectFileMenuItem(ui, IHelperConstants.MENU_FILE_NEW_OTHER);
            ui.wait(new ShellShowingCondition("New")); //$NON-NLS-1$

            ui.click(new FilteredTreeItemLocator("Java/Class")); //$NON-NLS-1$

            clickNext(ui);
            safeEnterText(ui, new LabeledLocator(Text.class, "Source fol&der:"), //$NON-NLS-1$
                          outputPath.toPortableString());
            safeEnterText(ui,
                          new LabeledLocator(Text.class, "Pac&kage:"), //$NON-NLS-1$
                          packageName);
            safeEnterText(ui,
                          new LabeledLocator(Text.class, "Na&me:"), //$NON-NLS-1$
                          className);

            if (superTypeClassName != null) {
                safeEnterText(ui,
                              new LabeledLocator(Text.class, "&Superclass:"), //$NON-NLS-1$
                              superTypeClassName);
            }

            clickFinish(ui);

            ui.wait(new ShellDisposedCondition("New Java Class")); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getMessage());
        }

        // verify the new file exists
        IPath newPath = outputPath.append(new Path(packageName.replace('.', '/') + "/" + className + ".java")); //$NON-NLS-1$ //$NON-NLS-2$
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.verifyFileExists(ui, newPath, true);

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();

        return newPath;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#createPackage(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String)
     */
    public void createPackage(IUIContext ui,
                              IPath outputPath,
                              String packageName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(outputPath);
        TestCase.assertFalse(outputPath.isEmpty());
        TestCase.assertNotNull(packageName);
        TestCase.assertFalse(packageName.length() == 0);

        logEntry2(outputPath.toPortableString(), packageName);

        try {
            selectFileMenuItem(ui, IHelperConstants.MENU_FILE_NEW_OTHER);
            ui.wait(new ShellShowingCondition("New")); //$NON-NLS-1$

            ui.click(new FilteredTreeItemLocator("Java/Package")); //$NON-NLS-1$
            clickNext(ui);

            safeEnterText(ui, new LabeledLocator(Text.class, "Source fol&der:"), //$NON-NLS-1$
                          outputPath.toPortableString());
            safeEnterText(ui,
                          new LabeledLocator(Text.class, "Na&me:"), //$NON-NLS-1$
                          packageName);

            clickFinish(ui);

            ui.wait(new ShellDisposedCondition("New Java Package")); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getMessage());
        }

        // next verify the package folder was created
        IPath packagePath = outputPath.append(packageName.replace('.', '/'));
        IFolder packageFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(packagePath);
        TestCase.assertTrue(packagePath + " DID NOT EXIST!", packageFolder.exists()); //$NON-NLS-1$

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#exportProjectJAROnClasspath(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void exportProjectJAROnClasspath(IUIContext ui,
                                            final IPath jarPath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(jarPath);
        TestCase.assertFalse(jarPath.isEmpty());

        logEntry2(jarPath.toPortableString());

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.verifyFileExists(ui, jarPath, true);

        // The project name is always the first segment in a full path
        String sourceProject = jarPath.segment(0);

        // The jar name is the last segment. We'll just be lazy in our
        // search for the jar to add and only look for the last segment
        String jarName = jarPath.lastSegment();

        IProjectHelper projectHelper = EclipseHelperFactory.getProjectHelper();
        final IProject project = projectHelper.getProjectForName(sourceProject);

        IClasspathEntry foundEntry = findClasspathEntryForJAR(project, jarPath);

        TestCase.assertNotNull("A CLASSPATH ENTRY WAS NOT FOUND FOR <" + jarPath.toPortableString() + ">", //$NON-NLS-1$ //$NON-NLS-2$
                               foundEntry);
        TestCase.assertFalse("THE JAR <" + jarPath.toPortableString() + "> IS ALREADY EXPORTED", //$NON-NLS-1$ //$NON-NLS-2$
                             foundEntry.isExported());

        try {
            projectHelper.invokeProjectPropertiesDialog(ui, sourceProject);

            ui.wait(new ShellShowingCondition("Properties for " + sourceProject)); //$NON-NLS-1$

            ui.click(new FilteredTreeItemLocator("Java Build Path")); //$NON-NLS-1$
            ui.click(new TabItemLocator("&Order and Export")); //$NON-NLS-1$

            ui.click(1, new TableItemLocator(jarName + ".*"), SWT.BUTTON1 | SWT.CHECK); //$NON-NLS-1$

            clickOK(ui);
            ui.wait(new ShellDisposedCondition("Properties for " + sourceProject)); //$NON-NLS-1$

        } catch (WidgetSearchException wse) {
            PlatformActivator.logException(wse);
            TestCase.fail(wse.getLocalizedMessage());
        }

        ui.wait(new ICondition() {
                public boolean test() {
                    IClasspathEntry finalEntry = findClasspathEntryForJAR(project, jarPath);

                    return finalEntry.isExported();
                }

                @Override
                public String toString() {
                    return " FOR THE JAR <" + jarPath.toString() + "> TO BE EXPORTED"; //$NON-NLS-1$ //$NON-NLS-2$
                }
            });

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * findClasspathEntryForJAR - Find the classpath entry for a JAR in the given project
     * or null if a classpath entry could not be found.
     *
     * @param  project  - The project to search
     * @param  jarPath  - Full path (project included) of the JAR to add to the containing
     *                  project's build path
     */
    private IClasspathEntry findClasspathEntryForJAR(IProject project, IPath jarPath) {
        IJavaProject javaProject = JavaCore.create(project);
        String jarPathString = jarPath.toPortableString();
        IClasspathEntry foundEntry = null;

        try {
            IClasspathEntry[] classpath = javaProject.getRawClasspath();

            for (IClasspathEntry nextEntry : classpath) {
                if ((nextEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) && (nextEntry.getPath().toPortableString().endsWith(jarPathString))) {
                    foundEntry = nextEntry;

                    break;
                }
            }
        } catch (JavaModelException jme) {
            PlatformActivator.logException(jme);
            TestCase.fail(jme.getLocalizedMessage());
        }

        return foundEntry;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#getJavaType(java.lang.String, java.lang.String)
     */
    public IType getJavaType(String projectName, String fullyQualifiedName) {
        return checkForJavaType(projectName, fullyQualifiedName, true);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#getPackageName(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public String getPackageName(IUIContext ui, IPath folderPath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(folderPath);
        TestCase.assertFalse(folderPath.isEmpty());

        logEntry2(folderPath.toPortableString());

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.verifyFolderExists(ui, folderPath, true);

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IFolder folder = root.getFolder(folderPath);
        TestCase.assertNotNull(folder);

        String name = null;
        IJavaElement elem = JavaCore.create(folder);

        if ((elem instanceof IPackageFragmentRoot)) {
            name = ""; //$NON-NLS-1$
        }

        if (elem instanceof IPackageFragment) {
            name = ((IPackageFragment) elem).getElementName();
        }

        logExit2(name);

        return name;
    }

    /**
     * Checks if the entry is in the project's classpath or not. Just check for existence
     * using the path of the entry
     *
     * @param   javaProject  the project whose classpath to check
     * @param   entry        the entry to check
     * @return  true if the entry is in the project's classpath, false otherwise
     * @throws  JavaModelException  if there is a problem getting the project's classpath
     */
    private boolean inClasspath(IJavaProject javaProject,
                                IClasspathEntry entry) {
        TestCase.assertNotNull(javaProject);
        TestCase.assertNotNull(entry);

        try {
            ArrayList<IClasspathEntry> oldEntries = new ArrayList<IClasspathEntry>(Arrays.asList(javaProject.getRawClasspath()));

            // THIS SIMPLE CHECK DOES NOT WORK BECAUSE IClassPathEntry DOES NOT IMPLEMENT
            // THE EQUALS METHOD
            //        if (oldEntries.contains(entry)) {
            //            return true;
            //        } else {
            //            return false;
            //        }

            IPath entryPath = entry.getPath();

            for (IClasspathEntry nextEntry : oldEntries) {
                if (nextEntry.getPath().equals(entryPath)) {
                    return true;
                }
            }
        } catch (JavaModelException jme) {
            PlatformActivator.logException(jme);
            TestCase.fail(jme.getLocalizedMessage());
        }

        return false;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#removeAllSourceFolders(com.windowtester.runtime.IUIContext,
     *       java.lang.String)
     */
    public void removeAllSourceFolders(IUIContext ui, String projectName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectName);

        logEntry2(projectName);

        try {
            IProjectHelper projects = EclipseHelperFactory.getProjectHelper();
            IProject javaIProject = projects.getProjectForName(projectName);
            TestCase.assertNotNull(javaIProject);

            IJavaProject javaProject = JavaCore.create(javaIProject);

            javaProject.setRawClasspath(new IClasspathEntry[] {}, null);
        } catch (JavaModelException jme) {
            PlatformActivator.logException(jme);
            TestCase.fail(jme.getLocalizedMessage());
        }

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#renameClass(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String)
     */
    public IPath renameClass(IUIContext ui, IPath sourceFilePath, String newClassName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourceFilePath);
        TestCase.assertFalse(sourceFilePath.isEmpty());
        TestCase.assertEquals(sourceFilePath.getFileExtension(), "java"); //$NON-NLS-1$
        TestCase.assertNotNull(newClassName);
        TestCase.assertFalse(newClassName.length() == 0);

        logEntry2(sourceFilePath.toPortableString(), newClassName);

        // first save all editors
        IWorkbenchHelper workbenchHelper = EclipseHelperFactory.getWorkbenchHelper();
        workbenchHelper.saveAndWait(ui);

        // open the package explorer
        workbenchHelper.openView(ui, IWorkbenchHelper.View.JAVA_PACKAGEEXPLORER);

        workbenchHelper.stopListeningForDialogRenameCompilationUnit(ui);

        try {
            // get the explorer tree
            IWidgetLocator treeReference = ui.find(new SWTWidgetLocator(Tree.class,
                                                                        new ViewLocator(IWorkbenchHelper.View.JAVA_PACKAGEEXPLORER.getViewID())));

            // click on the file's node and click the refactor/rename menu
            ui.contextClick(new TreeItemLocator(sourceFilePath.makeRelative().toPortableString(), treeReference),
                            "Refac&tor.*/Re&name.*"); //$NON-NLS-1$
            ui.wait(new ShellShowingCondition("Rename Compilation Unit")); //$NON-NLS-1$
            ui.enterText(newClassName);
            clickFinish(ui);

            ui.wait(new ShellDisposedCondition("Rename Compilation Unit")); //$NON-NLS-1$
        } catch (WidgetSearchException wse) {
            PlatformActivator.logException(wse);
            TestCase.fail(wse.getMessage());
        } finally {
            workbenchHelper.listenForDialogRenameCompilationUnit(ui);
        }

        // wait for the old file to be gone, and the new file to exist
        String ext = sourceFilePath.getFileExtension();
        IPath newPath = sourceFilePath.removeLastSegments(1).append(newClassName).addFileExtension(ext);
        ui.wait(new FileExistsCondition(sourceFilePath, false));
        ui.wait(new FileExistsCondition(newPath, true));

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();

        return newPath;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#verifyJavaType(java.lang.String, java.lang.String)
     */
    public void verifyJavaType(String projectName, String fullyQualifiedName) {
        logEntry2(projectName, fullyQualifiedName);
        checkForJavaType(projectName, fullyQualifiedName, true);
        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaHelper#verifyJavaType(java.lang.String,
     *       java.lang.String, boolean)
     */
    public void verifyJavaType(String projectName,
                               String fullyQualifiedName,
                               boolean exists) {
        logEntry2(projectName, fullyQualifiedName, Boolean.toString(exists));
        checkForJavaType(projectName, fullyQualifiedName, exists);
        logExit2();
    }
}
