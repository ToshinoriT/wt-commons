/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.locators;

import org.eclipse.swt.widgets.Combo;

/**
 * SWTWidgetLocator that can find Combo widgets by index without requiring a parent widget
 * locator.
 */
public class ComboByIndexLocator extends SWTWidgetByIndexLocator {
    private static final long serialVersionUID = 1777491466482690598L;

    /**
     * Save the data members.
     */
    public ComboByIndexLocator(int index) {
        super(Combo.class, index);
    }
}
