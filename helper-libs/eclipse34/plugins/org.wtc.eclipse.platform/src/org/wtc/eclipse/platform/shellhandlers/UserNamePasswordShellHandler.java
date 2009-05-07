/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import static com.windowtester.runtime.swt.locator.SWTLocators.button;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.ShellCondition;

/**
 * Clicks the "Cancel" button when the (SVN) "Enter Username and Password" dialog is shown.
 * 
 * @since 3.8.0
 */
public class UserNamePasswordShellHandler extends ShellCondition implements IShellConditionHandler {

	/**
     * Create an instance.
     *
     * @since 3.8.0
     */
	public UserNamePasswordShellHandler() {
		super("Enter Username and Password", true); //$NON-NLS-1$
	}

	/**
	 * @see com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
	 * 
	 * @since 3.8.0
	 */
	public void handle(IUIContext ui) throws Exception {
		ui.click(button("Cancel")); //$NON-NLS-1$
	}
}