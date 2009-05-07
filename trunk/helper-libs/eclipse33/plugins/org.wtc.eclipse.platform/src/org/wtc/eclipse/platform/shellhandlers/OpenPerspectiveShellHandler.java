/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically click
 * the "No" button when the "Open associated perspective?" option dialog is shown
 * 
 * @since 3.8.0
 */
public class OpenPerspectiveShellHandler extends AbstractNoToCloseShellHandler {
	/**
     * Create an instance.
     * @since 3.8.0
     */
    public OpenPerspectiveShellHandler() {
        super("Open Associated Perspective?", true); //$NON-NLS-1$
    }
}
