/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform;

import com.windowtester.runtime.util.ScreenCapture;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The activator class controls the plug-in life cycle.
 */
public class PlatformActivator extends AbstractUIPlugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.wtc.eclipse.platform"; //$NON-NLS-1$

    // The shared instance
    private static PlatformActivator _plugin;

    // Basic debugging option
    private static final String OPTION_DEBUG = "/logging/debug"; //$NON-NLS-1$

    private Map<String, Boolean> _options;

    // ILogger implementation details
    private ILog _logInstance = null;

    /**
     * The constructor.
     */
    public PlatformActivator() {
        _options = new HashMap<String, Boolean>();
    }

    /**
     * Generate a thread dump showing the state of all threads in the system. This can be
     * used for logging, etc.
     */
    public static void generateThreadDump(StringBuilder buffer) {
        Set<Thread> allThreads = getAllThreads();

        for (Thread nextThread : allThreads) {
            generateThreadDump(nextThread, nextThread.getStackTrace(), buffer);
        }
    }

    public static void generateThreadDump(Thread t,
                                          StackTraceElement[] frames,
                                          StringBuilder buffer) {
        buffer.append(t.toString());
        buffer.append("\n"); //$NON-NLS-1$

        for (StackTraceElement nextElement : frames) {
            buffer.append("   "); //$NON-NLS-1$

            if (nextElement == frames[0]) {
                buffer.append("   "); //$NON-NLS-1$
            } else {
                buffer.append("at "); //$NON-NLS-1$
            }

            buffer.append(nextElement.toString());
            buffer.append("\n"); //$NON-NLS-1$
        }

        buffer.append("\n"); //$NON-NLS-1$
    }

    public static Set<Thread> getAllThreads() {
        // Find the root thread group
        ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();

        while (root.getParent() != null) {
            root = root.getParent();
        }

        // Visit each thread group
        Set<Thread> allThreads = new HashSet<Thread>();
        getAllThreads(root, 0, allThreads);

        return allThreads;
    }

    /**
     * This method recursively visits all thread groups under `group'.
     */
    private static void getAllThreads(ThreadGroup group,
                                      int level, Set<Thread> io_allThreads) {
        // Get threads in `group'
        int numThreads = group.activeCount();
        Thread[] threads = new Thread[numThreads * 2];
        numThreads = group.enumerate(threads, false);

        // Enumerate each thread in `group'
        for (int i = 0; i < numThreads; i++) {
            // Get thread
            io_allThreads.add(threads[i]);
        }

        // Get thread subgroups of `group'
        int numGroups = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
        numGroups = group.enumerate(groups, false);

        // Recursively visit each subgroup
        for (int i = 0; i < numGroups; i++) {
            getAllThreads(groups[i], level + 1, io_allThreads);
        }
    }

    /**
     * Returns the shared instance.
     *
     * @return  the shared instance
     */
    public static PlatformActivator getDefault() {
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

    /**
     * @return  IPath - Get the path to the results directory where copied test file
     *          results will be stored
     */
    public IPath getResultsPath() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IPath workspacePath = root.getLocation();

        return workspacePath.append(new Path("results")); //$NON-NLS-1$
    }

    /**
     * @return  String - A formatted thread dump
     */
    public static String getThreadDump() {
        StringBuilder buffer = new StringBuilder();

        buffer.append("\n"); //$NON-NLS-1$
        buffer.append("\n"); //$NON-NLS-1$
        buffer.append("===========================================================\n"); //$NON-NLS-1$
        buffer.append("=         T H R E A D    D U M P    B E G I N S           =\n"); //$NON-NLS-1$
        buffer.append("===========================================================\n"); //$NON-NLS-1$
        buffer.append("\n"); //$NON-NLS-1$

        generateThreadDump(buffer);

        buffer.append("\n"); //$NON-NLS-1$
        buffer.append("===========================================================\n"); //$NON-NLS-1$
        buffer.append("=      T H R E A D    D U M P    C O M P L E T E          =\n"); //$NON-NLS-1$
        buffer.append("===========================================================\n"); //$NON-NLS-1$
        buffer.append("\n"); //$NON-NLS-1$

        return buffer.toString();
    }

    private static String inferCaller(int frames) {
        StringBuilder output = new StringBuilder();

        // Get the stack trace.
        StackTraceElement[] stack = (new Throwable()).getStackTrace();

        // First, search back to a method in the Logger class.
        int ix = 0;
        output.append("\n[THREAD DUMP INFER CALLER: "); //$NON-NLS-1$

        while ((ix < stack.length) && (ix <= frames)) {
            StackTraceElement frame = stack[ix];

            if (ix > 0) {
                output.append("\n            at "); //$NON-NLS-1$
            }

            output.append(frame.getClassName());
            output.append("."); //$NON-NLS-1$
            output.append(frame.getMethodName());
            output.append("("); //$NON-NLS-1$
            output.append(frame.getFileName());
            output.append(":"); //$NON-NLS-1$
            output.append(frame.getLineNumber());
            output.append(")"); //$NON-NLS-1$

            ix++;
        }

        output.append("  ]\n\n"); //$NON-NLS-1$

        return output.toString();
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
        logDebug(message, OPTION_DEBUG);
    }

    public static void logDebug(String message, String option) {
        if (isOptionEnabled(option)) {
            logDebugInternal(message);
        }
    }

    private static void logDebugInternal(String message) {
        log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.INFO, message, null));
        System.out.println(message);
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

    /**
     * Generate a thread dump and send it to the error log, to be reported from the given
     * plugin.
     */
    public static void logThreadDump() {
        String inferCaller = inferCaller(5);
        logError(inferCaller);

        ScreenCapture.createScreenCapture("logThreadDump"); //$NON-NLS-1$

        logError(getThreadDump());
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
