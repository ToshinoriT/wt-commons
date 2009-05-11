/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.locators;

import org.eclipse.swt.widgets.Text;

/**
 * SWTWidgetLocator that can find Text widgets by index without requiring a parent widget
 * locator.
 * 
 * @since 3.8.0
 */
public class TextByIndexLocator extends SWTWidgetByIndexLocator {
    private static final long serialVersionUID = 5822032254007604998L;

    /**
     * Save the data members.
     */
    public TextByIndexLocator(int index) {
        super(Text.class, index);
    }
}
