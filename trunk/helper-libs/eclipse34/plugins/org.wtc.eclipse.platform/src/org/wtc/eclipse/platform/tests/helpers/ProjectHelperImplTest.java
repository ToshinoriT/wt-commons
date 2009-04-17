/**
 * WT Commons Project 2008-2009
 *
 */
package org.wtc.eclipse.platform.tests.helpers;

import junit.framework.TestCase;

import org.wtc.eclipse.platform.internal.helpers.impl.ProjectHelperImpl;

import com.windowtester.runtime.util.StringComparator;

public class ProjectHelperImplTest extends TestCase {


	private final ProjectHelperImpl helper = new ProjectHelperImpl();
	
	public void testRegexpMatch() throws Exception {
		assertTrue(matches("Project", "Project (Project)"));	
		assertTrue(matches("Project", "Project (/Users/Joe/Workspaces/Project)"));	
		
	}

	public void testRegexpNoMatch() throws Exception {
		assertFalse(matches("Project", "ProjectA (ProjectA)"));	
		assertFalse(matches("Project", "ProjectA (/Users/Joe/Workspaces/ProjectA)"));	
	}	
	
	
	private boolean matches(String projectName, String nodeLabel) {
		return StringComparator.matches(nodeLabel, regexp(projectName));
	}

	private String regexp(String name) {
		return helper.buildProjectTreeNodeLabelRegexp(name);
	}
	
}
