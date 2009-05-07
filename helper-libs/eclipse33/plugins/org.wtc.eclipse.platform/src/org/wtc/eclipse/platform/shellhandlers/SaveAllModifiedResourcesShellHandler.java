/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.swt.condition.shell.IShellCondition;

/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically click
 * the "OK" button when the "Save Resources" option dialog with list is shown
 * 
 * @since 3.8.0
 */
public class SaveAllModifiedResourcesShellHandler extends AbstractOKToCloseShellHandler
    implements IShellCondition {
	/**
     * Create an instance.
     * @since 3.8.0
     */
    public SaveAllModifiedResourcesShellHandler() {
        super("Save All Modified Resources", false); //$NON-NLS-1$
    }

    //TO DO: CR292770 - Save All Modified Resources dialog is not
    //modal like it should be. The following is a workaround to the fact
    //that the short version of listenForDialog() checks modality.
    //This version does not.
    public boolean test(Shell shell) {
        boolean same = shell.getText().equals("Save All Modified Resources"); //$NON-NLS-1$

        return same;

    }
}
