/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;


/**
 * Handler for the Resource Exists Dialog.  Click "Yes" to close if it is ever shown.
 * 
 * @since 3.8.0
 */
public class ResourceExistsShellHandler extends AbstractYesToCloseShellHandler {
	/**
     * Create an instance.
     * @since 3.8.0
     */
    public ResourceExistsShellHandler() {
        super("Resource Exists", true); //$NON-NLS-1$
    }
}
