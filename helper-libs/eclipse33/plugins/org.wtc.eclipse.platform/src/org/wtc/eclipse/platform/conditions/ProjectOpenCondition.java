/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.condition.ICondition;
import junit.framework.TestCase;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Condition that waits until the project with the given name has the given opened state.
 */
public class ProjectOpenCondition implements ICondition {
    private IProject _project;
    private boolean _open;

    /**
     * @param  projectName  - The name of the project to verify
     * @param  open         - True if the project should exist and be open for this
     *                      condition to be met; false if the project should exist and be
     *                      closed for this condition to be met
     */
    public ProjectOpenCondition(String projectName, boolean open) {
        TestCase.assertNotNull(projectName);

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        _project = root.getProject(projectName);
        TestCase.assertTrue("THE PROJECT <" + projectName + "> DOES NOT EXIST", //$NON-NLS-1$ //$NON-NLS-2$
                            _project.exists());

        _open = open;
    }

    /**
     * @see  com.windowtester.runtime2.condition.ICondition#test()
     */
    public boolean test() {
        return (_project.isOpen() == _open);
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " FOR THE PROJECT <" + _project.getName() + "> TO BE " + (_open ? "OPEN" : "CLOSED"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }
}
