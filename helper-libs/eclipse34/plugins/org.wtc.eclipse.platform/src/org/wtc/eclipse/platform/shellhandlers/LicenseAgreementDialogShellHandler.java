/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically click
 * the "I Agree" button when the "License Agreement" dialog is shown
 * 
 * @since 3.8.0
 */
public class LicenseAgreementDialogShellHandler extends AbstractButtonToCloseShellHandler {
    /**
     * Save the UI context.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public LicenseAgreementDialogShellHandler() {
        super("License Agreement", true, "I Agree"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
