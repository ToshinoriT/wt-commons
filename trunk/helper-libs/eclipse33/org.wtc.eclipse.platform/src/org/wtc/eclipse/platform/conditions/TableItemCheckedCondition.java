/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import abbot.tester.swt.TableItemTester;
import abbot.tester.swt.TableTester;
import com.windowtester.runtime.condition.ICondition;
import junit.framework.TestCase;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import java.text.Collator;

/**
 * Condition to verify that the table item is checked.
 */
public class TableItemCheckedCondition implements ICondition {
    private final Table _source;
    private final String _itemText;

    private final TableItemTester _itemTester;
    private final TableTester _tableTester;

    /**
     * Save the data members.
     */
    public TableItemCheckedCondition(Table source, String itemText) {
        TestCase.assertNotNull(source);
        TestCase.assertNotNull(itemText);

        _source = source;
        _itemText = itemText;

        _itemTester = new TableItemTester();
        _tableTester = new TableTester();
    }

    /**
     * @see  com.windowtester.runtime2.condition.ICondition#test()
     */
    public boolean test() {
        boolean isSelected = false;

        TableItem[] items = _tableTester.getItems(_source);

        if (items != null) {
            for (TableItem nextItem : items) {
                String selectedText = _itemTester.getText(nextItem);

                if (Collator.getInstance().compare(selectedText, _itemText) == 0) {
                    isSelected = _itemTester.getChecked(nextItem);

                    break;
                }
            }
        }

        return isSelected;

    }
}
