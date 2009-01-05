/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.wtc.eclipse.core.CoreActivator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Suite for running tests and manages the calls to oneTimeSetUp and
 * oneTimeTearDownMethods.
 */
public class ManagedTestSuite extends TestSuite {
    private Class<?> _theClass;

    /**
     * Add the tests.
     */
    public ManagedTestSuite(Class<?> theClass) {
        _theClass = theClass;

        addTest(getTest(theClass));
        super.setName(theClass.getSimpleName());
    }

    /**
     * @return  boolean - True if the given class has a suite method
     */
    private Method getSuiteMethod(Class<?> theClass) {
        Method suiteMethod = null;

        try {
            suiteMethod = theClass.getMethod("suite", new Class[0]); //$NON-NLS-1$
        } catch (NoSuchMethodException nsme) {
            // Do nothing
        }

        return suiteMethod;
    }

    /**
     * @return  Test - Make sure to use nested suite methods
     */
    private Test getTest(final Class testClass) {
        try {
            // if test has a suite method use it
            Method suiteMethod = getSuiteMethod(testClass);

            if (suiteMethod != null) {
                Test test = (Test) suiteMethod.invoke(null, new Object[0]);

                return test;
            }

            return new TestSuite(testClass);
        } catch (InvocationTargetException ite) {
            String msg = "FAILED TO GET JUnit TEST FOR (" + testClass.getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            CoreActivator.logException(msg, ite);
            throw new RuntimeException(msg, ite);
        } catch (IllegalAccessException iae) {
            String msg = "FAILED TO GET JUnit TEST FOR (" + testClass.getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            CoreActivator.logException(msg, iae);
            throw new RuntimeException(msg, iae);
        }
    }

    /**
     * @see  junit.framework.Test#run(junit.framework.TestResult)
     */
    @Override
    public void run(TestResult result) {
        StringBuilder starting = new StringBuilder();
        starting.append("\n\n---------------------------------------------------\n"); //$NON-NLS-1$
        starting.append("STARTING TEST: "); //$NON-NLS-1$
        starting.append(_theClass.getName());
        starting.append("\n---------------------------------------------------\n"); //$NON-NLS-1$
        CoreActivator.logDebug(starting.toString());

        boolean runOneTimers = LifecycleUITest.class.isAssignableFrom(_theClass) && (getSuiteMethod(_theClass) == null);

        if (runOneTimers) {
            LifecycleUITest setUp = (LifecycleUITest) createTest(_theClass, LifecycleUITest.ONETIME_SETUP);
            setUp.setDisplayName(_theClass.getName());
            TestResult oneTimeResult = new TestResult();
            super.runTest(setUp, oneTimeResult);
        }

        super.run(result);

        if (runOneTimers) {
            LifecycleUITest tearDown = (LifecycleUITest) createTest(_theClass, LifecycleUITest.ONETIME_TEARDOWN);
            tearDown.setDisplayName(_theClass.getName());
            super.runTest(tearDown, new TestResult());
        }

        StringBuilder finished = new StringBuilder();
        finished.append("\n\n---------------------------------------------------\n"); //$NON-NLS-1$
        finished.append("ENDING TEST: "); //$NON-NLS-1$
        finished.append(_theClass.getName());
        finished.append("\n---------------------------------------------------\n"); //$NON-NLS-1$
        CoreActivator.logDebug(finished.toString());
    }
}
