/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.tsb;

/**
 * Command line and registry file constants used for parsing tests into loadable modules.
 */
public interface ITestSuiteBuilderConstants {
    // ------------------------------------------------------------------------
    // COMMAND LINE ARGUMENTS
    // ------------------------------------------------------------------------
    public static final String PROPERTY_EXCLUSIONREPORT = "tsb.exclusionReport"; //$NON-NLS-1$
    public static final String PROPERTY_MODULES = "tsb.modules"; //$NON-NLS-1$
    public static final String PROPERTY_PLUGINS_INCLUDE = "tsb.plugins"; //$NON-NLS-1$
    public static final String PROPERTY_TESTS = "tsb.tests"; //$NON-NLS-1$
    public static final String PROPERTY_LOOPS = "tsb.loops"; //$NON-NLS-1$

    // ------------------------------------------------------------------------
    // CC-SPECIFIC PROPERTY VALUES
    // ------------------------------------------------------------------------
    public static final String PROPERTY_CCTIMESTAMP = "tsb.cctimestamp"; //$NON-NLS-1$
    public static final String PROPERTY_CHANGELIST = "tsb.changelist"; //$NON-NLS-1$
    public static final String PROPERTY_RESET_TRIGGER = "tsb.reset.trigger"; //$NON-NLS-1$

    // ------------------------------------------------------------------------
    // DEFAULT PROPERTY VALUES
    // ------------------------------------------------------------------------
    public static String NULL_PROPERTY_VALUE = "<null>"; //$NON-NLS-1$

    // ------------------------------------------------------------------------
    // FIXED MODULE FILTERS
    // ------------------------------------------------------------------------
    public static String MODULE_FAILURE = "failure"; //$NON-NLS-1$
    public static String MODULE_NOLINUX = "nolinux"; //$NON-NLS-1$

    // ------------------------------------------------------------------------
    // PROPERTIES FILE USED TO SPLIT THE JUNIT RESULTS INTO PLUGINS
    // ------------------------------------------------------------------------
    public static final String TESTSUITEBUILDER_HISTORY_PROPERTIES_FILENAME =
        "TestSuiteBuilderHistory.properties"; //$NON-NLS-1$
    public static final String HISTORY_PLUGINS_LIST = "tsb.history.plugins"; //$NON-NLS-1$

    // ------------------------------------------------------------------------
    // EXTENSION POINT ELEMENTS
    // ------------------------------------------------------------------------
    public static final String TESTSUITEBUILDER_EXTENSION_POINT_ID =
        "org.wtc.eclipse.core.testSuiteBuilder"; //$NON-NLS-1$
    public static final String ELEMENT_TESTSUITEBUILDER = "testSuiteBuilder"; //$NON-NLS-1$
    public static final String ELEMENT_REGISTRY = "registry"; //$NON-NLS-1$
    public static final String ELEMENTATTR_REGISTRY_PATH = "path"; //$NON-NLS-1$

    // --------------
    public static final String ELEMENT_PLUGIN = "plugin"; //$NON-NLS-1$
    public static final String ELEMENTATTR_PLUGIN_ID = "id"; //$NON-NLS-1$

    // --------------
    public static final String TESTCOUNT_EXTENSION_POINT_ID =
        "org.wtc.eclipse.core.testCountVerification"; //$NON-NLS-1$
    public static final String ELEMENT_TESTCOUNT = "testCount"; //$NON-NLS-1$
    public static final String ELEMENTATTR_MODULE = "module"; //$NON-NLS-1$
    public static final String ELEMENTATTR_COUNT = "count"; //$NON-NLS-1$

    // ------------------------------------------------------------------------
    // EXCLUSION REPORT ELEMENTS
    // ------------------------------------------------------------------------
    public static final String EXCLUSION_FILE = "results/excludedTests.xml"; //$NON-NLS-1$
    public static final String NODE_ROOT = "excludedTests"; //$NON-NLS-1$
    public static final String NODEATTR_ROOT_GENERATED = "generated"; //$NON-NLS-1$
    public static final String NODE_TEST = "test"; //$NON-NLS-1$
    public static final String NODEATTR_TEST_NAME = "name"; //$NON-NLS-1$
    public static final String NODEATTR_TEST_REASONS = "reasons"; //$NON-NLS-1$

    // ------------------------------------------------------------------------
    // LOGGING OPTIONS
    // ------------------------------------------------------------------------
    public static final String LOGGING_TESTSUITEMODEL = "/logging/testsuitemodel"; //$NON-NLS-1$
}
