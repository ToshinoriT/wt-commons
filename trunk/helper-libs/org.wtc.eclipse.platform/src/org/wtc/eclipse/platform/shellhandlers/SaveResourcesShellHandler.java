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
 * the "OK" button when the "Save Resources" option dialog with list is shown
 */
public class SaveResourcesShellHandler extends AbstractOKToCloseShellHandler {
    /**
     * Save the UI context.
     *
     * @param  ui  - Driver for UI generated input
     */
    public SaveResourcesShellHandler(IUIContext ui) {
        super(ui, "Save Resources", true); //$NON-NLS-1$
    }
}
