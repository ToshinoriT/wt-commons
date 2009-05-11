/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.condition.ICondition;
import junit.framework.TestCase;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.wtc.eclipse.platform.helpers.IPerspective;

/**
 * Wait until the perspective is active in the active window.
 * 
 * @since 3.8.0
 */
public class PerspectiveActiveCondition implements ICondition {
    private IWorkbench _workbench;
    private IPerspective _perspective;

    /**
     * Save the data members.
     */
    public PerspectiveActiveCondition(IPerspective perspective) {
        _perspective = perspective;

        final IWorkbench[] workbench = new IWorkbench[1];
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    workbench[0] = PlatformUI.getWorkbench();
                }
            });

        TestCase.assertNotNull(workbench[0]);
        _workbench = workbench[0];
    }

    /**
     * @see  com.windowtester.swt.condition.ICondition#test()
     */
    public boolean test() {
        boolean isOpen = false;

        final IWorkbenchWindow[] window = new IWorkbenchWindow[1];
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    window[0] = _workbench.getActiveWorkbenchWindow();
                }
            });

        if (window[0] != null) {
            IWorkbenchPage page = window[0].getActivePage();

            if (page != null) {
                IPerspectiveDescriptor[] open = page.getOpenPerspectives();

                for (IPerspectiveDescriptor nextOpen : open) {
                    if (nextOpen.getId().equals(_perspective.getID())) {
                        isOpen = true;

                        break;
                    }
                }
            }
        }

        return isOpen;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " for perspective (" + _perspective.getID() + ") to be open and active"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
