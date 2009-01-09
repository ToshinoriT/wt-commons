/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

import org.wtc.eclipse.platform.internal.helpers.impl.AntHelperImpl;
import org.wtc.eclipse.platform.internal.helpers.impl.GenericEditorHelperImplAdapter;
import org.wtc.eclipse.platform.internal.helpers.impl.JavaDebuggingHelperImpl;
import org.wtc.eclipse.platform.internal.helpers.impl.JavaHelperImpl;
import org.wtc.eclipse.platform.internal.helpers.impl.JavaProjectHelperImpl;
import org.wtc.eclipse.platform.internal.helpers.impl.ProjectHelperImpl;
import org.wtc.eclipse.platform.internal.helpers.impl.ResourceHelperImpl;
import org.wtc.eclipse.platform.internal.helpers.impl.SimpleProjectHelperImpl;
import org.wtc.eclipse.platform.internal.helpers.impl.TestHelperImpl;
import org.wtc.eclipse.platform.internal.helpers.impl.UIHelperImpl;
import org.wtc.eclipse.platform.internal.helpers.impl.WorkbenchHelperImpl;

/**
 * Factory for finding Helper implementations.
 */
public class EclipseHelperFactory {
    // The shared instance
    private static EclipseHelperFactory _instance;

    // ----------------------------------------------------------------------
    // FIXED CONTEXT TYPES
    // ----------------------------------------------------------------------
    private IAntHelper _antHelper;
    private IJavaDebuggingHelper _javaDebuggingHelper;
    private IEditorHelper _genericEditorHelper;
    private IJavaHelper _javaHelper;
    private IJavaProjectHelper _javaProjectHelper;
    private IProjectHelper _projectHelper;
    private IResourceHelper _resourceHelper;
    private ISimpleProjectHelper _simpleProjectHelper;
    private ITestHelper _testHelper;
    private IUIHelper _uiHelper;
    private IWorkbenchHelper _workbenchHelper;

    /**
     * Get an Ant helper.
     *
     * @return  IAntHelper - Helper for running Ant scripts
     */
    public static IAntHelper getAntHelper() {
        return instance().getAntHelperInternal();
    }

    /**
     * Get an Ant helper.
     *
     * @return  IAntHelper - Helper for running Ant scripts
     */
    private IAntHelper getAntHelperInternal() {
        if (_antHelper == null) {
            _antHelper = new AntHelperImpl();
        }

        return _antHelper;
    }

    /**
     * Get a generic text editor Helper.
     *
     * @return  IEditorHelper - Generic Helper for editing files through a text-based
     *          editor.
     */
    public static IEditorHelper getEditorHelper() {
        return instance().getEditorHelperInternal();
    }

    /**
     * Get a generic text editor Helper.
     *
     * @return  IEditorHelper - Generic Helper for editing files through a text-based
     *          editor.
     */
    private IEditorHelper getEditorHelperInternal() {
        if (_genericEditorHelper == null) {
            _genericEditorHelper = new GenericEditorHelperImplAdapter();
        }

        return _genericEditorHelper;
    }

    /**
     * Get an instance of a helper for debugging tasks.
     *
     * @return  IJavaDebuggingHelper - Helper for setting breakpoints, continuing a paused
     *          process, etc.
     */
    public static IJavaDebuggingHelper getJavaDebuggingHelper() {
        return instance().getJavaDebuggingHelperInternal();
    }

    /**
     * Get an instance of a helper for debugging tasks.
     *
     * @return  IJavaDebuggingHelper - Helper for setting breakpoints, continuing a paused
     *          process, etc.
     */
    public IJavaDebuggingHelper getJavaDebuggingHelperInternal() {
        if (_javaDebuggingHelper == null) {
            _javaDebuggingHelper = new JavaDebuggingHelperImpl();
        }

        return _javaDebuggingHelper;
    }

    /**
     * Get the known helper used for java-specific tasks.
     *
     * @return  IJavaHelper
     */
    public static IJavaHelper getJavaHelper() {
        return instance().getJavaHelperInternal();
    }

    /**
     * Get the known helper used for java-specific tasks.
     *
     * @return  IJavaHelper
     */
    private IJavaHelper getJavaHelperInternal() {
        if (_javaHelper == null) {
            _javaHelper = new JavaHelperImpl();
        }

        return _javaHelper;
    }

    /**
     * Get a helper used to create Java projects.
     *
     * @return  IJavaProjectHelper
     */
    public static IJavaProjectHelper getJavaProjectHelper() {
        return instance().getJavaProjectHelperInternal();
    }

    /**
     * Get a helper used to create Java projects.
     *
     * @return  IJavaProjectHelper
     */
    private IJavaProjectHelper getJavaProjectHelperInternal() {
        if (_javaProjectHelper == null) {
            _javaProjectHelper = new JavaProjectHelperImpl();
        }

        return _javaProjectHelper;
    }

    /**
     * Get a helper that manipulates generic IProject types in the workspace.
     *
     * @return  IProjectHelper
     */
    public static IProjectHelper getProjectHelper() {
        return instance().getProjectHelperInternal();
    }

    /**
     * Get a helper that manipulates generic IProject types in the workspace.
     *
     * @return  IProjectHelper
     */
    private IProjectHelper getProjectHelperInternal() {
        if (_projectHelper == null) {
            _projectHelper = new ProjectHelperImpl();
        }

        return _projectHelper;
    }

    /**
     * Get the registered Helper for manipulating files.
     *
     * @return  IResourceHelper
     */
    public static IResourceHelper getResourceHelper() {
        return instance().getResourceHelperInternal();
    }

    /**
     * Get the registered Helper for manipulating files.
     *
     * @return  IResourceHelper
     */
    private IResourceHelper getResourceHelperInternal() {
        if (_resourceHelper == null) {
            _resourceHelper = new ResourceHelperImpl();
        }

        return _resourceHelper;
    }

    /**
     * Get a helper used to create simple projects.
     *
     * @return  ISimpleProjectHelper
     */
    public static ISimpleProjectHelper getSimpleProjectHelper() {
        return instance().getSimpleProjectHelperInternal();
    }

    /**
     * Get a helper used to create simple projects.
     *
     * @return  ISimpleProjectHelper
     */
    private ISimpleProjectHelper getSimpleProjectHelperInternal() {
        if (_simpleProjectHelper == null) {
            _simpleProjectHelper = new SimpleProjectHelperImpl();
        }

        return _simpleProjectHelper;
    }

    /**
     * Get a helper used to inject JUnit test behavior.
     *
     * @return  ITestHelper
     */
    public static ITestHelper getTestHelper() {
        return instance().getTestHelpernternal();
    }

    /**
     * Get a helper used to inject JUnit test behavior.
     *
     * @return  ITestHelper
     */
    private ITestHelper getTestHelpernternal() {
        if (_testHelper == null) {
            _testHelper = new TestHelperImpl();
        }

        return _testHelper;
    }

    /**
     * Get the known helper used for additional grouped UI commands.
     *
     * @param   ui  - Driver for UI generated input
     * @return  IUIHelper
     */
    public static IUIHelper getUIHelper() {
        return instance().getUIHelperInternal();
    }

    /**
     * Get the known helper used for additional grouped UI commands.
     *
     * @param   ui  - Driver for UI generated input
     * @return  IUIHelper
     */
    private IUIHelper getUIHelperInternal() {
        if (_uiHelper == null) {
            _uiHelper = new UIHelperImpl();
        }

        return _uiHelper;
    }

    /**
     * Get the known helper used for tasks with the eclipse workbench.
     *
     * @return  IWorkbenchHelper
     */
    public static IWorkbenchHelper getWorkbenchHelper() {
        return instance().getWorkbenchHelperInternal();
    }

    /**
     * Get the known helper used for tasks with the eclipse workbench.
     *
     * @return  IWorkbenchHelper
     */
    private IWorkbenchHelper getWorkbenchHelperInternal() {
        if (_workbenchHelper == null) {
            _workbenchHelper = new WorkbenchHelperImpl();
        }

        return _workbenchHelper;
    }

    /**
     * instance - Initialize the extension points and register helper implementations.
     */
    private static EclipseHelperFactory instance() {
        if (_instance == null) {
            _instance = new EclipseHelperFactory();
        }

        return _instance;
    }

}
