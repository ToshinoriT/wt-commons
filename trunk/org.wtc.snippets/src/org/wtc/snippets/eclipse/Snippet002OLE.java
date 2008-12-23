package org.wtc.snippets.eclipse;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.ole.win32.OleFrame;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.eclipse.ProjectExistsCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.condition.eclipse.ActiveEditorCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;

/**
 * A snippet that demonstrates finding the main {@link OleFrame} of an embedded OLE editor.  
 * Notice that the interactions with the frame itself are XY-based.  This is, obviously, not ideal.
 * More semantic interaction would have to involve communication with the underlying COM or ActiveX
 * document.
 * <p>
 * If you have ideas for how this could be neatly accomplished, please share them!
 * 
 * <p>
 * To run this snippet, you may need to increase your heap size.  It was developed using the following VM
 * args:
 * <pre>
 * -Xms256m
 * -Xmx512m
 * </pre>
 * 
 * 
 * @author Phil Quitslund
 *
 */
public class Snippet002OLE extends UITestCaseSWT {

		
	private static final String FILE_NAME = "test.xls";	

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		checkPreconditions();
		closeWelcomePageIfNecessary();
		createProject();
		createFile();
	}
	


	private void checkPreconditions() {
		assertTrue(Platform.getOS().equals(Platform.OS_WIN32));
		//a handler for xls-files needs to be installed as well...
	}
	
	private void closeWelcomePageIfNecessary() throws WidgetSearchException {
		IWidgetLocator[] welcomeTab = getUI().findAll(new CTabItemLocator("Welcome"));
		if (welcomeTab.length == 0)
			return;
		getUI().close(welcomeTab[0]);
	}
	

	private void createProject() throws WidgetSearchException {
		createProject(getProjectName());
	}
	
	private void createProject(String projectName) throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("(General|Simple)/Project"));
		ui.click(new ButtonLocator("Next >"));
		ui.enterText(projectName);
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Project"));
		ui.wait(new ProjectExistsCondition(projectName, true));
	}
	
	private String getProjectName() {
		return getClass().getSimpleName() + "Project";
	}

	//note: assumes output path is selected 
	private void createFile() throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/File"));
		ui.wait(new ShellShowingCondition("New File"));
		ui.enterText(FILE_NAME);
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New File"));
		ui.wait(ActiveEditorCondition.forName(FILE_NAME));
	}


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		//NOTE: saving does not clear the dirty flag so we need to be more brute force, triggering 
		//the save with a close
		close();
	}
	
	private void close() throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/Close"));
		ui.wait(new ShellShowingCondition("Save Resource"));
		ui.click(new ButtonLocator("Yes"));
		ui.wait(new ShellDisposedCondition("Save Resource"));
	}


	
	public void testDriveOLE() throws Exception {
		
		IUIContext ui = getUI();
		//NOTE this XY is a pure guess!
		ui.click(new XYLocator(new SWTWidgetLocator(OleFrame.class, new EditorLocator(FILE_NAME)), 50, 50));
		
		for (int i=0; i < 10; ++i) {
			ui.enterText(Integer.toString(i*100));
			ui.keyClick(WT.TAB);
		}		
	}
	
	
	
	
	
	
	
}
