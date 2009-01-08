/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;

/**
 * Some menu items are add/removed to menus based on selection and/or other conditions.
 * When under a heavy load, the workbench may not respond fast enough to render the menu
 * before a test thread tries to click the menu item
 */
public class MenuItemExistsCondition implements ICondition {
    // Menu Item Path
    protected String _menuItemPath;

    // True if this condition should wait for the menu item to
    // exist; false if it should wait for it to not exist
    private boolean _exists;

    // NEVER DO THIS!
    private IUIContext _ui;

    /**
     * Save the data members.
     */
    public MenuItemExistsCondition(IUIContext ui,
                                   String menuItemPath,
                                   boolean exists) {
        _ui = ui;
        _menuItemPath = menuItemPath;
        _exists = exists;
    }

    /**
     * @see  com.windowtester.runtime.condition.ICondition#test()
     */
    public boolean test() {
        IWidgetLocator[] found = _ui.findAll(new MenuItemLocator(_menuItemPath));

        return ((found != null) && ((found.length > 0) == _exists));
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " FOR THE MENU ITEM <" + _menuItemPath + "> TO EXIST <" + _exists + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
