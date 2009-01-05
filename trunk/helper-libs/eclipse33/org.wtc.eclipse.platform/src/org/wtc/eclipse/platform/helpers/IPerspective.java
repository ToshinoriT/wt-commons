/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

/**
 * Describes a perspective that can be opened in the workbench.
 */
public interface IPerspective {
    // The perspective ID as defined in the perspective extension
    public String getID();

    // The name to the perspective as it appears in the open
    // perspective dialog
    public String getPerspectiveLabel();
}
