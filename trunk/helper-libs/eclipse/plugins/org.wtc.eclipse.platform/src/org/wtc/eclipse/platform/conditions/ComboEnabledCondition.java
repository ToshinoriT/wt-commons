/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import abbot.tester.swt.ComboTester;
import com.windowtester.runtime.condition.ICondition;
import org.eclipse.swt.widgets.Combo;

/**
 * A condition that checks the enabled state of a given text field.
 * 
 * @since 3.8.0
 */
public class ComboEnabledCondition implements ICondition {
    private final Combo _combo;
    private final boolean _enabled;
    private final ComboTester _comboTester;

    /**
     * Save the data members.
     */
    public ComboEnabledCondition(Combo combo, boolean enabled) {
        _combo = combo;
        _enabled = enabled;
        _comboTester = new ComboTester();
    }

    /**
     * @see  com.windowtester.runtime2.condition.ICondition#test()
     */
    public boolean test() {
        boolean enabled = _comboTester.getEnabled(_combo);

        return _enabled == enabled;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _combo.toString() + " isEnabled() == " + _enabled; //$NON-NLS-1$
    }
}
