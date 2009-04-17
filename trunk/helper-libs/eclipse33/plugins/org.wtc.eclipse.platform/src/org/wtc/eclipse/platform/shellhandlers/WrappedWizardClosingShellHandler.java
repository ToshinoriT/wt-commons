/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.eclipse.WizardClosingShellHandler;
import com.windowtester.runtime.swt.condition.shell.IShellCondition;

/**
 * Wrapper for the Window Tester WizardClosingShellHandler that extends
 * AbstractShellHandler.
 * 
 * @since 3.8.0
 */
public class WrappedWizardClosingShellHandler extends AbstractShellHandler {
    private WizardClosingShellHandler _wizardClosingShellHandler;

    /**
     * Save the data members.
     */
    public WrappedWizardClosingShellHandler(IUIContext ui) {
        super(ui, "Wizard Closing", true); //$NON-NLS-1$

        _wizardClosingShellHandler = new WizardClosingShellHandler();
    }

    /**
     * Get the condition that determines when this handler will run.
     */
    public IShellCondition getShellCondition() {
        return _wizardClosingShellHandler;
    }

    /**
     * @see  com.windowtester.runtime.condition.IHandler#handle(com.windowtester.runtime.IUIContext)
     */
    public void handle(IUIContext ui) {
        _wizardClosingShellHandler.handle(ui);
    }
}
