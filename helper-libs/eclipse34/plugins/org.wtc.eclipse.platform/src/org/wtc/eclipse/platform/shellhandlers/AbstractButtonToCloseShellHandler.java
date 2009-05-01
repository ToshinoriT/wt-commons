/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import junit.framework.TestCase;

import org.wtc.eclipse.platform.PlatformActivator;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.locator.ButtonLocator;

/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test. This handler will specifically click
 * the specified button found by label
 * 
 * @since 3.8.0
 */
public class AbstractButtonToCloseShellHandler extends AbstractShellHandler {
    private final String _buttonLabel;

    /**
     * Save the data members.
     */
    public AbstractButtonToCloseShellHandler(String shellTitle,
                                             boolean isModal,
                                             String buttonLabel) {
        super(shellTitle, isModal);

        _buttonLabel = buttonLabel;
    }

    /**
     * Click the "Yes" button to close the shell.
     */
    public void handle(IUIContext ui) {
        PlatformActivator.logDebug(getClass().getSimpleName() + " handling shell"); //$NON-NLS-1$

        try {
            ui.click(new ButtonLocator(_buttonLabel));
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getLocalizedMessage());
        }
    }
    
	/**
	 * @return String - The label of the button that this handler will select to close the dialog
	 */
	public String getButtonLabel() {
		return _buttonLabel;
	}
}
