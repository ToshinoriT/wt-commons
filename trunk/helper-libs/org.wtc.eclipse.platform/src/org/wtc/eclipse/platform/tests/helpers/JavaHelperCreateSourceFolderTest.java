/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IJavaHelper;
import org.wtc.eclipse.platform.helpers.IJavaProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * DESCRIPTION:
 */
public class JavaHelperCreateSourceFolderTest extends EclipseUITest {
    public void test() {
        IUIContext ui = getUI();

        String projectName = "CreateSourceFolderTest"; //$NON-NLS-1$

        // -------- 1 -------- //
        IJavaProjectHelper javaProjectHelper = EclipseHelperFactory.getJavaProjectHelper();
        javaProjectHelper.createProject(ui, projectName);

        // -------- 2 -------- //
        IJavaHelper javaHelper = EclipseHelperFactory.getJavaHelper();
        javaHelper.addSourceFolder(ui, new Path(projectName + "/newSource1")); //$NON-NLS-1$

        // -------- 3 -------- //
        javaHelper.addSourceFolder(ui, new Path(projectName + "/newSource2/nested")); //$NON-NLS-1$

    }
}
