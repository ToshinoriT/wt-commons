/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically click
 * the "OK" button
 * 
 * @since 3.8.0
 */
public class AbstractOKToCloseShellHandler extends AbstractButtonToCloseShellHandler {
    /**
     * Save the data members.
     */
    public AbstractOKToCloseShellHandler(String title,
                                         boolean isModal) {
        super(title, isModal, "OK"); //$NON-NLS-1$
    }
}
