/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IJavaProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Tests for creating Java projects.
 */
public class ProjectHelperJavaTest extends EclipseUITest {
    /**
     * test creation with separate source folder option.
     */
    public void testCreationWithSeparateSourceAndOutput() {
        IUIContext ui = getUI();

        IJavaProjectHelper java = EclipseHelperFactory.getJavaProjectHelper();
        java.createProject(ui, "JPHCreationWithSeparateSourceAndOutput", true); //$NON-NLS-1$
    }

    /**
     * testSimpleCreation - Simple project creation with just a name.
     */
    public void testSimpleCreation() {
        IUIContext ui = getUI();

        IJavaProjectHelper java = EclipseHelperFactory.getJavaProjectHelper();
        java.createProject(ui, "JavaProjectHelperTest"); //$NON-NLS-1$
    }
}
