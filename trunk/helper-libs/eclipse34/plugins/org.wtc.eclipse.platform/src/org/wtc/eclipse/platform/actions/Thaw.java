/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IUIHelper;

/**
 * Reverse the effects of an IUIHelper.freeze().
 */
public class Thaw implements IWorkbenchWindowActionDelegate {
    /**
     * Not used.
     *
     * @see  org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
    }

    /**
     * Not used.
     *
     * @see  org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
    }

    /**
     * @see  org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        IUIHelper uiHelper = EclipseHelperFactory.getUIHelper();
        uiHelper.thaw();
    }

    /**
     * Not used.
     *
     * @see  org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *       org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

}
