/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import com.windowtester.runtime.IUIContext;

/**
 * Start listening for the Confirm Delete Dialog and click "Yes To All" (or "Yes") if it
 * is ever shown. The caller should call stop listening for this dialog
 *
 * @param  ui  - Driver for UI generated input
 */
public class ConfirmDeleteDialogShellHandler extends AbstractYesToCloseShellHandler {
    /**
     * Save the UI context.
     *
     * @param  ui  - Driver for UI generated input
     */
    public ConfirmDeleteDialogShellHandler(IUIContext ui) {
        super(ui, "Confirm Resource Delete", true); //$NON-NLS-1$
    }
}
