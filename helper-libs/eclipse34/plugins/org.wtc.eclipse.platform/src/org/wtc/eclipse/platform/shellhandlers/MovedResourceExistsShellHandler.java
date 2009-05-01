/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import junit.framework.TestCase;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.locator.ButtonLocator;

/**
 * Handler for the Resource Exists Dialog.  Click "Continue" to close if it is ever shown.
 */
public class MovedResourceExistsShellHandler extends AbstractContinueToCloseShellHandler {

    /**
     * Create an instance.
     */
    public MovedResourceExistsShellHandler() {
        super("Move Resources", true); //$NON-NLS-1$
    }
    
    
    /* (non-Javadoc)
     * @see org.wtc.eclipse.platform.shellhandlers.AbstractButtonToCloseShellHandler#handle(com.windowtester.runtime.IUIContext)
     */
    @Override
    public void handle(IUIContext ui) {
    	//only handle if button is visible
    	try {
    		ButtonLocator continueButton = new ButtonLocator(getButtonLabel());
			if (!continueButton.isVisible(ui))
				return;
		} catch (WidgetSearchException e) {
			TestCase.fail(e.getMessage());
		}
    	super.handle(ui);
    }
}
