/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically wait
 * until a popped "Setting build path" dialog is closed
 * 
 * @since 3.8.0
 */
public class SettingBuildPathShellHandler extends AbstractProgressDialogShellHandler {
    /**
     * Save the data members.
     */
    public SettingBuildPathShellHandler() {
        super("Setting build path"); //$NON-NLS-1$
    }

}
