/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.internal.reset;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.wtc.eclipse.core.CoreActivator;
import org.wtc.eclipse.core.reset.IResetDaemon;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Factory for parsing the reset daemon extension point and loading the reset daemon
 * implementations.
 */
public class ResetDaemonRegistry {
    // The shared instance
    private static ResetDaemonRegistry _instance;

    // ----------------------------------------------------------------------
    // EXTENSION POINT ELEMENTS
    // ----------------------------------------------------------------------
    private static final String RESET_DAEMON_EXTENSION_POINT_ID =
        "org.wtc.eclipse.core.resetDaemon"; //$NON-NLS-1$

    private static final String ELEMENT_RESETDAEMON = "resetDaemon"; //$NON-NLS-1$
    private static final String ELEMENTATTR_RESETDAEMON_CLASSNAME = "className"; //$NON-NLS-1$
    private static final String ELEMENTATTR_RESETDAEMON_PRIORITY = "priority"; //$NON-NLS-1$
    private static final String ELEMENTATTR_RESETDAEMON_TRIGGERLEVEL = "triggerLevel"; //$NON-NLS-1$

    private List<IResetDaemon> _registeredResetDaemons;

    /**
     * Parse the extension point and save the data members.
     */
    private ResetDaemonRegistry() {
        int resetTrigger = -1; //TestSuiteBuilder.getResetTrigger();

        ArrayList<ResetDaemon> tempList = new ArrayList<ResetDaemon>();

        IExtensionRegistry extensionReg = Platform.getExtensionRegistry();
        IConfigurationElement[] daemons = extensionReg.getConfigurationElementsFor(RESET_DAEMON_EXTENSION_POINT_ID);

        for (IConfigurationElement nextElement : daemons) {
            if (nextElement.getName().equals(ELEMENT_RESETDAEMON)) {
                String className = nextElement.getAttribute(ELEMENTATTR_RESETDAEMON_CLASSNAME);

                if (className != null) {
                    Bundle bundle = Platform.getBundle(nextElement.getNamespaceIdentifier());

                    try {
                        Class<?> loadedClass = bundle.loadClass(className);
                        Object objectInstance = loadedClass.newInstance();

                        if (objectInstance instanceof IResetDaemon) {
                            String priorityString = nextElement.getAttribute(ELEMENTATTR_RESETDAEMON_PRIORITY);
                            int priority = 25;

                            if (priorityString != null) {
                                try {
                                    priority = Integer.parseInt(priorityString);
                                } catch (NumberFormatException nfe) {
                                    // Do nothing. Default to 25
                                }
                            }

                            String triggerLevelString = nextElement.getAttribute(ELEMENTATTR_RESETDAEMON_TRIGGERLEVEL);
                            int triggerLevel = 10;

                            if (triggerLevelString != null) {
                                try {
                                    triggerLevel = Integer.parseInt(triggerLevelString);
                                } catch (NumberFormatException nfe) {
                                    // Do nothing. Default to 10.
                                }
                            }

                            if ((resetTrigger < 0) || (triggerLevel < resetTrigger)) {
                                ResetDaemon daemon = new ResetDaemon((IResetDaemon) objectInstance,
                                                                     priority);
                                tempList.add(daemon);
                            } else {
                                String message = MessageFormat.format("IGNORING RESET DAEMON <{0}> BECAUSE ITS TRIGGER LEVEL <{1}> IS GREATER THAN THE RESET TRIGGER <{2}>", //$NON-NLS-1$
                                                                      new Object[] {
                                        className,
                                        triggerLevel,
                                        resetTrigger
                                    });
                                CoreActivator.logDebug(message);
                            }
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

        // Reset daemons with a higher priority run first
        Collections.sort(tempList, new Comparator<ResetDaemon>() {
                public int compare(ResetDaemon o1, ResetDaemon o2) {
                    return o2.getPriority() - o1.getPriority();
                }
            });

        _registeredResetDaemons = new ArrayList<IResetDaemon>();

        for (ResetDaemon nextDaemon : tempList) {
            _registeredResetDaemons.add(nextDaemon.getResetDaemon());
        }
    }

    /**
     * @return  List<IResetDaemon> - Return a copied list of the registered IResetDaemon
     *          implementations in the order of declared priority
     */
    public static List<IResetDaemon> getResetDeamons() {
        return new ArrayList<IResetDaemon>(instance()._registeredResetDaemons);
    }

    /**
     * @return  ResetDaemonRegistry - Create a shared instance
     */
    private static ResetDaemonRegistry instance() {
        if (_instance == null) {
            _instance = new ResetDaemonRegistry();
        }

        return _instance;
    }

    /**
     * Something to sort on.
     */
    private class ResetDaemon {
        private final IResetDaemon _daemon;
        private final int _priority;

        /**
         * Save the data members.
         */
        public ResetDaemon(IResetDaemon daemon, int priority) {
            assert (daemon != null);
            _daemon = daemon;
            _priority = priority;
        }

        /**
         * @return  int
         */
        public int getPriority() {
            return _priority;
        }

        /**
         * @return  IResetDaemon
         */
        public IResetDaemon getResetDaemon() {
            return _daemon;
        }
    }
}
