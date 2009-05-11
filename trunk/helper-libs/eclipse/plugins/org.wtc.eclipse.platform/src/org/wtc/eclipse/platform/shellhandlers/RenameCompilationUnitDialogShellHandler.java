/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Shell handler that listens for the "Rename Compilation Unit" dialog and clicks
 * "Continue" if it ever comes up.
 * 
 * @since 3.8.0
 */
public class RenameCompilationUnitDialogShellHandler extends AbstractButtonToCloseShellHandler {
    /**
     * Save the data members.
     */
    public RenameCompilationUnitDialogShellHandler() {
        super("Rename Compilation Unit", true, "Con&tinue"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
