/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import com.windowtester.runtime.IUIContext;

/**
 * Handler for the Resource Exists Dialog.  Click "Yes" to close if it is ever shown.
 * 
 * @since 3.8.0
 */
public class ResourceExistsShellHandler extends AbstractYesToCloseShellHandler {
    /**
     * Save the UI context.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public ResourceExistsShellHandler(IUIContext ui) {
        super(ui, "Resource Exists", true); //$NON-NLS-1$
    }
}
