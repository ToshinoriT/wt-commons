/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.internal.tsb.xml;

import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wtc.eclipse.core.internal.tsb.model.ILogger;
import org.wtc.eclipse.core.internal.tsb.model.ModularModel;
import org.wtc.eclipse.core.internal.tsb.model.TestModel;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Class that handles the parsing of DOMs following the testproject.xsd schema.
 */
public class RegistryDOMParser {
    // TRACING OPTION
    private static final String DEBUG_DOM = "/logging/webxml/dom"; //$NON-NLS-1$

    // -----------------------------------------------------------
    // ELEMENTS IN THE SCHEMA
    // -----------------------------------------------------------
    private static final String ELEMENT_MODULE = "module"; //$NON-NLS-1$
    private static final String ELEMENT_TEST = "test"; //$NON-NLS-1$
    public static final String ELEMENT_TESTPROJECT = "test-project"; //$NON-NLS-1$

    // -----------------------------------------------------------
    // ATTRIBUTES IN THE SCHEMA
    // -----------------------------------------------------------
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
    private static final String ATTR_ID = "id"; //$NON-NLS-1$
    private static final String ATTR_OWNER = "owner"; //$NON-NLS-1$

    // The parsed tests
    private final Set<TestModel> _testDefinitions;

    /**
     * Parse the registry file without loading classes.
     */
    public RegistryDOMParser(File inputFile, ILogger logger)
                      throws ParserConfigurationException, IOException, RegistryDOMParseException,
                             SAXException {
        this(null, inputFile, logger);
    }

    /**
     * Parse the given file into a test suite builder model.
     */
    public RegistryDOMParser(Bundle parentBundle,
                             File registryFile,
                             final ILogger logger)
                      throws ParserConfigurationException, IOException, RegistryDOMParseException,
                             SAXException {
        _testDefinitions = new TreeSet<TestModel>(new TestModelComparator());

        DocumentBuilder builder = getDocumentBuilder(false);
        builder.setErrorHandler(new ErrorHandler() {
                public void warning(SAXParseException spe) throws SAXException {
                    handleParseException(logger, spe);
                }

                public void error(SAXParseException spe) throws SAXException {
                    handleParseException(logger, spe);
                }

                public void fatalError(SAXParseException spe) throws SAXException {
                    handleParseException(logger, spe);
                    throw spe;
                }

            });

        Document resultsDocument = null;
        FileInputStream fileContents = new FileInputStream(registryFile);

        try {
            String baseReference = registryFile.getAbsolutePath();
            resultsDocument = builder.parse(fileContents, baseReference);
        } finally {
            fileContents.close();
        }

        // ------- document -> test-project -------------- //
        Element testProjectElement = resultsDocument.getDocumentElement();

        if ((testProjectElement == null) || (!testProjectElement.getNodeName().equals(ELEMENT_TESTPROJECT))) {
            StringBuilder buffer = new StringBuilder();
            buffer.append("ERROR - COULD NOT PARSE REGISTRY DEFINITION FILE <"); //$NON-NLS-1$
            buffer.append(registryFile.getAbsolutePath());
            buffer.append(">: THE FILE DOES NOT CONTAIN THE DOCUMENT <"); //$NON-NLS-1$
            buffer.append(ELEMENT_TESTPROJECT);
            buffer.append(">"); //$NON-NLS-1$
            throw new RegistryDOMParseException(buffer.toString());
        }

        // ------- test-project -> test* -------------- //
        NodeList testNodeList = testProjectElement.getElementsByTagName(ELEMENT_TEST);

        if ((testNodeList != null) && (testNodeList.getLength() > 0)) {
            int testsLength = testNodeList.getLength();

            for (int i = 0; i < testsLength; i++) {
                Element nextTestElement = (Element) testNodeList.item(i);

                // ------- test -> @class -------------- //
                String testClass = nextTestElement.getAttribute(ATTR_CLASS);

                if ((testClass == null) || (testClass.trim().length() == 0)) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("WARNING - A TEST DEFINITION <"); //$NON-NLS-1$
                    buffer.append(ELEMENT_TEST);
                    buffer.append("> IN REGISTRY DEFINITION FILE <"); //$NON-NLS-1$
                    buffer.append(registryFile.getAbsolutePath());

                    if (parentBundle != null) {
                        buffer.append("> IN BUNDLE <"); //$NON-NLS-1$
                        buffer.append(parentBundle.getSymbolicName());
                    }

                    buffer.append(" >DID NOT DEFINE A CLASS ATTRIBUTE <"); //$NON-NLS-1$
                    buffer.append(ATTR_CLASS);
                    buffer.append(">: THE TEST DEFINITION WILL BE IGNORED"); //$NON-NLS-1$
                    logger.logWarning(buffer.toString());

                    continue;
                }

                // ------- test -> @owner -------------- //
                String testOwner = nextTestElement.getAttribute(ATTR_OWNER);

                if ((testOwner == null) || (testOwner.trim().length() == 0)) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("WARNING - A TEST DEFINITION <"); //$NON-NLS-1$
                    buffer.append(ELEMENT_TEST);
                    buffer.append("> IN REGISTRY DEFINITION FILE <"); //$NON-NLS-1$
                    buffer.append(registryFile.getAbsolutePath());

                    if (parentBundle != null) {
                        buffer.append("> IN BUNDLE <"); //$NON-NLS-1$
                        buffer.append(parentBundle.getSymbolicName());
                    }

                    buffer.append(" >DID NOT DEFINE AN OWNER ATTRIBUTE <"); //$NON-NLS-1$
                    buffer.append(ATTR_OWNER);
                    buffer.append(">: THE OWNER WILL BE SET TO \"unknown\""); //$NON-NLS-1$
                    logger.logDebug(buffer.toString());

                    testOwner = "unknown"; //$NON-NLS-1$
                }

                Class<?> loadedTestClass = null;

                if (parentBundle != null) {
                    try {
                        loadedTestClass = parentBundle.loadClass(testClass.trim());
                    } catch (ClassNotFoundException cnfe) {
                        StringBuilder buffer = new StringBuilder();
                        buffer.append("ERROR - THE TEST DEFINITION <"); //$NON-NLS-1$
                        buffer.append(ELEMENT_TEST);
                        buffer.append("> IN REGISTRY DEFINITION FILE <"); //$NON-NLS-1$
                        buffer.append(registryFile.getAbsolutePath());
                        buffer.append("> DEFINED A TEST CLASS <"); //$NON-NLS-1$
                        buffer.append(testClass);
                        buffer.append("> THAT WAS NOT FOUND ON THE CLASSPATH OF THE BUNDLE <"); //$NON-NLS-1$
                        buffer.append(parentBundle.getSymbolicName());
                        buffer.append(">: THE TEST DEFINITION WILL BE IGNORED"); //$NON-NLS-1$
                        logger.logError(buffer.toString());

                        continue;
                    }
                }

                // ------- test -> module+ -------------- //
                Set<String> testModules = getModuleSet(nextTestElement);

                if (testModules.isEmpty()) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("WARNING - THE TEST DEFINITION <"); //$NON-NLS-1$
                    buffer.append(testClass);
                    buffer.append("> IN REGISTRY DEFINITION FILE <"); //$NON-NLS-1$
                    buffer.append(registryFile.getAbsolutePath());

                    if (parentBundle != null) {
                        buffer.append("> IN BUNDLE <"); //$NON-NLS-1$
                        buffer.append(parentBundle.getSymbolicName());
                    }

                    buffer.append("> DID NOT DEFINE ANY MODULES <"); //$NON-NLS-1$
                    buffer.append(ELEMENT_MODULE);
                    buffer.append(">: THE TEST DEFINITION WILL BE IGNORED"); //$NON-NLS-1$
                    logger.logWarning(buffer.toString());

                    continue;
                }

                StringBuilder buffer = new StringBuilder();
                buffer.append("INFO - THE TEST DEFINITION <"); //$NON-NLS-1$
                buffer.append(testClass);
                buffer.append("> IN REGISTRY DEFINITION FILE <"); //$NON-NLS-1$
                buffer.append(registryFile.getAbsolutePath());

                if (parentBundle != null) {
                    buffer.append("> IN BUNDLE <"); //$NON-NLS-1$
                    buffer.append(parentBundle.getSymbolicName());
                }

                buffer.append("> SUCCESSFULLY LOADED WITH MODULES <"); //$NON-NLS-1$
                boolean isFirst = true;

                for (String nextModuleString : testModules) {
                    if (!isFirst) {
                        buffer.append("  "); //$NON-NLS-1$
                    }

                    buffer.append(nextModuleString);
                    isFirst = false;
                }

                buffer.append(">"); //$NON-NLS-1$
                logger.logDebug(buffer.toString());

                TestModel testModel = null;

                if (parentBundle != null) {
                    testModel = new TestModel(loadedTestClass, parentBundle.getSymbolicName(), testModules, testOwner);
                } else {
                    testModel = new TestModel(testClass.trim(), testModules, testOwner);
                }

                _testDefinitions.add(testModel);

            } //endfor testNodeList
        } // endif !testNodeList.isEmpty
        else {
            StringBuilder buffer = new StringBuilder();
            buffer.append("INFO - THE REGISTRY FILE <"); //$NON-NLS-1$
            buffer.append(registryFile.getAbsolutePath());

            if (parentBundle != null) {
                buffer.append("> IN BUNDLE <"); //$NON-NLS-1$
                buffer.append(parentBundle.getSymbolicName());
            }

            buffer.append("> DID NOT DEFINE ANY TEST DEFINITIONS <"); //$NON-NLS-1$
            buffer.append(ELEMENT_TEST);
            buffer.append(">"); //$NON-NLS-1$
            logger.logDebug(buffer.toString());
        }
    }

    /**
     * @return  List<TestModel> - All successfully loaded tests
     */
    public List<TestModel> getAllTests() {
        Iterator<TestModel> iter = _testDefinitions.iterator();
        List<TestModel> models = new ArrayList<TestModel>();

        while (iter.hasNext()) {
            TestModel next = iter.next();
            models.add(next);
        }

        return models;
    }

    /**
     * @return  DocumentBuilder
     */
    protected DocumentBuilder getDocumentBuilder(boolean validating)
                                          throws ParserConfigurationException {
        DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();

        instance.setValidating(validating);
        instance.setExpandEntityReferences(false);
        instance.setCoalescing(true);

        return instance.newDocumentBuilder();
    }

    /**
     * Utility for parsing a container of module elements.
     */
    private Set<String> getModuleSet(Element parentElement) {
        Set<String> testModules = new HashSet<String>();
        NodeList moduleNodeList = parentElement.getElementsByTagName(ELEMENT_MODULE);

        if ((moduleNodeList != null) && (moduleNodeList.getLength() > 0)) {
            int modulesLength = moduleNodeList.getLength();

            for (int j = 0; j < modulesLength; j++) {
                Element nextModule = (Element) moduleNodeList.item(j);

                // Fail safe
                if (nextModule.getParentNode() != parentElement) {
                    continue;
                }

                String textValue = nextModule.getTextContent();

                if ((textValue != null) || (textValue.trim().length() > 0)) {
                    testModules.add(textValue.trim());
                }
            }
        }

        return testModules;
    }

    /**
     * Log parse exceptions only if the option is explicitly enabled.
     */
    protected void handleParseException(ILogger logger, Exception e) {
        if (logger.isOptionEnabled(DEBUG_DOM)) {
            logger.logException(e);
        }
    }

    /**
     * Utility for building RegistryNode tree from modules.
     */
    private static void makeModuleNodes(RegistryNode io_parentNode,
                                        ModularModel modularModel) {
        Set<String> modules = modularModel.getModules();

        for (String nextModule : modules) {
            RegistryNode moduleNode = new RegistryNode(ELEMENT_MODULE);
            moduleNode.setTextValue(nextModule);
            io_parentNode.addChildNode(moduleNode);
        }
    }

    /**
     * Utility to write out a DOM from the given model data.
     */
    public static void writeRegistryFile(File outputFile, Collection<TestModel> tests)
                                  throws IOException {
        FileWriter writer = new FileWriter(outputFile);

        try {
            writeRegistryFile(writer, tests);
        } finally {
            writer.close();
        }
    }

    /**
     * Utility to write out a DOM from the given model data.
     */
    public static void writeRegistryFile(Writer writer, Collection<TestModel> tests)
                                  throws IOException {
        RegistryNode rootNode = new RegistryNode(ELEMENT_TESTPROJECT);

        for (TestModel nextTest : tests) {
            RegistryNode testNode = new RegistryNode(ELEMENT_TEST);
            String testClassName = nextTest.getTestClassName();

            if ((testClassName == null) || (testClassName.trim().length() == 0)) {
                continue;
            }

            testNode.addAttribute(ATTR_CLASS, testClassName.trim());

            if (nextTest.getTestOwner() != null) {
                testNode.addAttribute(ATTR_OWNER, nextTest.getTestOwner().trim());
            }

            makeModuleNodes(testNode, nextTest);

            rootNode.addChildNode(testNode);
        }

        rootNode.writeDOM(writer, ""); //$NON-NLS-1$
    }

    /**
     * Utility node for building a DOM.
     */
    private static class RegistryNode {
        private String _elementName;
        private Map<String, String> _attributes;
        private String _textValue = ""; //$NON-NLS-1$
        private List<RegistryNode> _children;

        /**
         * Save the data members.
         */
        public RegistryNode(String elementName) {
            _elementName = elementName;
            _attributes = new HashMap<String, String>();
            _children = new ArrayList<RegistryNode>();
        }

        /**
         * Add a name-value pair attribute.
         */
        public void addAttribute(String name, String value) {
            _attributes.put(name, value);
        }

        /**
         * Add a child node.
         */
        public void addChildNode(RegistryNode node) {
            _children.add(node);
        }

        /**
         * Set the text value of this element.
         */
        public void setTextValue(String value) {
            _textValue = value;
        }

        /**
         * write the DOM to teh given writer.
         */
        public void writeDOM(Writer writer, String indent) throws IOException {
            writer.write(indent);
            writer.write("<"); //$NON-NLS-1$
            writer.write(_elementName);

            for (String nextAttrName : _attributes.keySet()) {
                writer.write(" "); //$NON-NLS-1$
                writer.write(nextAttrName);
                writer.write("=\""); //$NON-NLS-1$
                writer.write(_attributes.get(nextAttrName));
                writer.write("\""); //$NON-NLS-1$
            }

            writer.write(">"); //$NON-NLS-1$

            if (!_children.isEmpty()) {
                writer.write("\n"); //$NON-NLS-1$

                for (RegistryNode nextChild : _children) {
                    nextChild.writeDOM(writer, indent + "   "); //$NON-NLS-1$
                }

                writer.write(indent);
            } else {
                writer.write(_textValue);
            }

            writer.write("</"); //$NON-NLS-1$
            writer.write(_elementName);
            writer.write(">\n"); //$NON-NLS-1$
        }
    }

    /**
     * Simple comparator of models by the test FQN.
     */
    private static class TestModelComparator implements Comparator<TestModel> {
        /**
         * @see  java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(TestModel m1, TestModel m2) {
            String c1 = m1.getTestClassName();
            String c2 = m2.getTestClassName();

            return c1.compareTo(c2);
        }
    }
}
