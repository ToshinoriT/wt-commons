/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.helpers.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Add and remove the list of expected jobs when waitNoJobs is called. For example, if a
 * server starts a job that is continuously running and listening for server status, then
 * the implementation of a server helper should register that job when a server is started
 * as an expected job. An expected job means that when waitNoJobs is called (waitNoJobs
 * waits until all of the Jobs in the JobManager are SLEEPING or STOPPING) the Job with
 * that title can be considered a RUNNING job and the condition can still be met
 */
public class WaitForJobsRegistry {
    // ------------------------------------------------------------------------
    // DEFAULT JOBS THAT WE'LL ALLOW TO RUN
    // ------------------------------------------------------------------------
    public static String JOB_JAVA_INDEXING = "Java indexing in progress"; //$NON-NLS-1$

    public static String[] DEFAULT_JOBS = {
            JOB_JAVA_INDEXING
        };

    private static WaitForJobsRegistry _instance;

    private final Set<String> _expectedJobs;

    /**
     * Initialize the data members.
     */
    private WaitForJobsRegistry() {
        _expectedJobs = new HashSet<String>();

        for (String nextDefaultJob : DEFAULT_JOBS) {
            _expectedJobs.add(nextDefaultJob);
        }
    }

    /**
     * @param  jobTitle  - Add a Job of this title to the list of expected jobs
     */
    public static void addExpectedJob(String jobTitle) {
        instance()._expectedJobs.add(jobTitle);
    }

    /**
     * @return  Collection<String> - Get the known collection of expected jobs by their
     *          title
     */
    public static Collection<String> getExpectedJobs() {
        return new ArrayList<String>(instance()._expectedJobs);
    }

    /**
     * @return  WaitForJobsRegistry - Get the shared instance
     */
    private static WaitForJobsRegistry instance() {
        if (_instance == null) {
            _instance = new WaitForJobsRegistry();
        }

        return _instance;
    }

    /**
     * @param  jobTitle  - Remove a Job of this title from the list of expected jobs
     */
    public static void removeExpectedJob(String jobTitle) {
        instance()._expectedJobs.remove(jobTitle);
    }
}
