package wt.commons.snippets.eclipse;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.MenuItemLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;

/**
 * A simple test to drive verification of a few default preference settings for
 * the workbench.
 * <p>
 * <strong>NOTE:</strong> actual properties may vary!
 * 
 * @author Phil Quitslund
 * 
 */
public class Snippet003PreferencePageAsserts extends UITestCaseSWT {

	/*
	 * The default values to test.
	 */
	private static final String WORKSPACE_SAVE_INTERVAL = "5";
	private static final boolean AUTO_BUILD_ENABLED = true;
	private static final boolean AUTO_SAVE_ENABLED = false;
	private static final boolean AUTO_REFRESH_ENABLED = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws Exception {
		openWorkbenchPreferences(getUI());
	}

	private void openWorkbenchPreferences(IUIContext ui)
			throws WidgetSearchException, WaitTimedOutException {
		ui.click(new MenuItemLocator("Window/Preferences"));
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new FilteredTreeItemLocator("General/Workspace"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		if (preferenceShellIsShowing())
			dismissPreferences(getUI());
	}

	private boolean preferenceShellIsShowing() {
		return new ShellShowingCondition("Preferences").test();
	}

	private void dismissPreferences(IUIContext ui) throws WidgetSearchException, WaitTimedOutException {
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}

	public void testAutoBuild() {
		getUI().assertThat(new ButtonLocator("&Build automatically").isSelected(AUTO_BUILD_ENABLED));
	}

	public void testAutoRefresh() {
		getUI().assertThat(new ButtonLocator("&Refresh automatically").isSelected(AUTO_REFRESH_ENABLED));
	}

	public void testAutoSave() {
		getUI().assertThat(new ButtonLocator("Save auto&matically before build").isSelected(AUTO_SAVE_ENABLED));
	}

	public void testSaveInterval() {
		getUI().assertThat(new LabeledTextLocator("&Workspace save interval (in minutes):").hasText(WORKSPACE_SAVE_INTERVAL));
	}

}
