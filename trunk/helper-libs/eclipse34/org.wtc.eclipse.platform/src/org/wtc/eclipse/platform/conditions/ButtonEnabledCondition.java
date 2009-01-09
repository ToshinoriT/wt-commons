/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import abbot.tester.swt.ButtonTester;
import com.windowtester.runtime.condition.ICondition;
import org.eclipse.swt.widgets.Button;

/**
 * A condition that checks the enabled state of a given button.
 */
public class ButtonEnabledCondition implements ICondition {
    private final Button _button;
    private final String _buttonText;
    private final boolean _enabled;
    private final ButtonTester _buttonTester;

    /**
     * Save the data members.
     */
    public ButtonEnabledCondition(Button button, boolean enabled) {
        _button = button;
        _enabled = enabled;
        _buttonTester = new ButtonTester();
        _buttonText = _buttonTester.getText(_button);
    }

    /**
     * @see  com.windowtester.runtime2.condition.ICondition#test()
     */
    public boolean test() {
        boolean enabled = _buttonTester.getEnabled(_button);

        return _enabled == enabled;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _buttonText + " isEnabled() == " + _enabled; //$NON-NLS-1$
    }
}
