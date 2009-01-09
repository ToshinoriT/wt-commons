/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers.adapters;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import junit.framework.TestCase;
import org.wtc.eclipse.platform.PlatformActivator;

/**
 * Helper adapter for actions that manipulate preferences.
 */
public abstract class PreferencesHelperAdapter extends HelperImplAdapter {
    /**
     * Click the Restore Defaults button.
     */
    protected void clickRestoreDefaultsButton(IUIContext ui) {
        clickButton(ui, "Restore &Defaults"); //$NON-NLS-1$
    }

    /**
     * Open the preferences dialog, and show the preferences page given by the category
     * name.
     *
     * @param  ui            - Driver for UI generated input
     * @param  categoryName  - The category name of the preferences page to open. Use
     *                       forward slashes for pages that are in nested categories
     */
    protected void openPreferencesPage(IUIContext ui, String categoryName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(categoryName);

        try {
            ui.click(new MenuItemLocator("&Window/&Preferences...")); //$NON-NLS-1$

            ui.wait(new ShellShowingCondition("Preferences")); //$NON-NLS-1$
            ui.click(new TreeItemLocator(categoryName));
        } catch (WidgetSearchException wse) {
            PlatformActivator.logException(wse);
            TestCase.fail(wse.getLocalizedMessage());
        }
    }
}
