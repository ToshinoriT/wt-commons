/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.locators;

import org.eclipse.swt.widgets.Tree;

/**
 * SWTWidgetLocator that can find Tree widgets by index without requiring a parent widget
 * locator.
 */
public class TreeByIndexLocator extends SWTWidgetByIndexLocator {
    private static final long serialVersionUID = -6689845417821346294L;

    /**
     * Save the data members.
     */
    public TreeByIndexLocator(int index) {
        super(Tree.class, index);
    }
}
