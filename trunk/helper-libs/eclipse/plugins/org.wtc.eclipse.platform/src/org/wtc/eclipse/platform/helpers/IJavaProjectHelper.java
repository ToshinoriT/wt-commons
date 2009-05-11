/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

import com.windowtester.runtime.IUIContext;

/**
 * Helper for creating Java projects.
 * 
 * @since 3.8.0
 */
public interface IJavaProjectHelper {
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
     * createProject - Create a simple project with the default location. Method should
     * wait until the project is created
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Should adhere to project name validation rules (not null,
     *                      not the empty string, legal characters, etc)
     */
    public void createProject(IUIContext ui, String projectName);

    /**
     * createProject - Create a simple project with the default location and separate
     * source and output folders, if specified. Method should wait until the project is
     * created
     *
     * @since 3.8.0
     * @param  ui                       - Driver for UI generated input
     * @param  projectName              - Should adhere to project name validation rules
     *                                  (not null, not the empty string, legal characters,
     *                                  etc)
     * @param  separateSourceAndOutput  - True to create separate source and output
     *                                  folders
     */
    public void createProject(IUIContext ui, String projectName, boolean separateSourceAndOutput);

    /**
     * deleteProject - Delete the given project from the workspace.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Should adhere to project name validation rules (not null,
     *                      not the empty string, legal characters, etc)
     */
    public void deleteProject(IUIContext ui, String projectName);

    /**
     * deleteProject - Delete the given project from the workspace.
     *
     * @since 3.8.0
     * @param  ui              - Driver for UI generated input
     * @param  projectName     - Should adhere to project name validation rules (not null,
     *                         not the empty string, legal characters, etc)
     * @param  deleteContents  - True if the contents should be deleted, too
     */
    public void deleteProject(IUIContext ui,
                              String projectName,
                              boolean deleteContents);
}
