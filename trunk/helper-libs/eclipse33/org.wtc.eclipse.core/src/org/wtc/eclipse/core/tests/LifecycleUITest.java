/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.tests;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.util.ScreenCapture;
import junit.extensions.UITestCaseSWT;
import junit.framework.TestCase;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.wtc.eclipse.core.CoreActivator;
import org.wtc.eclipse.core.internal.conditions.InitialConditionRegistry;
import org.wtc.eclipse.core.internal.preprocess.PreprocessorManager;
import org.wtc.eclipse.core.internal.reset.ResetDaemonRegistry;
import org.wtc.eclipse.core.reset.IResetDaemon;
import org.wtc.eclipse.core.reset.IResetDaemon.ResetContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ExtendedUITest - All UI Tests should extend this test. Container of context factories
 */
public abstract class LifecycleUITest extends UITestCaseSWT {
    static final String ONETIME_SETUP = "classSetUp"; //$NON-NLS-1$
    static final String ONETIME_TEARDOWN = "classTearDown"; //$NON-NLS-1$

    private String _displayName = null;

    private final List<String> _outputDirStack = new ArrayList<String>();

    protected LifecycleUITest() {
    }

    protected LifecycleUITest(String name) {
        super(name);
    }

    /**
     * classSetUp - Called once before the first test in this test class.
     */
    public void classSetUp() {
        // Subclasses should override this method if setup
        // that is run once at the class level is required
    }

    /**
     * classTearDown - Called once after the last test in this test class.
     */
    public void classTearDown() {
        // By default, reset the workspace
        resetWorkspace(getDisplayName());
    }

    /**
     * @return  String - A display name for this test or the fixed name if a display name
     *          was not set
     */
    public String getDisplayName() {
        return (_displayName == null) ? getName() : _displayName;
    }

    /**
     * returns a path, prefixed with start, containing the directories in the
     * outputDirStack.
     *
     * @param   start  the prefix for the output path.
     * @return  the output path
     */
    private IPath getOutputPath(IPath start) {
        IPath current = start;

        for (String s : _outputDirStack) {
            current = current.append(s);
        }

        return current;
    }

    /**
     * Used for existing tests written in Window Tester 1.6.1.
     *
     * @deprecated  - Use getUI() instead
     */
    protected com.windowtester.swt.IUIContext getUIContext() {
        IUIContext ui = getUI();
        Object oldUI = ui.getAdapter(com.windowtester.swt.IUIContext.class);
        TestCase.assertTrue(oldUI instanceof com.windowtester.swt.IUIContext);

        return (com.windowtester.swt.IUIContext) oldUI;
    }

    /**
     * methodSetUp - Called once before each test method in this test class. Subclasses
     * should override this method to initialize state required for each test method
     *
     * @throws  Exception  - Here because JUnit setUp throws Exception
     */
    protected void methodSetUp() throws Exception {
        // Subclasses should override this method if setup
        // that is run once at the method level is required
    }

    /**
     * methodTearDown - Called once after each test method in this test class. Subclasses
     * should override this method to reset state where other tests might be affected
     *
     * @throws  Exception  - Here because JUnit setUp throws Exception
     */
    protected void methodTearDown() throws Exception {
        // Subclasses should override this method if tear down
        // that is run once at the method level is required
    }

    /**
     * pops the output directory stack.  If the popped element does not match the expected
     * directory name, this test will fail.
     *
     * @param  expected  the expected directory name
     */
    public void popOutputDir(String expected) {
        int last = _outputDirStack.size() - 1;
        String name = _outputDirStack.get(last);

        if (expected.equals(name)) {
            _outputDirStack.remove(last);
        } else {
            fail("Unable to pop correct output Directory: " + name + " expected: " + expected); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    /**
     * Callback for hooks into lifecycle of test at the point just after test method set
     * up.
     */
    protected void postMethodSetUp() {
        // By default, do nothing. Most tests shouldn't won't use this method
    }

    /**
     * Callback for hooks into lifecycle of test at the point just after test method tear
     * down.
     */
    protected void postMethodTearDown() {
        // By default, do nothing. Most tests shouldn't won't use this method
    }

    /**
     * Callback for hooks into lifecycle of test at the point just prior to test method
     * set up.
     */
    protected void preMethodSetUp() {
        // By default, do nothing. Most tests shouldn't won't use this method
    }

    /**
     * Callback for hooks into lifecycle of test at the point just prior to test method
     * tear down.
     */
    protected void preMethodTearDown() {
        // By default, do nothing. Most tests shouldn't won't use this method
    }

    /**
     * pushes the given directory name onto the output stack.
     *
     * @param  name  the name of the directory to push.
     */
    public void pushOutputDir(String name) {
        _outputDirStack.add(name);
    }

    /**
     * Call all of the reset daemons and reset the workspace.
     */
    private void resetWorkspace(String testClassName) {
        List<IResetDaemon> resetDaemons = ResetDaemonRegistry.getResetDeamons();
        CoreActivator.logDebug("-CALLING (" + resetDaemons.size() + ") IResetDaemons"); //$NON-NLS-1$ //$NON-NLS-2$

        ResetContext context = new ResetContext(testClassName);
        IUIContext ui = getUI();

        for (IResetDaemon nextDaemon : resetDaemons) {
            CoreActivator.logDebug("-CALLING IResetDaemon :" + nextDaemon.getClass().getName()); //$NON-NLS-1$

            // If the reset daemon fails for some reason, log the exception and
            // continue
            try {
                nextDaemon.resetWorkspace(ui, context);
            }
            // NEVER DO THIS! EVER!
            catch (Throwable throwable) {
                ScreenCapture.createScreenCapture("RESET_DAEMON_FAILURE__" + nextDaemon.getClass().getCanonicalName()); //$NON-NLS-1$
                CoreActivator.logException(throwable);
            }
        }
    }

    /**
     * @param  name  - Set the display name for this test to be used in reset daemon
     *               processing
     */
    public void setDisplayName(String name) {
        _displayName = name;
    }

    /**
     * Called once prior to each test method in a TestCase.
     */
    @Override
    protected final void setUp() throws Exception {
        // Set the output location of the screenshots
        IPath installPath = new Path(Platform.getInstallLocation().getURL().getFile());
        IPath screenPath = installPath.append(new Path("wintest")); //$NON-NLS-1$

        IPath outputPath = getOutputPath(screenPath);
        File f = outputPath.toFile();

        if (!f.exists()) {
            f.mkdirs();
        }

        ScreenCapture.setOutputLocation(outputPath.toPortableString());

        String testName = getName();

        PreprocessorManager.runProcessorsIfNeeded();

        if (!ONETIME_SETUP.equals(testName) && !ONETIME_TEARDOWN.equals(testName)) {
            preMethodSetUp();
            methodSetUp();
            postMethodSetUp();

            // Now make sure any initial conditions are satisfied
            Collection<ICondition> initialConditions =
                InitialConditionRegistry.getInitialConditions();
            final IUIContext ui = getUI();

            for (final ICondition initialCondition : initialConditions) {
                CoreActivator.logDebug("WAITING ON INITIAL CONDITION: " + initialCondition.getClass().getName()); //$NON-NLS-1$
                ui.wait(new ICondition() {
                        public boolean test() {
                            ui.handleConditions();

                            return initialCondition.test();
                        }

                        @Override
                        public String toString() {
                            return " initialCondition <" //$NON-NLS-1$
                                + initialCondition.getClass().getName() + ">:" //$NON-NLS-1$
                                + initialCondition.toString();
                        }

                    }, 5000, 500);
            }
        }
    }

    /**
     * Called once after each test method in a TestCase.
     */
    @Override
    protected final void tearDown() throws Exception {
        String testName = getName();

        if (!ONETIME_SETUP.equals(testName) && !ONETIME_TEARDOWN.equals(testName)) {
            preMethodTearDown();
            methodTearDown();
            postMethodTearDown();
        }
    }
}
