/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.helpers.impl;

import com.windowtester.runtime.IUIContext;
import junit.framework.TestCase;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.wtc.eclipse.core.util.Timestamp;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IAntHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.adapters.HelperImplAdapter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper with interacting with and executing Ant build files.
 */
public class AntHelperImpl extends HelperImplAdapter implements IAntHelper {
    /**
     * @see  org.wtc.eclipse.platform.helpers.IAntHelper#executeExternalAntFile(com.windowtester.runtime.IUIContext,
     *       java.io.File, java.lang.String, java.lang.String[])
     */
    public void executeExternalAntFile(IUIContext ui,
                                       File antFile,
                                       String target,
                                       String... targetArgs) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(antFile);
        TestCase.assertTrue("The ant file <" + antFile.getAbsolutePath() + "> does not exist", //$NON-NLS-1$ //$NON-NLS-2$
                            antFile.exists());

        logEntry2(antFile.toString(), target, getDisplayValue(targetArgs));

        final AntRunnable runner = new AntRunnable(antFile, target, targetArgs);
        final IProgressService progress = PlatformUI.getWorkbench().getProgressService();

        final Exception[] exceptions = new Exception[1];
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    try {
                        progress.busyCursorWhile(runner);
                    } catch (InvocationTargetException ite) {
                        exceptions[0] = ite;
                    } catch (InterruptedException ie) {
                        exceptions[0] = ie;
                    }
                }
            });

        if (exceptions[0] != null) {
            PlatformActivator.logException(exceptions[0]);
            TestCase.fail(exceptions[0].getLocalizedMessage());
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IAntHelper#executePluginAntFile(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       java.lang.String, java.lang.String[])
     */
    public void executePluginAntFile(IUIContext ui,
                                     Plugin plugin,
                                     IPath antFilePath,
                                     String target,
                                     String... targetArgs) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(plugin);
        TestCase.assertNotNull(antFilePath);
        TestCase.assertFalse(antFilePath.isEmpty());

        logEntry2(plugin.getBundle().getSymbolicName(), antFilePath.toPortableString(), target, getDisplayValue(targetArgs));

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        File antFile = resources.getFileFromPlugin(ui, plugin, antFilePath);

        executeExternalAntFile(ui,
                               antFile,
                               target,
                               targetArgs);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IAntHelper#executeWorkspaceAntFile(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String, java.lang.String[])
     */
    public void executeWorkspaceAntFile(IUIContext ui,
                                        IPath antFilePath,
                                        String target,
                                        String... targetArgs) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(antFilePath);
        TestCase.assertFalse(antFilePath.isEmpty());

        logEntry2(antFilePath.toPortableString(), target, getDisplayValue(targetArgs));

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        IFile antFile = resources.getFileFromWorkspace(ui, antFilePath);
        IPath discAbsolutePath = antFile.getLocation();

        executeExternalAntFile(ui, discAbsolutePath.toFile(), target, targetArgs);

        logExit2();
    }

    /**
     * @return  IPath - The file-system absolute path of the log file location
     */
    private IPath getLogFileLocation(File antFile, String target) {
        IPath antFilePath = new Path(antFile.getAbsolutePath());

        IPath resultsPath = PlatformActivator.getDefault().getResultsPath();

        while (antFilePath.segmentCount() > 2) {
            antFilePath = antFilePath.removeFirstSegments(1);
        }

        while (antFilePath.segmentCount() > 0) {
            resultsPath = resultsPath.append(new Path("/" + antFilePath.segment(0))); //$NON-NLS-1$
            antFilePath = antFilePath.removeFirstSegments(1);
        }

        // First, make sure the directory exists
        File logFolder = resultsPath.toFile();
        logFolder.mkdirs();

        resultsPath = resultsPath.append("/" + new Timestamp().toString() + ".log"); //$NON-NLS-1$ //$NON-NLS-2$

        return resultsPath;
    }

    /**
     * IRunnableWithProgress wrapper for an AntRunner.
     */
    private class AntRunnable implements IRunnableWithProgress {
        private final AntRunner _runner;

        /**
         * Save the data members.
         */
        public AntRunnable(File antFile, String target, String... targetArgs) {
            _runner = new AntRunner();

            // need to turn the default logger back on
            _runner.addBuildLogger("org.apache.tools.ant.DefaultLogger"); //$NON-NLS-1$

            List<String> arguments = new ArrayList<String>();
            arguments.add("-verbose"); //$NON-NLS-1$

            arguments.add("-logfile"); //$NON-NLS-1$
            IPath logFileLocation = getLogFileLocation(antFile, target);
            arguments.add(logFileLocation.toOSString());

            Collections.addAll(arguments, targetArgs);
            _runner.setArguments(arguments.toArray(new String[arguments.size()]));

            _runner.setBuildFileLocation(antFile.getAbsolutePath());

            if (target != null) {
                _runner.setExecutionTargets(new String[] { target });
            }
        }

        /**
         * @see  org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        public void run(IProgressMonitor monitor)
                 throws InvocationTargetException, InterruptedException {
            try {
                _runner.run(monitor);
            } catch (CoreException e) {
                throw new InvocationTargetException(e);
            }
        }
    }
}
