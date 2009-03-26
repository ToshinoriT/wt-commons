/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.locators;

import org.eclipse.swt.widgets.Table;

/**
 * SWTWidgetLocator that can find Table widgets by index without requiring a parent widget
 * locator.
 */
public class TableByIndexLocator extends SWTWidgetByIndexLocator {
    private static final long serialVersionUID = 936193588405270199L;

    /**
     * Save the data members.
     */
    public TableByIndexLocator(int index) {
        super(Table.class, index);
    }
}
