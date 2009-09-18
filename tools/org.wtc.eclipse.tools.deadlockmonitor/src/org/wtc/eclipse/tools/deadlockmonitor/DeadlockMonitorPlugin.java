/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons
 */
package org.wtc.eclipse.tools.deadlockmonitor;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.wtc.eclipse.tools.deadlockmonitor.thread.DeadlockMonitorThread;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * DeadlockMonitorPlugin used to manage the startup of Deadlock Monitor Threads.
 */
public class DeadlockMonitorPlugin extends AbstractUIPlugin implements IStartup {
    private static final String INDENT = "     "; //$NON-NLS-1$

    // The shared instance.
    private static DeadlockMonitorPlugin _plugin;

    private static ILog _logInstance = null;
    public static final String DEBUG_OPTION_DEBUG = "/logging/debug"; //$NON-NLS-1$
    private static HashMap<String, Boolean> _options = new HashMap<String, Boolean>();

    private final String _option_DeadlockMonitorEnabled;
    private final String _option_DeadlockMonitorInterval;
    private final String _option_DisposeWatcherEnabled;

    /**
     * Save the data members.
     */
    public DeadlockMonitorPlugin() {
        _plugin = this;

        _option_DeadlockMonitorEnabled = "/deadlockmonitor/enabled"; //$NON-NLS-1$
        _option_DeadlockMonitorInterval = "/deadlockmonitor/interval"; //$NON-NLS-1$
        _option_DisposeWatcherEnabled = "/disposeWatcher"; //$NON-NLS-1$
    }

    /**
     * @see  org.eclipse.ui.IStartup#earlyStartup()
     */
    public void earlyStartup() {
        if (isDeadlockMonitorOptionEnabled()) {
            int minutes = getDeadlockMonitorInterval();

            String message = "~~ DEADLOCK MONITOR ENABLED ~~"; //$NON-NLS-1$
            System.out.println(message);
            System.err.println(message);
            logDebug(message);

            DeadlockMonitorThread monitor = new DeadlockMonitorThread(minutes);
            monitor.start();
        } else {
            String message = "~~ DEADLOCK MONITOR ___NOT___ ENABLED ~~"; //$NON-NLS-1$
            System.out.println(message);
            System.err.println(message);
            logDebug(message);
        }

        if (isDisposeWatcherOptionEnabled()) {
            new DisposeWatcher().start();
        }

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

    public static void generateThreadDump(Thread t, StackTraceElement[] frames, StringBuilder buffer) {
        buffer.append(t.toString());
        buffer.append("\n"); //$NON-NLS-1$

        for (StackTraceElement nextElement : frames) {
            buffer.append(INDENT);

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

    /**
     */
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
     * @return  int - Return the deadlock monitor interval (in minutes) from the options
     *          file
     */
    private int getDeadlockMonitorInterval() {
        String fullyQualifiedOption = getBundle().getSymbolicName() + _option_DeadlockMonitorInterval;

        String value = Platform.getDebugOption(fullyQualifiedOption);

        int minutes = 5;

        if (value != null) {
            try {
                minutes = Integer.parseInt(value);
            } catch (Exception e) {
                logException(e);
            }
        }

        return minutes;
    }

    /**
     * @return  DeadlockMonitorPlugin - Get a shared instance
     */
    public static DeadlockMonitorPlugin getDefault() {
        return _plugin;
    }

    /**
     * Returns the plugin ID.
     */
    public static String getID() {
        return _plugin.getBundle().getSymbolicName();
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

    /*************************************************************************/
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

    /**
     * @return  boolean - True if the runtime options indicate that a deadlock monitor
     *          should be enabled
     */
    private boolean isDeadlockMonitorOptionEnabled() {
        String fullyQualifiedOption = getBundle().getSymbolicName() + _option_DeadlockMonitorEnabled;

        String value = Platform.getDebugOption(fullyQualifiedOption);

        return (value != null) && (value.equalsIgnoreCase("true")); //$NON-NLS-1$
    }

    /**
     * @return  boolean - True if the runtime options indicate that a deadlock monitor's
     *          dispose watcher should be enabled
     */
    private boolean isDisposeWatcherOptionEnabled() {
        String fullyQualifiedOption = getBundle().getSymbolicName() + _option_DisposeWatcherEnabled;

        String value = Platform.getDebugOption(fullyQualifiedOption);

        return (value != null) && (value.equalsIgnoreCase("true")); //$NON-NLS-1$
    }

    /**
     * @return  boolean - Read the .options file for a property value
     */
    private static boolean isOptionEnabled(String option) {
        if (option == null) {
            return true;
        }

        String fullyQualifiedOption = getID() + option;

        Boolean optionValue = _options.get(fullyQualifiedOption);

        if (optionValue == null) {
            String value = Platform.getDebugOption(fullyQualifiedOption);
            optionValue = (value != null) && (value.equalsIgnoreCase("true")); //$NON-NLS-1$
            _options.put(fullyQualifiedOption, optionValue);
        }

        return optionValue;
    }

    /**
     * Log to the eclipse log.
     *
     * @param  message
     * @param  level
     * @param  throwable
     */
    private static void log(String option,
                            String message,
                            int level,
                            Throwable throwable) {
        if (_logInstance == null) {
            _logInstance = Platform.getLog(getDefault().getBundle());
        }

        if (isOptionEnabled(option)) {
            Status status = new Status(level, getDefault().getBundle().getSymbolicName(), level, (message != null) ? message : "", //$NON-NLS-1$
                                       throwable);

            _logInstance.log(status);
        }
    }

    /**
     * log the message to the exclipse logger.
     */
    public static void logDebug(String message) {
        logDebug(DEBUG_OPTION_DEBUG, message);
    }

    /**
     * log the message to the exclipse logger.
     */
    public static void logDebug(String option, String message) {
        log(option,
            message,
            IStatus.INFO,
            null);
    }

    /**
     * log the message to the exclipse logger.
     */
    public static void logError(String message) {
        log(null,
            message,
            IStatus.ERROR,
            null);
    }

    /**
     * log the exception to the exclipse logger.
     */
    public static void logException(Throwable throwable) {
        log(null, throwable.getMessage(), IStatus.ERROR, throwable);

        Throwable cause = throwable.getCause();

        if (cause != null) {
            logException(cause);
        }
    }

    /**
     * Generate a thread dump and send it to the error log, to be reported from the given
     * plugin.
     */
    public static void logThreadDump() {
        String inferCaller = inferCaller(5);
        logError(inferCaller);

//        ScreenCapture.createScreenCapture("logThreadDump");

        log(null, getThreadDump(), IStatus.ERROR, null);
    }

    /**
     * @see  org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * @see  org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        _plugin = null;
    }

    /**
     * Thread to wait until a workbench exists, then attaches a dispose listener to the
     * root shell.
     */
    private static class DisposeWatcher extends Thread {
        @Override
        public void run() {
            final boolean[] loaded = new boolean[1];
            loaded[0] = false;

            while (!loaded[0] && PlatformUI.isWorkbenchRunning()) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }

                logError("<DISPOSE WATCHER INITIALIZING>"); //$NON-NLS-1$
                Display display = Display.getDefault();

                if (display != null) {
                    display.syncExec(new Runnable() {
                            public void run() {
                                IWorkbench workbench = PlatformUI.getWorkbench();

                                if (workbench != null) {
                                    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

                                    if (window != null) {
                                        Shell shell = window.getShell();

                                        if (shell != null) {
                                            logError("<DISPOSE WATCHER ATTATCHING>"); //$NON-NLS-1$
                                            shell.addDisposeListener(new DisposeListener() {
                                                    public void widgetDisposed(DisposeEvent e) {
                                                        logError("THE FOLLOWING THREAD DUMP IS NOT A HANG. IT IS DEBUG INFO FOR CR 296562!"); //$NON-NLS-1$
                                                        logError("THE FOLLOWING THREAD DUMP IS NOT A HANG. IT IS DEBUG INFO FOR CR 296562!"); //$NON-NLS-1$
                                                        logThreadDump();
                                                        logError("THE PREVIOUS THREAD DUMP IS NOT A HANG. IT IS DEBUG INFO FOR CR 296562!"); //$NON-NLS-1$
                                                        logError("THE PREVIOUS THREAD DUMP IS NOT A HANG. IT IS DEBUG INFO FOR CR 296562!"); //$NON-NLS-1$
                                                        logError("~~~~~~~~~~~~"); //$NON-NLS-1$
                                                        logError(e.getSource().toString());
                                                        logError("/~~~~~~~~~~~~"); //$NON-NLS-1$
                                                    }
                                                });
                                            loaded[0] = true;
                                        }
                                    }
                                }
                            }
                        });
                }
            }
        }
    }

}
