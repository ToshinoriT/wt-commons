/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import com.windowtester.runtime.IUIContext;

/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically click
 * the "No" button when the "Open associated perspective?" option dialog is shown
 * 
 * @since 3.8.0
 */
public class OpenPerspectiveShellHandler extends AbstractNoToCloseShellHandler {
    /**
     * Save the UI context.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public OpenPerspectiveShellHandler(IUIContext ui) {
        super(ui, "Open Associated Perspective?", true); //$NON-NLS-1$
    }
}
