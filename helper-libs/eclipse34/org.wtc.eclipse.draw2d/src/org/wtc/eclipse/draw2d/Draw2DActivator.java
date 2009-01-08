/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.draw2d;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Draw2DActivator extends AbstractUIPlugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.wtc.eclipse.draw2d"; //$NON-NLS-1$

    // The shared instance
    private static Draw2DActivator _plugin;
    
    // ILogger implementation details
    private ILog _logInstance = null;

    /**
     * The constructor.
     */
    public Draw2DActivator() {
    }

    /**
     * Returns the shared instance.
     *
     * @return  the shared instance
     */
    public static Draw2DActivator getDefault() {
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
    
    public static void log(IStatus status) {
        getDefault().getLogInstance().log(status);
    }
    
    public static void logDebug(String message) {
        log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.INFO, message, null));
        System.out.println(message);
    }
    
    public static void logException(Throwable ex) {
        logException(ex.getLocalizedMessage(), ex);
    }

    public static void logException(String message, Throwable ex) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, ex));
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
