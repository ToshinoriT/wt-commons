/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests;

import com.windowtester.internal.runtime.condition.ConditionMonitor;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IConditionHandler;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.wtc.eclipse.core.tests.LifecycleUITest;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.internal.handlers.DefaultConditionHandlerRegistry;
import org.wtc.eclipse.platform.internal.shellhandlers.DefaultShellHandlersRegistry;
import org.wtc.eclipse.platform.shellhandlers.AbstractShellHandler;
import java.util.Collection;

/**
 * ExtendedUITest - All UI Tests should extend this test. Container of context factories
 */
public abstract class EclipseUITest extends LifecycleUITest {
    /**
     * (boolean) Whether or not the console view is shown when there is program output.
     */
    public static final String CONSOLE_OPEN_ON_OUT = "DEBUG.consoleOpenOnOut"; //$NON-NLS-1$

    /** (boolean) Whether or not the console view is shown when there is program error. */
    public static final String CONSOLE_OPEN_ON_ERR = "DEBUG.consoleOpenOnErr"; //$NON-NLS-1$

    protected EclipseUITest() {
    }

    protected EclipseUITest(String name) {
        super(name);
    }

    /**
     * classSetUp - Called once before the first test in this test class.
     */
    @Override
    public void classSetUp() {
        super.classSetUp();

        IUIContext ui = getUI();
        registerDefaultHandlers(ui);

        // Bring to front
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.bringToFront(ui);

        // full screen
        workbench.maximizeWorkbench(ui);
        workbench.closeWelcomePage(ui);
    }

    /**
     * methodSetUp - Called once before each test method in this test class. Subclasses
     * should override this method to initialize state required for each test method
     *
     * @throws  Exception  - Here because JUnit setUp throws Exception
     */
    @Override
    protected void methodSetUp() throws Exception {
        super.methodSetUp();

        IUIContext ui = getUI();

        registerDefaultHandlers(ui);

        // Bring to front
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.bringToFront(ui);

        // full screen
        workbench.maximizeWorkbench(ui);
        workbench.closeWelcomePage(ui);

        // Don't let the console take over
        IPreferenceStore store = DebugUIPlugin.getDefault().getPreferenceStore();
        store.setValue(CONSOLE_OPEN_ON_OUT, false);
        store.setValue(CONSOLE_OPEN_ON_ERR, false);
    }

    /**
     * methodSetUp - Called once before each test method in this test class. Subclasses
     * should override this method to initialize state required for each test method
     *
     * @throws  Exception  - Here because JUnit setUp throws Exception
     */
    @Override
    protected void methodTearDown() throws Exception {
        super.methodTearDown();

        IUIContext ui = getUI();

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.saveAndWait(ui);

        // Reset the default handlers
        workbench.stopListeningForAllDialogs(ui);
        registerDefaultHandlers(ui);
    }

    /**
     * We do NOT want any test to skip the required workspace-specific test initialization.
     *
     * @see  junit.extensions.UITestCase#oneTimeSetup()
     */
    @Override
    public final void oneTimeSetup() {
        // DON'T OVERRIDE OR REMOVE THIS!
    }

    /**
     * We do NOT want any test to skip the required workspace-specific test deconstruction.
     *
     * @see  junit.extensions.UITestCase#oneTimeTearDown()
     */
    @Override
    public final void oneTimeTearDown() {
        // DON'T OVERRIDE OR REMOVE THIS!
    }

    /**
     * register the default shell handlers.
     */
    protected void registerDefaultHandlers(IUIContext ui) {
        // Add the default condition handlers
        Collection<IConditionHandler> defaultHandlers =
            DefaultConditionHandlerRegistry.getDefaultConditionHandlers();

        for (IConditionHandler nextHandler : defaultHandlers) {
            ConditionMonitor.getInstance().add(nextHandler);
        }

        // ------------------------------------------------
        // THESE MUST BE ADDED **AFTER** THE FOCUS FIXING HANDLER
        // ------------------------------------------------
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.listenForDialogFileContentChanged(ui);
        workbench.listenForDialogLicenceAgreement(ui);
        workbench.listenForDialogOpenPerspective(ui);
        workbench.listenForDialogProgress(ui);
        workbench.listenForDialogRenameCompilationUnit(ui);
        workbench.listenForDialogSaveAllModifiedResources(ui);
        workbench.listenForDialogSaveResource(ui);
        workbench.listenForDialogSaveResources(ui);
        workbench.listenForDialogWizardClosing(ui);
        workbench.listenForDialogRebuilding(ui);

        Collection<AbstractShellHandler> defaultShellHandlers =
            DefaultShellHandlersRegistry.getDefaultShellHandlers(ui);

        for (AbstractShellHandler nextShellHandler : defaultShellHandlers) {
            workbench.listenForDialog(ui, nextShellHandler);
        }
    }
}
