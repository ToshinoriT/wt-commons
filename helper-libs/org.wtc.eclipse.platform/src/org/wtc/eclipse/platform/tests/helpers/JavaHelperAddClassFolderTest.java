/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IJavaHelper;
import org.wtc.eclipse.platform.helpers.IJavaProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Create a Java project, add a class folder.
 */
public class JavaHelperAddClassFolderTest extends EclipseUITest {
    /**
     * Smoke test.
     */
    public void testAddClassFolder() {
        IUIContext ui = getUI();

        String projectName = "JavaHelperAddClassFolderTest"; //$NON-NLS-1$

        IJavaProjectHelper javaProj = EclipseHelperFactory.getJavaProjectHelper();
        javaProj.createProject(ui, projectName);

        IPath newFolderPath = new Path(projectName + "/folder"); //$NON-NLS-1$
        IJavaHelper java = EclipseHelperFactory.getJavaHelper();
        java.addSourceFolder(ui, newFolderPath);

        java.removeAllSourceFolders(ui, projectName);
    }
}
