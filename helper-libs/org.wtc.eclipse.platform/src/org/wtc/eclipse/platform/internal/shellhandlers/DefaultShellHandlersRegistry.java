/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.shellhandlers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.shellhandlers.AbstractShellHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Parse an extension point that adds a shell handler to the default list of shell
 * handlers that apply to all test plugins loaded.
 */
public class DefaultShellHandlersRegistry {
    // The shared instance
    private static DefaultShellHandlersRegistry _instance;

    // ----------------------------------------------------------------------
    // EXTENSION POINT ELEMENTS
    // ----------------------------------------------------------------------
    private static final String DEFAULTSHELLHANDLERS_EXTENSION_POINT_ID =
        "org.wtc.eclipse.platform.shellHandlers"; //$NON-NLS-1$

    private static final String ELEMENT_SHELLHANDER = "shellHandler"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

    private Collection<AbstractShellHandler> _defaultHandlers;

    /**
     * Parse the extension point.
     */
    private DefaultShellHandlersRegistry(IUIContext ui) {
        _defaultHandlers = new ArrayList<AbstractShellHandler>();

        // Parse the default shell handlers
        IExtensionRegistry extensionReg = Platform.getExtensionRegistry();
        IConfigurationElement[] shellHandlers = extensionReg.getConfigurationElementsFor(DEFAULTSHELLHANDLERS_EXTENSION_POINT_ID);

        for (IConfigurationElement nextElement : shellHandlers) {
            if (nextElement.getName().equals(ELEMENT_SHELLHANDER)) {
                String bundleID = nextElement.getNamespaceIdentifier();
                Bundle bundle = Platform.getBundle(bundleID);

                String shellHandlerClassString = nextElement.getAttribute(ATTR_CLASS);

                if ((shellHandlerClassString == null) || (shellHandlerClassString.length() == 0)) {
                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED A DEFAULT SHELL HANDLER WITHOUT A CLASS ATTRIBUTE", //$NON-NLS-1$
                                                          new Object[] { bundleID });
                    PlatformActivator.logError(message);

                    continue;
                }

                Class<?> loadedClass = null;

                try {
                    loadedClass = bundle.loadClass(shellHandlerClassString);
                } catch (ClassNotFoundException ex) {
                    PlatformActivator.logException(ex);

                    continue;
                }

                if (!AbstractShellHandler.class.isAssignableFrom(loadedClass)) {
                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED A DEFAULT SHELL HANDLER <{1}> " //$NON-NLS-1$
                                                          + "THAT DOES NOT EXTEND {2}; " //$NON-NLS-1$
                                                          + "THE HANDLER WILL BE IGNORED", //$NON-NLS-1$
                                                          new Object[] {
                            bundleID, shellHandlerClassString, AbstractShellHandler.class.getName()
                        });
                    PlatformActivator.logError(message);

                    continue;
                }

                Constructor ctor = null;

                try {
                    ctor = loadedClass.getConstructor(new Class[] { IUIContext.class });
                } catch (SecurityException ex) {
                    PlatformActivator.logException(ex);
                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED A DEFAULT SHELL HANDLER <{1}> " //$NON-NLS-1$
                                                          + "THAT DOES NOT A CONSTRUCTOR WITH ONLY THE IUIContext PARAMETER; " //$NON-NLS-1$
                                                          + "THE HANDLER WILL BE IGNORED", //$NON-NLS-1$
                                                          new Object[] {
                            bundleID, shellHandlerClassString
                        });
                    PlatformActivator.logError(message);

                    continue;
                } catch (NoSuchMethodException ex) {
                    PlatformActivator.logException(ex);
                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED A DEFAULT SHELL HANDLER <{1}> " //$NON-NLS-1$
                                                          + "THAT DOES NOT A CONSTRUCTOR WITH ONLY THE IUIContext PARAMETER; " //$NON-NLS-1$
                                                          + "THE HANDLER WILL BE IGNORED", //$NON-NLS-1$
                                                          new Object[] {
                            bundleID, shellHandlerClassString
                        });
                    PlatformActivator.logError(message);

                    continue;
                }

                AbstractShellHandler loadedHandler = null;

                try {
                    loadedHandler = (AbstractShellHandler) ctor.newInstance(new Object[] { ui });
                } catch (InstantiationException ex) {
                    PlatformActivator.logException(ex);

                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED A DEFAULT SHELL HANDLER <{1}> " //$NON-NLS-1$
                                                          + "THAT DOES NOT A CONSTRUCTOR WITH ONLY THE IUIContext PARAMETER; " //$NON-NLS-1$
                                                          + "THE HANDLER WILL BE IGNORED", //$NON-NLS-1$
                                                          new Object[] {
                            bundleID, shellHandlerClassString
                        });
                    PlatformActivator.logError(message);

                    continue;
                } catch (IllegalAccessException ex) {
                    PlatformActivator.logException(ex);

                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED A DEFAULT SHELL HANDLER <{1}> " //$NON-NLS-1$
                                                          + "THAT COULD NOT BE LOADED; " //$NON-NLS-1$
                                                          + "THE HANDLER WILL BE IGNORED", //$NON-NLS-1$
                                                          new Object[] {
                            bundleID, shellHandlerClassString
                        });
                    PlatformActivator.logError(message);

                    continue;
                } catch (IllegalArgumentException ex) {
                    PlatformActivator.logException(ex);

                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED A DEFAULT SHELL HANDLER <{1}> " //$NON-NLS-1$
                                                          + "THAT COULD NOT BE LOADED; " //$NON-NLS-1$
                                                          + "THE HANDLER WILL BE IGNORED", //$NON-NLS-1$
                                                          new Object[] {
                            bundleID, shellHandlerClassString
                        });
                    PlatformActivator.logError(message);

                    continue;
                } catch (InvocationTargetException ex) {
                    PlatformActivator.logException(ex);

                    String message = MessageFormat.format("ERROR - THE BUNDLE <{0}> " //$NON-NLS-1$
                                                          + "> DECLARED A DEFAULT SHELL HANDLER <{1}> " //$NON-NLS-1$
                                                          + "THAT COULD NOT BE LOADED; " //$NON-NLS-1$
                                                          + "THE HANDLER WILL BE IGNORED", //$NON-NLS-1$
                                                          new Object[] {
                            bundleID, shellHandlerClassString
                        });
                    PlatformActivator.logError(message);

                    continue;
                }

                StringBuilder buffer = new StringBuilder();
                buffer.append("INFO - SUCCESSFULLY LOADED DEFAULT SHELL HANDLER <"); //$NON-NLS-1$
                buffer.append(shellHandlerClassString);
                buffer.append(">"); //$NON-NLS-1$
                PlatformActivator.logDebug(buffer.toString());

                _defaultHandlers.add(loadedHandler);
            }
        }
    }

    /**
     * Parse the extension point and get the initial conditions.
     */
    public static Collection<AbstractShellHandler> getDefaultShellHandlers(IUIContext ui) {
        return new ArrayList<AbstractShellHandler>(instance(ui)._defaultHandlers);
    }

    /**
     * Get the shared instance.
     */
    private static DefaultShellHandlersRegistry instance(IUIContext ui) {
        if (_instance == null) {
            _instance = new DefaultShellHandlersRegistry(ui);
        }

        return _instance;
    }
}
