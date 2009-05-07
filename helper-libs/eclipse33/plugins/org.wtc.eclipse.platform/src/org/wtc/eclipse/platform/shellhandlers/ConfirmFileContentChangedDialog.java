/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically click
 * the "Yes" button when the "File Changed" dialog is shown
 * 
 * @since 3.8.0
 */
public final class ConfirmFileContentChangedDialog extends AbstractYesToCloseShellHandler {
    /**
     * Save the UI context.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public ConfirmFileContentChangedDialog() {
        super("File Changed", true); //$NON-NLS-1$
    }
}
