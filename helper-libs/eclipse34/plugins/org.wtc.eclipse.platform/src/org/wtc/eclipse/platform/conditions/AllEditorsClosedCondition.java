/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.condition.ICondition;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;

/**
 * Condition that waits until there is no active editor open in the current workbench
 * page.
 */
public class AllEditorsClosedCondition implements ICondition {
    /**
     * @see  com.windowtester.runtime.condition.ICondition#test()
     */
    public boolean test() {
        // the condition is satisfied when there is no active editor
        IWorkbenchHelper helper = EclipseHelperFactory.getWorkbenchHelper();

        return (helper.getActiveEditor() == null);
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "all editors to be closed"; //$NON-NLS-1$
    }

}
