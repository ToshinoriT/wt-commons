/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.preprocess;

/**
 * Called one per TestRunner invocation (i.e.- once and only once per root level
 * TestSuite) a preprocessor should be very rarely used (the other setup methods should be
 * able to handle all other test setup). The preprocessor is equivalent to a suiteSetUp
 * call.
 */
public interface IPreprocessor {
    /**
     * A test suite is about to run and preprocessing logic should be executed. Since this
     * method will be called prior to any methodSetUp, the author or the preprocessor must
     * not make any assumptions that are otherwise handled in test methodSetUp calls
     */
    public void run();
}
