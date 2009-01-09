/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import abbot.tester.swt.TextTester;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.WidgetReference;
import junit.framework.TestCase;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.wtc.eclipse.platform.PlatformActivator;
import java.text.Collator;

/**
 * Condition to verify that the given text field contains the expected text value.
 */
public class TextValueCondition implements ICondition {
    private final Collator _collator;
    private final String _expectedText;
    private final Text _textField;
    private final TextTester _textTester;

    /**
     * Save the data members.
     */
    public TextValueCondition(Text textField, String expectedText) {
        TestCase.assertNotNull(textField);
        TestCase.assertNotNull(expectedText);

        _collator = Collator.getInstance();
        _expectedText = expectedText;
        _textField = textField;
        _textTester = new TextTester();
    }

    /**
     * Save the data members.
     */
    public TextValueCondition(IUIContext ui,
                              IWidgetLocator textLocator,
                              String expectedText) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(textLocator);

        Widget widget = null;

        try {
            IWidgetLocator textRef = ui.find(textLocator);
            TestCase.assertTrue(textRef instanceof WidgetReference);

            widget = (Widget) ((WidgetReference) textRef).getWidget();
            TestCase.assertTrue(widget instanceof Text);
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getLocalizedMessage());
        }

        _collator = Collator.getInstance();
        _expectedText = expectedText;
        _textField = (Text) widget;
        _textTester = new TextTester();
    }

    /**
     * @see  com.windowtester.swt.condition.ICondition#test()
     */
    public boolean test() {
        String textValue = _textTester.getText(_textField);

        return _collator.equals(textValue, _expectedText);
    }

    @Override
    public String toString() {
        return " for text field to have value: " + _expectedText; //$NON-NLS-1$
    }

}
