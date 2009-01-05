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
 * Test the Java Helper's create package method.
 */
public class JavaHelperCreatePackageTest extends EclipseUITest {
    /**
     * Create a Java project, create a package.
     */
    public void testCreatePackage() {
        IUIContext ui = getUI();

        IJavaProjectHelper javaProject = EclipseHelperFactory.getJavaProjectHelper();
        javaProject.createProject(ui, "JavaHelperCreatePackageTest"); //$NON-NLS-1$

        IJavaHelper java = EclipseHelperFactory.getJavaHelper();
        java.createPackage(ui, new Path("JavaHelperCreatePackageTest"), "pack1.pack2.pack3"); //$NON-NLS-1$ //$NON-NLS-2$
        java.createPackage(ui, new Path("JavaHelperCreatePackageTest"), "pack1.pack4.pack3"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
