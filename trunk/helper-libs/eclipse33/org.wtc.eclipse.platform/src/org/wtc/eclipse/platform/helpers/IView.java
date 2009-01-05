/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

/**
 * Describes a view that can be opened in the workbench.
 */
public interface IView {
    // The view ID as defined in the view extension
    public String getViewID();

    // The path to the view in the open view dialog
    public String getViewPath();
}
