/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.handlers;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.wtc.eclipse.platform.PlatformActivator;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Registry for declaring which shells should be considered shells that are always active
 * and focused.
 */
public class FocusFixingConditionRegistry {
    // The shared instance
    private static FocusFixingConditionRegistry _instance;

    // ----------------------------------------------------------------------
    // EXTENSION POINT ELEMENTS
    // ----------------------------------------------------------------------
    private static final String DEFAULT_HANDLERS_EXTENSION_POINT_ID =
        "org.wtc.eclipse.platform.focusFixingShells"; //$NON-NLS-1$

    private static final String ELEMENT_SHELL = "shell"; //$NON-NLS-1$
    private static final String ATTR_TITLE = "title"; //$NON-NLS-1$
    private static final String ATTR_OS = "os"; //$NON-NLS-1$

    // The names of the shells that are always to have focus
    private Set<String> _focusFixingShellNames;

    /**
     * Parse the extension point.
     */
    private FocusFixingConditionRegistry() {
        _focusFixingShellNames = new HashSet<String>();
        boolean _isLinux = Platform.getOS().equalsIgnoreCase(Platform.OS_LINUX);

        // Parse the extended helpers
        IExtensionRegistry extensionReg = Platform.getExtensionRegistry();
        IConfigurationElement[] conditionHandlers = extensionReg.getConfigurationElementsFor(DEFAULT_HANDLERS_EXTENSION_POINT_ID);
        String os = null;

        for (IConfigurationElement nextElement : conditionHandlers) {
            String bundleID = nextElement.getNamespaceIdentifier();

            if (nextElement.getName().equals(ELEMENT_SHELL)) {
                String title = nextElement.getAttribute(ATTR_TITLE);
                os = nextElement.getAttribute(ATTR_OS);

                // we only want the focus fixing shells associated with a specific OS
                boolean incorrectOS = (os != null) && os.equalsIgnoreCase(Platform.OS_LINUX) && !_isLinux;

                if (((title != null) && (title.trim().length() > 0))) {
                    if (incorrectOS) {
                        String message = MessageFormat.format("THE BUNDLE <{0}> FOR A FOCUS FIXING SHELL: <{1}> WILL NOT BE USED BECAUSE IT IS NOT" //$NON-NLS-1$
                                                              + " SPECIFIED FOR THIS OS", //$NON-NLS-1$
                                                              new Object[] {
                                bundleID, title.trim()
                            });
                        PlatformActivator.logWarning(message);

                        continue;
                    } else {
                        String message = MessageFormat.format("THE BUNDLE <{0}> SUCCESSFULLY DECLARED A FOCUS FIXING SHELL: <{1}>", //$NON-NLS-1$
                                                              new Object[] {
                                bundleID, title.trim()
                            });
                        PlatformActivator.logWarning(message);
                        _focusFixingShellNames.add(title.trim());
                    }
                } else {
                    String message = MessageFormat.format("THE BUNDLE <{0}> DECLARED A FOCUS FIXING SHELL EXTENSION WITH AN EMPTY TITLE", //$NON-NLS-1$
                                                          new Object[] { bundleID });
                    PlatformActivator.logWarning(message);
                }
            } else {
                String message = MessageFormat.format("THE BUNDLE <{0}> DECLARED A FOCUS FIXING SHELL EXTENSION WITH UNKNOWN ELEMENTS", //$NON-NLS-1$
                                                      new Object[] { bundleID });
                PlatformActivator.logWarning(message);
            }
        }

    }

    /**
     * @return  Set<String> - Get the list of shell titles that are to be considered
     *          shells that are always active and focused
     */
    public static Set<String> getFocusFixingShells() {
        return new HashSet<String>(instance()._focusFixingShellNames);
    }

    /**
     * @return  FocusFixingConditionRegistry - The shared instance
     */
    private static synchronized FocusFixingConditionRegistry instance() {
        if (_instance == null) {
            _instance = new FocusFixingConditionRegistry();
        }

        return _instance;
    }
}
