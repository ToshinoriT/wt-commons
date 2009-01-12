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
 * the "Yes" button when the "Save Resource?" option dialog is shown
 */
public class SaveResourceShellHandler2 extends AbstractYesToCloseShellHandler {
    /**
     * Save the UI context.
     *
     * @param  ui  - Driver for UI generated input
     */
    public SaveResourceShellHandler2(IUIContext ui) {
        super(ui, "Save Resource", true); //$NON-NLS-1$
    }
}