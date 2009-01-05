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
 * the "Yes" button when the "Source Folder Added" option dialog is shown
 */
public class SourceFolderAddedShellHandler extends AbstractYesToCloseShellHandler {
    /**
     * Save the UI context.
     *
     * @param  ui  - Driver for UI generated input
     */
    public SourceFolderAddedShellHandler(IUIContext ui) {
        super(ui,
              "Source Folder Added", //$NON-NLS-1$
              true);
    }
}
