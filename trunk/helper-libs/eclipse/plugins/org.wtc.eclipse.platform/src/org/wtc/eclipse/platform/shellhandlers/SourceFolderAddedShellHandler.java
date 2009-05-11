/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically click
 * the "Yes" button when the "Source Folder Added" option dialog is shown
 * 
 * @since 3.8.0
 */
public class SourceFolderAddedShellHandler extends AbstractYesToCloseShellHandler {
   
	/**
     * Create an instance.
     *
     * @since 3.8.0
     */
    public SourceFolderAddedShellHandler() {
        super("Source Folder Added", //$NON-NLS-1$
              true);
    }
}
