/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.wtc.eclipse.platform.shellhandlers.IWorkbenchShellHandler;
import java.util.regex.Pattern;

/**
 * Helper workbench specific tasks.
 * 
 * @since 3.8.0
 */
public interface IWorkbenchHelper {
    public static enum JobWaitType {
        JOB_MUST_RUN,
        RETURN_IF_NO_JOBS,
        WAIT_FOR_JOBS
    }

    public static enum Perspective implements IPerspective {
        // Wildcards are used because the "(default)" text is appended to
        //    the perspective name depending on the product definition
//        CVS("CVS Repository Exploring", "org.eclipse.team.cvs.ui.cvsPerspective"), //$NON-NLS-1$ //$NON-NLS-2$
        DEBUG("Debug", "org.eclipse.debug.ui.DebugPerspective"), //$NON-NLS-1$ //$NON-NLS-2$
        JAVA("(Java|Java \\(default\\))", "org.eclipse.jdt.ui.JavaPerspective"), //$NON-NLS-1$ //$NON-NLS-2$
        RESOURCE("Resource(.*default.*)?", "org.eclipse.ui.resourcePerspective"); //$NON-NLS-1$ //$NON-NLS-2$

        private String _label;
        private String _id;

        Perspective(String label, String id) {
            _label = label;
            _id = id;
        }

        public String getID() {
            return _id;
        }

        public String getPerspectiveLabel() {
            return _label;
        }

        @Override
        public String toString() {
            return _label;
        }
    }

    // Eclipse common views
    public static enum View implements IView {
        BASIC_CONSOLE("(General|Basic)/Console", //$NON-NLS-1$
            "org.eclipse.ui.console.ConsoleView"), //$NON-NLS-1$
        BASIC_INTERNALWEBBROWSER("(General|Basic)/Internal Web Browser", //$NON-NLS-1$
            "org.eclipse.ui.browser.view"), //$NON-NLS-1$
        BASIC_NAVIGATOR("(General|Basic)/Navigator", //$NON-NLS-1$
            "org.eclipse.ui.views.ResourceNavigator"), //$NON-NLS-1$
        BASIC_OUTLINE("(General|Basic)/Outline", //$NON-NLS-1$
            "org.eclipse.ui.views.ContentOutline"), //$NON-NLS-1$
        BASIC_PROBLEMS("(General|Basic)/Problems", //$NON-NLS-1$
            "org.eclipse.ui.views.ProblemView"), //$NON-NLS-1$
        BASIC_PROJECTEXPLORER("(General|Basic)/Project Explorer", //$NON-NLS-1$
            "org.eclipse.ui.navigator.ProjectExplorer"), //$NON-NLS-1$
        BASIC_PROPERTIES("(General|Basic)/Properties", //$NON-NLS-1$
            "org.eclipse.ui.views.PropertySheet"), //$NON-NLS-1$
//        CVS_CVSANNOTATE("CVS/CVS Annotate", //$NON-NLS-1$
//            "org.eclipse.team.ccvs.ui.AnnotateView"), //$NON-NLS-1$
//        CVS_CVSEDITORS("CVS/CVS Editors", //$NON-NLS-1$
//            "org.eclipse.team.ccvs.ui.EditorsView"), //$NON-NLS-1$
//        CVS_CVSREPOSITORIES("CVS/CVS Repositories", //$NON-NLS-1$
//            "org.eclipse.team.ccvs.ui.RepositoriesView"), //$NON-NLS-1$
        DEBUG_BREAKPOINTS("Debug/Breakpoints", //$NON-NLS-1$
            "org.eclipse.debug.ui.BreakpointView"), //$NON-NLS-1$
        JAVA_PACKAGEEXPLORER("Java/Package Explorer", //$NON-NLS-1$
            "org.eclipse.jdt.ui.PackageExplorer"), //$NON-NLS-1$
        PDERUNTIME_ERRORLOG("PDE Runtime/Error Log", //$NON-NLS-1$
            "org.eclipse.pde.runtime.LogView"); //$NON-NLS-1$

        private String _viewSelectionPath;
        private String _viewID;

        private View(String label, String viewID) {
            _viewSelectionPath = label;
            _viewID = viewID;
        }

        public String getViewID() {
            return _viewID;
        }

        public String getViewPath() {
            return _viewSelectionPath;
        }

        @Override
        public String toString() {
            return _viewID;
        }
    }

    /**
     * addExpectedJob - Add a Job by the given title to the list of expected jobs when
     * waitNoJobs is called. An expected job means that when waitNoJobs is called
     * (waitNoJobs waits until all of the Jobs in the JobManager are SLEEPING or STOPPING)
     * the Job with that title can be considered a RUNNING job and the condition can still
     * be met
     *
     * @param  jobTitle  - Expected Job
     */
    public void addExpectedJob(String jobTitle);

    /**
     * bringToFront - Bring the active workbench to the front.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void bringToFront(IUIContext ui);

    /**
     * buildProject - Explicitly executes a build of the specified type and waits for the
     * build to complete (@see #waitForBuild for deadlock warning; won't wait if
     * auto-build is disabled). Issues a TestCase failure if the project does not exist or
     * an unexpected build error occurs (build problem markers will not trigger a
     * failure).
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - The name of the project in the workspace to build. Should
     *                      not be null.
     * @param  fullBuild    True to trigger an IncrementalProjectBuilder.FULL_BUILD, false
     *                      to trigger an IncrementalProjectBuilder.INCREMENTAL_BUILD.
     */
    public void buildProject(IUIContext ui, String projectName, boolean fullBuild);

    /**
     * cleanAllProjects - Use the project -> clean menu to clean all projects. Wait for
     * the auto build jobs to complete before returning
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void cleanAllProjects(IUIContext ui);

    /**
     * cleanBuildProject - Explicitly executes a clean build (which is a clean then a full
     * build) of the given project in the workspace. (@see #waitForBuild for deadlock
     * warning; won't wait if auto-build is disabled). Issues a TestCase failure if the
     * project does not exist or an unexpected build error occurs (build problem markers
     * will not trigger a failure).
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI-generated input
     * @param  projectName  - Name of the project in the workspace to clean and build
     */
    public void cleanBuildProject(IUIContext ui, String projectName);

    /**
     * closeActiveEditor - closes the currently active editor, based on the active
     * workbench window.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void closeActiveEditor(IUIContext ui);

    /**
     * closeActivePerspective - Close the active perspective.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void closeActivePerspective(IUIContext ui);

    /**
     * closeAllEditors - closes all editors, based on the active workbench window.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void closeAllEditors(IUIContext ui);

    /**
     * closeAllEditors - closes all editors, based on the active workbench window.
     *
     * @since 3.8.0
     * @param  ui    - Driver for UI generated input
     * @param  save  - True if the editors should be saved as they are closed
     */
    public void closeAllEditors(IUIContext ui, boolean save);

    /**
     * closeView - Close the view of the given type.
     *
     * @since 3.8.0
     * @param  ui    - Driver for UI generated input
     * @param  view  - View to open.
     */
    public void closeView(IUIContext ui, IView view);

    /**
     * closeWelcomePage - Close the welcome page.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void closeWelcomePage(IUIContext ui);

    /**
     * disableAutoBuild - Turn the auto build option off, will fail if option is already
     * off.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void disableAutoBuild(IUIContext ui);

    /**
     * disableAutoBuildViaAPI - Disables auto-building. Performed via API calls not via
     * the UI.
     */
    public void disableAutoBuildViaAPI();

    /**
     * enableAutoBuild - Turn the build automatically option on, will fail if option is
     * already on.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void enableAutoBuild(IUIContext ui);

    /**
     * enableAutoBuildViaAPI - Enables auto-building. Performed via API calls not via the
     * UI.
     */
    public void enableAutoBuildViaAPI();

    /**
     * getActiveEditor - Get the active editor based on the active workbench window.
     *
     * @return  the active IEditorPart, or null if no editors are active.
     */
    public IEditorPart getActiveEditor();

    /**
     * getActiveEditorComposite - Get the active editor based on the active workbench
     * window, then find the edit part and grab the Composite parent from that edit part.
     *
     * <p>THIS IS A PRETTY SKETCHY METHOD AND IS ONLY USED IN THE RARE CASES WHERE WE WANT
     * TO MANIPULATE EDITORS WITH WIDGETS</p>
     *
     * @return  Composite - The active composite for the IEditorPart, or <code>null</code>
     *          if no editors are active.
     */
    public Composite getActiveEditorComposite();

    /**
     * invokeRedo - Invoke the main workbench "edit->redo" menu item. This will fail if
     * the item is not enabled; but will not do any further verification.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void invokeRedo(IUIContext ui);

    /**
     * invokeUndo - Invoke the main workbench "edit->undo" menu item. This will fail if
     * the item is not enabled; but will not do any further verification.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void invokeUndo(IUIContext ui);

    /**
     * Start listening for the the given dialog and use the given dialog handler to react
     * to the dialog when it is shown. THIS IS FOR UNEXPACTED DIALOGS.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialog(IUIContext ui, IWorkbenchShellHandler handler);

    /**
     * listenForDialogConfirmDelete - Start listening for the Confirm Delete Dialog and
     * click "Yes To All" if it is ever shown. The caller should call stop listening for
     * this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogConfirmDelete(IUIContext ui);

    /**
     * listenForDialogFileContentChanged - Start listening for the File Content Changed
     * Dialog click "Yes" to close if ever shown. The caller should call stop listening
     * for this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogFileContentChanged(IUIContext ui);

    /**
     * listenForDialogFileDeleted - Start listening for the File Deleted Dialog click
     * "Close" or "OK" to close if ever shown. The caller should call stop listening for
     * this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogFileDeleted(IUIContext ui);

    /**
     * listenForDialogLicenceAgreement - Start listening for the License Agreement Dialog
     * and click "I Agree" if it is ever shown. The caller should call stop listening for
     * this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogLicenceAgreement(IUIContext ui);

    /**
     * listenForDialogOpenPerspective - Start listening for the Open Perspective Dialog
     * and click "No" if it ever shown. The caller should call stop listening for this
     * dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI input
     */
    public void listenForDialogOpenPerspective(IUIContext ui);

    /**
     * listenForDialogOverwriteFiles - Start listening for the Overwrite Files Dialog and
     * click "OK" if it ever shown. The caller should call stop listening for this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI input
     */
    public void listenForDialogOverwriteFiles(IUIContext ui);

    /**
     * listenForDialogProgress - Start listening for the Progress Dialog and wait for it
     * to close if ever shown. The caller should call stop listening for this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI input
     */
    public void listenForDialogProgress(IUIContext ui);

    /**
     * listenForDialogRebuilding - Start listening for the Rebuilding Dialog. The caller
     * should call stop listening for this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogRebuilding(IUIContext ui);

    /**
     * listenForDialogRenameCompilationUnit - Start listening for the Rename Compilation
     * Unit Dialog and click "Continue" to close if it is ever shown. The caller should
     * call stop listening for this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogRenameCompilationUnit(IUIContext ui);

    /**
     * listenForDialogResourceExists - Start listening for the Resource Exists Dialog and
     * click "Yes" to close if it is ever shown. The caller should call stop listening for
     * this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogResourceExists(IUIContext ui);

    /**
     * listenForDialogSaveAllModifiedResources - Start listening for the Save All Modified
     * Resources Dialog click "OK" to close if ever shown. The caller should call stop
     * listening for this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogSaveAllModifiedResources(IUIContext ui);

    /**
     * listenForDialogSaveResource - Start listening for the Save Resource Dialog click
     * "Yes" to close if ever shown. The caller should call stop listening for this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI input
     */
    public void listenForDialogSaveResource(IUIContext ui);

    /**
     * listenForDialogSaveResources - Start listening for the Save Resources Dialog click
     * "OK" to close if ever shown. The caller should call stop listening for this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogSaveResources(IUIContext ui);

    /**
     * listenForDialogSettingBuildPath - Start listening for the Setting build path Dialog
     * click and wait if ever shown. The caller should call stop listening for this dialog
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogSettingBuildPath(IUIContext ui);

    /**
     * listenForDialogWizardClosing - Start listening for the Wizard Closing Dialog and
     * click "OK" to close if ever shown.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialogWizardClosing(IUIContext ui);

    /**
     * logDebug - Log a message with the debug level to the Eclipse log. See
     * LoggingService for more details
     *
     * @since 3.8.0
     * @param  ui       - Driver for UI generated input
     * @param  plugin   - The plugin issuing this message
     * @param  message  - The literal text to log
     */
    public void logDebug(IUIContext ui, Plugin plugin, String message);

    /**
     * maximizeWorkbench - Maximize the active workbench window.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void maximizeWorkbench(IUIContext ui);

    /**
     * openPerspective - Open the perspective of the given type.
     *
     * @since 3.8.0
     * @param  ui    - Driver for UI generated input
     * @param  type  - Type to open.
     */
    public void openPerspective(IUIContext ui, IPerspective type);

    /**
     * openView - Open the view of the given type.
     *
     * @since 3.8.0
     * @param  ui    - Driver for UI generated input
     * @param  view  - View to open.
     */
    public void openView(IUIContext ui, IView type);

    /**
     * removeExpectedJob - Remove a Job by the given title from the list of expected jobs
     * when waitNoJobs is called. An expected job means that when waitNoJobs is called
     * (waitNoJobs waits until all of the Jobs in the JobManager are SLEEPING or STOPPING)
     * the Job with that title can be considered a RUNNING job and the condition can still
     * be met
     *
     * @param  jobTitle  - Expected Job
     */
    public void removeExpectedJob(String jobTitle);

    /**
     * save - Save all open editors and do NOT wait for jobs to complete.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void save(IUIContext ui);

    /**
     * saveAndWait - Save all open editors and wait for jobs to complete.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void saveAndWait(IUIContext ui);

    /**
     * stopListeningForAllDialogs - Stop listening for all unexpected dialogs. This is the
     * complimentary method to the listenForDialog method
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI input
     */
    public void stopListeningForAllDialogs(IUIContext ui);

    /**
     * stopListeningForDialog - Stop listening for the given dialog.
     *
     * @since 3.8.0
     * @param  ui       - Driver for UI generated input
     * @param  handler  - The shell handler that should no longer be handled through the
     *                  Shell Monitor. Should have been registered through the
     *                  listenForDialog method
     */
    public void stopListeningForDialog(IUIContext ui, IWorkbenchShellHandler handler);

    /**
     * Stop listening for the Confirm Delete Dialog.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void stopListeningForDialogConfirmDelete(IUIContext ui);

    /**
     * Stop listening for the Confirm Overwrite Files Dialog.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void stopListeningForDialogConfirmOverwrite(IUIContext ui);

    /**
     * stopListeningForDialogFileContentChanged - Stop listening for the File Content
     * Changed Dialog.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void stopListeningForDialogFileContentChanged(IUIContext ui);

    /**
     * stopListeningForDialogFileDeleted - Stop listening for the File Deleted Dialog.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void stopListeningForDialogFileDeleted(IUIContext ui);

    /**
     * stopListeningForDialogRebuilding - Stop listening for the Rebuilding dialog.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void stopListeningForDialogRebuilding(IUIContext ui);

    /**
     * stopListeningForDialogRenameCompilationUnit - Stop listening for the Rename
     * Compilation Unit Dialog.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void stopListeningForDialogRenameCompilationUnit(IUIContext ui);

    /**
     * stopListeningForDialogSaveAllModifiedResources - Stop listening for the File Save
     * All Modified Resources Dialog.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void stopListeningForDialogSaveAllModifiedResources(IUIContext ui);

    /**
     * verifyBuild - Explicitly executes a full build on the project, waits for the build
     * job to complete (@see #waitForBuild for deadlock warning; won't wait if auto-build
     * is disabled) and verifies that the project in the workspace with the given name has
     * no errors by checking the known error markers for the project. If the project does
     * not exist or contains errors, then this method will issue a TestCase failure
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - The name of the project in the workspace to verify. Should
     *                      not be null
     */
    public void verifyBuild(IUIContext ui, String projectName);

    /**
     * verifyBuild- Explicitly executes a build of the specified type, waits for the build
     * to complete (@see #waitForBuild for deadlock warning; won't wait if auto-build is
     * disabled), and then verifies if the build succeeded or failed, as specified. Issues
     * a TestCase failure if the project does not exist or the specified build conditions
     * are not met.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - The name of the project in the workspace to verify. Should
     *                      not be null.
     * @param  fullBuild    True to trigger an IncrementalProjectBuilder.FULL_BUILD, false
     *                      to trigger an IncrementalProjectBuilder.INCREMENTAL_BUILD.
     * @param  shouldPass   True if there should be no error problem marker on the project
     *                      after the build completes. False if there should be at least
     *                      one error problem marker on the project after the build
     *                      completes.
     */
    public void verifyBuild(IUIContext ui,
                            String projectName,
                            boolean fullBuild,
                            boolean shouldPass);

    /**
     * verifyMarkers - Verifies that the project has (or does not have) problem markers
     * with the specified details. If necessary, callers must execute and wait for a build
     * prior to calling this method. Generates a TestCase failure if the conditions are
     * not met.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Source of the project to verify
     * @param  markerInfos  - Describes the markers that should Marker type to check
     */
    public void verifyMarkers(IUIContext ui,
                              String projectName,
                              MarkerInfo[] markerInfos);

    /**
     * verifyMarkerType - Verifies that the project has (or does not have) a marker of the
     * specified type. If necessary, callers must execute and wait for a build prior to
     * calling this method. Generates a TestCase failure if the conditions are not met.
     *
     * @param  projectName        - Source of the project to verify
     * @param  type               Marker type to check
     * @param  onlyCheckErrors    True to only check problem markers with severity error.
     * @param  shouldHaveMarkers  True if the project should
     */
    public void verifyMarkerType(String projectName,
                                 String type,
                                 boolean checkOnlyErrors,
                                 boolean shouldHaveMarkers);

    /**
     * waitForBuild - Waits until all jobs of the build family are finished.  This method
     * will block the calling thread until all such jobs have finished executing.  If
     * there are no jobs in the family that are currently waiting, running, or sleeping,
     * this method returns immediately.  If a job is canceled a runtime exception will be
     * thrown.
     *
     * <p>Note that there is a deadlock risk when using join.  If the calling thread owns
     * a lock or object monitor that the joined thread is waiting for, deadlock will
     * occur. This method can also result in starvation of the current thread if another
     * thread continues to add jobs of the given family, or if a job in the given family
     * reschedules itself in an infinite loop.</p>
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void waitForBuild(IUIContext ui);

    /**
     * @since 3.8.0
     * @param  ui    - Driver for UI generated input
     * @param  type  - The job wait type.
     */
    public void waitForBuild(IUIContext ui, JobWaitType type);

    /**
     * waitForBuild - Waits until all jobs of the build family are finished.  This method
     * will block the calling thread until all such jobs have finished executing.  If
     * there are no jobs in the family that are currently waiting, running, or sleeping,
     * this method returns immediately.  If a job is canceled a runtime exception will be
     * thrown.
     *
     * <p>Note that there is a deadlock risk when using join.  If the calling thread owns
     * a lock or object monitor that the joined thread is waiting for, deadlock will
     * occur. This method can also result in starvation of the current thread if another
     * thread continues to add jobs of the given family, or if a job in the given family
     * reschedules itself in an infinite loop.</p>
     *
     * @since 3.8.0
     * @param  ui               - Driver for UI generated input
     * @param  timeout          - How long (in ms) to wait for the job activity to settle
     *                          down before marking the test as a failure.
     * @param  pollingInverval  - How long (in ms) to wait between each test of the job
     *                          activity. Should be proportional to the timeout (ie- a ten
     *                          minute timeout (600000) should not check every 100ms.
     */
    public void waitForBuild(IUIContext ui, long timeout, int pollingInterval);

    /**
     * @since 3.8.0
     * @param  ui    - Driver for UI generated input
     * @param  type  - The job wait type.
     */
    public void waitForBuild(IUIContext ui,
                             JobWaitType type,
                             long timeout,
                             int pollingInterval);

    /**
     * waitForJobFamily - Waits for jobs in the specified family to finish. Job does not
     * necessarily have to run but repeated checks are made for it.
     *
     * @see    #waitForBuild for deadlock warning
     * @since 3.8.0
     * @param  ui      Driver for UI generated input
     * @param  family  Job family. Cannot be null.
     */
    public void waitForJobFamily(IUIContext ui, Object family);

    /**
     * @since 3.8.0
     * @param  ui           Driver for UI generated input
     * @param  family       Job family. Cannot be null.
     * @param  jobWaitType  Either RETURN_IF_NO_JOBS, WAIT_FOR_JOBS or JOB_MUST_RUN.
     */
    public void waitForJobFamily(IUIContext ui, Object family, JobWaitType type);

    /**
     * @since 3.8.0
     * @param  ui           Driver for UI generated input
     * @param  family       Job family. Cannot be null.
     * @param  jobWaitType  Either RETURN_IF_NO_JOBS, WAIT_FOR_JOBS or JOB_MUST_RUN.
     */
    public void waitForJobFamily(IUIContext ui,
                                 Object family,
                                 JobWaitType type,
                                 long timeout,
                                 int pollingInterval);

    /**
     * waitNoJobs - Wait for the eclipse JobManager to have only sleeping or stopping
     * jobs. A running job is anything that was scheduled through the Job.schedule() call.
     * Use the default exclusion list for jobs that are allowed to run under any
     * circumstance.
     *
     * @see    #waitForBuild for deadlock warning.
     *
     *         <p>Use the default timeout and polling interval</p>
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void waitNoJobs(IUIContext ui);

    /**
     * waitNoJobs - Wait for the eclipse JobManager to have only sleeping or stopping
     * jobs. A running job is anything that was scheduled through the Job.schedule() call.
     * Use the default exclusion list for jobs that are allowed to run under any
     * circumstance.
     *
     * @see    #waitForBuild for deadlock warning
     * @since 3.8.0
     * @param  ui               - Driver for UI generated input
     * @param  timeout          - How long (in ms) to wait for the job activity to settle
     *                          down before marking the test as a failure.
     * @param  pollingInverval  - How long (in ms) to wait between each test of the job
     *                          activity. Should be proportional to the timeout (ie- a ten
     *                          minute timeout (600000) should not check every 100ms.
     */
    public void waitNoJobs(IUIContext ui, long timeout, int pollingInterval);

    /**
     * waitNoResourceChangeEvents - Wait for the eclipse resource changed event
     * broadcaster to stop. For each resource changed event that is broadcast, this
     * condition will reset a timer. This wait will return when that timer expires.  In
     * other words, wait for all resource change events to stop then wait the given
     * timeout value after the last resource changed event to make sure no new events are
     * broadcast. By default, use a 6 second timeout
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void waitNoResourceChangeEvents(IUIContext ui);

    /**
     * waitNoResourceChangeEvents - Wait for the eclipse resource changed event
     * broadcaster to stop. For each resource changed event that is broadcast, this
     * condition will reset a timer. This wait will return when that timer expires.  In
     * other words, wait for all resource change events to stop then wait the given
     * timeout value after the last resource changed event to make sure no new events are
     * broadcast.
     *
     * @since 3.8.0
     * @param  ui       - Driver for UI generated input
     * @param  timeout  - How long to wait after the last resource changed event
     */
    public void waitNoResourceChangeEvents(IUIContext ui, int timeout);

    /**
     * Class that describes expected markers for a workspace build.
     */
    public static class MarkerInfo {
        private boolean _isError = false;
        private String _description = null;
        private String _resourceName = null;

        /**
         * No arg constructor to meet specification.
         */
        public MarkerInfo() {
        }

        /**
         * @return  boolean - True if the properties on this maker match the given IMarker
         */
        public boolean equals(IMarker marker) {
            int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);

            boolean equals = ((severity == IMarker.SEVERITY_WARNING) && !_isError) || ((severity == IMarker.SEVERITY_ERROR) && _isError);

            if (equals && (_description != null)) {
                String actualDescription = marker.getAttribute(IMarker.MESSAGE, ""); //$NON-NLS-1$
                equals = Pattern.matches(_description, actualDescription);
            }

            if (equals && (_resourceName != null)) {
                IResource actualResource = marker.getResource();
                equals = (actualResource != null) && (actualResource.getName().equals(_resourceName));
            }

            return equals;
        }

        /**
         * @param  description  - Regex pattern of the marker description text (as seen in
         *                      the problems view). Set to <code>null</code> if the
         *                      description is not to be checked when verifying build
         *                      markers
         */
        public void setDescription(String description) {
            _description = description;
        }

        /**
         * @param  isError  - True if this marker info describes an error marker; False if
         *                  this marker info describes a warning marker
         */
        public void setIsError(boolean isError) {
            _isError = isError;
        }

        /**
         * @param  resourceName  - The exact name of the resource expected for the marker
         *                       or <code>null</code> if the resource name is not part of
         *                       the build verification for this marker
         */
        public void setResourceName(String resourceName) {
            _resourceName = resourceName;
        }

        /**
         * @see  java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("MARKER INFO["); //$NON-NLS-1$
            buffer.append("resource<"); //$NON-NLS-1$
            buffer.append(_resourceName);
            buffer.append(">; isError<"); //$NON-NLS-1$
            buffer.append(_isError);
            buffer.append(">; description<"); //$NON-NLS-1$
            buffer.append(_description);
            buffer.append(">]"); //$NON-NLS-1$

            return buffer.toString();
        }
    }
}
