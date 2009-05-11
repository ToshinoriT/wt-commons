/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import abbot.tester.swt.TreeItemTester;
import com.windowtester.runtime.condition.ICondition;
import junit.framework.TestCase;
import org.eclipse.swt.widgets.TreeItem;

/**
 * TreeItemCheckedCondition - Condition that tests whether or not a given TreeItem's
 * checked state is equal to the given expected state.
 * 
 * @since 3.8.0
 */
public class TreeItemCheckedCondition implements ICondition {
    private TreeItem _treeItem;
    private boolean _checked;
    private TreeItemTester _treeItemTester;

    /**
     * TreeItemCheckedCondition.
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  treeKey       - The key to which the key is registered with in the given UI
     *                       context
     * @param  treeItemPath  - The path to the tree item delimited by "/." For example,
     *                       "Grand Parent/Parent/Child"
     * @param  checked       - True if the TreeItem should be checked
     */
    public TreeItemCheckedCondition(TreeItem treeItem,
                                    boolean checked) {
        TestCase.assertNotNull(treeItem);

        _treeItem = treeItem;
        _checked = checked;
        _treeItemTester = new TreeItemTester();
    }

    /**
     * test.
     *
     * @return  boolean - True if the TreeItem's checked state is equal to the expected
     *          checked state; False otherwise
     */
    public boolean test() {
        return _treeItemTester.getChecked(_treeItem) == _checked;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String text = _treeItemTester.getText(_treeItem);

        return text + " to be checked: " + _checked; //$NON-NLS-1$
    }
}
