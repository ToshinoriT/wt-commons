/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically click
 * the "Close" or "OK" button when the "File Deleted" dialog is shown
 * 
 * @since 3.8.0
 */
public class FileDeletedShellHandler extends AbstractButtonToCloseShellHandler {
	/**
     * Create an instance.
     * @since 3.8.0
     */
    public FileDeletedShellHandler() {
        super("File Deleted", true, "(Close|OK)"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
