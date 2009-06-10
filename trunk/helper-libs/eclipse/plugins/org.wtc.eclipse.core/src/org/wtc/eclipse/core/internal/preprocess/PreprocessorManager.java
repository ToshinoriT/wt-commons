/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.internal.preprocess;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.wtc.eclipse.core.CoreActivator;
import org.wtc.eclipse.core.preprocess.IPreprocessor;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Manages and executes preprocessors. Manages the "called once and only once per
 * TestRunner invocation" contract. A preprocessor can only be added through the
 * preprocessor extension point
 */
public class PreprocessorManager {
    // The shared instance
    private static PreprocessorManager _instance;

    // -------------------------------------------------------------
    // EXTENSION POINT ELEMENTS
    // -------------------------------------------------------------
    private static final String PREPROCESSOR_EXTENSION_POINT_ID = "org.wtc.eclipse.core.preprocessor"; //$NON-NLS-1$

    private static final String ELEMENT_PREPROCESSOR = "preprocessor"; //$NON-NLS-1$
    private static final String ELEMENTATTR_PREPROCESSOR_CLASS = "class"; //$NON-NLS-1$
    private static final String ELEMENTATTR_PREPROCESSOR_PRIORITY = "priority"; //$NON-NLS-1$

    private List<Preprocessor> _registeredProcessors;

    /**
     * Parse the extension point.
     */
    private PreprocessorManager() {
        ArrayList<Preprocessor> tempList = new ArrayList<Preprocessor>();

        IExtensionRegistry extensionReg = Platform.getExtensionRegistry();
        IConfigurationElement[] processors = extensionReg.getConfigurationElementsFor(PREPROCESSOR_EXTENSION_POINT_ID);

        CoreActivator.logDebug("-FOUND " + processors.length + " PREPROCESSOR EXTENSIONS"); //$NON-NLS-1$ //$NON-NLS-2$

        for (IConfigurationElement nextElement : processors) {
            if (nextElement.getName().equals(ELEMENT_PREPROCESSOR)) {
                String className = nextElement.getAttribute(ELEMENTATTR_PREPROCESSOR_CLASS);

                if (className != null) {
                    Bundle bundle = Platform.getBundle(nextElement.getNamespaceIdentifier());

                    try {
                        Class<?> loadedClass = bundle.loadClass(className);
                        Object objectInstance = loadedClass.newInstance();

                        if (objectInstance instanceof IPreprocessor) {
                            String priorityString = nextElement.getAttribute(ELEMENTATTR_PREPROCESSOR_PRIORITY);

                            int priority = 25;

                            if (priorityString != null) {
                                try {
                                    priority = Integer.parseInt(priorityString);
                                } catch (NumberFormatException nfe) {
                                    // Do nothing. Default to 25
                                }
                            }

                            String message = MessageFormat.format("(P) SUCCESSFULLY CREATES PREPROCESSOR <{0}>", objectInstance.getClass().getName()); //$NON-NLS-1$
                            CoreActivator.logDebug(message);

                            Preprocessor processor = new Preprocessor((IPreprocessor)
                                                                      objectInstance,
                                                                      priority);
                            tempList.add(processor);
                        }
                    } catch (ClassNotFoundException e) {
                        CoreActivator.logException(e);
                    } catch (InstantiationException e) {
                        CoreActivator.logException(e);
                    } catch (IllegalAccessException e) {
                        CoreActivator.logException(e);
                    }
                }
            }
        }

        Collections.sort(tempList, new Comparator<Preprocessor>() {
                public int compare(Preprocessor o1, Preprocessor o2) {
                    return o2.getPriority() - o1.getPriority();
                }
            });

        _registeredProcessors = new ArrayList<Preprocessor>(tempList);
    }

    /**
     * Execute the registered preprocessors.
     */
    private void run() {
        CoreActivator.logDebug("-CALLING (" + _registeredProcessors.size() + ") IPreprocessors"); //$NON-NLS-1$ //$NON-NLS-2$

        for (Preprocessor nextPX : _registeredProcessors) {
            CoreActivator.logDebug("-CALLING IPreprocessor :" + nextPX.toString()); //$NON-NLS-1$
            nextPX.getPreprocessor().run();
        }
    }

    /**
     * If the PreprocessorManager has never been run, parse the preprocessor extension
     * point and, if any preprocessors were registered, execute the registered
     * preprocessors. If the PreprocessorManager has already been run, then do nothing
     */
    public synchronized static void runProcessorsIfNeeded() {
        if (_instance == null) {
            _instance = new PreprocessorManager();
            _instance.run();
        }
    }

    /**
     * Something to sort on.
     */
    private class Preprocessor {
        private final IPreprocessor _processor;
        private final int _priority;

        /**
         * Save the data members.
         */
        public Preprocessor(IPreprocessor processor, int priority) {
            assert (processor != null);
            _processor = processor;
            _priority = priority;
        }

        /**
         * @return  IPreprocessor
         */
        public IPreprocessor getPreprocessor() {
            return _processor;
        }

        /**
         * @return  int
         */
        public int getPriority() {
            return _priority;
        }

        /**
         * @see  java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return _processor.getClass().getName();
        }
    }
}
