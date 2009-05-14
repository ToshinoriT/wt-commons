package com.windowtester.runtime.swt.incubator.test;


import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.incubator.swt.locator.PropertyTabItemLocator;
import com.windowtester.runtime.incubator.swt.locator.PropertyTabReference;
import com.windowtester.runtime.incubator.swt.locator.TabbedPropertyListLocator;
import com.windowtester.runtime.incubator.swt.locator.TabbedPropertyListReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * A test to drive experiments locating property tab items.
 * <p/>
 * NOTE: this test depends on the eclipse tabbed property example as described here:
 * <p>
 * http://www.eclipse.org/articles/Article-Tabbed-Properties/tabbed_properties_view.html
 * </p>
 * A team project set for the examples source can be found in this project's 
 * <b>resources/</b> directory.
 * 
 * <p/>
 * 
 * @author Phil Quitslund
 *
 * @since 3.8.0
 */
public class PropertyTabDrivingTest extends UITestCaseSWT {

	@Override
	protected void oneTimeSetup() throws Exception {
		super.oneTimeSetup();
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Open Perspective/Other..."));
		ui.wait(new ShellShowingCondition("Open Perspective"));
		ui.click(new TableItemLocator("Tabbed Properties View Tests Perspective"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Open Perspective"));
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IUIContext ui = getUI();

		ui.click(new TableItemLocator(
				"Information",
				new ViewLocator(
						"org.eclipse.ui.tests.views.properties.tabbed.override.OverrideTestsView")));
	}
	
	public void testClickTabsByLabel() throws Exception {

		IUIContext ui = getUI();		
		ui.click(new PropertyTabItemLocator("Information"));
		ui.click(new PropertyTabItemLocator("Warning"));
		ui.click(new PropertyTabItemLocator("Error"));
	}
	
	
	public void testCollectAndClickTabs() throws Exception {

		IUIContext ui = getUI();

		TabbedPropertyListReference tabListRef = (TabbedPropertyListReference) ui.find(new TabbedPropertyListLocator());
		PropertyTabReference[] tabs = tabListRef.getTabs();
		for (PropertyTabReference ref : tabs) {
			ui.click(ref);
			//ui.pause(1000); //pause for visual confirmation
		}
	}
	
}
