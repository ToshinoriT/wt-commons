/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.tsb;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.wtc.eclipse.core.CoreActivator;
import org.wtc.eclipse.core.internal.tsb.model.ILogger;
import org.wtc.eclipse.core.internal.tsb.model.ModularModel.LoadState;
import org.wtc.eclipse.core.internal.tsb.model.TestModel;
import org.wtc.eclipse.core.internal.tsb.xml.RegistryDOMParseException;
import org.wtc.eclipse.core.internal.tsb.xml.RegistryDOMParser;
import org.wtc.eclipse.core.tests.ManagedTestSuite;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * TestSuite that loads tests from the testSuiteBuilder extension point.
 */
public class TestSuiteBuilder extends TestCase {
    // The shared instance
    private static TestSuiteBuilder _instance;

    // Describes the test model in detail
    private final TestSuiteModel _testModel;
    private final Set<String> _modulesToLoad;
    private final Set<String> _pluginsToLoad;
    private final Set<String> _testsToLoad;

    // About the environment
    private final boolean _isLinux;

    // How much information should be written to the exclusion report
    private final int _exclusionReportLevel;

    // CC-Specific, suite-level, properties
    private final String _cctimestamp;
    private final String _changelist;
    private final int _resetTrigger;

    // Let's use this to quickly look up test case info
    private Map<Test, TestModel> _testModelMap;

    /**
     * Build the test model.
     */
    public TestSuiteBuilder() {
        _testModelMap = new HashMap<Test, TestModel>();

        _modulesToLoad = getTSBModules();
        _pluginsToLoad = getTSBPlugins();
        _testsToLoad = getTSBTests();
        _isLinux = Platform.getOS().equals(Platform.OS_LINUX);
        int loops = getTSBLoops();
        _cctimestamp = getCCTimestampInternal();
        _changelist = getChangelistNumberInternal();
        _resetTrigger = getResetTriggerInternal();

        // First, parse the registry files and do some prefiltering
        _testModel = loadTestSuiteModel(_modulesToLoad,
                                        _pluginsToLoad,
                                        _testsToLoad,
                                        _isLinux,
                                        loops);

        // Now write the report of excluded tests
        _exclusionReportLevel = getTSBExclusionReportLevel();
    }

    /**
     * @return  String - Get the 'cctimestamp' property. This property will only be set by
     *          test runs on a CC machine
     */
    public static String getCCTimestamp() {
        return instance()._cctimestamp;
    }

    /**
     * @return  String - Get the 'cctimestamp' property. This property will only be set by
     *          test runs on a CC machine
     */
    private String getCCTimestampInternal() {
        String property = getProperty(ITestSuiteBuilderConstants.PROPERTY_CCTIMESTAMP);

        if ((property == null) || (property.length() == 0)) {
            property = "LOCAL-" + System.currentTimeMillis(); //$NON-NLS-1$
        }

        streamProperty(property,
                       ITestSuiteBuilderConstants.PROPERTY_CCTIMESTAMP);

        return property;
    }

    /**
     * @return  String - Get the latest changelist number used for this CC run. This
     *          property will only be set by test runs on a CC machine
     */
    public static String getChangelistNumber() {
        return instance()._changelist;
    }

    /**
     * @return  String - Get the latest changelist number used for this CC run. This
     *          property will only be set by test runs on a CC machine
     */
    private String getChangelistNumberInternal() {
        String clNumber = getProperty(ITestSuiteBuilderConstants.PROPERTY_CHANGELIST);
        streamProperty(clNumber,
                       ITestSuiteBuilderConstants.PROPERTY_CHANGELIST);

        return clNumber;
    }

    /**
     * Utility for getting a file given a plugin-relative path.
     */
    private File getFileFromBundle(Bundle bundle, String pluginRelativePath) {
        File file = null;

        URL fileDir = FileLocator.find(bundle, new Path(pluginRelativePath), null);

        if (fileDir != null) {
            try {
                URL localURL = FileLocator.toFileURL(fileDir);
                file = new File(localURL.getPath());
            } catch (Exception e) {
                CoreActivator.logException(e);
            }
        }

        return file;
    }

    /**
     * Get a system property that is expected to exist.
     *
     * @param   name  - Name of a JVM-aware system property to extract
     * @return  String - The property from the system or null if the property was not
     *          found
     */
    private String getProperty(String name) {
        String property = System.getProperty(name);

        if ((property == null) || (property.length() == 0)) {
            CoreActivator.logDebug("SYSTEM PROPERTY [" + name + "] WAS NOT FOUND", //$NON-NLS-1$ //$NON-NLS-2$
                                   ITestSuiteBuilderConstants.LOGGING_TESTSUITEMODEL);

            property = null;
        }

        return property;
    }

    /**
     * @return  int - The reset trigger or -1 if no reset trigger was defined. A reset
     *          trigger is the number that a test suite can set to indicate that all reset
     *          daemons with trigger value less then (non- inclusive) this given value can
     *          run while all other reset daemons should not be run. The reset trigger is
     *          intended for use with a collection of eclipse test runner invocations that
     *          run tests using data from a previous invocation. For example, a
     *          TestSuiteBuilder suite is run that creates a project then, in a separate
     *          launch of Eclipse, a TestSuiteBuilder suite depends on the project created
     *          from the first run. If no reset trigger is defined then all reset daemons
     *          should run.
     */
    public static int getResetTrigger() {
        return instance()._resetTrigger;
    }

    /**
     * Read the reset trigger property and parse the value.
     */
    private int getResetTriggerInternal() {
        int triggerValue = -1;
        String property = getProperty(ITestSuiteBuilderConstants.PROPERTY_RESET_TRIGGER);

        if ((property != null) && (property.length() > 0)) {
            try {
                triggerValue = Integer.parseInt(property);
            } catch (NumberFormatException nfe) { /* gulp! */
            }
        }

        streamProperty(Integer.toString(triggerValue), ITestSuiteBuilderConstants.PROPERTY_RESET_TRIGGER);

        return triggerValue;
    }

    /**
     * @return  TestModel - Find the registry info for the test instance
     */
    public static TestModel getTestModel(Test testInstance) {
        return instance()._testModelMap.get(testInstance);
    }

    /**
     * @return  TestSuiteModel - Get the model representation of a filtered test suite
     */
    public static TestSuiteModel getTestSuiteModel() {
        return instance()._testModel;
    }

    /**
     * @return  int - The level of detail to include in the exclusion report
     */
    private int getTSBExclusionReportLevel() {
        // ----------------------------------------------------------------
        // tsb.exclusionReport
        // ----------------------------------------------------------------
        int exclusionReport = 0;
        String exclusionReportLevelString = getProperty(ITestSuiteBuilderConstants.PROPERTY_EXCLUSIONREPORT);

        if (exclusionReportLevelString != null) {
            try {
                exclusionReport = Integer.parseInt(exclusionReportLevelString);

                if (exclusionReport > 0) {
                    exclusionReport = 2;
                }
            } catch (NumberFormatException nfe) {
                CoreActivator.logError(ITestSuiteBuilderConstants.PROPERTY_EXCLUSIONREPORT + " WAS SET WITH A NON INT VALUE: " + exclusionReportLevelString); //$NON-NLS-1$
            }
        }

        streamProperty(Integer.toString(exclusionReport), ITestSuiteBuilderConstants.PROPERTY_EXCLUSIONREPORT);

        return exclusionReport;
    }

    /**
     * @return  int - The number of loops of tests to load or <code>1</code> if the loops
     *          property was not given
     */
    private int getTSBLoops() {
        // ----------------------------------------------------------------
        // tsb.loops
        // ----------------------------------------------------------------
        int testLoops = 1;
        String loopsProperty = getProperty(ITestSuiteBuilderConstants.PROPERTY_LOOPS);

        if (loopsProperty != null) {
            try {
                testLoops = Integer.parseInt(loopsProperty);

                if (testLoops <= 0) {
                    testLoops = 1;
                }
            } catch (NumberFormatException nfe) {
                CoreActivator.logError(ITestSuiteBuilderConstants.PROPERTY_LOOPS + " WAS SET WITH A NON INT VALUE: " + loopsProperty); //$NON-NLS-1$
            }
        }

        streamProperty(Integer.toString(testLoops), ITestSuiteBuilderConstants.PROPERTY_LOOPS);

        return testLoops;
    }

    /**
     * @return  Set<String> - The list of modules to load or <code>null</code> if the
     *          modules property was not given
     */
    private Set<String> getTSBModules() {
        // ----------------------------------------------------------------
        // tsb.modules
        // ----------------------------------------------------------------
        Set<String> modulesToLoad = null;
        String modulesProperty = getProperty(ITestSuiteBuilderConstants.PROPERTY_MODULES);

        if (modulesProperty != null) {
            modulesToLoad = splitModules(modulesProperty);
        }

        streamProperties(modulesToLoad,
                         ITestSuiteBuilderConstants.PROPERTY_MODULES);

        return modulesToLoad;
    }

    /**
     * @return  Set<String> - The list of plugins to load or <code>null</code> if the
     *          plugins property was not given
     */
    private Set<String> getTSBPlugins() {
        // ----------------------------------------------------------------
        // tsb.plugins
        // ----------------------------------------------------------------
        Set<String> pluginsToLoad = null;
        String pluginsProperty = getProperty(ITestSuiteBuilderConstants.PROPERTY_PLUGINS_INCLUDE);

        if (pluginsProperty != null) {
            pluginsToLoad = splitModules(pluginsProperty);
        }

        streamProperties(pluginsToLoad,
                         ITestSuiteBuilderConstants.PROPERTY_PLUGINS_INCLUDE);

        return pluginsToLoad;
    }

    /**
     * @return  Set<String> - The list of tests to load or <code>null</code> if the tests
     *          property was not given
     */
    private Set<String> getTSBTests() {
        // ----------------------------------------------------------------
        // tsb.tests
        // ----------------------------------------------------------------
        Set<String> testsToLoad = null;
        String testsProperty = getProperty(ITestSuiteBuilderConstants.PROPERTY_TESTS);

        if (testsProperty != null) {
            testsToLoad = splitModules(testsProperty);
        }

        streamProperties(testsToLoad,
                         ITestSuiteBuilderConstants.PROPERTY_TESTS);

        return testsToLoad;
    }

    /**
     * @return  TestSuiteBuilder - The shared instance
     */
    private static TestSuiteBuilder instance() {
        if (_instance == null) {
            _instance = new TestSuiteBuilder();
        }

        return _instance;
    }

    /**
     * Loop through the extension points, parse the registry files, and build the test
     * tree.
     */
    private TestSuiteModel loadTestSuiteModel(Set<String> modulesToLoad,
                                              Set<String> pluginsToLoad,
                                              Set<String> testsToLoad,
                                              boolean isLinux,
                                              int loops) {
        TestSuiteModel model = new TestSuiteModel(modulesToLoad,
                                                  pluginsToLoad,
                                                  testsToLoad,
                                                  isLinux,
                                                  loops);

        ILogger pluginLogger = new ILogger() {
                public boolean isOptionEnabled(String optionKey) {
                    return CoreActivator.isOptionEnabled(optionKey);
                }

                public void logDebug(String message) {
                    CoreActivator.logDebug(message,
                                           ITestSuiteBuilderConstants.LOGGING_TESTSUITEMODEL);
                }

                public void logError(String message) {
                    CoreActivator.logError(message);
                }

                public void logException(Throwable exception) {
                    CoreActivator.logException(exception);
                }

                public void logWarning(String message) {
                    CoreActivator.logWarning(message);
                }

            };

        IExtensionRegistry extensionReg = Platform.getExtensionRegistry();
        IConfigurationElement[] tsbElements = extensionReg.getConfigurationElementsFor(ITestSuiteBuilderConstants.TESTSUITEBUILDER_EXTENSION_POINT_ID);

        if ((tsbElements == null) || (tsbElements.length == 0)) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("ERROR - NO TESTS WILL RUN BECAUSE NO PLUGINS "); //$NON-NLS-1$
            buffer.append("THAT EXTEND THE TEST SUITE BUILDER EXTENSION POINT <"); //$NON-NLS-1$
            buffer.append(ITestSuiteBuilderConstants.TESTSUITEBUILDER_EXTENSION_POINT_ID);
            buffer.append("> WERE FOUND"); //$NON-NLS-1$
            CoreActivator.logError(buffer.toString());

            return null;
        }

        // Loop over each tsbElement
        for (IConfigurationElement nextTSBElement : tsbElements) {
            if (nextTSBElement.getName().equals(ITestSuiteBuilderConstants.ELEMENT_TESTSUITEBUILDER)) {
                String bundleID = nextTSBElement.getNamespaceIdentifier();
                Bundle bundle = Platform.getBundle(bundleID);

                IConfigurationElement[] registryChildren = nextTSBElement.getChildren();

                for (IConfigurationElement nextRegElement : registryChildren) {
                    if (nextRegElement.getName().equals(ITestSuiteBuilderConstants.ELEMENT_REGISTRY)) {
                        String pathAttr = nextRegElement.getAttribute(ITestSuiteBuilderConstants.ELEMENTATTR_REGISTRY_PATH);

                        if ((pathAttr == null) || (pathAttr.trim().length() == 0)) {
                            StringBuilder buffer = new StringBuilder();
                            buffer.append("ERROR - THE BUNDLE <"); //$NON-NLS-1$
                            buffer.append(bundleID);
                            buffer.append("> DECLARED A TEST SUITE BUILDER EXTENSION REGISRTY ELEMENT <"); //$NON-NLS-1$
                            buffer.append(ITestSuiteBuilderConstants.ELEMENT_REGISTRY);
                            buffer.append("> THAT WAS MISSING THE REQUIRED ATTRIBUTE <"); //$NON-NLS-1$
                            buffer.append(ITestSuiteBuilderConstants.ELEMENTATTR_REGISTRY_PATH);
                            buffer.append(">: THE REGISTRY ENTRY WILL BE IGNORED"); //$NON-NLS-1$
                            CoreActivator.logError(buffer.toString());

                            continue;
                        }

                        File registryFile = getFileFromBundle(bundle, pathAttr.trim());

                        if ((registryFile == null) || (!registryFile.exists())) {
                            StringBuilder buffer = new StringBuilder();
                            buffer.append("ERROR - THE BUNDLE <"); //$NON-NLS-1$
                            buffer.append(bundleID);
                            buffer.append("> DECLARED A TEST SUITE BUILDER EXTENSION REGISRTY FILE <"); //$NON-NLS-1$
                            buffer.append(pathAttr);
                            buffer.append("> THAT COULD NOT BE FOUND: THE REGISTRY ENTRY WILL BE IGNORED"); //$NON-NLS-1$
                            CoreActivator.logError(buffer.toString());

                            continue;
                        }


                        try {
                            RegistryDOMParser domParser = new RegistryDOMParser(bundle,
                                                                                registryFile,
                                                                                pluginLogger);
                            model.addAllTests(domParser.getAllTests());
                        } catch (RegistryDOMParseException e) {
                            CoreActivator.logError(e.getLocalizedMessage());

                            continue;
                        } catch (Exception e) {
                            StringBuilder buffer = new StringBuilder();
                            buffer.append("ERROR - THE REGISTRY FILE <"); //$NON-NLS-1$
                            buffer.append(pathAttr);
                            buffer.append("> IN BUNDLE <"); //$NON-NLS-1$
                            buffer.append(bundleID);
                            buffer.append("> THAT COULD NOT BE PARSED BECAUSE OF AN UNEXPECTED EXCEPTION: THE REGISTRY ENTRY WILL BE IGNORED"); //$NON-NLS-1$
                            CoreActivator.logException(buffer.toString(), e);

                            continue;
                        }
                    }
                }
            }
        }

        return model;
    }

    /**
     * Simple utility to split a whitespace or comma separated list of test modules into a
     * Set of Strings.
     */
    private Set<String> splitModules(String moduleList) {
        HashSet<String> moduleSet = new HashSet<String>();

        StringTokenizer tokenizer = new StringTokenizer(moduleList, " ,:"); //$NON-NLS-1$

        while (tokenizer.hasMoreTokens()) {
            moduleSet.add(tokenizer.nextToken());
        }

        return moduleSet;
    }

    /**
     * Write the given list of plugin names to a fixed location. The properties file of
     * plugins loaded is used to make the JUnit results file split and formatted by plugin
     * name
     */
    private static void storePlugins(Collection<String> pluginNames) {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;

        for (String nextName : pluginNames) {
            if (!isFirst) {
                builder.append(","); //$NON-NLS-1$
            }

            builder.append(nextName);
            isFirst = false;
        }

        Properties properties = new Properties();
        properties.put(ITestSuiteBuilderConstants.HISTORY_PLUGINS_LIST, builder.toString());
        File file = new File(ITestSuiteBuilderConstants.TESTSUITEBUILDER_HISTORY_PROPERTIES_FILENAME);

        try {
            FileOutputStream out = new FileOutputStream(file);

            try {
                properties.store(out, "TestSuiteBuilder properties:"); //$NON-NLS-1$
            } finally {
                out.close();
            }
        } catch (IOException e) {
            CoreActivator.logException(e);
            TestCase.fail("Could not create a properties file for TestSuiteBuilder: " + e.getLocalizedMessage()); //$NON-NLS-1$
        }
    }

    /**
     * Stream the given properties to message service.
     *
     * @param  properties  - Set<String> to stream. Can be null
     * @param  header      - Header for the streamed set
     */
    private void streamProperties(Set<String> properties, String header) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("PROPERTIES FOR:"); //$NON-NLS-1$
        buffer.append(header);
        buffer.append(":"); //$NON-NLS-1$

        if (properties != null) {
            for (String nextProp : properties) {
                buffer.append("\n\t- "); //$NON-NLS-1$
                buffer.append(nextProp);
            }
        } else {
            buffer.append("\n - <null>"); //$NON-NLS-1$
        }

        CoreActivator.logDebug(buffer.toString());
    }

    /**
     * Stream the given properties to message service.
     *
     * @param  properties  - String to stream. Can be null
     * @param  header      - Header for the streamed set
     */
    private void streamProperty(String property, String header) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("PROPERTIES FOR:"); //$NON-NLS-1$
        buffer.append(header);
        buffer.append(":"); //$NON-NLS-1$

        if (property != null) {
            buffer.append(property);
        } else {
            buffer.append(" - <null>"); //$NON-NLS-1$
        }

        CoreActivator.logDebug(buffer.toString());
    }

    /**
     * Parse the extension point for the registered test classes, load them from the
     * extending bundles, and create suites for these tests.
     *
     * @return  Test - All registered tests that should run in the current target as a
     *          TestSuite
     */
    public static Test suite() {
        // Now that we've parsed the registry files and done some pre-filtering,
        //   get the list of tests
        TestSuiteModel model = instance()._testModel;
        TestSuite suite = model.suite();

        // Now write the report of excluded tests
        instance().writeExclusionReport();

        // Make the JUnit reporting prettier
        Set<String> loadedPlugins = model.getLoadedPlugins();
        storePlugins(loadedPlugins);

        return suite;
    }

    /**
     * Create an exclusion report in the current workspace location.
     */
    private void writeExclusionReport() {
        // Only write an exclusion report if we need to
        if (_exclusionReportLevel == 0) {
            CoreActivator.logDebug("NO EXLCUSION REPORT REQUESTED"); //$NON-NLS-1$

            return;
        }

        List<TestModel> excludedTests = _testModel.getExcludedTests();

        ExclusionNode root = new ExclusionNode(ITestSuiteBuilderConstants.NODE_ROOT);
        root.addAttribute(ITestSuiteBuilderConstants.NODEATTR_ROOT_GENERATED, new Date().toString());

        for (TestModel nextTest : excludedTests) {
            ExclusionNode testNode = new ExclusionNode(ITestSuiteBuilderConstants.NODE_TEST);
            testNode.addAttribute(ITestSuiteBuilderConstants.NODEATTR_TEST_NAME, nextTest.getTestClass().getName());
            testNode.addAttribute(ITestSuiteBuilderConstants.NODEATTR_TEST_REASONS, "failure"); //$NON-NLS-1$
            root.addChildNode(testNode);
        }

        IPath workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        IPath outputFileLocation = workspaceLocation.append(new Path(ITestSuiteBuilderConstants.EXCLUSION_FILE));
        File outputFile = outputFileLocation.toFile();

        CoreActivator.logDebug("WRITING EXCLUSION REPORT FILE TO <" + outputFile.getAbsolutePath() + "> "); //$NON-NLS-1$ //$NON-NLS-2$

        try {
            File parentDir = outputFile.getParentFile();
            parentDir.mkdirs();
            outputFile.createNewFile();

            FileWriter fileWriter = new FileWriter(outputFile);

            try {
                fileWriter.write(root.stream());
            } finally {
                fileWriter.close();
            }
        } catch (IOException ioe) {
            CoreActivator.logException(ioe);
        }
    }

    /**
     * Simple model object for a test runnable.
     */
    private abstract class AbstractNestedTestRunnable extends AbstractTestRunnable {
        // All of this runnable's children
        protected final List<AbstractTestRunnable> _children;

        /**
         * Save the data members.
         */
        public AbstractNestedTestRunnable(TestSuiteModel model, Class<?> runnableClass) {
            super(model, runnableClass);

            _children = new ArrayList<AbstractTestRunnable>();
        }

        /**
         * Add the given test child.
         */
        public void addTestChild(TestRunnable test) {
            _children.add(test);
        }

        /**
         * Build a suite from this item.
         */
        @Override
        public TestSuite suite() {
            TestSuite suite = new TestSuite();

            for (AbstractTestRunnable nextChild : _children) {
                suite.addTest(nextChild.suite());
            }

            return suite;
        }
    }

    /**
     * Simple model object for a test runnable.
     */
    private abstract class AbstractTestRunnable {
        protected final TestSuiteModel _model;
        protected final Class<?> _runnableClass;

        /**
         * Save the data member.
         */
        public AbstractTestRunnable(TestSuiteModel model, Class<?> runnableClass) {
            _model = model;
            _runnableClass = runnableClass;
        }

        /**
         * Build a suite.
         */
        public abstract TestSuite suite();
    }

    /**
     * Utility node for exclusion reports.
     */
    private class ExclusionNode {
        private final Map<String, String> _attributes;
        private final List<ExclusionNode> _children;
        private final String _name;

        /**
         * Save the data members.
         */
        public ExclusionNode(String name) {
            assert (name != null);
            _name = name;
            _attributes = new HashMap<String, String>();
            _children = new ArrayList<ExclusionNode>();
        }

        /**
         * Add an attribute.
         */
        public void addAttribute(String name, String value) {
            assert (name != null);
            assert (value != null);
            _attributes.put(name, value);
        }

        /**
         * Add a child node.
         */
        public void addChildNode(ExclusionNode child) {
            assert (child != null);
            _children.add(child);
        }

        /**
         * Stream the DOM.
         */
        public String stream() {
            return streamInternal(""); //$NON-NLS-1$
        }

        /**
         * Stream with indent.
         */
        private String streamInternal(String indent) {
            StringBuilder builder = new StringBuilder();
            builder.append(indent);
            builder.append("<"); //$NON-NLS-1$
            builder.append(_name);

            Set<String> attributeNames = _attributes.keySet();

            for (String nextName : attributeNames) {
                builder.append(" "); //$NON-NLS-1$
                builder.append(nextName);
                builder.append("=\""); //$NON-NLS-1$
                builder.append(_attributes.get(nextName));
                builder.append("\""); //$NON-NLS-1$
            }

            builder.append(">\n"); //$NON-NLS-1$

            String nextIndent = indent + "   "; //$NON-NLS-1$

            for (ExclusionNode nextChild : _children) {
                builder.append(nextChild.streamInternal(nextIndent));
            }

            builder.append(indent);
            builder.append("</"); //$NON-NLS-1$
            builder.append(_name);
            builder.append(">\n"); //$NON-NLS-1$

            return builder.toString();
        }
    }

    /**
     * Root of all tests.
     */
    private class TestRootRunable extends AbstractNestedTestRunnable {
        /**
         * Save the data members.
         */
        public TestRootRunable(TestSuiteModel model) {
            super(model, null);
        }

        /**
         * Build a suite from this item.
         */
        @Override
        public TestSuite suite() {
            TestSuite suite = new TestSuite();

            for (AbstractTestRunnable nextChild : _children) {
                suite.addTest(nextChild.suite());
            }

            return suite;
        }
    }

    /**
     * Simple model object for a test class runnable.
     */
    private class TestRunnable extends AbstractTestRunnable {
        // The registry description for this test
        private final TestModel _testModel;

        /**
         * Save the data members.
         */
        public TestRunnable(TestSuiteModel model, TestModel testModel) {
            super(model, testModel.getTestClass());

            _testModel = testModel;
        }

        /**
         * @return  List<TestCase> - List of tests only
         */
        private List<TestCase> getTestCases(TestSuite suite) {
            List<TestCase> testCases = new ArrayList<TestCase>();

            int totalTests = suite.testCount();

            for (int i = 0; i < totalTests; i++) {
                Test nextTest = suite.testAt(i);

                if (nextTest instanceof TestCase) {
                    testCases.add((TestCase) nextTest);
                } else if (nextTest instanceof TestSuite) {
                    testCases.addAll(getTestCases((TestSuite) nextTest));
                }
            }

            return testCases;
        }

        /**
         * @return  TestModel - The registry description for this test
         */
        public TestModel getTestModel() {
            return _testModel;
        }

        /**
         * Build a suite from this item.
         */
        @Override
        public TestSuite suite() {
            ManagedTestSuite suite = new ManagedTestSuite(_runnableClass);
            List<TestCase> testCases = getTestCases(suite);

            for (TestCase nextCase : testCases) {
                CoreActivator.logDebug(" ((( ADDING <" + nextCase.getClass().getCanonicalName() + "." + nextCase.getName() + ":" + nextCase.hashCode() + "> - " + _testModel.hashCode() + " )))"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                _testModelMap.put(nextCase, _testModel);
            }

            return suite;
        }
    }

    /**
     * Model of all of the known registry-declared tests.
     */
    public class TestSuiteModel {
        // All of the loaded declared tests
        private final List<TestModel> _tests;

        // All of the excluded tests mapped to whether or not
        //    the test should have run but was marked with
        //    a failure flag
        private final List<TestModel> _excludedTests;

        // Load criteria
        private final Set<String> _modules;
        private final Set<String> _testsToLoad;
        private final Set<String> _pluginsToLoad;
        private final boolean _isLinux;
        private final int _loops;

        // The root runnable
        private final TestRootRunable _root;

        /**
         * Initialize.
         */
        private TestSuiteModel(Set<String> modules,
                               Set<String> pluginsToLoad,
                               Set<String> testsToLoad,
                               boolean isLinux,
                               int loops) {
            _tests = new ArrayList<TestModel>();
            _excludedTests = new ArrayList<TestModel>();

            _modules = modules;
            _testsToLoad = testsToLoad;
            _pluginsToLoad = pluginsToLoad;
            _isLinux = isLinux;
            _loops = loops;

            _root = new TestRootRunable(this);
        }

        /**
         * Add tests to the list of known tests.
         */
        public void addAllTests(List<TestModel> tests) {
            for (TestModel nextTest : tests) {
                LoadState shouldLoad = nextTest.shouldLoad(_modules,
                                                           _testsToLoad,
                                                           _pluginsToLoad,
                                                           _isLinux);

                if (shouldLoad == LoadState.LOAD) {
                    _tests.add(nextTest);
                } else if ((shouldLoad == LoadState.DONT_LOAD_FAILURE) || (shouldLoad == LoadState.DONT_LOAD_NOLINUX)) {
                    _excludedTests.add(nextTest);
                }
            }
        }

        /**
         * @return  List<TestModel> - The models of the tests that are excluded
         */
        public List<TestModel> getExcludedTests() {
            return _excludedTests;
        }

        /**
         * @return  TestSuite - The tests that have been excluded from this run (ie - met
         *          the run criteria but were disabled)
         */
        public TestSuite getExcludedTestSuite() {
            TestRootRunable excludedSuite = new TestRootRunable(this);

            for (TestModel nextModel : _excludedTests) {
                TestRunnable test = new TestRunnable(this, nextModel);
                excludedSuite.addTestChild(test);
            }

            return excludedSuite.suite();
        }

        /**
         * @return  Set<String> - The plugin IDs of all the test plugins loaded given the
         *          known filter criteria
         */
        public Set<String> getLoadedPlugins() {
            Set<String> plugins = new HashSet<String>();

            for (TestModel nextTest : _tests) {
                String nextPluginName = nextTest.getPluginName();
                plugins.add(nextPluginName);
            }

            return plugins;
        }

        /**
         * @return  Set<String> - The modules argument given to this test run
         */
        public Set<String> getModules() {
            return _modules;
        }

        /**
         * Build a test suite.
         */
        public TestSuite suite() {
            for (int i = 0; i < _loops; i++) {
                for (TestModel nextTest : _tests) {
                    TestRunnable test = new TestRunnable(this, nextTest);
                    _root.addTestChild(test);
                } //endfor _tests
            } //endfor _loops

            return _root.suite();
        }
    } //end TestSuiteModel

}
