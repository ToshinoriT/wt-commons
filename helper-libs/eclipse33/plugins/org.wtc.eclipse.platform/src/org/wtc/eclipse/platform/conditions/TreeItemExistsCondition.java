/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import abbot.tester.swt.TreeTester;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.util.StringComparator;
import com.windowtester.tester.swt.TreeItemTester;
import junit.framework.TestCase;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Condition that searches the given tree for a tree item. The item to search for is the
 * item text where sub-items are found by delimiting the tree item path with forward
 * slashes
 * 
 * @since 3.8.0
 */
public class TreeItemExistsCondition implements ICondition {
    private Tree _tree;
    private String _treeItemPath;
    private boolean _exists;

    private TreeTester _treeTester;
    private TreeItemTester _treeItemTester;

    /**
     * Save the data members.
     *
     * @param  tree          - The source tree to search
     * @param  treeItemPath  - Forward slash delimited path to the tree item to check for
     *                       existence
     * @param  exists        - True if the tree item should exist for this condition to be
     *                       met; false if the tree item should not exist for this
     *                       condition to be met
     */
    public TreeItemExistsCondition(Tree tree, String treeItemPath, boolean exists) {
        TestCase.assertNotNull(tree);
        TestCase.assertNotNull(treeItemPath);

        _tree = tree;
        _treeItemPath = treeItemPath;
        _exists = exists;

        _treeTester = new TreeTester();
        _treeItemTester = new TreeItemTester();
    }

    /**
     * recursion utility for searching for tree items.
     */
    private boolean searchTreeItems(TreeItem[] items, String itemPath) {
        boolean found = false;

        if ((items != null) && (items.length > 0) && (itemPath != null) && (itemPath.length() > 0)) {
            String searchString = itemPath;
            String nextPath = null;

            int index = itemPath.indexOf("/"); //$NON-NLS-1$

            if (index > 0) {
                searchString = itemPath.substring(0, index);
                nextPath = itemPath.substring(index + 1);
            }

            for (TreeItem nextItem : items) {
                String itemText = _treeItemTester.getText(nextItem);

                if (textMatches(itemText, searchString)) {
                    if ((nextPath != null) && (nextPath.length() > 0)) {
                        TreeItem[] subItems = _treeItemTester.getItems(nextItem);
                        found = searchTreeItems(subItems, nextPath);
                    } else {
                        found = true;

                        break;
                    }
                }
            }
        }

        return found;
    }

	private boolean textMatches(String itemText, String searchString) {
		return StringComparator.matches(itemText, searchString);
	}

    /**
     * @see  com.windowtester.runtime2.condition.ICondition#test()
     */
    public boolean test() {
        TreeItem[] topLevel = _treeTester.getItems(_tree);
        boolean found = searchTreeItems(topLevel, _treeItemPath);

        return (found == _exists);
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " FOR THE TREE ITEM <" + _treeItemPath + "> TO HAVE EXISTENCE <" + _exists + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
