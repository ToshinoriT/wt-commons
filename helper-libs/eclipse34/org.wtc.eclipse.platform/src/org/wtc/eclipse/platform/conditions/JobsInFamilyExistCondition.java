/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.condition.ICondition;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.util.ExceptionUtil;
import java.text.MessageFormat;

/**
 * JobsInFamilyExistCondition - Condition that checks for jobs in the specified family.
 * Will return true if either the max number of iterations are hit or if a call to
 * JobManager.find(family) returns a non-zero number of jobs and the call to join
 * terminates.
 */
public class JobsInFamilyExistCondition implements ICondition {
    private static final String DEBUG_OPTION = "/logging/jobFamilyExistsCondition"; //$NON-NLS-1$

    /**
     * Default length of time (in msec) that the logic which waits for jobs sleeps on each
     * iteration.
     */
    public static final int DEFAULT_SLEEP_DURATION = 150;

    /**
     * We want to join on the Job Manager by job family. We want to join instead of
     * counting jobs because there can be jobs in the WAITING, SLEEPING, or NONE states
     * for the family in the Job Manager. Join will only return when all the RUNNING jobs
     * have completed. Since join blocks the current thread, and the target thread for
     * ui.wait(ICondition) execution is on the test thread, a join in the test method
     * would block the test thread. What happens if a Job in that family sends an event to
     * the UI thread to pop a dialog? Since the test thread is blocked,
     * ui.handleConditions() is never called and the Shell Monitor is never alerted of the
     * raised dialog. The result? A hanging test because the test thread (which sends UI
     * events) is blocked on the Job to complete, but the Job is waiting on UI events to
     * complete.
     *
     * <p>The solution is to wait on a separate Job Manager monitor thread, then poll that
     * thread from the condition's test method. When the monitor thread completes
     * execution then we know the Job Manager is done and we can contiune. If any dialogs
     * were popped during the wait then ui.handleConditions() can still be called since
     * the test thread is not blocked</p>
     */
    private enum WaitResult {
        DONE_JOBSCOMPLETE,
        DONE_NOJOBSFOUND,
        STARTING,
        WAITING
    }

    private final Object _family;
    private final boolean _returnIfNoJobs;

    // Monitor the JobManager without blocking the test thread
    private volatile JobManagerMonitorThread _monitor;

    private boolean _hasLogged = false;

    /**
     * JobInFamilyExistCondition.
     */
    public JobsInFamilyExistCondition(Object family) {
        this(family, true);
    }

    /**
     * JobInFamilyExistCondition.
     */
    public JobsInFamilyExistCondition(Object jobFamily,
                                      boolean returnIfNoJobs) {
        _family = jobFamily;
        _returnIfNoJobs = returnIfNoJobs;
    }

    /**
     * Will only log a thread dump once per condition.
     */
    private void singleLogThreadDump() {
        if (!_hasLogged) {
            PlatformActivator.logThreadDump();
            _hasLogged = true;
        }
    }

    /**
     * @return  boolean -If a job manager monitor thread exists, do nothing. If a job
     *          manager monitor does not exist, then create one and start it. Return true
     *          iff a job manager monitor thread was started
     */
    private synchronized boolean startIfNeeded() {
        boolean started = false;

        if (_monitor == null) {
            _monitor = new JobManagerMonitorThread();
            _monitor.start();
            started = true;

            PlatformActivator.logDebug("[JFEC] --> A MONITOR THREAD WAS STARTED!", DEBUG_OPTION); //$NON-NLS-1$
        }

        return started;
    }

    /**
     * test.
     *
     * @return  Returns false if there were no scheduled jobs in the specified family. If
     *          there were specified jobs, returns true after executing a join().
     */
    public boolean test() {
        // First, if a monitor has yet to be started, or a monitor
        // has died, then start a new one
        if (!startIfNeeded()) {
            // If a monitor was already started check the status.
            // If jobs were started and successfully joined, then we're
            // done
            WaitResult currentStatus = _monitor.getStatus();

            if (currentStatus == WaitResult.DONE_JOBSCOMPLETE) {
                PlatformActivator.logDebug("[JFEC] --> JOBS WERE FOUND AND JOINED", //$NON-NLS-1$
                                           DEBUG_OPTION); 

                return true;
            }

            // If the thread has completed but no jobs were run,
            // then short circuit if and only if we're *not* requiring
            // jobs to run for the condition to be met
            if (currentStatus == WaitResult.DONE_NOJOBSFOUND) {
                // If the thread is dead, and we expected jobs to
                // be run, then keep waiting (The next loop will
                // restart the thread)
                if (!_returnIfNoJobs) {
                    PlatformActivator.logDebug("[JFEC] --> NO JOBS WERE FOUND BUT WERE EXPECTED.", //$NON-NLS-1$
                                               DEBUG_OPTION); 
                    _monitor = null;

                    return false;
                }

                // Otherwise, we're not requiring jobbs to run,
                // and no jobs were found, so we're done waiting
                PlatformActivator.logDebug("[JFEC] --> NO JOBS WERE FOUND BUT WE DON'T CARE", //$NON-NLS-1$
                                           DEBUG_OPTION);

                return true;
            }
        }

        // If a monitor thread was started, then let's wait for it
        // to poll the job manager
        PlatformActivator.logDebug("[JFEC] --> STILL WAITING...", DEBUG_OPTION); //$NON-NLS-1$

        return false;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        singleLogThreadDump();

        return "All Jobs in family " + _family + " to finish executing"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    private class JobManagerMonitorThread extends Thread {
        private WaitResult _waitResult = WaitResult.STARTING;

        /**
         * Save the data members.
         */
        public JobManagerMonitorThread() {
            _waitResult = WaitResult.STARTING;
        }

        /**
         * @return  WaitResult - Safely get the status
         */
        public synchronized WaitResult getStatus() {
            return _waitResult;
        }

        /**
         * @see  java.lang.Thread#run()
         */
        @Override
        public void run() {
            // Make sure to reset the status
            setStatus(WaitResult.WAITING);

            final IJobManager jobMgr = Job.getJobManager();
            final Job[] jobs = jobMgr.find(_family);

            if (jobs.length > 0) {
                try {
                    jobMgr.join(_family, null);
                } catch (InterruptedException ie) {
                    // if interrupted, keep the waiting status
                    // and try again
                } catch (OperationCanceledException oce) {
                    // Keep the waiting status and try again
                    String message = MessageFormat.format("JobManager.join() cancelled({0})", //$NON-NLS-1$
                                                          new Object[] {
                            ExceptionUtil.formatThrowable(oce)
                        });
                    PlatformActivator.logDebug(message);
                }

                // The job family contained running jobs and we've
                // successfully waited for those jobs to complete
                setStatus(WaitResult.DONE_JOBSCOMPLETE);
            }

            // if we are returning immediately then mark the
            // status accordingly and we're done
            setStatus(WaitResult.DONE_NOJOBSFOUND);

            PlatformActivator.logDebug("[JFEC] --> A MONITOR THREAD COMPLETED <" + _waitResult + ">", DEBUG_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /**
         * Safely set the status.
         */
        public synchronized void setStatus(WaitResult status) {
            _waitResult = status;
        }
    }
}
