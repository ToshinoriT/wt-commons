/**
 * WT Commons Project 2008-2009
 *
 */
package org.wtc.eclipse.platform.tests.helpers;

import junit.framework.TestCase;

import org.wtc.eclipse.core.util.Eclipse;
import org.wtc.eclipse.platform.internal.helpers.impl.ProjectHelperImpl;

import com.windowtester.runtime.util.StringComparator;

/**
 * A simple test to verify regexp building for 3.4+.  
 * NOTE: this test is effectively disabled for 3.3.
 */
public class ProjectHelperProjectTreeLabelTest extends TestCase {


	private final ProjectHelperImpl helper = new ProjectHelperImpl();
	
    public void testRegexpMatch() throws Exception {
    	if (Eclipse.VERSION.is(3, 3))
    		return;
    	assertTrue(matches("Project", "Project (Project)")); //$NON-NLS-1$ //$NON-NLS-2$
    	assertTrue(matches("Project", "Project (/Users/Joe/Workspaces/Project)")); //$NON-NLS-1$ //$NON-NLS-2$
    	assertTrue(matches("Project A", "Project A (/Users/Joe/Workspaces/Project A)")); //$NON-NLS-1$ //$NON-NLS-2$
    }

	public void testRegexpNoMatch() throws Exception {
	  	if (Eclipse.VERSION.is(3, 3))
    		return;
		assertFalse(matches("Project", "ProjectA (ProjectA)")); //$NON-NLS-1$ //$NON-NLS-2$	
		assertFalse(matches("Project", "ProjectA (/Users/Joe/Workspaces/ProjectA)")); //$NON-NLS-1$ //$NON-NLS-2$
		assertFalse(matches("Project", "Project A (/Users/Joe/Workspaces/Project A)")); //$NON-NLS-1$ //$NON-NLS-2$
	}	
	
	
	private boolean matches(String projectName, String nodeLabel) {
		return StringComparator.matches(nodeLabel, regexp(projectName));
	}

	private String regexp(String name) {
		return helper.buildProjectTreeNodeLabelRegexp(name);
	}
	
}
