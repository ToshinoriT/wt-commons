package wt.commons.snippets.eclipse;

import org.eclipse.swt.widgets.Label;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.MenuItemLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.forms.HyperlinkLocator;



/**
 * A snippet that demonstrates dynamic help content verification by
 * driving the "General/Content Types" Eclipse preference page.
 * 
 * @author Phil Quitslund
 *
 */
public class Snippet005VerifyDynamicHelpContents extends UITestCaseSWT {


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		selectContentTypesPreferencePage();
		invokeContextHelp();
	}

	private void selectContentTypesPreferencePage() throws WidgetSearchException {
        IUIContext ui = getUI();
        ui.click(new MenuItemLocator("Window/Preferences"));
        ui.wait(new JobsCompleteCondition()); //help indexer may kick off
        ui.wait(new ShellShowingCondition("Preferences"));
        ui.click(new FilteredTreeItemLocator("General/Content Types"));
	}
	
	private void invokeContextHelp() {
        IUIContext ui = getUI();
        ui.keyClick(WT.F1);
        ui.wait(contextHelpIsShowing());
	}
	
	/**
	 * Return a condition that verifies that context help is showing by looking for
	 * it's signature "Related Topics" label.  
	 * <p>
	 * 
	 * <strong>Note:</strong> this could be more semantic.
	 */
    private static ICondition contextHelpIsShowing() {
    	return new SWTWidgetLocator(Label.class, "Related Topics").isVisible();
    }
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		IUIContext ui = getUI();
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}
	
		
    public void testContentTypePreferenceHelpLinks() throws Exception {
        IUIContext ui = getUI();
		ui.assertThat(hyperlink("Preferences - Content Types").inSection("About Content Types").isVisible());
		ui.assertThat(hyperlink("Preferences - File Associations").inSection("About Content Types").isVisible());
	}
    
    public void testContentTypePreferenceHelpHREFs() throws Exception {
    	IUIContext ui = getUI();
		ui.assertThat(hyperlink("Preferences - Content Types").inSection("About Content Types")
									.hasHRef("/org.eclipse.platform.doc.user/reference/ref-content-type.htm"));
		ui.assertThat(hyperlink("Preferences - File Associations").inSection("About Content Types")
									.hasHRef("/org.eclipse.platform.doc.user/reference/ref-13.htm"));
	}
    
    private static HyperlinkLocator hyperlink(String linkText) {
    	return new HyperlinkLocator(linkText);
    }
    
}
