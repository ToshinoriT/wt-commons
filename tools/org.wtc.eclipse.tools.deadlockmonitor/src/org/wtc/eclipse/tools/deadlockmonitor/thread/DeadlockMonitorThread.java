/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons
 */
package org.wtc.eclipse.tools.deadlockmonitor.thread;

import org.eclipse.core.resources.ResourcesPlugin;
import org.wtc.eclipse.tools.deadlockmonitor.DeadlockMonitorPlugin;

import java.io.File;
import java.util.Set;

/**
 * This isn't a full-blown deadlock detection tool. This is a lightweight guess to output
 * a thread dump when we think that we may be hanging during tests
 */
public class DeadlockMonitorThread extends Thread {
    private long _logLastModifed = -1;

    public final int _sleepIntervalMilis;

    /**
     * Save the data members.
     *
     * @param  intervalMinutes  - Poll the log for updates on this interval in minutes
     */
    public DeadlockMonitorThread(int intervalMinutes) {
        assert (intervalMinutes > 0);

        _sleepIntervalMilis = intervalMinutes // sleep interval in minutes
        * 60 // s/min
        * 1000; // ms/s
    }

    /**
     * @return  boolean - True if there is at least one Thread on this VM that is active;
     *          false if all Threads are waiting on locks from other Threads (a true
     *          deadlock)
     */
    private boolean areAnyThreadsActive() {
        boolean atLeastOneActive = false;

        Set<Thread> allThreads = DeadlockMonitorPlugin.getAllThreads();

        for (Thread nextThread : allThreads) {
            // Skip this thread
            if (nextThread != Thread.currentThread()) {
                State nextState = nextThread.getState();

                if (nextState.equals(State.NEW) || nextState.equals(State.RUNNABLE)) {
                    atLeastOneActive = true;

                    break;
                }
            }
        } // endfor

        if (!atLeastOneActive) {
            DeadlockMonitorPlugin.logError(" ~~ DEADLOCK DETECTED (areAnyThreadsActive) ~~"); //$NON-NLS-1$
        }

        return atLeastOneActive;
    }

    /**
     * @return  boolean - True if the log file has been modified (according to the
     *          timestamp) since the last time this Deadlock Monitor polled
     */
    private boolean hasLogBeenUpdated() {
        boolean wasUpdated = false;

        File workspaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

        File[] workspaceFiles = workspaceRoot.listFiles(new MetaDataDirFileFilter());

        if (workspaceFiles.length == 1) {
            File metadataDir = workspaceFiles[0];

            File[] metaDataFiles = metadataDir.listFiles(new LogFileFilter());

            if (metaDataFiles.length == 1) {
                File logFile = metaDataFiles[0];
                long lastModified = logFile.lastModified();

                if (lastModified != _logLastModifed) {
                    _logLastModifed = lastModified;
                    wasUpdated = true;
                }
            }
        }

        if (!wasUpdated) {
            DeadlockMonitorPlugin.logError(" ~~ DEADLOCK DETECTED (hasLogBeenUpdated) ~~"); //$NON-NLS-1$
        }

        return wasUpdated;

    }

    /**
     * @return  boolean - True if the criteria defined in this Deadlock Monitor indicate
     *          that a deadlock may have occurred.
     */
    private boolean isApplicationDeadlocked() {
        boolean deadlocked = false;

        //--------------------------------------------------------------------
        // 1. Check the log timestamp. If the log hasn't been updated since the
        //    last check then assume that a deadlock is keeping tracing output
        //    from being logged
        //--------------------------------------------------------------------
        deadlocked = deadlocked || !hasLogBeenUpdated();

        //--------------------------------------------------------------------
        // 2. Check active thread state. If all threads (other than this one)
        //    are waiting, then there is a deadlock
        //--------------------------------------------------------------------
        deadlocked = deadlocked || !areAnyThreadsActive();

        return deadlocked;
    }

    /**
     * run - While the JVM exists, poll the system state at a fixed interval and determine
     * if a deadlock condition exists. If so, print the Threads
     */
    @Override
    public void run() {
        while (true) {
            try {
                if (isApplicationDeadlocked()) {
                    DeadlockMonitorPlugin.logThreadDump();
                }

                sleep(_sleepIntervalMilis);
            } catch (InterruptedException ie) {
                DeadlockMonitorPlugin.logException(ie);
            }
        }
    }

    /**
     * Simple FileFilter for getting an Eclipse ".log" file.
     */
    private class LogFileFilter implements java.io.FileFilter {
        public boolean accept(File file) {
            return file.isFile() && file.getName().equals(".log"); //$NON-NLS-1$
        }

    }

    /**
     * Simple FileFilter for getting the directory (".metadata") that contains the Eclipse
     * log file.
     */
    private class MetaDataDirFileFilter implements java.io.FileFilter {
        public boolean accept(File file) {
            return file.isDirectory() && file.getName().equals(".metadata"); //$NON-NLS-1$
        }

    }

}
