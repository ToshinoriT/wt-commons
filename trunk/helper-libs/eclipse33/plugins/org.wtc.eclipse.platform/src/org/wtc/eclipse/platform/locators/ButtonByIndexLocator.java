/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.locators;

import org.eclipse.swt.widgets.Button;

/**
 * SWTWidgetLocator that can find Button widgets by index without requiring a parent
 * widget locator.
 */
public class ButtonByIndexLocator extends SWTWidgetByIndexLocator {
    private static final long serialVersionUID = 307033222006576979L;

    /**
     * Save the data members.
     */
    public ButtonByIndexLocator(int index) {
        super(Button.class, index);
    }
}
