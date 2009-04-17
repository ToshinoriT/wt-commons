/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.handlers;

import com.windowtester.runtime.condition.IConditionHandler;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.wtc.eclipse.platform.PlatformActivator;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Before Window Tester executes any UI operations, it calls all registered
 * IConditionHandlers to make sure the state of the UI meets all assumptions about the UI.
 * For example, when a user is driving the UI there is no way that a modal dialog can be
 * sent to back and focus given to its parent Shell. Programatically, however, a user can
 * call workbench.bringToFront() and break that assumption. In this case, a condition
 * handler can be called to very that the root shells are in the correct order on the
 * visibility stack.
 *
 * <p>This class is the extension point reader for all test plugins that want to
 * contribute to root test condition handlers added during a test's setUp method. For
 * example, if a plugin changes the default text editor behavior, then all plugins in the
 * workbench will be affected and a condition handler that even base plugins can use
 * should be added</p>
 * 
 * @since 3.8.0
 */
public class DefaultConditionHandlerRegistry {
    private static DefaultConditionHandlerRegistry _instance;

    // ----------------------------------------------------------------------
    // EXTENSION POINT ELEMENTS
    // ----------------------------------------------------------------------
    private static final String DEFAULT_HANDLERS_EXTENSION_POINT_ID =
        "org.wtc.eclipse.platform.conditionHandlers"; //$NON-NLS-1$

    private static final String ELEMENT_CONDITIONHANDLER = "conditionHandler"; //$NON-NLS-1$
    private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

    private Collection<IConditionHandler> _defaultHandlers;

    /**
     * Parse the extension point.
     */
    private DefaultConditionHandlerRegistry() {
        _defaultHandlers = new ArrayList<IConditionHandler>();

        // Parse the extended helpers
        IExtensionRegistry extensionReg = Platform.getExtensionRegistry();
        IConfigurationElement[] conditionHandlers = extensionReg.getConfigurationElementsFor(DEFAULT_HANDLERS_EXTENSION_POINT_ID);

        for (IConfigurationElement nextElement : conditionHandlers) {
            if (nextElement.getName().equals(ELEMENT_CONDITIONHANDLER)) {
                String bundleID = nextElement.getNamespaceIdentifier();
                Bundle bundle = Platform.getBundle(bundleID);

                String handlerClassString = nextElement.getAttribute(ATTR_CLASS);

                if ((handlerClassString == null) || (handlerClassString.length() == 0)) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("ERROR - THE BUNDLE <"); //$NON-NLS-1$
                    buffer.append(bundleID);
                    buffer.append("> DECLARED A DEFAULT CONDITION HANDLER WITHOUT A CLASS ATTRIBUTE"); //$NON-NLS-1$
                    PlatformActivator.logError(buffer.toString());

                    continue;
                }

                Class<?> loadedClass = null;

                try {
                    loadedClass = bundle.loadClass(handlerClassString);
                } catch (ClassNotFoundException ex) {
                    PlatformActivator.logException(ex);

                    continue;
                }

                if (!IConditionHandler.class.isAssignableFrom(loadedClass)) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("ERROR - THE BUNDLE <"); //$NON-NLS-1$
                    buffer.append(bundleID);
                    buffer.append("> DECLARED A DEFAULT CONDITION HANDLER <"); //$NON-NLS-1$
                    buffer.append(handlerClassString);
                    buffer.append("> THAT DOES NOT IMPLEMENT "); //$NON-NLS-1$
                    buffer.append(IConditionHandler.class.getName());
                    buffer.append("; THE HANDLER WILL BE IGNORED"); //$NON-NLS-1$
                    PlatformActivator.logError(buffer.toString());

                    continue;
                }

                IConditionHandler loadedHandler = null;

                try {
                    loadedHandler = (IConditionHandler) loadedClass.newInstance();
                } catch (InstantiationException ex) {
                    PlatformActivator.logException(ex);

                    StringBuilder buffer = new StringBuilder();
                    buffer.append("ERROR - THE BUNDLE <"); //$NON-NLS-1$
                    buffer.append(bundleID);
                    buffer.append("> DECLARED A DEFAULT CONDITION HANDLER <"); //$NON-NLS-1$
                    buffer.append(handlerClassString);
                    buffer.append("> THAT DOES NOT HAVE A NO-ARG CONSTRUCTOR"); //$NON-NLS-1$
                    buffer.append("; THE HANDLER WILL BE IGNORED"); //$NON-NLS-1$
                    PlatformActivator.logError(buffer.toString());

                    continue;
                } catch (IllegalAccessException ex) {
                    PlatformActivator.logException(ex);

                    StringBuilder buffer = new StringBuilder();
                    buffer.append("ERROR - THE BUNDLE <"); //$NON-NLS-1$
                    buffer.append(bundleID);
                    buffer.append("> DECLARED A DEFAULT CONDITION HANDLER <"); //$NON-NLS-1$
                    buffer.append(handlerClassString);
                    buffer.append("> THAT COULD NOT BE LOADED"); //$NON-NLS-1$
                    buffer.append("; THE HANDLER WILL BE IGNORED"); //$NON-NLS-1$
                    PlatformActivator.logError(buffer.toString());

                    continue;
                }

                StringBuilder buffer = new StringBuilder();
                buffer.append("INFO - SUCCESSFULLY LOADED IConditionHandler <"); //$NON-NLS-1$
                buffer.append(handlerClassString);
                buffer.append(">"); //$NON-NLS-1$
                PlatformActivator.logDebug(buffer.toString());

                _defaultHandlers.add(loadedHandler);
            }
        }
    }

    /**
     * @return  Collection<IConditionHandler> - The condition handlers that should be
     *          added by default for each test
     */
    public static Collection<IConditionHandler> getDefaultConditionHandlers() {
        return instance()._defaultHandlers;
    }

    /**
     * @return  DefaultConditionHandlerRegistry - The shared instance
     */
    private static DefaultConditionHandlerRegistry instance() {
        if (_instance == null) {
            _instance = new DefaultConditionHandlerRegistry();
        }

        return _instance;
    }
}
