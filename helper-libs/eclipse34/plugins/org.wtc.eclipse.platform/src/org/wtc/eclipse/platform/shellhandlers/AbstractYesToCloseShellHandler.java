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
 * the "Yes" button
 * 
 * @since 3.8.0
 */
public abstract class AbstractYesToCloseShellHandler extends AbstractButtonToCloseShellHandler {
    /**
     * Save the data members.
     */
    public AbstractYesToCloseShellHandler(IUIContext ui, String title, boolean isModal) {
        super(ui, title, isModal, ".*es"); //$NON-NLS-1$
    }
}
