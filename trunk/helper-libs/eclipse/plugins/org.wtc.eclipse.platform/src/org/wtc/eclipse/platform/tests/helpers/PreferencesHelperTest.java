
package org.wtc.eclipse.platform.tests.helpers;

import org.wtc.eclipse.core.tests.LifecycleUITest;
import org.wtc.eclipse.platform.helpers.adapters.PreferencesHelperAdapter;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.ButtonLocator;

/**
 * Simple test to drive preference page access.
 */
public class PreferencesHelperTest extends LifecycleUITest {

	private class PreferencesHelper extends PreferencesHelperAdapter { 
		@Override
		public void openPreferencesPage(IUIContext ui, String categoryName) {
			//protected in super -- made public here for testing
			super.openPreferencesPage(ui, categoryName);
		}
	}
	
	public void testOpenPreferences() throws Exception {
		IUIContext ui = getUI();
		PreferencesHelper ph = new PreferencesHelper();
		ph.openPreferencesPage(ui, "General"); //$NON-NLS-1$
		ui.click(new ButtonLocator("Cancel")); //$NON-NLS-1$
	}
	
}
