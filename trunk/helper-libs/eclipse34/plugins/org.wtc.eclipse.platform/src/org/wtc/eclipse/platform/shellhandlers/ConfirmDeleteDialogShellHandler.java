/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Start listening for the Confirm Delete Dialog and click "Yes To All" (or "Yes") if it
 * is ever shown. The caller should call stop listening for this dialog
 *
 * @since 3.8.0
 */
public class ConfirmDeleteDialogShellHandler extends AbstractYesToCloseShellHandler {
    /**
     * Save the UI context.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public ConfirmDeleteDialogShellHandler() {
        super("Confirm Resource Delete", true); //$NON-NLS-1$
    }
}
