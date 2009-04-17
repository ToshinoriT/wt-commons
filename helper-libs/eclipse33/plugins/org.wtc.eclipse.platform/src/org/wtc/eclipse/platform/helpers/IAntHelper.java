/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import java.io.File;

/**
 * Helper to execute Ant scripts.
 * 
 * @since 3.8.0
 */
public interface IAntHelper {
    /**
     * executeExternalAntFile - Create a system process to execute an Ant build file at
     * the given arbitrary location.
     *
     * @since 3.8.0
     * @param  ui               - Driver for UI generated input
     * @param  antFile-         The file representation of the ant script to execute
     * @param  target           - The target in the Ant file to execute
     * @param  targetArguments  - Up to 10 additional arguments may be provided to the Ant
     *                          file
     */
    public void executeExternalAntFile(IUIContext ui,
                                       File antFile,
                                       String target,
                                       String... targetArgs);

    /**
     * executePluginAntFile - Create a system process to execute an Ant build file at the
     * given location in the given plugin.
     *
     * @since 3.8.0
     * @param  ui               - Driver for UI generated input
     * @param  plugin           - The plugin containing the Ant file to execute
     * @param  antFilePath      - The plugin-relative path of the Ant file to execute.
     *                          <br/>
     *                          Ex: <i>
     *                          /resources/testfiles/AntTest/subfolder/build.xml</i>
     * @param  target           - The target in the Ant file to execute
     * @param  targetArguments  - Up to 10 additional arguments may be provided to the Ant
     *                          file
     */
    public void executePluginAntFile(IUIContext ui,
                                     Plugin plugin,
                                     IPath antFilePath,
                                     String target,
                                     String... targetArgs);

    /**
     * executeWorkspaceAntFile - Create a system process to execute an Ant build file at
     * the given workspace location.
     *
     * @since 3.8.0
     * @param  ui               - Driver for UI generated input
     * @param  antFilePath      - The full path (project name included) of the Ant file in
     *                          the workspace to execute.<br/>
     *                          Ex: <i>/RuntimeTestProject/subfolder/build.xml</i>
     * @param  target           - The target in the Ant file to execute
     * @param  targetArguments  - Up to 10 additional arguments may be provided to the Ant
     *                          file
     */
    public void executeWorkspaceAntFile(IUIContext ui,
                                        IPath antFilePath,
                                        String target,
                                        String... targetArgs);

}
