/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.internal.conditions;

import com.windowtester.runtime.condition.ICondition;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.wtc.eclipse.core.CoreActivator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Parse the initial condition extension point. Initial conditions are ui.wait() calls to
 * be executed on test set up
 */
public class InitialConditionRegistry {
    // The shared instance
    private static InitialConditionRegistry _instance;

    // ----------------------------------------------------------------------
    // EXTENSION POINT ELEMENTS
    // ----------------------------------------------------------------------
    private static final String INITIALCONDITIONS_EXTENSION_POINT_ID =
        "org.wtc.eclipse.core.initialConditions"; //$NON-NLS-1$

    private static final String ELEMENT_INITIALCONDITION = "initialCondition"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

    private Collection<ICondition> _initialConditions;

    /**
     * Parse the extension point.
     */
    private InitialConditionRegistry() {
        _initialConditions = new ArrayList<ICondition>();

        // Parse the initial conditions
        IExtensionRegistry extensionReg = Platform.getExtensionRegistry();
        IConfigurationElement[] conditionHandlers = extensionReg.getConfigurationElementsFor(INITIALCONDITIONS_EXTENSION_POINT_ID);

        for (IConfigurationElement nextElement : conditionHandlers) {
            if (nextElement.getName().equals(ELEMENT_INITIALCONDITION)) {
                String bundleID = nextElement.getNamespaceIdentifier();
                Bundle bundle = Platform.getBundle(bundleID);

                String conditionClassString = nextElement.getAttribute(ATTR_CLASS);

                if ((conditionClassString == null) || (conditionClassString.length() == 0)) {
                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED AN INITIAL CONDITION WITHOUT A CLASS ATTRIBUTE", //$NON-NLS-1$
                                                          new Object[] { bundleID });
                    CoreActivator.logError(message);

                    continue;
                }

                Class<?> loadedClass = null;

                try {
                    loadedClass = bundle.loadClass(conditionClassString);
                } catch (ClassNotFoundException ex) {
                    CoreActivator.logException(ex);

                    continue;
                }

                if (!ICondition.class.isAssignableFrom(loadedClass)) {
                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED AN INITIAL CONDITION <{1}> " //$NON-NLS-1$
                                                          + "THAT DOES NOT IMPLEMENT {2}; " //$NON-NLS-1$
                                                          + "THE CONDITION WILL BE IGNORED", new Object[] { //$NON-NLS-1$
                            bundleID, conditionClassString, ICondition.class.getName()
                        });
                    CoreActivator.logError(message);

                    continue;
                }

                ICondition loadedCondition = null;

                try {
                    loadedCondition = (ICondition) loadedClass.newInstance();
                } catch (InstantiationException ex) {
                    CoreActivator.logException(ex);

                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED AN INITIAL CONDITION <{1}> " //$NON-NLS-1$
                                                          + "THAT DOES NOT HAVE A NO-ARG CONSTRUCTOR; " //$NON-NLS-1$
                                                          + "THE CONDITION WILL BE IGNORED", new Object[] { //$NON-NLS-1$
                            bundleID, conditionClassString
                        });
                    CoreActivator.logError(message);

                    continue;
                } catch (IllegalAccessException ex) {
                    CoreActivator.logException(ex);

                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED AN INITIAL CONDITION <{1}> " //$NON-NLS-1$
                                                          + "THAT COULD NOT BE LOADED; " //$NON-NLS-1$
                                                          + "THE CONDITION WILL BE IGNORED", new Object[] { //$NON-NLS-1$
                            bundleID, conditionClassString
                        });
                    CoreActivator.logError(message);

                    continue;
                }

                StringBuilder buffer = new StringBuilder();
                buffer.append("INFO - SUCCESSFULLY LOADED INITIAL CONDITION <"); //$NON-NLS-1$
                buffer.append(conditionClassString);
                buffer.append(">"); //$NON-NLS-1$
                CoreActivator.logDebug(buffer.toString());

                _initialConditions.add(loadedCondition);
            }
        }
    }

    /**
     * Parse the extension point and get the initial conditions.
     */
    public static Collection<ICondition> getInitialConditions() {
        return new ArrayList<ICondition>(instance()._initialConditions);
    }

    /**
     * Get the shared instance.
     */
    private static InitialConditionRegistry instance() {
        if (_instance == null) {
            _instance = new InitialConditionRegistry();
        }

        return _instance;
    }
}
