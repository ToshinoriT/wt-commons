/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

import com.windowtester.runtime.IUIContext;

/**
 * Helper for creating Simple projects.
 */
public interface ISimpleProjectHelper {
    /**
     * createProject - Create a simple project with the default location. Method should
     * wait until the project is created
     *
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Should adhere to project name validation rules (not null,
     *                      not the empty string, legal characters, etc)
     */
    public void createProject(IUIContext ui, String projectName);

    /**
     * deleteProject - Delete the given project from the workspace.
     *
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Should adhere to project name validation rules (not null,
     *                      not the empty string, legal characters, etc)
     */
    public void deleteProject(IUIContext ui, String projectName);

    /**
     * deleteProject - Delete the given project from the workspace.
     *
     * @param  ui              - Driver for UI generated input
     * @param  projectName     - Should adhere to project name validation rules (not null,
     *                         not the empty string, legal characters, etc)
     * @param  deleteContents  - True if the contents should be deleted, too
     */
    public void deleteProject(IUIContext ui,
                              String projectName,
                              boolean deleteContents);
}
