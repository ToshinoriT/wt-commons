/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.helpers.impl;

import abbot.tester.swt.ButtonTester;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.eclipse.FileExistsCondition;
import com.windowtester.runtime.swt.condition.eclipse.FolderExistsCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.LabeledLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.misc.ContainerSelectionGroup;
import org.osgi.framework.Bundle;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.conditions.FileOpenCondition;
import org.wtc.eclipse.platform.conditions.JobExistsCondition;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IHelperConstants;
import org.wtc.eclipse.platform.helpers.IProjectHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.helpers.adapters.HelperImplAdapter;
import org.wtc.eclipse.platform.util.FileUtil;
import org.wtc.eclipse.platform.util.StringUtil;
import org.wtc.eclipse.platform.util.ZipFileUtil;
import org.wtc.eclipse.platform.util.diff.DifferenceException;
import org.wtc.eclipse.platform.util.diff.FileBlockDiffer;
import org.wtc.eclipse.platform.util.diff.IFileDiffer;
import org.wtc.eclipse.platform.util.diff.LineByLineDiffer;
import org.wtc.eclipse.platform.util.diff.LineByLineRegexDiffer;
import org.wtc.eclipse.platform.util.diff.LineByLineRexexIgnoreDiffer;
import org.wtc.eclipse.platform.util.diff.LineByLineSetDiffer;
import org.wtc.eclipse.platform.util.diff.StringExistsFileDiffer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Helper for manipulating files and folders at a low level.
 */
public class ResourceHelperImpl extends HelperImplAdapter implements IResourceHelper {
    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#closeFile(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void closeFile(IUIContext ui, IPath filePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(filePath.toPortableString());

        ui.handleConditions();

        final IPath absoluteFilePath = filePath.makeAbsolute();
        final Exception[] exceptions = new Exception[1];
        Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                    if (window != null) {
                        IWorkbenchPage page = window.getActivePage();

                        if (page != null) {
                            IEditorReference[] references = page.getEditorReferences();

                            for (IEditorReference nextRef : references) {
                                try {
                                    IEditorInput input = nextRef.getEditorInput();

                                    if (input instanceof IFileEditorInput) {
                                        IFileEditorInput fileInput = (IFileEditorInput) input;
                                        IFile file = fileInput.getFile();
                                        IPath actualFilePath = file.getFullPath();

                                        if (actualFilePath.equals(absoluteFilePath)) {
                                            IEditorPart editorPart = nextRef.getEditor(false);
                                            page.closeEditor(editorPart, false);

                                            break;
                                        }
                                    }
                                } catch (PartInitException e) {
                                    exceptions[0] = e;
                                }
                            }
                        }
                    }
                }
            });

        if (exceptions[0] != null) {
            PlatformActivator.logException(exceptions[0]);
            TestCase.fail(exceptions[0].getLocalizedMessage());
        }

        ui.wait(new FileOpenCondition(ui, filePath, false));

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#copyFile(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath)
     */
    public void copyFile(final IUIContext ui,
                         final IPath sourceFilePath,
                         final IPath destinationFilePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourceFilePath);
        TestCase.assertTrue(sourceFilePath.segmentCount() >= 2); // Project + file
        TestCase.assertNotNull(destinationFilePath);
        TestCase.assertTrue(destinationFilePath.segmentCount() >= 2);

        logEntry2(sourceFilePath.toPortableString(), destinationFilePath.toPortableString());

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.saveAndWait(ui);

        verifyFileExists(ui, sourceFilePath, true);

        IProjectHelper projects = EclipseHelperFactory.getProjectHelper();
        projects.waitForProjectExists(ui, destinationFilePath.segment(0), true);

        // Atomic operation setup
        ui.handleConditions();
        final Throwable[] thrown = new Throwable[1];
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    IFile file = getFileFromWorkspace(ui, sourceFilePath);

                    try {
                        file.copy(destinationFilePath, true, new NullProgressMonitor());
                    } catch (Throwable t) {
                        thrown[0] = t;
                    }
                }
            });

        if (thrown[0] != null) {
            PlatformActivator.logException(thrown[0]);
            TestCase.fail(thrown[0].getLocalizedMessage());
        }

        verifyFileExists(ui, destinationFilePath, true);
        workbench.saveAndWait(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#createFileFromInput(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath)
     */
    public IFile createFileFromInput(IUIContext ui,
                                     Plugin sourcePlugin,
                                     IPath sourcePath,
                                     IPath fullDestinationPath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(sourcePath);
        TestCase.assertFalse(sourcePath.isEmpty());
        TestCase.assertNotNull(fullDestinationPath);
        TestCase.assertFalse(fullDestinationPath.isEmpty());

        logEntry2(sourcePlugin.getBundle().getSymbolicName(), sourcePath.toPortableString(), fullDestinationPath.toPortableString());

        File sourceFile = getFileFromPlugin(ui, sourcePlugin, sourcePath);
        IFile destinationFile = null;

        try {
            URL sourceFileURL = FileLocator.toFileURL(sourceFile.toURL());

            InputStream inStream = sourceFileURL.openStream();

            try {
                IPath containerPath = fullDestinationPath.removeLastSegments(1);
                createFolderViaAPI(containerPath);

                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                destinationFile = root.getFile(fullDestinationPath);
                TestCase.assertFalse(fullDestinationPath.toPortableString(), destinationFile.exists());

                destinationFile.create(inStream, true, null);

                ui.wait(new FileExistsCondition(fullDestinationPath, true),
                        10000,
                        1000);
            } catch (CoreException ce) {
                PlatformActivator.logException(ce);
                TestCase.fail(ce.getMessage());
            } finally {
                inStream.close();
            }

        } catch (IOException ioe) {
            PlatformActivator.logException(ioe);
            TestCase.fail(ioe.getMessage());
        }

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();

        return destinationFile;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#createFolder(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void createFolder(IUIContext ui, IPath folderPath) {
        TestCase.assertNotNull(folderPath);
        TestCase.assertTrue(!folderPath.isEmpty());

        logEntry2(folderPath.toPortableString());

        try {
            selectFileMenuItem(ui, IHelperConstants.MENU_FILE_NEW_OTHER);
            ui.wait(new ShellShowingCondition("New")); //$NON-NLS-1$
            ui.click(new FilteredTreeItemLocator("(General|Simple)/Folder")); //$NON-NLS-1$
            clickNext(ui);

            IPath noLastSegment = folderPath.removeLastSegments(1);
            safeEnterText(ui, new LabeledLocator(Text.class, "&Enter or select the parent folder:"), //$NON-NLS-1$
                          noLastSegment.toPortableString());

            String lastSegment = folderPath.lastSegment();
            safeEnterText(ui,
                          new LabeledLocator(Text.class, "Folder &name:"), //$NON-NLS-1$
                          lastSegment);

            clickFinish(ui);
            ui.wait(new ShellDisposedCondition("New Folder")); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail("Failed to create Folder [" //$NON-NLS-1$
                          + folderPath + "]:" + e.getLocalizedMessage()); //$NON-NLS-1$
        }

        ui.wait(new FolderExistsCondition(folderPath, true));

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#createFolderViaAPI(org.eclipse.core.runtime.IPath)
     */
    public IFolder createFolderViaAPI(IPath folderPath) {
        TestCase.assertNotNull(folderPath);
        TestCase.assertTrue(!folderPath.isEmpty());

        logEntry2(folderPath.toPortableString());

        String projectName = folderPath.segment(0);
        IPath relativeFolderPath = folderPath.removeFirstSegments(1);

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(projectName);

        int count = relativeFolderPath.segmentCount();

        IFolder folder = null;

        if (count > 0) {
            // Create the first folder
            String[] segments = relativeFolderPath.segments();
            folder = project.getFolder(segments[0]);

            try {
                if (!folder.exists()) {
                    folder.create(false, true, null);
                }

                // If there are more folders, create them 1 by 1
                for (int i = 1; i < segments.length; i++) {
                    IFolder previous = folder;
                    folder = previous.getFolder(segments[i]);

                    if (!folder.exists()) {
                        folder.create(false, true, null);
                    }
                }
            } catch (CoreException ce) {
                PlatformActivator.logException(ce);
                TestCase.fail("Failed to create Folder [" //$NON-NLS-1$
                              + relativeFolderPath + "] in project [" //$NON-NLS-1$
                              + projectName + "]"); //$NON-NLS-1$
            }
        }

        logExit2();

        return folder;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#createSimpleFile(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String)
     */
    public IPath createSimpleFile(IUIContext ui,
                                  IPath outputPath,
                                  String fileName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(outputPath);
        TestCase.assertFalse(outputPath.isEmpty());
        TestCase.assertNotNull(fileName);

        logEntry2(outputPath.toPortableString(), fileName);

        IPath fullPath = null;

        try {
            selectFileMenuItem(ui, IHelperConstants.MENU_FILE_NEW_OTHER);
            ui.wait(new ShellShowingCondition("New")); //$NON-NLS-1$
            ui.click(new FilteredTreeItemLocator("(General|Simple)/File")); //$NON-NLS-1$
            clickNext(ui);

            safeEnterText(ui, new SWTWidgetLocator(Text.class, new SWTWidgetLocator(ContainerSelectionGroup.class)), outputPath.toPortableString());
            safeEnterText(ui,
                          new LabeledLocator(Text.class, "File na&me:"), //$NON-NLS-1$
                          fileName);

            clickFinish(ui);
            ui.wait(new ShellDisposedCondition("New File")); //$NON-NLS-1$

            fullPath = outputPath.append(new Path(fileName));
            verifyFileExists(ui, fullPath, true);
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getMessage());
        }

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();

        return fullPath;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#createZipCopy(com.windowtester.runtime.IUIContext,
     *       java.lang.String, java.io.File[], java.io.FilenameFilter)
     */
    public void createZipCopy(IUIContext ui,
                              String zipFileName,
                              File[] filesToZip,
                              FilenameFilter fileFilter) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(zipFileName);
        TestCase.assertFalse(zipFileName.trim().length() == 0);
        TestCase.assertNotNull(filesToZip);

        logEntry2(zipFileName, getDisplayValue(filesToZip), fileFilter.toString());

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.saveAndWait(ui);

        workbench.waitForBuild(ui);

        ui.wait(new JobExistsCondition(null),
                120000,
                1000);

        ZipFileUtil zipUtil = new ZipFileUtil();
        zipUtil.createZipCopy(ui,
                              zipFileName,
                              filesToZip,
                              fileFilter);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#deleteFileOrFolder(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void deleteFileOrFolder(IUIContext ui, IPath path) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(path);
        TestCase.assertFalse(path.isEmpty());

        logEntry2(path.toPortableString());

        // because this is looking for a tree item with this path, make it relative
        IPath relPath = path.makeRelative();

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.openView(ui, IWorkbenchHelper.View.BASIC_NAVIGATOR);

        try {
            ui.contextClick(new TreeItemLocator(relPath.toPortableString(), new ViewLocator(IWorkbenchHelper.View.BASIC_NAVIGATOR.getViewID())),
                            "&Delete"); //$NON-NLS-1$

            ui.wait(new ShellShowingCondition("(Delete Resources|Confirm Delete)"), 30000); //$NON-NLS-1$
            clickOK(ui);
            ui.wait(new ShellDisposedCondition("(Delete Resources|Confirm Delete)"), 30000); //$NON-NLS-1$

            verifyFileExists(ui, path, false);
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getMessage());
        }

        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#deleteFileOrFolderViaAPI(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void deleteFileOrFolderViaAPI(IUIContext ui, IPath path) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(path);
        TestCase.assertTrue(!path.isEmpty());

        logEntry2(path.toPortableString());

        final String projectName = path.segment(0);
        final IPath relativePath = path.removeFirstSegments(1);
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        TestCase.assertTrue("Attempting to delete a file in a non-accessible project: " + project, project.isAccessible()); //$NON-NLS-1$
        final IResource resource = project.findMember(relativePath);
        TestCase.assertNotNull("Attempting to delete a non-existent resource " + path, resource); //$NON-NLS-1$

        try {
            resource.delete(true, null);
        } catch (CoreException ce) {
            Assert.fail(ce.getMessage());
            PlatformActivator.logException(ce);
        }

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#getFileContents(java.io.File)
     */
    public String getFileContents(File sourceFile) {
        TestCase.assertNotNull(sourceFile);
        TestCase.assertTrue(sourceFile.exists());

        logEntry2(sourceFile.toString());

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));

            String line = null;
            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n"); //$NON-NLS-1$
            }

            logExit2();

            return builder.toString();
        } catch (IOException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getLocalizedMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    PlatformActivator.logException(ioe);
                    TestCase.fail(ioe.getLocalizedMessage());
                }
            }
        }

        logExit2();

        return null; // for the compiler, exceptions call TestCase.fail
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#getFileContents(org.eclipse.core.resources.IFile)
     */
    public String getFileContents(IFile sourceFile) {
        TestCase.assertNotNull(sourceFile);
        TestCase.assertTrue(sourceFile.isAccessible());

        logEntry2(sourceFile.getFullPath().toPortableString());

        String contents = ""; //$NON-NLS-1$

        IPath fullPath = sourceFile.getFullPath();
        ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();

        try {
            try {
                bufferManager.connect(fullPath, null);
                ITextFileBuffer buff = bufferManager.getTextFileBuffer(fullPath);

                TestCase.assertNotNull(buff);

                ISchedulingRule rule = buff.computeCommitRule();
                Job.getJobManager().beginRule(rule, new NullProgressMonitor());

                try {
                    IDocument doc = buff.getDocument();
                    contents = doc.get();
                } finally {
                    Job.getJobManager().endRule(rule);
                }
            } finally {
                bufferManager.disconnect(fullPath, null);
            }
        } catch (CoreException ce) {
            TestCase.fail(ce.getLocalizedMessage());
        }

        logExit2();

        return contents;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#getFileFromPlugin(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath)
     */
    public File getFileFromPlugin(IUIContext ui, Plugin plugin, IPath filePath) {
        return getFileFromPluginInternal(ui, plugin.getBundle(), filePath);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#getFileFromPlugin(com.windowtester.runtime.IUIContext,
     *       java.lang.String, org.eclipse.core.runtime.IPath)
     */
    public File getFileFromPlugin(IUIContext ui, String pluginID, IPath filePath) {
        TestCase.assertNotNull(pluginID);
        Bundle bundle = Platform.getBundle(pluginID);

        return getFileFromPluginInternal(ui, bundle, filePath);
    }

    /**
     * @see  IResourceHelper#getFileFromPlugin(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath)
     */
    private File getFileFromPluginInternal(IUIContext ui, Bundle bundle, IPath filePath) {
        TestCase.assertNotNull(bundle);
        TestCase.assertNotNull(filePath);
        TestCase.assertTrue(!filePath.isEmpty());

        logEntry2(bundle.getSymbolicName(), filePath.toPortableString());

        verifyFileExistsInternal(ui, bundle, filePath, true);

        File theFile = FileUtil.getFileFromBundle(bundle, filePath.toPortableString());

        logExit2(theFile.toString());

        return theFile;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#getFileFromWorkspace(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public IFile getFileFromWorkspace(IUIContext ui, IPath filePath) {
        TestCase.assertNotNull(filePath);
        TestCase.assertTrue(!filePath.isEmpty());
        verifyFileExists(ui, filePath, true);

        logEntry2(filePath.toPortableString());

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.getFile(filePath);
        TestCase.assertNotNull("EXPECTED THE RESOURCE AT, " //$NON-NLS-1$
                               + filePath.toPortableString() + ", TO NOT BE NULL", //$NON-NLS-1$
                               resource);
        TestCase.assertTrue("EXPECTED THE RESOURCE, " //$NON-NLS-1$
                            + resource.getLocation().toPortableString() + ", TO BE A FILE", //$NON-NLS-1$
                            resource.getType() == IResource.FILE);

        IFile theFile = (IFile) resource;
        File theRawFile = theFile.getLocation().toFile();

        TestCase.assertTrue("EXPECTED THE RESOURCE AT, " //$NON-NLS-1$
                            + filePath.toPortableString() + ", TO EXIST", //$NON-NLS-1$
                            theRawFile.exists());

        logExit2();

        return theFile;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#getFileFromWorkspaceRoot(com.windowtester.runtime.IUIContext,
     *       java.lang.String)
     */
    public File getFileFromWorkspaceRoot(IUIContext ui, String fileName) {
        TestCase.assertNotNull(ui);
        TestCase.assertFalse(StringUtil.isEmpty(fileName));

        logEntry2(fileName);

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IPath rootLocation = root.getLocation();
        File rootFile = rootLocation.toFile();
        File[] children = rootFile.listFiles();

        File found = null;

        for (File nextChild : children) {
            if (nextChild.getName().equals(fileName)) {
                found = nextChild;

                break;
            }
        }

        TestCase.assertNotNull("The file <" //$NON-NLS-1$
                               + fileName + "> was not found in the workspace root at location <" //$NON-NLS-1$
                               + rootLocation.toPortableString() + ">", //$NON-NLS-1$
                               found);

        logExit2();

        return found;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#getFileLength(org.eclipse.core.resources.IFile)
     */
    public int getFileLength(IFile sourceFile) {
        TestCase.assertNotNull(sourceFile);

        logEntry2(sourceFile.getFullPath().toPortableString());

        String contents = getFileContents(sourceFile);
        TestCase.assertNotNull(contents);

        logExit2();

        return contents.length();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#getFileTimestamp(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public long getFileTimestamp(IUIContext ui, IPath filePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(filePath.toPortableString());

        IFile theFile = getFileFromWorkspace(ui, filePath);
        long timestamp = getFileTimstampInternal(theFile);

        logExit2(Long.toString(timestamp));

        return timestamp;
    }

    /**
     * @return  long - The current timestap of the given file assuming an existence check
     *          has already taken place
     */
    private long getFileTimstampInternal(IFile theFile) {
        // Why is this a method? because if the impl ever changes
        // we want to make sure the getter method and the verification
        // method always use the same impl
        return theFile.getModificationStamp();
    }

    /**
     * getLocalFileFromWorkspacePath - Convert the given full path into a local file
     * system absolute path using the current workspace as a root location.
     *
     * @param   ui        - Driver for UI generated input
     * @param   filePath  - Full path (project included) to a resource in the current
     *                    worksapce
     * @return  File - Handle to a local file
     */
    private File getLocalFileFromWorkspace(IUIContext ui, IPath filePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertTrue(!filePath.isEmpty());

        logEntry2(filePath.toPortableString());

        IFile file = getFileFromWorkspace(ui, filePath);
        IPath absolutePath = file.getLocation();

        logExit2();

        return absolutePath.toFile();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#importFiles(com.windowtester.runtime.IUIContext,
     *       java.lang.String, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath)
     */
    public void importFiles(IUIContext ui,
                            String sourcePluginID,
                            IPath importFromPath,
                            IPath toPath) {
        TestCase.assertNotNull(sourcePluginID);
        TestCase.assertTrue(sourcePluginID.length() > 0);
        TestCase.assertNotNull(importFromPath);
        TestCase.assertFalse(importFromPath.isEmpty());
        TestCase.assertNotNull(toPath);
        TestCase.assertFalse(importFromPath.isEmpty());

        logEntry2(sourcePluginID, importFromPath.toPortableString(), toPath.toPortableString());

        Bundle bundle = getBundleForPluginID(sourcePluginID);
        importFilesInternal(ui,
                            bundle,
                            importFromPath,
                            toPath,
                            false);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#importFiles(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath)
     */
    public void importFiles(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath importFromPath,
                            IPath toPath) {
        importFiles(ui,
                    sourcePlugin,
                    importFromPath,
                    toPath,
                    false);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#importFiles(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath, boolean)
     */
    public void importFiles(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath importFromPath,
                            IPath toPath,
                            boolean overwrite) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(importFromPath);
        TestCase.assertFalse(importFromPath.isEmpty());
        TestCase.assertNotNull(toPath);
        TestCase.assertFalse(importFromPath.isEmpty());

        logEntry2(sourcePlugin.getBundle().getSymbolicName(), importFromPath.toPortableString(), toPath.toPortableString());

        importFilesBeforeUI(ui,
                            sourcePlugin,
                            importFromPath,
                            toPath,
                            overwrite);

        Bundle sourceBundle = sourcePlugin.getBundle();
        importFilesInternal(ui,
                            sourceBundle,
                            importFromPath,
                            toPath,
                            overwrite);

        importFilesAfterUI(ui,
                           sourcePlugin,
                           importFromPath,
                           toPath,
                           overwrite);

        logExit2();
    }

    /**
     * Called just after importing files with the UI. In this case, wait for job activity
     * to settle down. Subclasses of this helper should override this method to do any
     * setup before files are imported
     */
    protected void importFilesAfterUI(IUIContext ui,
                                      Plugin sourcePlugin,
                                      IPath importFromPath,
                                      IPath toPath,
                                      boolean overwrite) {
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);
    }

    /**
     * Called just before importing files with the UI. In this case, wait for job activity
     * to settle down. Subclasses of this helper should override this method to do any
     * setup before files are imported
     */
    protected void importFilesBeforeUI(IUIContext ui,
                                       Plugin sourcePlugin,
                                       IPath importFromPath,
                                       IPath toPath,
                                       boolean overwrite) {
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);
    }

    /**
     * @see  IResourceHelper#importFiles(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath, boolean)
     */
    private void importFilesInternal(IUIContext ui,
                                     Bundle sourceBundle,
                                     IPath importFromPath,
                                     IPath toPath,
                                     boolean overwrite) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourceBundle);
        TestCase.assertNotNull(importFromPath);
        TestCase.assertFalse(importFromPath.isEmpty());
        TestCase.assertNotNull(toPath);
        TestCase.assertFalse(importFromPath.isEmpty());

        logEntry2(sourceBundle.getSymbolicName(), importFromPath.toPortableString(), toPath.toPortableString());

        File importFolder = getFileFromPluginInternal(ui,
                                                      sourceBundle,
                                                      importFromPath);

        try {
            selectFileMenuItem(ui, "&Import..."); //$NON-NLS-1$
            ui.wait(new ShellShowingCondition("Import")); //$NON-NLS-1$
            ui.click(new FilteredTreeItemLocator("General/File System")); //$NON-NLS-1$
            clickNext(ui);

            ui.click(new SWTWidgetLocator(Combo.class, new SWTWidgetLocator(Composite.class)));

            PlatformActivator.logDebug("Entering text, import from path : " + importFolder.getAbsolutePath()); //$NON-NLS-1$

            ui.enterText(importFolder.getAbsolutePath());

            PlatformActivator.logDebug("Entering text, import to path : " + toPath.toPortableString()); //$NON-NLS-1$
            safeEnterText(ui, new LabeledLocator(Text.class, "Into fo&lder:"), //$NON-NLS-1$
                          toPath.toPortableString());

            ui.click(1, new TreeItemLocator(importFromPath.lastSegment()), SWT.BUTTON1 | SWT.CHECK);

            IWidgetLocator w = ui.find(new ButtonLocator("&Overwrite existing resources without warning")); //$NON-NLS-1$
            TestCase.assertTrue(w instanceof WidgetReference);
            Button cb = (Button) ((WidgetReference) w).getWidget();

            ButtonTester buttonTester = new ButtonTester();
            boolean checkboxState = buttonTester.getSelection(cb);

            if (overwrite != checkboxState) {
                ui.click(w);
            }

            clickFinish(ui);

            ui.wait(new ShellDisposedCondition("Import")); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getMessage());
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#moveFile(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath)
     */
    public IPath moveFile(IUIContext ui,
                          IPath filePath,
                          IPath targetPath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());
        TestCase.assertNotNull(targetPath);
        TestCase.assertFalse(targetPath.isEmpty());

        logEntry2(filePath.toPortableString(), targetPath.toPortableString());

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.openView(ui, IWorkbenchHelper.View.BASIC_NAVIGATOR);

        workbench.listenForDialogResourceExists(ui);

        verifyFileExists(ui, filePath, true);
        verifyFolderExists(ui, targetPath, true);

        IPath newPath = targetPath.append(new Path(filePath.lastSegment()));
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IFile resource = (IFile) root.getFile(newPath);
        boolean alreadyExists = resource.exists();

        try {
            ui.contextClick(new TreeItemLocator(filePath.makeRelative().toPortableString(), new ViewLocator(IWorkbenchHelper.View.BASIC_NAVIGATOR.getViewID())),
                            "Mo&ve..."); //$NON-NLS-1$
            ui.wait(new ShellShowingCondition("Move Resources")); //$NON-NLS-1$

            ui.click(new TreeItemLocator(targetPath.makeRelative().toPortableString()));

            clickOK(ui);

            /*
             * In the clobber case, "Continue" needs to be pressed
             */
            if (alreadyExists) {
                ui.wait(new ButtonLocator("Continue").isVisible()); //$NON-NLS-1$
                ui.click(new ButtonLocator("Continue")); //$NON-NLS-1$
            }

            ui.wait(new ShellDisposedCondition("Move Resources")); //$NON-NLS-1$

        } catch (WidgetSearchException wse) {
            PlatformActivator.logException(wse);
            TestCase.fail(wse.getLocalizedMessage());
        }

        verifyFileExists(ui, filePath, false);
        verifyFileExists(ui, newPath, true);

        workbench.waitNoJobs(ui);

        logExit2(newPath.toPortableString());

        return newPath;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#openFile(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void openFile(final IUIContext ui, final IPath filePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(filePath.toPortableString());

        final IFile file = getFileFromWorkspace(ui, filePath);

        ui.handleConditions();
        Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    TestCase.assertNotNull(window);

                    IWorkbenchPage page = window.getActivePage();
                    TestCase.assertNotNull(page);

                    try {
                        IDE.openEditor(page, file);
                    } catch (PartInitException e) {
                        PlatformActivator.logException(e);
                        TestCase.fail("Error opening file in editor: " + e.getLocalizedMessage()); //$NON-NLS-1$
                    }
                }
            });

        ui.wait(new FileOpenCondition(ui, filePath, true));

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#setFileContents(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.Plugin,
     *       org.eclipse.core.runtime.IPath)
     */
    public void setFileContents(IUIContext ui,
                                IPath filePath,
                                Plugin sourcePlugin,
                                IPath sourceFilePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(sourceFilePath);
        TestCase.assertFalse(sourceFilePath.isEmpty());

        logEntry2(filePath.toPortableString(), sourcePlugin.getBundle().getSymbolicName(), sourceFilePath.toPortableString());

        IFile targetFile = getFileFromWorkspace(ui, filePath);
        TestCase.assertTrue(targetFile.isAccessible());

        File sourceFile = getFileFromPlugin(ui, sourcePlugin, sourceFilePath);

        try {
            FileInputStream sourceInput = new FileInputStream(sourceFile);

            try {
                targetFile.setContents(sourceInput, true, false, null);
            } finally {
                sourceInput.close();
            }
        } catch (CoreException ce) {
            PlatformActivator.logException(ce);
            TestCase.fail(ce.getLocalizedMessage());
        } catch (IOException ioe) {
            PlatformActivator.logException(ioe);
            TestCase.fail(ioe.getLocalizedMessage());
        }

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.saveAndWait(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileByBlock(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath)
     */
    public void verifyFileByBlock(IUIContext ui,
                                  Plugin plugin,
                                  IPath baselineFilePath,
                                  IPath filePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(plugin);
        TestCase.assertNotNull(baselineFilePath);
        TestCase.assertFalse(baselineFilePath.isEmpty());
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(plugin.getBundle().getSymbolicName(), baselineFilePath.toPortableString(), filePath.toPortableString());

        try {
            verifyFileByBlockWithException(ui,
                                           plugin,
                                           baselineFilePath,
                                           filePath);
        } catch (DifferenceException de) {
            PlatformActivator.logException(de);
            TestCase.fail(de.getLocalizedMessage());
        }

        logExit2();
    }

    /**
     * verifyFileByBlockWithException - Use a block comparison (the baseline file is a
     * block that must exist in the given source file) to compare the given file. If the
     * baseline file could not be found or the source file could not be found then this
     * method will issue a TestCase failure.
     *
     * @param   ui                - Driver for UI generated input
     * @param   plugin            - The source plugin where the baseline file is located
     * @param   baselineFilePath  - Plugin relative path to the baseline block file.
     * @param   filePath          - Full path (project included) to the file that is to be
     *                            compared with the given baseline resource
     * @throws  DifferenceException  - If the given file has a file block difference
     */
    private void verifyFileByBlockWithException(IUIContext ui,
                                                Plugin plugin,
                                                IPath baselineFilePath,
                                                IPath filePath) throws DifferenceException {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(plugin);
        TestCase.assertNotNull(baselineFilePath);
        TestCase.assertFalse(baselineFilePath.isEmpty());
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        verifyFileWithException(ui,
                                plugin,
                                baselineFilePath,
                                filePath,
                                new FileBlockDiffer());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileByLine(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath)
     */
    public void verifyFileByLine(IUIContext ui,
                                 Plugin plugin,
                                 IPath baselineFilePath,
                                 IPath filePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(plugin);
        TestCase.assertNotNull(baselineFilePath);
        TestCase.assertFalse(baselineFilePath.isEmpty());
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(plugin.getBundle().getSymbolicName(), baselineFilePath.toPortableString(), filePath.toPortableString());

        try {
            verifyFileByLineWithException(ui,
                                          plugin,
                                          baselineFilePath,
                                          filePath);
        } catch (DifferenceException de) {
            PlatformActivator.logException(de);
            TestCase.fail(de.getLocalizedMessage());
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileByLineSet(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath)
     */
    public void verifyFileByLineSet(IUIContext ui,
                                    Plugin plugin,
                                    IPath baselineFilePath,
                                    IPath filePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(plugin);
        TestCase.assertNotNull(baselineFilePath);
        TestCase.assertFalse(baselineFilePath.isEmpty());
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(plugin.getBundle().getSymbolicName(), baselineFilePath.toPortableString(), filePath.toPortableString());

        try {
            verifyFileByLineSetWithException(ui,
                                             plugin,
                                             baselineFilePath,
                                             filePath);
        } catch (DifferenceException de) {
            PlatformActivator.logException(de);
            TestCase.fail(de.getLocalizedMessage());
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileByLineSet(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       java.lang.String[])
     */
    public void verifyFileByLineSet(IUIContext ui,
                                    Plugin plugin,
                                    IPath baselineFilePath,
                                    String[] actualLines) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(plugin);
        TestCase.assertNotNull(baselineFilePath);
        TestCase.assertFalse(baselineFilePath.isEmpty());
        TestCase.assertNotNull(actualLines);

        logEntry2(plugin.getBundle().getSymbolicName(), baselineFilePath.toPortableString(), getDisplayValue(actualLines));

        try {
            verifyFileByLineSetWithException(ui,
                                             plugin,
                                             baselineFilePath,
                                             actualLines);
        } catch (DifferenceException de) {
            PlatformActivator.logException(de);
            TestCase.fail(de.getLocalizedMessage());
        }

        logExit2();
    }

    /**
     * verifyFileByLineSetWithException - Compare the lines of the given file as a set
     * (lines must exist but order is not important). If the baseline file could not be
     * found or the source file could not be found then this method will issue a TestCase
     * failure. If the given file has a line difference then this method will issue a
     * TestCase failure
     *
     * @param  ui                - Driver for UI generated input
     * @param  plugin            - The source plugin where the baseline file is located
     * @param  baselineFilePath  - A plugin relative path (plugin NOT included) to the
     *                           baseline file containing a block of text to compare
     *                           against. Ex: <i>
     *                           resources/expected/TestName/ProjectName/folder/file.txt</i>
     * @param  actualLines       - A set of lines to be checked
     */
    public void verifyFileByLineSetWithException(IUIContext ui,
                                                 Plugin plugin,
                                                 IPath baselineFilePath,
                                                 String[] actualLines) throws DifferenceException {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(plugin);
        TestCase.assertNotNull(baselineFilePath);
        TestCase.assertFalse(baselineFilePath.isEmpty());
        TestCase.assertNotNull(actualLines);

        File baselineFile = getFileFromPlugin(ui, plugin, baselineFilePath);

        LineByLineSetDiffer differ = new LineByLineSetDiffer();
        differ.compare(ui, baselineFile, actualLines);
    }

    /**
     * verifyFileByLineSetWithException - Compare the lines of the given file as a set (
     * lines must exist but order is not important). If the baseline file could not be
     * found or the source file could not be found then this method will issue a TestCase
     * failure.
     *
     * @param   ui                - Driver for UI generated input
     * @param   plugin            - The source plugin where the baseline file is located
     * @param   baselineFilePath  - Plugin relative path to the baseline file. The file
     *                            should be in the format determined by the file
     *                            verification method used
     * @param   filePath          - Full path (project included) to the file
     * @throws  DifferenceException  - If the given file has a line difference
     */
    private void verifyFileByLineSetWithException(IUIContext ui,
                                                  Plugin plugin,
                                                  IPath baselineFilePath,
                                                  IPath filePath) throws DifferenceException {
        verifyFileWithException(ui,
                                plugin,
                                baselineFilePath,
                                filePath,
                                new LineByLineSetDiffer());
    }

    /**
     * verifyFileByLine - Use a line-by-line exact match comparison to compare the given
     * file. If the baseline file could not be found or the source file could not be found
     * then this method will issue a TestCase failure.
     *
     * @param  ui                - Driver for UI generated input
     * @param  plugin            - The source plugin where the baseline file is located
     * @param  baselineFilePath  - Plugin relative path to the baseline file. The file
     *                           should be in the format determined by the file
     *                           verification method used
     * @param  filePath          - Full path (project included) to the file
     */
    private void verifyFileByLineWithException(IUIContext ui,
                                               Plugin plugin,
                                               IPath baselineFilePath,
                                               IPath filePath) throws DifferenceException {
        verifyFileWithException(ui,
                                plugin,
                                baselineFilePath,
                                filePath,
                                new LineByLineDiffer());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileByLineWithIgnores(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath, java.lang.String)
     */
    public void verifyFileByLineWithIgnores(IUIContext ui,
                                            Plugin plugin,
                                            IPath baselineFilePath,
                                            IPath filePath,
                                            String ignorePattern) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(plugin);
        TestCase.assertNotNull(baselineFilePath);
        TestCase.assertFalse(baselineFilePath.isEmpty());
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(plugin.getBundle().getSymbolicName(), baselineFilePath.toPortableString(), filePath.toPortableString());

        try {
            verifyFileByLineWithIgnoresWithException(ui,
                                                     plugin,
                                                     baselineFilePath,
                                                     filePath,
                                                     ignorePattern);
        } catch (DifferenceException de) {
            PlatformActivator.logException(de);
            TestCase.fail(de.getLocalizedMessage());
        }

        logExit2();
    }

    /**
     * verifyFileByLineWithIgnoresWithException - Use a line-by-line exact match
     * comparison to compare the given file. If the baseline file could not be found or
     * the source file could not be found then this method will issue a TestCase failure.
     * Ignore differences between the two files for all actual file lines that match the
     * given regular expression. If the given file has a line difference then this method
     * will issue a TestCase failure
     *
     * @param  ui                - Driver for UI generated input
     * @param  plugin            - The source plugin where the baseline file is located
     * @param  baselineFilePath  - A plugin relative path (plugin NOT included) to the
     *                           baseline file containing a block of text to compare
     *                           against. Ex: <i>
     *                           resources/expected/TestName/ProjectName/folder/file.txt</i>
     * @param  filePath          - Full path (project included) to the file that is to be
     *                           compared with the given baseline resource Ex: <i>
     *                           RuntimeTestProject/src/myFolder/File.java</i>
     * @param  ignorePattern     - Ignore lines (do not perform a diff) where the source
     *                           file matches this regular expression
     */
    public void verifyFileByLineWithIgnoresWithException(IUIContext ui,
                                                         Plugin plugin,
                                                         IPath baselineFilePath,
                                                         IPath filePath,
                                                         String ignorePattern)
                                                  throws DifferenceException {
        verifyFileWithException(ui,
                                plugin,
                                baselineFilePath,
                                filePath,
                                new LineByLineRexexIgnoreDiffer(ignorePattern));
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileByRegexLine(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath)
     */
    public void verifyFileByRegexLine(IUIContext ui,
                                      Plugin plugin,
                                      IPath baselineFilePath,
                                      IPath filePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(plugin);
        TestCase.assertNotNull(baselineFilePath);
        TestCase.assertFalse(baselineFilePath.isEmpty());
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(plugin.getBundle().getSymbolicName(), baselineFilePath.toPortableString(), filePath.toPortableString());

        try {
            verifyFileByRegexLineWithException(ui,
                                               plugin,
                                               baselineFilePath,
                                               filePath);
        } catch (DifferenceException de) {
            PlatformActivator.logException(de);
            TestCase.fail(de.getLocalizedMessage());
        }

        logExit2();
    }

    /**
     * verifyFilesByRegexLineWithException - Use a line-by-line regular expression
     * comparison to compare the given file. If the baseline file could not be found or
     * the source file could not be found then this method will issue a TestCase failure
     *
     * @param   ui                - Driver for UI generated input
     * @param   plugin            - The source plugin where the baseline file is located
     * @param   baselineFilePath  - Plugin relative path to the baseline file. The file
     *                            should be in the format determined by the file
     *                            verification method used
     * @param   filePath          - Full path (project included) to the file that is to be
     *                            compared with the given baseline resource
     * @throws  DifferenceException  - If one of the given files has a file difference
     */
    private void verifyFileByRegexLineWithException(IUIContext ui,
                                                    Plugin plugin,
                                                    IPath baselineFilePath,
                                                    IPath filePath) throws DifferenceException {
        verifyFileWithException(ui,
                                plugin,
                                baselineFilePath,
                                filePath,
                                new LineByLineRegexDiffer());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileByString(com.windowtester.runtime.IUIContext,
     *       java.lang.String, org.eclipse.core.runtime.IPath, boolean)
     */
    public void verifyFileByString(IUIContext ui,
                                   String baselineString,
                                   IPath filePath,
                                   boolean exists) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(baselineString);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(baselineString, filePath.toPortableString(), Boolean.toString(exists));

        try {
            verifyFileByStringWithException(ui,
                                            baselineString,
                                            filePath,
                                            exists);
        } catch (DifferenceException de) {
            PlatformActivator.logException(de);
            TestCase.fail(de.getLocalizedMessage());
        }

        logExit2();

    }

    /**
     * verifyFileByStringWithException - Verify that the given String exists in the given
     * workspace file. The String *must not* span multiple lines since this comparison
     * will search line by line in the given file. The given String does not need to be a
     * full line of text and will be considered a match if a line of text in the given
     * file contains the String.
     *
     * @param   ui              - Driver for UI generated input
     * @param   baselineString  - A single line of text (does not need to be a complete
     *                          line) to search for in the given file.
     * @param   filePath        - Full path (project included) to the file that is to be
     *                          compared with the given String Ex: <i>
     *                          RuntimeTestProject/src/myFolder/File.java</i>
     * @param   exists          - True if the text should exist in the target file for the
     *                          verification to succeed; False if the text should not
     *                          exist for the verification to succeed
     * @throws  DifferenceException  - Thrown when the String is not found in any of the
     *                               lines in the file and it was expected to be found, or
     *                               when the String was found in at least one of the
     *                               lines in the file and it was expected to not be found
     */
    private void verifyFileByStringWithException(IUIContext ui,
                                                 String baselineString,
                                                 IPath filePath,
                                                 boolean exists) throws DifferenceException {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(baselineString);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(baselineString, filePath.toPortableString(), Boolean.toString(exists));

        File actualFile = getLocalFileFromWorkspace(ui, filePath);

        StringExistsFileDiffer stringExistsDiffer = new StringExistsFileDiffer();
        stringExistsDiffer.compare(ui,
                                   baselineString,
                                   actualFile,
                                   exists);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileExists(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, boolean)
     */
    public void verifyFileExists(IUIContext ui,
                                 IPath fullFilePath,
                                 boolean exists) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(fullFilePath);
        TestCase.assertFalse(fullFilePath.isEmpty());

        logEntry2(fullFilePath.toPortableString(), Boolean.toString(exists));

        ui.wait(new FileExistsCondition(fullFilePath, exists));

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileExists(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath, boolean)
     */
    public void verifyFileExists(IUIContext ui,
                                 Plugin plugin,
                                 final IPath filePath,
                                 final boolean exists) {
        Bundle bundle = plugin.getBundle();
        verifyFileExistsInternal(ui,
                                 bundle,
                                 filePath,
                                 exists);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileExists(com.windowtester.runtime.IUIContext,
     *       java.lang.String, org.eclipse.core.runtime.IPath, boolean)
     */
    public void verifyFileExists(IUIContext ui,
                                 String pluginID,
                                 final IPath filePath,
                                 final boolean exists) {
        Bundle bundle = getBundleForPluginID(pluginID);
        verifyFileExistsInternal(ui,
                                 bundle,
                                 filePath,
                                 exists);
    }

    /**
     * @see  IResourceHelper#verifyFileExists(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath, boolean)
     */
    private void verifyFileExistsInternal(IUIContext ui,
                                          final Bundle bundle,
                                          final IPath filePath,
                                          final boolean exists) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(bundle);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(bundle.getSymbolicName(), filePath.toPortableString(), Boolean.toString(exists));

        ui.wait(new ICondition() {
                public boolean test() {
                    URL fileURL = FileLocator.find(bundle, filePath, null);

                    return (fileURL != null) == exists;
                }

                @Override
                public String toString() {
                    return " FOR THE PLUGIN <" //$NON-NLS-1$
                        + bundle.getSymbolicName() + "> TO HAVE A FILE <" //$NON-NLS-1$
                        + filePath.toPortableString() + "> TO HAVE EXISTENCE <" //$NON-NLS-1$
                        + exists + ">"; //$NON-NLS-1$
                }
            }, 2000, 100);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFileUpdated(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, long, boolean)
     */
    public void verifyFileUpdated(IUIContext ui,
                                  final IPath filePath,
                                  final long timestamp,
                                  boolean wasUpdated) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(filePath.toPortableString(), Long.toString(timestamp), Boolean.toString(wasUpdated));

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);

        final IFile theFile = getFileFromWorkspace(ui, filePath);

        if (!wasUpdated) {
            // Along with the wait no jobs, we should give the file some
            // time to allow it to update (to avoid the case where it hasn't
            // been updated yet and therefore giving a false positive)
            new SWTIdleCondition().waitForIdle();
            ui.pause(5000);

            long actualTimestamp = getFileTimstampInternal(theFile);
            TestCase.assertEquals("EXPECTED THE FILE <" //$NON-NLS-1$
                                  + filePath.toPortableString() + "> TO NOT HAVE BEEN UPDATED", //$NON-NLS-1$
                                  timestamp,
                                  actualTimestamp);
        } else {
            ui.wait(new ICondition() {
                    public boolean test() {
                        long actualTimestamp = getFileTimstampInternal(theFile);

                        return actualTimestamp > timestamp;
                    }

                    @Override
                    public String toString() {
                        return " FOR THE FILE <" //$NON-NLS-1$
                            + filePath.toPortableString() + "> TO HAVE BEEN UPDATED"; //$NON-NLS-1$
                    }
                });
        }

        logExit2();

    }

    /**
     * verifyFilesWithException - Use the given differ to compare the lines of the given
     * file. If the baseline file could not be found or the source file could not be found
     * then this method will issue a TestCase failure.
     *
     * @param   ui                - Driver for UI generated input
     * @param   plugin            - The source plugin where the baseline file is located
     * @param   baselineFilePath  - Plugin relative path to the baseline file. The file
     *                            should be in the format determined by the file
     *                            verification method used
     * @param   filePath          - Full path (project included) to the file that is to be
     *                            compared with the given baseline resource
     * @param   IFileDiffer       - The line-by-line file comparison algorithm to use when
     *                            comparing the files
     * @throws  DifferenceException  - If one of the given files has a line difference
     */
    private void verifyFileWithException(IUIContext ui,
                                         Plugin plugin,
                                         IPath baselineFilePath,
                                         IPath filePath,
                                         IFileDiffer lineDiffer) throws DifferenceException {
        TestCase.assertNotNull(plugin);

        TestCase.assertNotNull(baselineFilePath);
        TestCase.assertTrue(!baselineFilePath.isEmpty());

        File baselineFile = getFileFromPlugin(ui, plugin, baselineFilePath);

        TestCase.assertNotNull(filePath);
        TestCase.assertTrue(!filePath.isEmpty());

        File actualFile = getLocalFileFromWorkspace(ui, filePath);

        lineDiffer.compare(ui, baselineFile, actualFile);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyFolderExists(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, boolean)
     */
    public void verifyFolderExists(IUIContext ui,
                                   IPath fullFolderPath,
                                   boolean exists) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(fullFolderPath);
        TestCase.assertFalse(fullFolderPath.isEmpty());

        logEntry2(fullFolderPath.toPortableString(), Boolean.toString(exists));

        ui.wait(new FolderExistsCondition(fullFolderPath, exists));

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#verifyStringByRegexFileLine(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       java.lang.String)
     */
    public void verifyStringByRegexFileLine(IUIContext ui,
                                            Plugin plugin,
                                            IPath baselineFilePath,
                                            String contents) {
        try {
            verifyStringByRegexFileLineWithException(ui,
                                                     plugin,
                                                     baselineFilePath,
                                                     contents);
        } catch (DifferenceException de) {
            PlatformActivator.logException(de);
            TestCase.fail(de.getLocalizedMessage());
        }
    }

    /**
     * @see  IResourceHelper#verifyStringByRegexFileLine(IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       java.lang.String)
     */
    public void verifyStringByRegexFileLineWithException(IUIContext ui,
                                                         Plugin plugin,
                                                         IPath baselineFilePath,
                                                         String contents)
                                                  throws DifferenceException {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(baselineFilePath);
        TestCase.assertFalse(baselineFilePath.isEmpty());
        TestCase.assertNotNull(contents);

        logEntry2(plugin.getBundle().getSymbolicName(), baselineFilePath.toPortableString(), contents);

        File baselineFile = getFileFromPlugin(ui, plugin, baselineFilePath);
        LineByLineRegexDiffer lineDiffer = new LineByLineRegexDiffer();
        lineDiffer.compare(ui, baselineFile, contents);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IResourceHelper#writeFileContents(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String)
     */
    public void writeFileContents(IUIContext ui,
                                  IPath filePath,
                                  String contents) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertTrue(!filePath.isEmpty());
        TestCase.assertNotNull(contents);

        logEntry2(filePath.toPortableString(), Integer.toString(contents.length()));

        String projectName = filePath.segment(0);
        IPath folderPath = filePath.removeLastSegments(1);

        IProjectHelper anyProject = EclipseHelperFactory.getProjectHelper();
        anyProject.waitForProjectExists(ui, projectName, true);

        createFolderViaAPI(folderPath);

        IFile worksapceFile = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
        File outputFile = worksapceFile.getLocation().toFile();

        try {
            outputFile.createNewFile();

            FileWriter fileWriter = new FileWriter(outputFile);

            try {
                fileWriter.write(contents);
            } finally {
                fileWriter.close();
            }
        } catch (IOException ioe) {
            PlatformActivator.logException(ioe);
            TestCase.fail(ioe.getLocalizedMessage());
        }

        verifyFileExists(ui, filePath, true);

        logExit2();
    }
}
