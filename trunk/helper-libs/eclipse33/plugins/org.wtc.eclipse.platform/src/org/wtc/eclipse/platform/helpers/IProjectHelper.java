/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import java.util.Collection;
import java.util.List;

/**
 * Helper for creating and manipulating projects.
 * 
 * @since 3.8.0
 */
public interface IProjectHelper {
    /**
     * addProjectBuildDependency - Open the project properties for a given project and add
     * a project dependency to another given project.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void addProjectBuildDependency(IUIContext ui,
                                          String sourceProject,
                                          String targetProject);

    /**
     * addProjectNatureViaAPI - Use the Eclipse API to add a project nature to the project
     * with the given name.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Name of the project to add the nature to
     * @param  natureID     - The ID of the nature (as determined by the the nature's
     *                      declaring plugin.xml)
     */
    public void addProjectNatureViaAPI(IUIContext ui,
                                       String projectName,
                                       String natureID);

    /**
     * closeProject - Use the navigator view to close a project in the workspace. This
     * method will issue a test case failure if the project cannot be found for any
     * reason. If the project is already closed, this operation is a no-op.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Name of the project to close
     */
    public void closeProject(IUIContext ui, String projectName);

    /**
     * exportProjectsToArchive - Export the projects in the workspace with the given names
     * to an archive file with the given name. The archive will be placed in a directory
     * under new directory that is a peer to the workspace (the archive cannot be put in
     * the results directory because it conflicts with the workspace root) and returned as
     * a disk-absolute location IPath. This method will issue a test case failure if any
     * of the projects selected could not be found or exported for any reason.
     *
     * @param   ui            - Drive for UI generated input
     * @param   projectNames  - The names of the projects in the workspace to export. The
     *                        list should not be empty and the named projects should exist
     *                        in the workspace
     * @param   archiveName   - The short name of the archive to export the projects to.
     *                        Ex: "ExportProjectsTest"
     * @return  IPath - The absolute disk location of the exported archive
     */
    public IPath exportProjectsToArchive(IUIContext ui,
                                         String[] projectNames,
                                         String archiveName);

    /**
     * getAllWorkspaceProjects - Get all of the projects in the workspace.
     *
     * @param   ui  - Driver for UI generated input
     * @return  List<IProject> - The list of all the project objects in the current
     *          workspace or an empty list if no projects exist
     */
    public List<IProject> getAllWorkspaceProjects(IUIContext ui);

    /**
     * getProjectForName - Get the workspace project for the given name.
     *
     * @param  projectName  - Should adhere to project name validation rules (not null,
     *                      not the empty string, legal characters, etc)
     */
    public IProject getProjectForName(String projectName);

    /**
     * getWorkspaceProjectNames - Get a list of string project names for all projects in
     * the active workspace.
     *
     * @return  List<String> - List of project names or an empty list if no projects exist
     */
    public Collection<String> getWorkspaceProjectNames();

    /**
     * importExistingProjectFromArchive - Import an existing project in an archive into
     * the current workspace. THIS METHOD SHOULD NOT TYPICALLY BE USED TO CREATE PROEJCTS
     * IN THE WORKSPACE. INSTEAD, THE createNewProject() METHODS ON SPECIFIC PROJECT
     * HELPERS SHOULD BE USED
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  sourcePlugin  - The test plugin containing the archive with the project to
     *                       import
     * @param  archivePath   - The plugin-relative path to the archive with the project to
     *                       import
     * @param  projectName   - The name of the project to import
     */
    public void importExistingProjectFromArchive(IUIContext ui,
                                                 Plugin sourcePlugin,
                                                 IPath archivePath,
                                                 String projectName);

    /**
     * importExistingProjectFromArchive - Import an existing project in an archive into
     * the current workspace. THIS METHOD SHOULD NOT TYPICALLY BE USED TO CREATE PROEJCTS
     * IN THE WORKSPACE. INSTEAD, THE createNewProject() METHODS ON SPECIFIC PROJECT
     * HELPERS SHOULD BE USED
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  sourcePlugin  - The test plugin containing the archive with the project to
     *                       import
     * @param  archivePath   - The plugin-relative path to the archive with the project to
     *                       import
     * @param  projectName   - The name of the project to import
     * @param  timeout       - The number of milliseconds to wait for the import to
     *                       finish. Must be larger than the default 60000
     */
    public void importExistingProjectFromArchive(IUIContext ui,
                                                 Plugin sourcePlugin,
                                                 IPath archivePath,
                                                 String projectName,
                                                 long timeout);

    /**
     * importExistingProjectFromSource - Import an existing project from a source location
     * into the current workspace. Since imported projects will typically need to have
     * read/write permissions, first copy the files into a folder under the current
     * workspace directory, then import from that copied location. THIS METHOD SHOULD NOT
     * TYPICALLY BE USED TO CREATE PROEJCTS IN THE WORKSPACE. INSTEAD, THE
     * createNewProject() METHODS ON SPECIFIC PROJECT HELPERS SHOULD BE USED
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  sourcePlugin  - The test plugin containing the project source to import
     * @param  projectRoot   - The plugin-relative path to the project to import. The
     *                       folder must have a .project file as a first-level child. The
     *                       folder should be named the same name as the name defined in
     *                       the .project file.
     * @param  projectName   - The name of the project to import
     */
    public void importExistingProjectFromSource(IUIContext ui,
                                                Plugin sourcePlugin,
                                                IPath projectRootPath,
                                                String projectName);

    /**
     * importExistingProjectFromSource - Import an existing project from a source location
     * into the current workspace. Since imported projects will typically need to have
     * read/write permissions, first copy the files into a folder under the current
     * workspace directory, then import from that copied location. THIS METHOD SHOULD NOT
     * TYPICALLY BE USED TO CREATE PROEJCTS IN THE WORKSPACE. INSTEAD, THE
     * createNewProject() METHODS ON SPECIFIC PROJECT HELPERS SHOULD BE USED
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  sourcePlugin  - The test plugin containing the project source to import
     * @param  projectRoot   - The plugin-relative path to the project to import. The
     *                       folder must have a .project file as a first-level child. The
     *                       folder should be named the same name as the name defined in
     *                       the .project file.
     * @param  projectName   - The name of the project to import
     * @param  timeout       - The number of milliseconds to wait for the import to
     *                       finish. Must be larger than the default 60000
     */
    public void importExistingProjectFromSource(IUIContext ui,
                                                Plugin sourcePlugin,
                                                IPath projectRootPath,
                                                String projectName,
                                                long timeout);

    /**
     * importExistingProjectsFromArchive - Import all projects in an archive into the
     * current workspace. THIS METHOD SHOULD NOT TYPICALLY BE USED TO CREATE PROEJCTS IN
     * THE WORKSPACE. INSTEAD, THE createNewProject() METHODS ON SPECIFIC PROJECT HELPERS
     * SHOULD BE USED
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  sourcePlugin  - The test plugin containing the archive with the project to
     *                       import
     * @param  archivePath   - The plugin-relative path to the archive with the project to
     *                       import
     */
    public void importExistingProjectsFromArchive(IUIContext ui,
                                                  Plugin sourcePlugin,
                                                  IPath archivePath);

    /**
     * importExistingProjectsFromArchive - Import all projects in an archive into the
     * current workspace. THIS METHOD SHOULD NOT TYPICALLY BE USED TO CREATE PROEJCTS IN
     * THE WORKSPACE. INSTEAD, THE createNewProject() METHODS ON SPECIFIC PROJECT HELPERS
     * SHOULD BE USED
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  sourcePlugin  - The test plugin containing the archive with the project to
     *                       import
     * @param  archivePath   - The plugin-relative path to the archive with the project to
     *                       import
     * @param  timeout       - The number of milliseconds to wait for the import to
     *                       finish. Must be larger than the default 60000
     */
    public void importExistingProjectsFromArchive(IUIContext ui,
                                                  Plugin sourcePlugin,
                                                  IPath archivePath,
                                                  long timeout);

    /**
     * invokeProjecPropertiesDialog - Show the properties dialog for the project with the
     * given name.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - The project whose properties dialog is to be shown
     */
    public void invokeProjectPropertiesDialog(IUIContext ui,
                                              String projectName);

    /**
     * openProject - Use the navigator view to open a project in the workspace. This
     * method will issue a test case failure if the project cannot be found for any
     * reason. If the project is already opened, this operation is a no-op.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Name of the project to open
     */
    public void openProject(IUIContext ui, String projectName);

    /**
     * waitForProjectExists - Wait for the project with the given name to exist.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Should not be null
     * @param  exists       - True if the project should exist for this condition to be
     *                      met
     */
    public void waitForProjectExists(IUIContext ui,
                                     String projectName,
                                     boolean exists);

    /**
     * waitForProjectExists - Wait for the project with the given name to exist (IF YOU
     * AREN'T SURE WHAT THE TIMEOUT AND INTERVAL VALUES ARE SUPPOSED TO BE, THEN YOU
     * SHOULDN'T BE USING THIS METHOD).
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Should not be null
     * @param  exists       - True if the project should exist for this condition to be
     *                      met
     * @param  timeout      - How long to wait before issuing a test case failure if the
     *                      condition is not met
     * @param  interval     - How long to wait between each test of the condition
     */
    public void waitForProjectExists(IUIContext ui,
                                     String projectName,
                                     boolean exists,
                                     long timeout,
                                     int interval);

    /**
     * waitForProjectOpen - Wait for the project with the given name to exist and to have
     * the given open/closed state.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Should not be null
     * @param  open         - True if the project should exist and be open for this
     *                      condition to be met; false if the project should exist and be
     *                      closed for this condition to be met
     */
    public void waitForProjectOpen(IUIContext ui,
                                   String projectName,
                                   boolean open);
}
