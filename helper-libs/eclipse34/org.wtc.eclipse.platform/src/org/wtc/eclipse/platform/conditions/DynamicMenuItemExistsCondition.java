/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.platform.PlatformActivator;

/**
 * Just like a menu item exists condition, but don't fail if after the given number of
 * millis because the menu may be a dynamic menu. Instead, after the given number of
 * millis just return true.
 */
public class DynamicMenuItemExistsCondition extends MenuItemExistsCondition {
    // Just return true after this amount of time
    private final long _bailOutTime;

    /**
     * Save the data members.
     */
    public DynamicMenuItemExistsCondition(IUIContext ui,
                                          String menuItemPath,
                                          long millisToWait) {
        super(ui, menuItemPath, true);

        _bailOutTime = System.currentTimeMillis() + millisToWait;
    }

    /**
     * @see org.wtc.eclipse.platform.conditions.MenuItemExistsCondition#test()
     */
    @Override
    public boolean test() {
        boolean test = super.test();

        if (!test) {
            long now = System.currentTimeMillis();
            PlatformActivator.logDebug("The menu item path <" + _menuItemPath + "> was not found. Since it may be a dynamic menu, return true anyway"); //$NON-NLS-1$ //$NON-NLS-2$
            test = (now >= _bailOutTime);
        }

        return test;
    }
}
