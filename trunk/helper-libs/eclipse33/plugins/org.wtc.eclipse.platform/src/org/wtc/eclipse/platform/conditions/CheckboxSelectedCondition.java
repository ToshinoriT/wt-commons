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
 * Condition for verifying a button of type check is checked.
 */
public class CheckboxSelectedCondition implements ICondition {
    private boolean _checked;
    private Button _checkbox;
    private ButtonTester _buttonTester;

    /**
     * Save the data members.
     */
    public CheckboxSelectedCondition(Button checkbox, boolean checked) {
        _checkbox = checkbox;
        _checked = checked;
        _buttonTester = new ButtonTester();
    }

    /**
     * @see  com.windowtester.runtime2.condition.ICondition#test()
     */
    public boolean test() {
        return _buttonTester.getSelection(_checkbox) == _checked;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _buttonTester.getText(_checkbox) + " to be CHECKED : " + _checked; //$NON-NLS-1$
    }

}
