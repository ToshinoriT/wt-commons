/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import com.windowtester.runtime.IUIContext;

/**
 * Start listening for the Overwrite Files Dialog and click "OK" if it is ever shown. The
 * caller should call stop listening for this dialog
 *
 * @param  ui  - Driver for UI generated input
 */
public class ConfirmOverwriteFilesDialogShellHandler extends AbstractOKToCloseShellHandler {
    /**
     * Save the UI context.
     *
     * @param  ui  - Driver for UI generated input
     */
    public ConfirmOverwriteFilesDialogShellHandler(IUIContext ui) {
        super(ui, "Overwrite Files?", true); //$NON-NLS-1$
    }
}
