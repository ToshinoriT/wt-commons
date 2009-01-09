/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.helpers.impl;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import junit.framework.TestCase;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IJavaProjectHelper;
import org.wtc.eclipse.platform.helpers.IProjectHelperConstants;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;

/**
 * Helper for creating and manipulating java projects.
 */
public class JavaProjectHelperImpl extends ProjectHelperImplAdapter implements IJavaProjectHelper {
    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaProjectHelper#createProject(com.windowtester.runtime.IUIContext,
     *       java.lang.String)
     */
    public void createProject(IUIContext ui,
                              String projectName) {
        logEntry2(projectName);
        createProject(ui, projectName, false);
        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaProjectHelper#createProject(com.windowtester.runtime.IUIContext,
     *       java.lang.String, boolean)
     */
    public void createProject(IUIContext ui,
                              String projectName,
                              boolean separateSourceAndOutput) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(projectName);

        logEntry2(projectName, Boolean.toString(separateSourceAndOutput));

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.listenForDialogOpenPerspective(ui);

        try {
            selectFileMenuItem(ui, IProjectHelperConstants.MENU_NEWPROJECT);
            ui.wait(new ShellShowingCondition("New Project")); //$NON-NLS-1$

            ui.click(new FilteredTreeItemLocator("Java/Java Project")); //$NON-NLS-1$
            clickNext(ui);

            IWidgetLocator textLoc = new LabeledTextLocator("&Project name:"); //$NON-NLS-1$
            safeEnterText(ui, textLoc, projectName);

            if (separateSourceAndOutput) {
                ui.click(new ButtonLocator("&Create separate folders for sources and class files")); //$NON-NLS-1$
            } else {
                ui.click(new ButtonLocator("&Use project folder as root for sources and class files")); //$NON-NLS-1$
            }

            clickFinish(ui);

            //wait for the project creation dialog to be dismissed
            ui.wait(new ShellDisposedCondition("New Java Project")); //$NON-NLS-1$

            waitForProjectExists(ui, projectName, true);
            workbench.waitNoResourceChangeEvents(ui);
        } catch (WidgetSearchException wse) {
            PlatformActivator.logException(wse);
            TestCase.fail(wse.getLocalizedMessage());
        }

        workbench.waitNoJobs(ui);

        logExit2();
    }
}
