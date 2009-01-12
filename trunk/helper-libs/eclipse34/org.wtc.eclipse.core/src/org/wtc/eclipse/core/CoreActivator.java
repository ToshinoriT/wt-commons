/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import java.util.HashMap;
import java.util.Map;

/**
 * The activator class controls the plug-in life cycle.
 */
public class CoreActivator extends AbstractUIPlugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.wtc.eclipse.core"; //$NON-NLS-1$

    // The shared instance
    private static CoreActivator _plugin;

    // ILogger implementation details
    private ILog _logInstance = null;

    private Map<String, Boolean> _options;

    /**
     * The constructor.
     */
    public CoreActivator() {
        _options = new HashMap<String, Boolean>();
    }

    /**
     * Returns the shared instance.
     *
     * @return  the shared instance
     */
    public static CoreActivator getDefault() {
        return _plugin;
    }

    /**
     * @return  ILog - A shared instance of an eclipse log
     */
    private synchronized ILog getLogInstance() {
        if (_logInstance == null) {
            _logInstance = Platform.getLog(getBundle());
        }

        return _logInstance;
    }

    public String getOptionValue(String option) {
        if (option == null) {
            return null;
        }

        return Platform.getDebugOption(option);
    }

    public static boolean isOptionEnabled(String option) {
        return getDefault().isOptionEnabledInternal(PLUGIN_ID + option);
    }

    private synchronized boolean isOptionEnabledInternal(String option) {
        if (option == null) {
            return false;
        }

        Boolean optionValue = _options.get(option);

        if (optionValue == null) {
            String value = getOptionValue(option);
            optionValue = (value != null) && (value.equalsIgnoreCase("true")); //$NON-NLS-1$
            _options.put(option, optionValue);
        }

        return optionValue;
    }

    public static void log(IStatus status) {
        getDefault().getLogInstance().log(status);
    }

    public static void logDebug(String message) {
        log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.INFO, message, null));
        System.out.println(message);
    }

    public static void logDebug(String message, String option) {
        if (isOptionEnabled(option)) {
            logDebug(message);
        }
    }

    public static void logError(String message) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, (message == null) ? "" : message, //$NON-NLS-1$
                       null));
    }

    public static void logException(Throwable ex) {
        logException(ex.getLocalizedMessage(), ex);
    }

    public static void logException(String message, Throwable ex) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, ex));
    }
    
    public static void logWarning(String message) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.WARNING, message, null));
    }

    /**
     * @see  org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        _plugin = this;
    }

    /**
     * @see  org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        _plugin = null;
        super.stop(context);
    }

}
