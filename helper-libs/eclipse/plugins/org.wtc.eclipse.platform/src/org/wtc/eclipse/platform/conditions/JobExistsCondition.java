/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.condition.ICondition;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.internal.helpers.impl.WaitForJobsRegistry;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JobExistsCondition - Condition that polls the eclipse job manager and tests if there
 * are any running jobs. Will return false (condition not met) when there are WAITING or
 * RUNNING jobs. Can be passed a list of exceptions (jobs that can be RUNNING or WAITING
 * and still meet this condition)
 *
 * @since  3.8.0
 */
public class JobExistsCondition implements ICondition {
    // default to waiting 6 minutes
    public static long DEFAULT_WAIT_FOR_JOBS_TIMEOUT = 360000;

    private final Set<Pattern> _exceptions;

    private boolean _hasLogged = false;

    /**
     * JobExistsCondition - Default constructor. No job exceptions allowed
     */
    public JobExistsCondition() {
        this(null);
    }

    /**
     * JobExistsCondition - Constructor that defines the list of jobs that may still be
     * RUNNING or WAITING and still meet this condition.
     *
     * @param  exceptions  - If jobs with these names are running, the condition can still
     *                     be met
     */
    public JobExistsCondition(Collection<String> exceptions) {
        _exceptions = new HashSet<Pattern>();

        if (exceptions != null) {
            compileExceptions(exceptions);
        }

        Collection<String> expectedPatterns = WaitForJobsRegistry.getExpectedJobs();
        compileExceptions(expectedPatterns);
    }

    /**
     * Compile regex patterns into the exceptions list.
     */
    private void compileExceptions(Collection<String> exceptions) {
        for (String nextPatternString : exceptions) {
            Pattern nextPattern = Pattern.compile(nextPatternString);
            _exceptions.add(nextPattern);
        }
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
     * test.
     *
     * @return  boolean - True if there are only jobs that are SLEEPING or RUNNING/WAITING
     *          and in the exclusions list; False otherwise
     */
    public boolean test() {
        IJobManager manager = Job.getJobManager();

        Job[] allJobs = manager.find(null);

        StringBuffer runningJobs = new StringBuffer();
        runningJobs.append("WAIT --> For IJobManager running jobs ("); //$NON-NLS-1$
        runningJobs.append(allJobs.length);
        runningJobs.append(")"); //$NON-NLS-1$

        boolean isIdle = true; // optimist

        for (Job nextJob : allJobs) {
            String stateString = "?? UNKNOWN STATE ??"; //$NON-NLS-1$
            int state = nextJob.getState();
            String jobName = nextJob.getName();

            switch (state) {
                case Job.RUNNING: {
                    stateString = "RUNNING"; //$NON-NLS-1$

                    if (!isExpected(jobName)) {
                        isIdle = false;
                    }

                    break;
                }

                case Job.WAITING: {
                    stateString = "WAITING"; //$NON-NLS-1$

                    if (!isExpected(jobName)) {
                        isIdle = false;
                    }

                    break;
                }

                case Job.SLEEPING: {
                    stateString = "SLEEPING"; //$NON-NLS-1$

                    break;
                }

                default: {
                    break;
                }
            } // endswitch

            runningJobs.append("\n"); //$NON-NLS-1$
            runningJobs.append(nextJob.getName());
            runningJobs.append(" [["); //$NON-NLS-1$
            runningJobs.append(stateString);
            runningJobs.append("]] <"); //$NON-NLS-1$
            ISchedulingRule rule = nextJob.getRule();
            runningJobs.append((rule == null) ? "null" : rule.toString()); //$NON-NLS-1$
            runningJobs.append(">"); //$NON-NLS-1$
        }

        runningJobs.append("\n"); //$NON-NLS-1$
        PlatformActivator.logDebug(runningJobs.toString());

        return isIdle;
    }
    
    /**
     * Is the given job an expected exception?
     */
    private boolean isExpected(String jobName) {
        
        for(Pattern nextPattern : _exceptions) {
            Matcher m = nextPattern.matcher(jobName);
            if(m.matches()) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        singleLogThreadDump();

        return "the JobManager to be idle! \n"; //$NON-NLS-1$
    }

}
