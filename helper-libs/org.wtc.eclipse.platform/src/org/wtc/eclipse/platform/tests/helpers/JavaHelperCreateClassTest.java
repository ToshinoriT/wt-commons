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
 * Test the Java Helper's create package method.
 */
public class JavaHelperCreateClassTest extends EclipseUITest {
    /**
     * Create a Java project, create a class.
     */
    public void testCreateClass() {
        IUIContext ui = getUI();

        IJavaProjectHelper javaProject = EclipseHelperFactory.getJavaProjectHelper();
        javaProject.createProject(ui, "JavaHelperCreateClassTest"); //$NON-NLS-1$

        IJavaHelper java = EclipseHelperFactory.getJavaHelper();
        java.createClass(ui, new Path("JavaHelperCreateClassTest"), "", "Foo"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        IPath barPath = java.createClass(ui, new Path("JavaHelperCreateClassTest"), "pack1", "Bar"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        java.createClass(ui, new Path("JavaHelperCreateClassTest"), "pack1.pack2", "Baz"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        java.createClass(ui,
                         new Path("JavaHelperCreateClassTest"), //$NON-NLS-1$
                         "pack1.pack2", //$NON-NLS-1$
                         "SuperTypeTest", //$NON-NLS-1$
                         "java.lang.Exception"); //$NON-NLS-1$ 

        java.renameClass(ui, barPath, "NewFooName"); //$NON-NLS-1$
    }
}
