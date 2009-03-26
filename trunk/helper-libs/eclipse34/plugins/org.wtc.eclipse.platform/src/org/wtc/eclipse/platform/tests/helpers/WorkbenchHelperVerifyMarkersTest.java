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
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IEditorHelper.Placement;
import org.wtc.eclipse.platform.helpers.IJavaHelper;
import org.wtc.eclipse.platform.helpers.IJavaProjectHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper.MarkerInfo;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * Create a java class with errors and verify the marker can be found.
 */
public class WorkbenchHelperVerifyMarkersTest extends EclipseUITest {
    /**
     * Create a Java project, create a Java class, edit the class with something that
     * gives a compile error, check the markers.
     */
    public void testMarkers() {
        IUIContext ui = getUI();

        String projectName = "TestMarkers"; //$NON-NLS-1$

        IJavaProjectHelper javaProject = EclipseHelperFactory.getJavaProjectHelper();
        javaProject.createProject(ui, projectName);

        IPath sourcePath = new Path(projectName + "/src"); //$NON-NLS-1$

        IJavaHelper java = EclipseHelperFactory.getJavaHelper();
        java.addSourceFolder(ui, sourcePath);

        String packageName = "test"; //$NON-NLS-1$
        String className = "TestMarkers"; //$NON-NLS-1$
        IPath classPath = java.createClass(ui, sourcePath, packageName, className);

        IEditorHelper editor = EclipseHelperFactory.getEditorHelper();
        editor.insertBlock(ui, PlatformActivator.getDefault(), new Path("/resources/testfiles/VerifyMarkersTest/insert.txt"), //$NON-NLS-1$
                           classPath,
                           "{", //$NON-NLS-1$
                           Placement.AFTER);

//        IUIHelper uihelper = EclipseHelperFactory.getUIHelper();
//        uihelper.freeze(ui);

        MarkerInfo error = new MarkerInfo();
        error.setIsError(true);
        error.setDescription("This method must return a result of type String"); //$NON-NLS-1$
        error.setResourceName(className + ".java"); //$NON-NLS-1$

        MarkerInfo warning = new MarkerInfo();
        warning.setIsError(false);
        warning.setDescription("The method .* from the type .* is never used locally"); //$NON-NLS-1$

        // Don't set resource name

        MarkerInfo[] infos = { error, warning };

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.verifyMarkers(ui, projectName, infos);
    }
}
