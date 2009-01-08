/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import com.windowtester.runtime.IUIContext;

/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically wait
 * until a popped progress dialog is closed
 */
public class UserOperationWaitingShellHandler extends AbstractProgressDialogShellHandler {
    /**
     * Save the data members.
     */
    public UserOperationWaitingShellHandler(IUIContext ui) {
        super(ui, "User Operation is Waiting"); //$NON-NLS-1$
    }
}
