/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.locators;

import org.eclipse.swt.widgets.Link;

/**
 * SWTWidgetLocator that can find Link widgets by index without requiring a parent widget
 * locator.
 * 
 * @since 3.8.0
 */
public class LinkByIndexLocator extends SWTWidgetByIndexLocator {
    private static final long serialVersionUID = -6812950735169053759L;

    /**
     * Save the data members.
     */
    public LinkByIndexLocator(int index) {
        super(Link.class, index);
    }
}
