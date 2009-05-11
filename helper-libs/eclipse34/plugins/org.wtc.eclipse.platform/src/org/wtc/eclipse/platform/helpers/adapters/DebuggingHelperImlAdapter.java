/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers.adapters;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaStratumLineBreakpoint;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper.Perspective;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;

/**
 * Common methods for debugging in any file type.
 * 
 * @since 3.8.0
 */
public abstract class DebuggingHelperImlAdapter extends HelperImplAdapter {
    protected static final int DEFAULT_BREAKPOINT_TIMEOUT = 120000; // Two minutes

    /**
     * clickRunMenuItem - Utility for executing menu items in the Run menu.
     *
     * @since 3.8.0
     * @param  ui    - Driver for UI generated input
     * @param  menu  - Menu item to execute
     */
    protected void clickRunMenuItem(IUIContext ui, String menuItem) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(menuItem);

        try {
            ui.click(new MenuItemLocator("&Run/" + menuItem)); //$NON-NLS-1$
        } catch (WidgetSearchException wse) {
            PlatformActivator.logException(wse);
            TestCase.fail(wse.getLocalizedMessage());
        }
    }

    /**
     * Get the editor helper used in setting the cursor position before breakpoints are
     * toggled. Subclasses should implement this method to return an editor helper capable
     * of placing the cursor
     *
     * @param   filePath  - Not necessarily used. Here in case a subclass needs more
     *                    information to determine the type of editor helper to return
     * @return  IEditorHelpr - The file-specific editor helper
     */
    protected abstract IEditorHelper getEditorHelper(IPath filePath);

    /**
     * removeAllBreakpoints - Remove all breakpoints in the current workspace.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void removeAllBreakpoints(IUIContext ui) {
        TestCase.assertNotNull(ui);

        logEntry2();

        // The debug perspective has the right menu items
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.openPerspective(ui, Perspective.DEBUG);

        clickRunMenuItem(ui, "Remo&ve All Breakpoints.*"); //$NON-NLS-1$

        ui.wait(new ShellShowingCondition("Remove All Breakpoints")); //$NON-NLS-1$

        try {
            ui.click(new ButtonLocator("&Yes")); //$NON-NLS-1$
            ui.wait(new ShellDisposedCondition("Remove All Breakpoints")); //$NON-NLS-1$
        } catch (WidgetSearchException wse) {
            PlatformActivator.logException(wse);
            TestCase.fail(wse.getLocalizedMessage());
        }

        final IBreakpointManager breakpointManager =
            DebugPlugin.getDefault().getBreakpointManager();

        ui.wait(new ICondition() {
                public boolean test() {
                    IBreakpoint[] breakpoints = breakpointManager.getBreakpoints();

                    return breakpoints.length == 0;
                }
            });

        logExit2();
    }

    /**
     * @since 3.8.0
     * @param  ui
     */
    public void resumeDebugging(IUIContext ui) {
        TestCase.assertNotNull(ui);

        logEntry2();

        // The debug perspective has the right menu items
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.openPerspective(ui, Perspective.DEBUG);

        clickRunMenuItem(ui, "Resu&me.*"); //$NON-NLS-1$

        // Since another breakpoint may be immediately hit, there's not much
        // we can wait for here
        waitForIdle(ui);

        logExit2();
    }

	/**
	 * Wait for SWT Idle.
	 * @since 3.8.0
	 */
	protected void waitForIdle(IUIContext ui) throws WaitTimedOutException {
		/*
		 * This condition replaces earlier deprecated wait for idle strategies.
		 * Since idle waits have historically been a hotspot for timing issues
		 * this is a good place to look if we see timing-related regressions.
		 */
		ui.wait(new SWTIdleCondition());
	}

    /**
     * setLineBreakpoint - Add a breakpoint to the given file on the given line.
     *
     * @since 3.8.0
     * @param  ui              - Driver for UI generated input
     * @param  filePath        - The full path (project included) of the file in which to
     *                         set a breakpoint.
     * @param  breakpointLine  - The line number (Eclipse files start numbering files with
     *                         line number 1) on which to set a breakpoint. If the given
     *                         line number is greater than the line length of the given
     *                         file, this method will issue a test case failure. Note that
     *                         if the line number is placed on a non-executable line (in a
     *                         Java comment or on a line with a brace ('{') for a
     *                         conditional statement, the breakpoint may not be set or may
     *                         jump to the nearest possible executable line depending on
     *                         internal Eclipse breakpoint processing and this method will
     *                         issue a test case failure
     */
    public void setLineBreakpoint(IUIContext ui,
                                  IPath filePath,
                                  int breakpointLine) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertTrue(breakpointLine > 0);

        logEntry2(filePath.toPortableString(), Integer.toString(breakpointLine));

        switchPerspectiveOpenFile(ui, filePath);

        IEditorHelper editor = getEditorHelper(filePath);
        editor.gotoLine(ui, breakpointLine);

        clickRunMenuItem(ui, "Toggle Line Brea&kpoint.*"); //$NON-NLS-1$

        verifyLineBreakpointExists(ui, filePath, breakpointLine, true);

        logExit2();
    }

    /**
     * switchPerspectiveOpenFile - Utility to prepare a file for setting breakpoints by
     * opening the file and opening the DEBUG perspective.
     *
     * @since 3.8.0
     * @param  ui        - Driver for UI generated input
     * @param  filePath  - Full path (project included) of the file to open
     */
    protected void switchPerspectiveOpenFile(IUIContext ui,
                                             IPath filePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);

        // The debug perspective has the right menu items
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.openPerspective(ui, Perspective.DEBUG);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.openFile(ui, filePath);

        waitForIdle(ui);
    }

    /**
     * verifyLineBreakpointExists - Wait for a line breakpoint in the given file and the
     * given line to have the given expected existence.
     *
     * @since 3.8.0
     * @param  ui              - Driver for UI generated input
     * @param  filePath        - Full path (project included) of the file whose
     *                         breakpoints (if any) are to be verified
     * @param  breakpointLine  - The line number of the file to search for a breakpoint
     * @param  boolean         exists - True if the breakpoint exists, false otherwise
     */
    public void verifyLineBreakpointExists(IUIContext ui,
                                           IPath filePath,
                                           int breakpointLine,
                                           final boolean exists) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertTrue(breakpointLine > 0);

        logEntry2(filePath.toPortableString(), Integer.toString(breakpointLine), Boolean.toString(exists));

        final IBreakpointManager breakpointManager =
            DebugPlugin.getDefault().getBreakpointManager();

        final LineBreakpointInfo bpInfo = new LineBreakpointInfo(filePath, breakpointLine);

        ui.wait(new ICondition() {
                public boolean test() {
                    IBreakpoint[] breakpoints = breakpointManager.getBreakpoints();
                    boolean found = false;

                    for (IBreakpoint nextBreakpoint : breakpoints) {
                        if (bpInfo.equals(nextBreakpoint)) {
                            found = true;

                            break;
                        }
                    }

                    return (found == exists);
                }

                @Override
                public String toString() {
                    return " FOR BREAKPOINT <" //$NON-NLS-1$
                        + bpInfo.toString() + "> TO EXIST <" //$NON-NLS-1$
                        + exists + ">"; //$NON-NLS-1$
                }
            });

        logExit2();
    }

    /**
     * verifySuspendedAtBreakpoint - Wait until a debugged process exists and is suspended
     * at the given breakpoint.  It is the responsibility of the caller to begin a process
     * in debug mode, and to resume a suspended process.
     *
     * @since 3.8.0
     * @param  ui          - Driver for UI generated input
     * @param  breakpoint  - Describes the breakpoint we're looking for
     */
    protected void verifySuspendedAtBreakpoint(IUIContext ui,
                                               final BreakpointInfo breakpoint) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(breakpoint);

        final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

        ui.wait(new ICondition() {
                public boolean test() {
                    ILaunch[] launches = launchManager.getLaunches();

                    for (ILaunch nextLaunch : launches) {
                        IDebugTarget[] debugTargets = nextLaunch.getDebugTargets();

                        for (IDebugTarget nextTarget : debugTargets) {
                            try {
                                IThread[] allThreads = nextTarget.getThreads();

                                for (IThread nextThread : allThreads) {
                                    if (nextThread.isSuspended()) {
                                        // This will usually return one, but it is
                                        // possible for more than two breakpoints
                                        // to exist at the same location in a program
                                        IBreakpoint[] breakpoints = nextThread.getBreakpoints();

                                        for (IBreakpoint nextBreakpoint : breakpoints) {
                                            if (breakpoint.equals(nextBreakpoint)) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            } catch (DebugException de) {
                                // Do nothing. Assume the breakpoint can't be found
                            }
                        }
                    }

                    return false;
                }

                /**
                 * @see  java.lang.Object#toString()
                 */
                @Override
                public String toString() {
                    return " TO BE SUSPENDED AT BREAKPOINT <" + breakpoint + ">"; //$NON-NLS-1$ //$NON-NLS-2$
                }

            }, DEFAULT_BREAKPOINT_TIMEOUT, 1000);
    }

    /**
     * @param ui
     * @param filePath
     * @param breakpointLine
     */
    public void verifySuspendedAtLine(IUIContext ui,
                                      IPath filePath,
                                      int breakpointLine) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertTrue(breakpointLine > 0);

        logEntry2(filePath.toPortableString(), Integer.toString(breakpointLine));

        final LineBreakpointInfo bpInfo = new LineBreakpointInfo(filePath, breakpointLine);

        verifySuspendedAtBreakpoint(ui, bpInfo);

        logExit2();
    }

    /**
     * Utility class to manage breakpoint comparisons.
     */
    protected abstract static class BreakpointInfo {
        private final IPath _filePath;
        private final String _simpleTypeName;

        /**
         * Save the data members for any breakpoint.
         */
        public BreakpointInfo(IPath filePath) {
            _filePath = filePath;
            _simpleTypeName = filePath.removeFileExtension().lastSegment();
        }

        /**
         * @return  IPath - The path of the file containing the breakpoint
         */
        protected IPath getFilePath() {
            return _filePath;
        }

        /**
         * getReturnType - Utility for getting the return type from a method signature.
         *
         * @param   signature  - Encoded in the JavaMethodBreakpoint method signature
         *                     format
         * @return  String - The return type or "void" if there is no return type
         */
        protected String getReturnType(String signature) {
            String returnType = "void"; //$NON-NLS-1$

            int index = signature.lastIndexOf(")L"); //$NON-NLS-1$

            if (index > 0) {
                String returnTypeEncoded = signature.substring(index + 2, signature.length() - 1);

                if (returnTypeEncoded.length() > 0) {
                    returnType = returnTypeEncoded.replace('/', '.');
                }
            }

            return returnType;
        }

        /**
         * @return  String - The simple name for the containing type
         */
        protected String getSimpleTypeName() {
            return _simpleTypeName;
        }
    }

    /**
     * Model class representing a line breakpoint.
     */
    protected static class LineBreakpointInfo extends BreakpointInfo {
        private final int _lineNumber;
        private final boolean _isJSP;

        /**
         * Save the data members for a line breakpoint.
         */
        public LineBreakpointInfo(IPath filePath, int lineNumber) {
            super(filePath);

            String extension = filePath.getFileExtension();
            _isJSP = ((extension != null) && ((extension.equalsIgnoreCase("jsp")) //$NON-NLS-1$
                    || (extension.equalsIgnoreCase("jspx")))); //$NON-NLS-1$
            _lineNumber = lineNumber;
        }

        /**
         * Compare the Eclipse breakpoint model object to this object.
         */
        @Override
        public boolean equals(Object breakpointObj) {
            boolean equals = false;

            // check first that this is a Java breakpoint
            if (breakpointObj instanceof IJavaBreakpoint) {
                if (_isJSP && (breakpointObj instanceof IJavaStratumLineBreakpoint)) {
                    equals = equalsJavaStratumLineBreakpoint((IJavaStratumLineBreakpoint)
                                                             breakpointObj);
                } else if (breakpointObj instanceof IJavaLineBreakpoint) {
                    equals = equalsJavaLineBreakpoint((IJavaLineBreakpoint) breakpointObj);
                }
            }

            return equals;
        }

        /**
         * @return  boolean - Utility for comparing a breakpoint set on a Java method
         */
        private boolean equalsJavaLineBreakpoint(IJavaLineBreakpoint jlb) {
            boolean equals = false;

            try {
                String typeName = jlb.getTypeName();
                String simpleTypeName = getSimpleTypeName();

//                StringBuilder builder = new StringBuilder();
//                builder.append("\n--------------------------------------\n");
//                builder.append("typeName=");
//                builder.append(typeName);
//                builder.append("\n");
//                builder.append("simpleTypeName=");
//                builder.append(simpleTypeName);
//                builder.append("\n");
//                builder.append("_lineNumber=");
//                builder.append(_lineNumber);
//                builder.append("\n");
//                builder.append("jlb.getLineNumber()=");
//                builder.append(jlb.getLineNumber());
//                builder.append("\n");
//                builder.append("\n--------------------------------------\n");
//                System.err.println(builder.toString());

                equals = ((typeName.endsWith(simpleTypeName)) && (_lineNumber == jlb.getLineNumber()));
            } catch (CoreException e) {
                // Do nothing. Assume the breakpoints aren't equal
            }

            return equals;
        }

        /**
         * @return  boolean - Utility for comparing a breakpoint set on a JSP line
         */
        private boolean equalsJavaStratumLineBreakpoint(IJavaStratumLineBreakpoint jslb) {
            boolean equals = false;

            try {
                String sourceName = jslb.getSourceName();
                String jslbTypeName = sourceName.substring(0, sourceName.indexOf(".")); //$NON-NLS-1$
                String simpleTypeName = getSimpleTypeName();

                equals = ((simpleTypeName.equals(jslbTypeName)) && (_lineNumber == jslb.getLineNumber()));
            } catch (CoreException e) {
                // Do nothing. Assume the breakpoints aren't equal
            }

            return equals;
        }

        /**
         * @see  java.lang.Object#toString()
         */
        @Override
        public String toString() {
            String typeString = _isJSP ? "LINE_JSP" : "LINE"; //$NON-NLS-1$ //$NON-NLS-2$

            StringBuilder builder = new StringBuilder();
            builder.append("[type="); //$NON-NLS-1$
            builder.append(typeString);
            builder.append(", filePath="); //$NON-NLS-1$
            builder.append(getFilePath().toPortableString());
            builder.append(", lineNumber="); //$NON-NLS-1$
            builder.append(_lineNumber);
            builder.append("]"); //$NON-NLS-1$

            return builder.toString();
        }
    }

}
