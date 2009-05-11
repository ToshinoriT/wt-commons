/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically click
 * the "No" button
 * 
 * @since 3.8.0
 */
public class AbstractNoToCloseShellHandler extends AbstractButtonToCloseShellHandler {
    /**
     * Save the data members.
     */
    public AbstractNoToCloseShellHandler(String title,
                                         boolean isModal) {
        super(title, isModal, "&No"); //$NON-NLS-1$
    }
}
