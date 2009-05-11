/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.shellhandlers.UserNamePasswordShellHandler;
import org.wtc.eclipse.platform.tests.EclipseUITest;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;

/**
 * Quick smoke test of the editor helper "Go to line" support.
 */
public class EditorHelperGoToLineTest extends EclipseUITest {
	
	private final UserNamePasswordShellHandler passwordShellHandler = new UserNamePasswordShellHandler();
	
	@Override
	protected void methodSetUp() throws Exception {
		super.methodSetUp();
		IUIContext ui = getUI();
		IShellMonitor sm = (IShellMonitor) ui.getAdapter(IShellMonitor.class);
		sm.add(passwordShellHandler);
	}
	
	@Override
	protected void methodTearDown() throws Exception {
		super.methodTearDown();
		IUIContext ui = getUI();
		IShellMonitor sm = (IShellMonitor) ui.getAdapter(IShellMonitor.class);
		sm.remove(passwordShellHandler);
	}
	
	
    /**
     * Create a project, create a file, go to some lines.
     */
    public void testGoToLine() {
        IUIContext ui = getUI();

        String projectName = "EditorHelperGoToLineTest"; //$NON-NLS-1$

        ISimpleProjectHelper simple = EclipseHelperFactory.getSimpleProjectHelper();
        simple.createProject(ui, projectName);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.importFiles(ui, PlatformActivator.getDefault(), new Path("resources/testfiles/FindReplaceAllTest"), //$NON-NLS-1$
                              new Path(projectName));

        IPath fullFilePath = new Path(projectName + "/testFindReplaceAll.txt"); //$NON-NLS-1$
        resources.openFile(ui, fullFilePath);

        IEditorHelper editor = EclipseHelperFactory.getEditorHelper();
        editor.gotoLine(ui, 4);
        editor.gotoLine(ui, 1);
    }
}
