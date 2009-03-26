/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.reset;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.IPerspective;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper.Perspective;
import java.text.MessageFormat;

/**
 * Extension point that will register the default view and perspective to be set in the
 * ViewsPerspectivesResetDaemon.
 */
public class ViewsPerspectiveResetRegistry {
    // The shared instance
    private static ViewsPerspectiveResetRegistry _instance;

    // ----------------------------------------------------------------------
    // EXTENSION POINT ELEMENTS
    // ----------------------------------------------------------------------
    private static final String DEFAULT_PERSPECTIVE_EXTENSION_POINT_ID =
        "org.wtc.eclipse.platform.defaultPerspective"; //$NON-NLS-1$

    private static final String ELEMENT_PERSPECTIVE = "perspective"; //$NON-NLS-1$
    private static final String ATTR_ENUMVALUE = "enumValue"; //$NON-NLS-1$
    private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$

    // The default IPerspective to set on workbench reset
    private IPerspective _defaultPerspective;
    private int _activePriority = -1;

    /**
     * Parse the extension point.
     */
    private ViewsPerspectiveResetRegistry() {
        // Let's just set some eclipse defaults
        _defaultPerspective = Perspective.RESOURCE;

        // Parse the extended helpers
        IExtensionRegistry extensionReg = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = extensionReg.getConfigurationElementsFor(DEFAULT_PERSPECTIVE_EXTENSION_POINT_ID);

        boolean setOnce = false;

        for (IConfigurationElement nextElement : extensions) {
            String bundleID = nextElement.getNamespaceIdentifier();

            if (nextElement.getName().equals(ELEMENT_PERSPECTIVE)) {
                String enumValue = nextElement.getAttribute(ATTR_ENUMVALUE);

                if ((enumValue != null) && (enumValue.trim().length() > 0) && (enumValue.lastIndexOf('.') > 0) && (!enumValue.endsWith("."))) //$NON-NLS-1$
                {
                    // OK, let's load the enum class
                    int lastDot = enumValue.lastIndexOf('.');
                    String enumClassString = enumValue.substring(0, lastDot);
                    String enumValueString = enumValue.substring(lastDot + 1);

                    Bundle bundle = Platform.getBundle(bundleID);

                    try {
                        Class enumClass = bundle.loadClass(enumClassString);

                        if ((enumClass != null) && (Enum.class.isAssignableFrom(enumClass)) && (IPerspective.class.isAssignableFrom(enumClass))) {
                            try {
                                String detail = setOnce
                                    ? "BECAUSE A DEFAULT PERSPECTIVE HAS ALREADY BEEN SET, THE ELEMENT WILL BE IGNORED" //$NON-NLS-1$
                                    : "THE DEFAULT PRIORITY VALUE WILL BE USED"; //$NON-NLS-1$

                                int tempPriority = 0;
                                String priorityString = nextElement.getAttribute(ATTR_PRIORITY);

                                if ((priorityString != null) && (priorityString.trim().length() > 0)) {
                                    try {
                                        tempPriority = Integer.parseInt(priorityString);
                                    } catch (NumberFormatException nfe) {
                                        String message = MessageFormat.format("THE PLUGIN <{0}> DEFINED AN ILLEGAL PRIORITY VALUE <{1}>. {2}, arguments", //$NON-NLS-1$
                                                                              new Object[] {
                                                bundleID, priorityString, detail
                                            });
                                        PlatformActivator.logException(message, nfe);

                                        if (setOnce) {
                                            continue;
                                        }
                                    }
                                } else {
                                    String message = MessageFormat.format("THE PLUGIN <{0}> DEFINED AN ILLEGAL PRIORITY VALUE <{1}>. {2}, arguments", //$NON-NLS-1$
                                                                          new Object[] {
                                            bundleID, priorityString, detail
                                        });
                                    PlatformActivator.logError(message);

                                    if (setOnce) {
                                        continue;
                                    }
                                }

                                if (tempPriority > _activePriority) {
                                    IPerspective old = _defaultPerspective;
                                    _defaultPerspective = (IPerspective) Enum.valueOf(enumClass, enumValueString);

                                    if (_defaultPerspective == null) {
                                        _defaultPerspective = old;
                                    }

                                    if (setOnce) {
                                        String message = MessageFormat.format("A DEFAULT PERSPECTIVE <{0}> HAS ALREADY BEEN DEFINED; THE NEW VALUE WILL BE <{1}> ", //$NON-NLS-1$
                                                                              new Object[] {
                                                old, _defaultPerspective
                                            });
                                        PlatformActivator.logWarning(message);
                                    }

                                    setOnce = true;
                                    _activePriority = tempPriority;
                                }
                            } catch (IllegalArgumentException e) {
                                String message = MessageFormat.format("THE PLUGIN <{0}> DEFINED a DEFAULT PERSPECTIVE VALUE <{1}> THAT DOES NOT EXIST ON ENUM <{2}>", //$NON-NLS-1$
                                                                      new Object[] {
                                        bundleID, enumValueString, enumClassString
                                    });
                                PlatformActivator.logWarning(message);
                            }
                        } else {
                            String message = MessageFormat.format("THE PLUGIN <{0}> DEFINED AN ILLEGAL DEFAULT PERSPECTIVE CLASS <{1}>", //$NON-NLS-1$
                                                                  new Object[] {
                                    bundleID, enumClassString
                                });
                            PlatformActivator.logWarning(message);
                        }
                    } catch (ClassNotFoundException e) {
                        String message = MessageFormat.format("THE PLUGIN <{0}> DEFINED A DEFAULT PERSPECTIVE ELEMENT CLASS <{1}> THAT COULD NOT BE FOUND", //$NON-NLS-1$
                                                              new Object[] {
                                bundleID, enumClassString
                            });
                        PlatformActivator.logWarning(message);
                    }
                } else {
                    String message = MessageFormat.format("THE PLUGIN <{0}> DEFINED AN ILLEGAL DEFAULT PERSPECTIVE ELEMENT VALUE", //$NON-NLS-1$
                                                          new Object[] { bundleID });
                    PlatformActivator.logWarning(message);
                }
            }
        }

        String message = MessageFormat.format("THE DEFAULT PERSPECTIVE <{0}> WILL BE APPLIED AT PRIORITY <{1}>", //$NON-NLS-1$
                                              new Object[] {
                _defaultPerspective.getID(), Integer.toString(_activePriority)
            });
        PlatformActivator.logDebug(message);
    }

    /**
     * @return  IPerspective - Return the perspective to switch to when resetting the
     *          workbench to an initial state
     */
    public static IPerspective getDefaultPerspective() {
        return instance()._defaultPerspective;
    }

    /**
     * @return  ViewsPerspectiveResetRegistry - The shared instance
     */
    private synchronized static ViewsPerspectiveResetRegistry instance() {
        if (_instance == null) {
            _instance = new ViewsPerspectiveResetRegistry();
        }

        return _instance;
    }
}
