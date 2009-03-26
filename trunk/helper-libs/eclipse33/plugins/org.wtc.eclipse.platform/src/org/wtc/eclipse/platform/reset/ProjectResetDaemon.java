/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.reset;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.util.ScreenCapture;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.core.reset.IResetDaemon;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.conditions.JobExistsCondition;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

/**
 * Reset daemon that makes sure all projects in the workspace have been copied into a
 * fixed location then removed from the workspace.
 */
public class ProjectResetDaemon implements IResetDaemon {
    private static final String[] TOFILTER = {
            "class", //$NON-NLS-1$
            "jar", "JAR", //$NON-NLS-1$ //$NON-NLS-2$
            "zip", "ZIP" //$NON-NLS-1$ //$NON-NLS-2$
        };

    // THE EXTENSTIONS OF FILES THAT ARE NOT BE BE COPIED IN THE
    // RESULTS ZIP FILES
    private final Set<String> FILTERED_EXTENSIONS;

    private final ProjectResetFileFilter _fileFilter;

    /**
     * Save the data members.
     */
    public ProjectResetDaemon() {
        FILTERED_EXTENSIONS = new HashSet<String>();

        for (String nextExt : TOFILTER) {
            FILTERED_EXTENSIONS.add(nextExt);
        }

        _fileFilter = new ProjectResetFileFilter();
    }

    /**
     * @see  org.wtc.eclipse.core.reset.IResetDaemon#resetWorkspace(com.windowtester.runtime.IUIContext,
     *       org.wtc.eclipse.core.reset.IResetDaemon.ResetContext)
     */
    public void resetWorkspace(final IUIContext ui, final ResetContext context) {
        // Make sure all of the files are up to date
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.saveAndWait(ui);

        workbench.waitForBuild(ui);

        ui.wait(new JobExistsCondition(null),
                120000,
                1000);

        // If there are no projects, then there's nothing to do
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        if (root == null) {
            return;
        }

        final IProject[] allProjects = root.getProjects();

        if ((allProjects == null) || (allProjects.length == 0)) {
            return;
        }

        // OK, projects exist. Now collect and copy the projects in the workspace
        File[] projectLocations = new File[allProjects.length];

        for (int i = 0; i < allProjects.length; i++) {
            projectLocations[i] = allProjects[i].getLocation().toFile();
        }

        // Try to zip up the project contents. Even if we fail for some
        // reason, let's make sure everything is closed
        try {
            IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
            resources.createZipCopy(ui, context.getTestClassName(), projectLocations, _fileFilter);
        }
        // Never do this
        catch (Throwable t) {
            ScreenCapture.createScreenCapture(context.getTestClassName() + "_ProjectResetDaemon"); //$NON-NLS-1$
            PlatformActivator.logException(t);
        } finally {
            // Now that we've copied the files, let's close the projects
            // in a runnable that queues up resource change events
            IWorkspaceRunnable noResourceChangedEventsRunner = new IWorkspaceRunnable() {
                    public void run(IProgressMonitor runnerMonitor) throws CoreException {
                        CoreException lastCE = null;

                        for (IProject nextProject : allProjects) {
                            try {
                                nextProject.close(runnerMonitor);
                            } catch (CoreException ce) {
                                PlatformActivator.logException(ce);
                                lastCE = ce;
                            }
                        }

                        if (lastCE != null) {
                            throw lastCE;
                        }
                    }
                };

            boolean success = false;
            int retry = 0;

            while (!success && (retry < 5)) {
                try {
                    IWorkspace workspace = ResourcesPlugin.getWorkspace();
                    workspace.run(noResourceChangedEventsRunner, workspace.getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
                    success = true;
                } catch (CoreException ce) {
                    // We've already logged it
                    ui.pause(2000);
                    retry++;
                }
            }

            // Now that projects are closed, wait for the resource change events
            // to get fired and the change listeners to react
            ui.pause(5000);
            workbench.waitForBuild(ui);
            ui.wait(new JobExistsCondition(null),
                    120000,
                    1000);

            // Finally, delete the projects from the workspace
            noResourceChangedEventsRunner = new IWorkspaceRunnable() {
                public void run(IProgressMonitor runnerMonitor) throws CoreException {
                    CoreException lastCE = null;

                    for (IProject nextProject : allProjects) {
                        if (nextProject.exists()) {
                            try {
                                nextProject.delete(true, true, null);
                            } catch (CoreException ce) {
                                PlatformActivator.logException(ce);
                                lastCE = ce;
                            }
                        }
                    }

                    if (lastCE != null) {
                        throw lastCE;
                    }
                }
            };

            success = false;
            retry = 0;

            while (!success && (retry < 5)) {
                try {
                    IWorkspace workspace = ResourcesPlugin.getWorkspace();
                    workspace.run(noResourceChangedEventsRunner, workspace.getRoot(), IWorkspace.AVOID_UPDATE, new NullProgressMonitor());
                    success = true;
                } catch (CoreException ce) {
                    // We've already logged it
                    ui.pause(2000);
                    retry++;
                }
            }

            ui.pause(3000);
            workbench.waitForBuild(ui);

            ui.wait(new JobExistsCondition(null),
                    120000,
                    1000);

        }
    }

    /**
     * File filter for file extensions.
     */
    private class ProjectResetFileFilter implements FilenameFilter {
        /**
         * @see  java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File file, String name) {
            boolean shouldFilter = false;

            IPath filePath = new Path(file.getAbsolutePath());
            String extension = filePath.getFileExtension();

            if (extension != null) {
                shouldFilter = FILTERED_EXTENSIONS.contains(extension);
            }

            return !shouldFilter;
        }
    }
}
