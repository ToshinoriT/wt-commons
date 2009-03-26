/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IJavaHelper;
import org.wtc.eclipse.platform.helpers.IJavaProjectHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Create a java project, add a JAR to the build path, export that JAR to the external
 * classpath.
 */
public class JavaHelperBuildPathJAROpsTest extends EclipseUITest {
    /**
     * Do some stuff.
     */
    public void testBuldPathJAROps() {
        IUIContext ui = getUI();

        String projectName = "BuildPathJAROpsTest"; //$NON-NLS-1$

        IJavaProjectHelper javaProject = EclipseHelperFactory.getJavaProjectHelper();
        javaProject.createProject(ui, projectName);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.importFiles(ui, PlatformActivator.getDefault(), new Path("/resources/testfiles/JavaHelperBuildPathJAROpsTest"), //$NON-NLS-1$
                              new Path(projectName + "/libs")); //$NON-NLS-1$

        IPath jarPath = new Path(projectName + "/libs/jarone.jar"); //$NON-NLS-1$
        IJavaHelper java = EclipseHelperFactory.getJavaHelper();
        java.addProjectJARToClasspath(ui, jarPath);

        java.exportProjectJAROnClasspath(ui, jarPath);
    }
}
