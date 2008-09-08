package wt.commons.samples;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;

import com.windowtester.finder.swt.ShellFinder;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.condition.eclipse.DirtyEditorCondition;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;

/**
 * Common base class for tests.
 * 
 * @author Steve Messick
 * @author Phil Quitslund
 */
public class BaseTest extends UITestCaseSWT {

	protected void setUp() throws Exception {
		checkForPDERequirement();
		ensureWorkbenchIsInFront();
		closeWelcomePageIfNecessary();
	}

	private void ensureWorkbenchIsInFront() {
		ShellFinder.bringRootToFront(Display.getDefault());
	}


	private void checkForPDERequirement() {
		assertTrue("This test must be run as a PDE test", Platform.isRunning());
	}


	//useful for tearDown
	protected void saveAllIfNecessary() throws WidgetSearchException {
		if (anyUnsavedChanges())
			getUI().click(new MenuItemLocator("File/Save All"));
	}

	private boolean anyUnsavedChanges() {
		return new DirtyEditorCondition().test();
	}
	
	/**
	 * Recent versions of Eclipse do not close the welcome page when view
	 * is opened. Make sure it gets closed.
	 * @throws WidgetSearchException
	 */
	protected void closeWelcomePageIfNecessary() throws WidgetSearchException {
		IWidgetLocator[] welcomeTab = getUI().findAll(new CTabItemLocator("Welcome"));
		if (welcomeTab.length == 0)
			return;
		getUI().close(welcomeTab[0]);

	}

}
