/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import abbot.tester.swt.TextTester;
import com.windowtester.runtime.condition.ICondition;
import org.eclipse.swt.widgets.Text;

/**
 * A condition that checks the enabled state of a given text field.
 * 
 * @since 3.8.0 
 */
public class TextEnabledCondition implements ICondition {
    private final Text _textField;
    private final boolean _enabled;
    private final TextTester _textTester;

    /**
     * Save the data members.
     */
    public TextEnabledCondition(Text text, boolean enabled) {
        _textField = text;
        _enabled = enabled;
        _textTester = new TextTester();
    }

    /**
     * @see  com.windowtester.runtime2.condition.ICondition#test()
     */
    public boolean test() {
        boolean enabled = _textTester.getEnabled(_textField);

        return _enabled == enabled;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _textField.toString() + " isEnabled() == " + _enabled; //$NON-NLS-1$
    }
}
