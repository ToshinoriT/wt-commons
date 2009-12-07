/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.helpers.impl;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import junit.framework.TestCase;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.EditorReference;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.intro.IIntroManager;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.conditions.AllEditorsClosedCondition;
import org.wtc.eclipse.platform.conditions.JobExistsCondition;
import org.wtc.eclipse.platform.conditions.JobsInFamilyExistCondition;
import org.wtc.eclipse.platform.conditions.NoResourceChangedEventsCondition;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IPerspective;
import org.wtc.eclipse.platform.helpers.IProjectHelper;
import org.wtc.eclipse.platform.helpers.IView;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.helpers.adapters.HelperImplAdapter;
import org.wtc.eclipse.platform.shellhandlers.ConfirmDeleteDialogShellHandler;
import org.wtc.eclipse.platform.shellhandlers.ConfirmFileContentChangedDialog;
import org.wtc.eclipse.platform.shellhandlers.ConfirmOverwriteFilesDialogShellHandler;
import org.wtc.eclipse.platform.shellhandlers.FileDeletedShellHandler;
import org.wtc.eclipse.platform.shellhandlers.LicenseAgreementDialogShellHandler;
import org.wtc.eclipse.platform.shellhandlers.OpenPerspectiveShellHandler;
import org.wtc.eclipse.platform.shellhandlers.ProgressDialogShellHandler;
import org.wtc.eclipse.platform.shellhandlers.RebuildingShellHandler;
import org.wtc.eclipse.platform.shellhandlers.RenameCompilationUnitDialogShellHandler;
import org.wtc.eclipse.platform.shellhandlers.ResourceExistsShellHandler;
import org.wtc.eclipse.platform.shellhandlers.SaveAllModifiedResourcesShellHandler;
import org.wtc.eclipse.platform.shellhandlers.SaveResourceShellHandler;
import org.wtc.eclipse.platform.shellhandlers.SaveResourceShellHandler2;
import org.wtc.eclipse.platform.shellhandlers.SaveResourcesShellHandler;
import org.wtc.eclipse.platform.shellhandlers.SettingBuildPathShellHandler;
import org.wtc.eclipse.platform.shellhandlers.UserOperationWaitingShellHandler;
import org.wtc.eclipse.platform.shellhandlers.WrappedWizardClosingShellHandler;
import org.wtc.eclipse.platform.util.ExceptionHandler;
import org.wtc.eclipse.platform.util.MarkerUtil;
import org.wtc.eclipse.platform.util.ThreadUtil;
import org.wtc.eclipse.platform.util.WorkbenchUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Helper workbench specific tasks.
 */
public class WorkbenchHelperImpl extends HelperImplAdapter implements IWorkbenchHelper {
    private static enum ExpectedResult {
        NO_PROBLEMS,
        PROBLEMS,
        UNSPECIFIED
    }

    /**
     * Add a Job by the given title to the list of expected jobs when waitNoJobs is
     * called. An expected job means that when waitNoJobs is called (waitNoJobs waits
     * until all of the Jobs in the JobManager are SLEEPING or STOPPING) the Job with that
     * title can be considered a RUNNING job and the condition can still be met
     *
     * @param  jobTitle  - Expected Job
     */
    public void addExpectedJob(String jobTitle) {
        TestCase.assertNotNull(jobTitle);
        WaitForJobsRegistry.addExpectedJob(jobTitle);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#bringToFront(com.windowtester.runtime.IUIContext)
     */
    public void bringToFront(IUIContext ui) {
        handleConditions(ui);
        WorkbenchUtil.bringWorkbenchToFront();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#buildProject(com.windowtester.runtime.IUIContext,
     *       java.lang.String, boolean)
     */
    public void buildProject(IUIContext ui, String projectName, boolean fullBuild) {
        logEntry2(projectName, Boolean.toString(fullBuild));

        int buildType = fullBuild ? IncrementalProjectBuilder.FULL_BUILD
                                  : IncrementalProjectBuilder.INCREMENTAL_BUILD;

        verifyBuild(ui, projectName, buildType, ExpectedResult.UNSPECIFIED);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#cleanAllProjects(com.windowtester.runtime.IUIContext)
     */
    public void cleanAllProjects(IUIContext ui) {
        TestCase.assertNotNull(ui);

        logEntry2();

        cleanAllProjectsBeforeUI(ui);

        try {
            selectProjectMenuItem(ui, "Clea&n..."); //$NON-NLS-1$
            ui.wait(new ShellShowingCondition("Clean")); //$NON-NLS-1$

            ui.click(new ButtonLocator("Clean &all projects")); //$NON-NLS-1$
            clickOK(ui);

            ui.wait(new ShellDisposedCondition("clean")); //$NON-NLS-1$
        } catch (WidgetSearchException wse) {
            ExceptionHandler.handle(wse);
        }

        cleanAllProjectsAfterUI(ui);

        logExit2();
    }

    /**
     * Called just after the UI operations on cleanAllProjects. By default, wait for the
     * auto build job to finish. Subclasses should implement this method for any post-UI
     * operations
     *
     * @since  3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void cleanAllProjectsAfterUI(IUIContext ui) {
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.waitNoJobs(ui);
    }

    /**
     * Called just before the UI operations on cleanAllProjects. By default, do nothing.
     * Subclasses should implement this method for any pre-UI operations
     *
     * @since  3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void cleanAllProjectsBeforeUI(IUIContext ui) {
        // By default do nothing
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#cleanBuildProject(com.windowtester.runtime.IUIContext,
     *       java.lang.String)
     */
    public void cleanBuildProject(IUIContext ui, String projectName) {
        verifyBuild(ui,
                    projectName,
                    IncrementalProjectBuilder.CLEAN_BUILD,
                    ExpectedResult.UNSPECIFIED);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#closeActiveEditor(com.windowtester.runtime.IUIContext)
     */
    public void closeActiveEditor(IUIContext ui) {
        logEntry2();

        if (getActiveEditor() != null) {
            selectFileMenuItem(ui, "&Close.*"); //$NON-NLS-1$
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#closeActivePerspective(com.windowtester.runtime.IUIContext)
     */
    public void closeActivePerspective(IUIContext ui) {
        TestCase.assertNotNull(ui);

        logEntry2();

        try {
            // close the active perspective
            ui.click(new MenuItemLocator("&Window/&Close Perspective")); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            ExceptionHandler.handle(e);
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#closeAllEditors(com.windowtester.runtime.IUIContext)
     */
    public void closeAllEditors(IUIContext ui) {
        closeAllEditors(ui, false);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#closeAllEditors(com.windowtester.runtime.IUIContext,
     *       boolean)
     */
    public void closeAllEditors(IUIContext ui, final boolean save) {
        logEntry2();

        if (getActiveEditor() != null) {
            selectFileMenuItem(ui, "C&lose All.*"); //$NON-NLS-1$
        }

        // make sure we wait until they are all closed
        ui.wait(new AllEditorsClosedCondition());

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#closeView(com.windowtester.runtime.IUIContext,
     *       org.wtc.eclipse.platform.helpers.IView)
     */
    public void closeView(IUIContext ui, final IView view) {
        TestCase.assertNotNull(ui);

        logEntry2(view.getViewID());

        ui.handleConditions();

        // TODO: use the com.windowtester.swt.IUIContext instead of going through base eclipse APIs
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    IViewPart part = page.findView(view.getViewID());

                    if (part != null) {
                        page.hideView(part);
                    }
                }
            });

        verifyViewOpen(ui, view, false);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#closeWelcomePage(com.windowtester.runtime.IUIContext)
     */
    public void closeWelcomePage(IUIContext ui) {
        logEntry2();

        ui.handleConditions();

        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    IWorkbench workbench = PlatformUI.getWorkbench();
                    IIntroManager intro = workbench.getIntroManager();
                    intro.closeIntro(intro.getIntro());
                }
            });
        new SWTIdleCondition().waitForIdle();

        logExit2();
    }

    /**
     * @see org.wtc.eclipse.platform.helpers.IWorkbenchHelper#disableAutoBuild(com.windowtester.runtime.IUIContext)
     */
    public void disableAutoBuild(IUIContext ui) {
        logEntry2();

        TestCase.assertNotNull(ui);

        try {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IWorkspaceDescription description = workspace.getDescription();

            TestCase.assertTrue("Build Automatically is already dis-abled.", description.isAutoBuilding()); //$NON-NLS-1$

            ui.click(new MenuItemLocator("&Project/Build Auto&matically")); //$NON-NLS-1$

            description = workspace.getDescription();
            TestCase.assertFalse("Failed to disable Build Automatically.", description.isAutoBuilding()); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            ExceptionHandler.handle(e);
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#disableAutoBuildViaAPI()
     */
    public void disableAutoBuildViaAPI() {
        logEntry2();
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceDescription description = workspace.getDescription();

        if (description.isAutoBuilding()) {
            description.setAutoBuilding(false);

            try {
                workspace.setDescription(description);
            } catch (CoreException ce) {
                ExceptionHandler.handle(ce, "Failed to disable auto-build: "); //$NON-NLS-1$
            }
        }

        logExit2();
    }

    /**
     * @see org.wtc.eclipse.platform.helpers.IWorkbenchHelper#enableAutoBuild(com.windowtester.runtime.IUIContext)
     */
    public void enableAutoBuild(IUIContext ui) {
        logEntry2();

        TestCase.assertNotNull(ui);

        try {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IWorkspaceDescription description = workspace.getDescription();

            TestCase.assertFalse("Build Automatically is already enabled.", description.isAutoBuilding()); //$NON-NLS-1$

            ui.click(new MenuItemLocator("&Project/Build Auto&matically")); //$NON-NLS-1$

            description = workspace.getDescription();
            TestCase.assertTrue("Failed to set disable Build Automatically.", description.isAutoBuilding()); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            ExceptionHandler.handle(e);
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#enableAutoBuildViaAPI()
     */
    public void enableAutoBuildViaAPI() {
        logEntry2();

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceDescription description = workspace.getDescription();

        if (!description.isAutoBuilding()) {
            description.setAutoBuilding(true);

            try {
                workspace.setDescription(description);
            } catch (CoreException ce) {
                ExceptionHandler.handle(ce, "Failed to enable auto-build: "); //$NON-NLS-1$
            }
        }

        logExit2();
    }

    /**
     * getActiveEditor - Get the active editor, based on the active workbench window.
     *
     * @return  the active IEditorPart, or null if no editors are active.
     */
    public IEditorPart getActiveEditor() {
        final IEditorPart[] editorParts = new IEditorPart[1];
        ThreadUtil.ensureRunOnUIThread(new Runnable() {
                public void run() {
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                    if (window != null) {
                        IWorkbenchPage page = window.getActivePage();

                        if (page != null) {
                            editorParts[0] = page.getActiveEditor();
                        }
                    }
                }
            });

        return editorParts[0];
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#getActiveEditorComposite()
     */
    public Composite getActiveEditorComposite() {
        final Composite[] root = new Composite[1];

        ThreadUtil.ensureRunOnUIThread(new Runnable() {
                public void run() {
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                    if (window != null) {
                        IWorkbenchPage page = window.getActivePage();

                        if (page != null) {
                            IEditorPart activeEditor = page.getActiveEditor();

                            if (activeEditor != null) {
                                IEditorReference[] refs = page.getEditorReferences();

                                for (IEditorReference nextRef : refs) {
                                    IEditorPart refPart = nextRef.getEditor(false);

                                    if ((refPart != null) && (refPart == activeEditor)) {
                                        if (nextRef instanceof EditorReference) {
                                            EditorReference refImpl = (EditorReference) nextRef;
                                            PartPane pane = refImpl.getPane();

                                            if (pane != null) {
                                                Control control = pane.getControl();

                                                if (control instanceof Composite) {
                                                    root[0] = (Composite) control;

                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });

        return root[0];
    }

    /**
     * @param   project
     * @param   reportOnlyErrors
     * @return
     */
    private IMarker[] getMarkers(IProject project,
                                 boolean reportOnlyErrors) {
        IMarker[] markers = new IMarker[0];

        try {
            markers = project.findMarkers(IMarker.PROBLEM, true,
                                          IResource.DEPTH_INFINITE);

            if (reportOnlyErrors) {
                ArrayList<IMarker> updatedMarkers = new ArrayList<IMarker>();
                Collections.addAll(updatedMarkers, markers);

                for (int i = 0; i < updatedMarkers.size();) {
                    IMarker nextMarker = updatedMarkers.get(i);

                    if ((nextMarker != null) && (((Integer) nextMarker.getAttribute(IMarker.SEVERITY)) == IMarker.SEVERITY_ERROR)) {
                        i++;
                    } else {
                        updatedMarkers.remove(i);
                    }
                }

                markers = new IMarker[updatedMarkers.size()];
                markers = updatedMarkers.toArray(markers);
            }
        } catch (CoreException ce) {
            ExceptionHandler.handle(ce, "Error retrieving problem markers for project " //$NON-NLS-1$
                                    + project + ": "); //$NON-NLS-1$
        }

        return markers;
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#invokeRedo(com.windowtester.runtime.IUIContext)
     */
    public void invokeRedo(IUIContext ui) {
        logEntry2();
        TestCase.assertNotNull(ui);

        try {
            ui.click(new MenuItemLocator("&Edit/&Redo.*")); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            ExceptionHandler.handle(e);
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#invokeUndo(com.windowtester.runtime.IUIContext)
     */
    public void invokeUndo(IUIContext ui) {
        logEntry2();
        TestCase.assertNotNull(ui);

        try {
            ui.click(new MenuItemLocator("&Edit/&Undo.*")); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            ExceptionHandler.handle(e);
        }

        logExit2();
    }

    /**
     * @return  boolean - True if the workspace is autobuilding. False otherwise
     */
    protected boolean isAutoBuildEnabled() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceDescription description = workspace.getDescription();

        return description.isAutoBuilding();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogConfirmDelete(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogConfirmDelete(IUIContext ui) {
        listenForDialog(ui, new ConfirmDeleteDialogShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogFileContentChanged(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogFileContentChanged(IUIContext ui) {
        listenForDialog(ui, new ConfirmFileContentChangedDialog());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogFileDeleted(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogFileDeleted(IUIContext ui) {
        listenForDialog(ui, new FileDeletedShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogLicenceAgreement(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogLicenceAgreement(IUIContext ui) {
        listenForDialog(ui, new LicenseAgreementDialogShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogOpenPerspective(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogOpenPerspective(IUIContext ui) {
        listenForDialog(ui, new OpenPerspectiveShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogOverwriteFiles(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogOverwriteFiles(IUIContext ui) {
        listenForDialog(ui, new ConfirmOverwriteFilesDialogShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogProgress(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogProgress(IUIContext ui) {
        TestCase.assertNotNull(ui);

        ProgressDialogShellHandler progressHandler = new ProgressDialogShellHandler();
        listenForDialog(ui, progressHandler, progressHandler);

        UserOperationWaitingShellHandler operationWaitingHandler =
            new UserOperationWaitingShellHandler();
        listenForDialog(ui, operationWaitingHandler, operationWaitingHandler);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogRebuilding(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogRebuilding(IUIContext ui) {
        listenForDialog(ui, new RebuildingShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogRenameCompilationUnit(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogRenameCompilationUnit(IUIContext ui) {
        listenForDialog(ui, new RenameCompilationUnitDialogShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogResourceExists(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogResourceExists(IUIContext ui) {
        listenForDialog(ui, new ResourceExistsShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogSaveAllModifiedResources(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogSaveAllModifiedResources(IUIContext ui) {
        //TODO: Save All Modified Resources dialog is not
        //modal like it should be. The following is a workaround to the fact
        //that the short version of listenForDialog() checks modality.
        //This version does not.
        SaveAllModifiedResourcesShellHandler temp = new SaveAllModifiedResourcesShellHandler();
        listenForDialog(ui, temp, temp);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogSaveResource(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogSaveResource(IUIContext ui) {
        listenForDialog(ui, new SaveResourceShellHandler());
        listenForDialog(ui, new SaveResourceShellHandler2());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogSaveResources(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogSaveResources(IUIContext ui) {
        listenForDialog(ui, new SaveResourcesShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogSettingBuildPath(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogSettingBuildPath(IUIContext ui) {
        listenForDialog(ui, new SettingBuildPathShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#listenForDialogWizardClosing(com.windowtester.runtime.IUIContext)
     */
    public void listenForDialogWizardClosing(IUIContext ui) {
        WrappedWizardClosingShellHandler handler = new WrappedWizardClosingShellHandler();
        listenForDialog(ui, handler, handler.getShellCondition());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#logDebug(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, java.lang.String)
     */
    public void logDebug(IUIContext ui, Plugin plugin, String message) {
        PlatformActivator.logDebug(message);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#maximizeWorkbench(com.windowtester.runtime.IUIContext)
     */
    public void maximizeWorkbench(IUIContext ui) {
        handleConditions(ui);

        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    IWorkbench workbench = PlatformUI.getWorkbench();
                    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                    window.getShell().setMaximized(true);
                }
            });

        waitNoJobs(ui);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#openPerspective(com.windowtester.runtime.IUIContext,
     *       org.wtc.eclipse.platform.helpers.IPerspective)
     */
    public void openPerspective(IUIContext ui, IPerspective type) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(type);

        logEntry2(type.getPerspectiveLabel());

        try {
            ui.click(new MenuItemLocator("&Window/&Open Perspective/&Other...")); //$NON-NLS-1$
            ui.wait(new ShellShowingCondition("Open Perspective")); //$NON-NLS-1$
            ui.click(new TableItemLocator(type.getPerspectiveLabel()));
            clickOK(ui);
            ui.wait(new ShellDisposedCondition("Open Perspective")); //$NON-NLS-1$

        } catch (WidgetSearchException e) {
            ExceptionHandler.handle(e);
        }

        verifyPerspectiveOpen(ui, type, true);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#openView(com.windowtester.runtime.IUIContext,
     *       org.wtc.eclipse.platform.helpers.IView)
     */
    public void openView(IUIContext ui, final IView view) {
        openView(ui, view, 30000);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#openView(com.windowtester.runtime.IUIContext,
     *       org.wtc.eclipse.platform.helpers.IView, long)
     */
    public void openView(IUIContext ui, final IView view, long timeout) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(view);

        logEntry2(view.getViewPath());

        // If the view is already open, just make sure it has focus
        final PartInitException[] pie = new PartInitException[1];
        final boolean[] shouldActivate = new boolean[1];
        shouldActivate[0] = true;
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    IViewPart part = page.findView(view.getViewID());

                    if (part != null) {
                        shouldActivate[0] = false;

                        try {
                            page.showView(view.getViewID());
                        } catch (PartInitException e) {
                            pie[0] = e;
                        }
                    }
                }
            });

        if (pie[0] != null) {
            ExceptionHandler.handle(pie[0]);
        }

        if (shouldActivate[0]) {
            try {
                ui.pause(500);
                ui.click(new MenuItemLocator("Window/Show View/Other.*")); //$NON-NLS-1$
                ui.click(new FilteredTreeItemLocator(view.getViewPath()));
                clickOK(ui);
            } catch (WidgetSearchException e) {
                ExceptionHandler.handle(e);
            }

            verifyViewOpen(ui, view, true, timeout);
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#removeExpectedJob(java.lang.String)
     */
    public void removeExpectedJob(String jobTitle) {
        TestCase.assertNotNull(jobTitle);
        WaitForJobsRegistry.removeExpectedJob(jobTitle);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#save(com.windowtester.runtime.IUIContext)
     */
    public void save(IUIContext ui) {
        logEntry2();

        setExpectedDelay(ui, 180000);
        ui.handleConditions();
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    IWorkbench workbench = PlatformUI.getWorkbench();
                    workbench.saveAllEditors(false);
                }
            });
        ui.handleConditions();

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#saveAndWait(com.windowtester.runtime.IUIContext)
     */
    public void saveAndWait(IUIContext ui) {
        logEntry2();

        save(ui);
        waitNoJobs(ui);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#stopListeningForDialogConfirmDelete(com.windowtester.runtime.IUIContext)
     */
    public void stopListeningForDialogConfirmDelete(IUIContext ui) {
        stopListeningForDialog(ui, new ConfirmDeleteDialogShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#stopListeningForDialogConfirmOverwrite(com.windowtester.runtime.IUIContext)
     */
    public void stopListeningForDialogConfirmOverwrite(IUIContext ui) {
        stopListeningForDialog(ui, new ConfirmOverwriteFilesDialogShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#stopListeningForDialogFileContentChanged(com.windowtester.runtime.IUIContext)
     */
    public void stopListeningForDialogFileContentChanged(IUIContext ui) {
        stopListeningForDialog(ui, new ConfirmFileContentChangedDialog());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#stopListeningForDialogFileDeleted(com.windowtester.runtime.IUIContext)
     */
    public void stopListeningForDialogFileDeleted(IUIContext ui) {
        stopListeningForDialog(ui, new FileDeletedShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#stopListeningForDialogRebuilding(com.windowtester.runtime.IUIContext)
     */
    public void stopListeningForDialogRebuilding(IUIContext ui) {
        stopListeningForDialog(ui, new RebuildingShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#stopListeningForDialogRenameCompilationUnit(com.windowtester.runtime.IUIContext)
     */
    public void stopListeningForDialogRenameCompilationUnit(IUIContext ui) {
        stopListeningForDialog(ui, new RenameCompilationUnitDialogShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#stopListeningForDialogSaveAllModifiedResources(com.windowtester.runtime.IUIContext)
     */
    public void stopListeningForDialogSaveAllModifiedResources(IUIContext ui) {
        stopListeningForDialog(ui, new SaveAllModifiedResourcesShellHandler());
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#verifyBuild(com.windowtester.runtime.IUIContext,
     *       java.lang.String)
     */
    public void verifyBuild(IUIContext ui, String projectName) {
        verifyBuild(ui, projectName, true, true);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#verifyBuild(com.windowtester.runtime.IUIContext,
     *       java.lang.String, boolean, boolean)
     */
    public void verifyBuild(IUIContext ui,
                            String projectName,
                            boolean fullBuild,
                            boolean shouldPass) {
        logEntry2(projectName, Boolean.toString(fullBuild), Boolean.toString(shouldPass));

        int buildType = fullBuild ? IncrementalProjectBuilder.FULL_BUILD
                                  : IncrementalProjectBuilder.INCREMENTAL_BUILD;
        verifyBuild(ui,
                    projectName,
                    buildType,
                    shouldPass ? ExpectedResult.NO_PROBLEMS : ExpectedResult.PROBLEMS);

        logExit2();
    }

    private void verifyBuild(final IUIContext ui,
                             final String projectName,
                             final int buildType,
                             final ExpectedResult expectedResult) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectName);
        logEntry2(projectName, Integer.toString(buildType), expectedResult.toString());

        IProjectHelper any = EclipseHelperFactory.getProjectHelper();
        IProject project = any.getProjectForName(projectName);
        TestCase.assertNotNull(project);
        TestCase.assertTrue(project.isOpen());

        try {
            // XXX should really only need to explicitly execute the build if
            // auto build is disabled, however,
            // a full build was being forced before with auto-build enabled so
            // leave the call...
            project.build(buildType, null);

            if (isAutoBuildEnabled()) {
                // wait for auto-build job to complete
                waitForBuild(ui);
                // XXX old logic was also waiting for all jobs to finish
                // executing - have
                // removed that since it that should not be necessary to just
                // verify
                // build results...
            }

            // verify the build succeeded
            if (expectedResult != ExpectedResult.UNSPECIFIED) {
                final IMarker[] projectMarkers = getMarkers(project, true);
                boolean failed = false;

                if (projectMarkers.length > 0) {
                    failed = true;
                    Map<?, ?> attributes = projectMarkers[0].getAttributes();
                    TestCase.assertNotNull(attributes);
                }

                if (expectedResult == ExpectedResult.NO_PROBLEMS) {
                    final String markers = new MarkerUtil().toString(projectMarkers);
                    TestCase.assertFalse("Build for project <" + project.getName() //$NON-NLS-1$
                                         + "> should have passed, error markers: " + markers, //$NON-NLS-1$
                                         failed);
                } else if (expectedResult == ExpectedResult.PROBLEMS) {
                    TestCase.assertTrue("Build for project <" + project.getName() //$NON-NLS-1$
                                        + "> should have failed", failed); //$NON-NLS-1$
                }
            }
        } catch (CoreException ce) {
            ExceptionHandler.handle(ce);
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#verifyMarkers(com.windowtester.runtime.IUIContext,
     *       java.lang.String,
     *       org.wtc.eclipse.platform.helpers.IWorkbenchHelper.MarkerInfo[])
     */
    public void verifyMarkers(IUIContext ui,
                              String projectName,
                              MarkerInfo[] markerInfos) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectName);
        TestCase.assertNotNull(markerInfos);

        logEntry2(projectName, getDisplayValue(markerInfos));

        saveAndWait(ui);

        IProjectHelper any = EclipseHelperFactory.getProjectHelper();
        IProject project = any.getProjectForName(projectName);

        List<MarkerInfo> expectedMarkers = new ArrayList<MarkerInfo>();
        Collections.addAll(expectedMarkers, markerInfos);

        IMarker[] actualMarkersArray = getMarkers(project, false);
        List<IMarker> actualMarkers = new ArrayList<IMarker>();
        Collections.addAll(actualMarkers, actualMarkersArray);

        for (int i = 0; i < expectedMarkers.size();) {
            MarkerInfo nextExpected = expectedMarkers.get(i);
            boolean found = false;

            for (int j = 0; j < actualMarkers.size();) {
                IMarker nextActual = actualMarkers.get(j);

                if (nextExpected.equals(nextActual)) {
                    expectedMarkers.remove(i);
                    actualMarkers.remove(j);
                    found = true;

                    break;
                }

                j++;
            }

            if (!found) {
                i++;
            }
        }

        if (!expectedMarkers.isEmpty()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("THE FOLLOWING MARKERS WERE NOT FOUND:\n"); //$NON-NLS-1$
            buffer.append(getDisplayValue(expectedMarkers));
            TestCase.fail(buffer.toString());
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#verifyMarkerType(java.lang.String,java.lang.String,
     *       boolean, boolean)
     */
    public void verifyMarkerType(String projectName,
                                 String type,
                                 boolean checkOnlyErrors,
                                 boolean shouldHaveMarkers) {
        TestCase.assertNotNull(projectName);
        TestCase.assertNotNull("Must specify non-null Marker type", type); //$NON-NLS-1$

        logEntry2(projectName, type, Boolean.toString(checkOnlyErrors), Boolean.toString(shouldHaveMarkers));

        IProjectHelper any = EclipseHelperFactory.getProjectHelper();
        IProject project = any.getProjectForName(projectName);

        boolean hasType = false;
        IMarker[] markers = getMarkers(project, checkOnlyErrors);

        try {
            for (IMarker marker : markers) {
                if (marker.getType().equals(type)) {
                    hasType = true;

                    break;
                }
            }
        } catch (CoreException ce) {
            ExceptionHandler.handle(ce);
        }

        if (shouldHaveMarkers) {
            TestCase.assertTrue("Project " + projectName //$NON-NLS-1$
                                + " does not have markers of type " + type, hasType); //$NON-NLS-1$
        } else {
            TestCase.assertFalse("Project " + projectName //$NON-NLS-1$
                                 + " should not have any markers of type " + type, hasType); //$NON-NLS-1$
        }

        logExit2();
    }

    /**
     * Wait for the given perspective to be open/closed.
     *
     * @since  3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  perspective  - The perspective whose visibility is to be verified
     * @param  open         - True if the perspective is to be open for this condition to
     *                      be met; False if the perspective is to be closed for this
     *                      condition to be met
     */
    private void verifyPerspectiveOpen(IUIContext ui,
                                       final IPerspective perspective,
                                       final boolean open) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(perspective);

        final String perspectiveID = perspective.getID();

        logEntry2(perspectiveID, Boolean.toString(open));

        ui.wait(new ICondition() {
                private IPerspectiveDescriptor[] _lastCheck;

                public boolean test() {
                    final boolean[] found = new boolean[1];
                    found[0] = false;
                    Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                                _lastCheck = page.getOpenPerspectives();

                                for (IPerspectiveDescriptor nextPerspective : _lastCheck) {
                                    if (perspectiveID.equals(nextPerspective.getId())) {
                                        found[0] = true;

                                        break;
                                    }
                                }
                            }
                        });

                    return found[0] == open;
                }

                @Override
                public String toString() {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("FOR THE PERSPECTIVE <"); //$NON-NLS-1$
                    buffer.append(perspective.getID());
                    buffer.append("> TO BE OPEN <"); //$NON-NLS-1$
                    buffer.append(open);
                    buffer.append(">;"); //$NON-NLS-1$

                    if (_lastCheck != null) {
                        buffer.append("THE OPEN PERSPECTIVES WERE:\n"); //$NON-NLS-1$

                        for (IPerspectiveDescriptor nextPerspective : _lastCheck) {
                            buffer.append("   - "); //$NON-NLS-1$
                            buffer.append(nextPerspective.getId());
                            buffer.append("\n"); //$NON-NLS-1$
                        }
                    }

                    return buffer.toString();
                }
            });

        logExit2();
    }

    /**
     * Wait for the given view to be open/closed.
     *
     * @since  3.8.0
     * @param  ui    - Driver for UI generated input
     * @param  view  - The view whose visibility is to be verified
     * @param  open  - True if the view is to be open for this condition to be met; False
     *               if the view is to be closed for this condition to be met
     */
    private void verifyViewOpen(IUIContext ui,
                                final IView view,
                                final boolean open) {
        verifyViewOpen(ui, view, open, 30000);
    }

    /**
     * Wait for the given view to be open/closed.
     *
     * @since  3.8.0
     * @param  ui       - Driver for UI generated input
     * @param  view     - The view whose visibility is to be verified
     * @param  open     - True if the view is to be open for this condition to be met;
     *                  False if the view is to be closed for this condition to be met
     * @param  timeout  - The number of milliseconds to wait for the view to be open. The
     *                  default is 30 seconds
     */
    private void verifyViewOpen(IUIContext ui,
                                final IView view,
                                final boolean open,
                                long timeout) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(view);

        logEntry2(view.getViewID(), Boolean.toString(open));

        ui.wait(new ICondition() {
                private IViewReference[] _lastCheck;

                public boolean test() {
                    final boolean[] found = new boolean[1];
                    found[0] = false;
                    Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                                _lastCheck = page.getViewReferences();
                                IViewPart part = page.findView(view.getViewID());
                                found[0] = (part != null);
                            }
                        });

                    return found[0] == open;
                }

                @Override
                public String toString() {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("FOR THE VIEW <"); //$NON-NLS-1$
                    buffer.append(view.getViewID());
                    buffer.append("> TO BE OPEN <"); //$NON-NLS-1$
                    buffer.append(open);
                    buffer.append(">;"); //$NON-NLS-1$

                    if (_lastCheck != null) {
                        buffer.append("THE OPEN VIEWS WERE:\n"); //$NON-NLS-1$

                        for (IViewReference nextView : _lastCheck) {
                            buffer.append("   - "); //$NON-NLS-1$
                            buffer.append(nextView.getId());
                            buffer.append("\n"); //$NON-NLS-1$
                        }
                    }

                    return buffer.toString();
                }
            }, timeout);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitForBuild(com.windowtester.runtime.IUIContext)
     */
    public void waitForBuild(IUIContext ui) {
        waitForBuild(ui,
                     JobExistsCondition.DEFAULT_WAIT_FOR_JOBS_TIMEOUT,
                     JobsInFamilyExistCondition.DEFAULT_SLEEP_DURATION);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitForBuild(com.windowtester.runtime.IUIContext,
     *       org.wtc.eclipse.platform.helpers.IWorkbenchHelper.JobWaitType)
     */
    public void waitForBuild(IUIContext ui, JobWaitType type) {
        waitForBuild(ui,
                     JobWaitType.RETURN_IF_NO_JOBS,
                     JobExistsCondition.DEFAULT_WAIT_FOR_JOBS_TIMEOUT,
                     JobsInFamilyExistCondition.DEFAULT_SLEEP_DURATION);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitForBuild(com.windowtester.runtime.IUIContext,
     *       long, int)
     */
    public void waitForBuild(IUIContext ui, long timeout, int pollingInterval) {
        waitForBuild(ui, JobWaitType.RETURN_IF_NO_JOBS, timeout, pollingInterval);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitForBuild(com.windowtester.runtime.IUIContext,
     *       org.wtc.eclipse.platform.helpers.IWorkbenchHelper.JobWaitType, long, int)
     */
    public void waitForBuild(IUIContext ui,
                             JobWaitType type,
                             long timeout,
                             int pollingInterval) {
        logEntry2(type.toString(), Long.toString(timeout), Integer.toString(pollingInterval));

        // This will wait indefinitely
        setExpectedDelay(ui, timeout);

        waitForJobFamily(ui,
                         ResourcesPlugin.FAMILY_AUTO_BUILD,
                         type,
                         timeout,
                         pollingInterval);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitForJobFamily(com.windowtester.runtime.IUIContext,
     *       java.lang.Object)
     */
    public void waitForJobFamily(IUIContext ui, Object family) {
        waitForJobFamily(ui, family, JobWaitType.RETURN_IF_NO_JOBS);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitForJobFamily(com.windowtester.runtime.IUIContext,
     *       java.lang.Object,
     *       org.wtc.eclipse.platform.helpers.IWorkbenchHelper.JobWaitType)
     */
    public void waitForJobFamily(IUIContext ui,
                                 Object family,
                                 JobWaitType type) {
        waitForJobFamily(ui,
                         family,
                         type,
                         JobExistsCondition.DEFAULT_WAIT_FOR_JOBS_TIMEOUT,
                         JobsInFamilyExistCondition.DEFAULT_SLEEP_DURATION);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitForJobFamily(com.windowtester.runtime.IUIContext,
     *       java.lang.Object,
     *       org.wtc.eclipse.platform.helpers.IWorkbenchHelper.JobWaitType)
     */
    public void waitForJobFamily(IUIContext ui,
                                 Object family,
                                 JobWaitType type,
                                 long timeout,
                                 int pollingInterval) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull("Must specify a non-null Job family", family); //$NON-NLS-1$

        logEntry2(getDisplayValue(family), type.toString(), Long.toString(timeout), Integer.toString(pollingInterval));

        try {
            ui.wait(new JobsInFamilyExistCondition(family,
                                                   type == JobWaitType.RETURN_IF_NO_JOBS),
                    timeout,
                    pollingInterval);
        } catch (WaitTimedOutException wtoe) {
            if (type == JobWaitType.JOB_MUST_RUN) {
                // if the job is expected to run and we got a timeout, it failed
                // to run to propagate the WTOE
                throw wtoe;
            }
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitNoJobs(com.windowtester.runtime.IUIContext)
     */
    public void waitNoJobs(IUIContext ui) {
        waitNoJobs(ui, JobExistsCondition.DEFAULT_WAIT_FOR_JOBS_TIMEOUT, 1000);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitNoJobs(com.windowtester.runtime.IUIContext,
     *       long, int)
     */
    public void waitNoJobs(IUIContext ui, long timeout, int pollingInterval) {
        logEntry2();

        waitForBuild(ui);

        ui.wait(new JobExistsCondition(WaitForJobsRegistry.getExpectedJobs()),
                timeout,
                pollingInterval);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitNoResourceChangeEvents(com.windowtester.runtime.IUIContext)
     */
    public void waitNoResourceChangeEvents(IUIContext ui) {
        waitNoResourceChangeEvents(ui, 6000);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IWorkbenchHelper#waitNoResourceChangeEvents(com.windowtester.runtime.IUIContext,
     *       int)
     */
    public void waitNoResourceChangeEvents(IUIContext ui, int timeout) {
        logEntry2();

        ui.wait(new NoResourceChangedEventsCondition(timeout));

        logExit2();
    }
}
