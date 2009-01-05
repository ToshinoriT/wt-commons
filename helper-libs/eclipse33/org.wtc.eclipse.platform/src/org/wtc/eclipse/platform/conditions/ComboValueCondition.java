/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import abbot.tester.swt.ComboTester;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.WidgetReference;
import junit.framework.TestCase;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Widget;
import java.text.Collator;

public final class ComboValueCondition implements ICondition {
    private final Collator _collator;
    private final String _expectedText;
    private final Combo _field;
    private final ComboTester _tester;

    /**
     * Save the data members.
     */
    public ComboValueCondition(Combo comboField, String expectedText) {
        TestCase.assertNotNull(comboField);
        TestCase.assertNotNull(expectedText);

        _collator = Collator.getInstance();
        _expectedText = expectedText;
        _field = comboField;
        _tester = new ComboTester();
    }

    /**
     * Save the data members.
     */
    public ComboValueCondition(IUIContext ui,
                               IWidgetLocator comboLocator,
                               String expectedText) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(comboLocator);

        Widget widget = null;

        try {
            IWidgetLocator comboRef = ui.find(comboLocator);
            TestCase.assertTrue(comboRef instanceof WidgetReference);

            widget = (Widget) ((WidgetReference) comboRef).getWidget();
            TestCase.assertTrue(widget instanceof Combo);
        } catch (WidgetSearchException e) {
            TestCase.fail(e.getLocalizedMessage());
        }

        _collator = Collator.getInstance();
        _expectedText = expectedText;
        _field = (Combo) widget;
        _tester = new ComboTester();
    }

    /**
     * @see  com.windowtester.swt.condition.ICondition#test()
     */
    public boolean test() {
        String textValue = _tester.getText(_field);

        return _collator.equals(textValue, _expectedText);
    }

    @Override
    public String toString() {
        return " for combo field to have value: " + _expectedText; //$NON-NLS-1$
    }
}
