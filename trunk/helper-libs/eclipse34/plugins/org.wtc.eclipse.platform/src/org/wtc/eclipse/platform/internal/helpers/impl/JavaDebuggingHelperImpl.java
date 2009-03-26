/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.internal.helpers.impl;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import junit.framework.TestCase;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.debug.core.IJavaMethodBreakpoint;
import org.eclipse.jdt.debug.core.IJavaMethodEntryBreakpoint;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IJavaDebuggingHelper;
import org.wtc.eclipse.platform.helpers.adapters.DebuggingHelperImlAdapter;

public class JavaDebuggingHelperImpl extends DebuggingHelperImlAdapter
    implements IJavaDebuggingHelper {
    /**
     * @see  DebuggingHelperImlAdapter#getEditorHelper(IPath)
     */
    @Override
    protected IEditorHelper getEditorHelper(IPath filePath) {
        return EclipseHelperFactory.getEditorHelper();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaDebuggingHelper#setMethodBreakpoint(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String, java.lang.String,
     *       java.lang.String)
     */
    public void setMethodBreakpoint(IUIContext ui,
                                    IPath filePath,
                                    String typeName,
                                    String methodName,
                                    String methodReturnType) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertNotNull(typeName);
        TestCase.assertNotNull(methodName);
        TestCase.assertNotNull(methodReturnType);

        String fileExtension = filePath.getFileExtension();
        TestCase.assertTrue("METHOD BREAKPOINTS CAN ONLY BE SET IN JAVA FILES. YOU TRIED TO SET ONE IN <" //$NON-NLS-1$
                            + filePath.toPortableString() + ">", //$NON-NLS-1$
                            (fileExtension != null) && (fileExtension.equalsIgnoreCase("java"))); //$NON-NLS-1$

        logEntry2(filePath.toPortableString(), typeName, methodName, methodReturnType);

        switchPerspectiveOpenFile(ui, filePath);

        // Find the type declaration first
        IEditorHelper editor = EclipseHelperFactory.getEditorHelper();
        editor.findFirst(ui, "class " + typeName); //$NON-NLS-1$

        // Then find the method in that declaration
        editor.findFirst(ui, methodReturnType + " " + methodName); //$NON-NLS-1$

        clickRunMenuItem(ui, "Toggle &Method Breakpoint.*"); //$NON-NLS-1$

        verifyMethodBreakpointExists(ui,
                                     filePath,
                                     typeName,
                                     methodName,
                                     methodReturnType,
                                     true);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IJavaDebuggingHelper#verifyMethodBreakpointExists(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String, java.lang.String,
     *       java.lang.String, boolean)
     */
    public void verifyMethodBreakpointExists(IUIContext ui,
                                             IPath filePath,
                                             String typeName,
                                             String methodName,
                                             String methodReturnType,
                                             final boolean exists) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertNotNull(typeName);
        TestCase.assertNotNull(methodName);
        TestCase.assertNotNull(methodReturnType);

        logEntry2(filePath.toPortableString(), typeName, methodName, methodReturnType);

        final IBreakpointManager breakpointManager =
            DebugPlugin.getDefault().getBreakpointManager();

        final MethodBreakpointInfo bpInfo = new MethodBreakpointInfo(filePath,
                                                                     typeName,
                                                                     methodName,
                                                                     methodReturnType);

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
     * @see  org.wtc.eclipse.platform.helpers.IJavaDebuggingHelper#verifySuspendedAtMethod(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String, java.lang.String,
     *       java.lang.String)
     */
    public void verifySuspendedAtMethod(IUIContext ui,
                                        IPath filePath,
                                        String typeName,
                                        String methodName,
                                        String methodReturnType) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertNotNull(typeName);
        TestCase.assertNotNull(methodReturnType);
        TestCase.assertNotNull(filePath);

        logEntry2(filePath.toPortableString(), typeName, methodName, methodReturnType);

        final MethodBreakpointInfo bpInfo = new MethodBreakpointInfo(filePath,
                                                                     typeName,
                                                                     methodName,
                                                                     methodReturnType);

        verifySuspendedAtBreakpoint(ui, bpInfo);

        logExit2();
    }

    /**
     * Model class representing a method breakpoint.
     */
    private static class MethodBreakpointInfo extends BreakpointInfo {
        private final String _methodName;
        private final String _methodReturnType;

        /**
         * Save the data members for a method breakpoint.
         */
        public MethodBreakpointInfo(IPath filePath,
                                    String typeName,
                                    String methodName,
                                    String methodReturnType) {
            super(filePath);

            _methodName = methodName;
            _methodReturnType = methodReturnType;
            ;
        }

        /**
         * @return  boolean - Utility for comparing a breakpoint set on a Java method
         */
        @Override
        public boolean equals(Object obj) {
            boolean equals = false;

            if (obj instanceof IJavaMethodBreakpoint) {
                IJavaMethodBreakpoint jmb = (IJavaMethodBreakpoint) obj;

                try {
                    String typeName = jmb.getTypeName();
                    String methodName = jmb.getMethodName();
                    String signature = jmb.getMethodSignature();
                    equals = equalsJavaMethodBreakpoint(typeName, methodName, signature);
                } catch (CoreException e) {
                    // Do nothing. Assume the breakpoint isn't equal
                }
            } else if (obj instanceof IJavaMethodEntryBreakpoint) {
                IJavaMethodEntryBreakpoint jmeb = (IJavaMethodEntryBreakpoint) obj;

                try {
                    String typeName = jmeb.getTypeName();
                    String methodName = jmeb.getMethodName();
                    String signature = jmeb.getMethodSignature();
                    equals = equalsJavaMethodBreakpoint(typeName, methodName, signature);
                } catch (CoreException e) {
                    // Do nothing. Assume the breakpoint isn't equal
                }
            }

            return equals;
        }

        /**
         * @return  boolean - Utility for comparing a breakpoint set on a Java method
         */
        private boolean equalsJavaMethodBreakpoint(String typeName,
                                                   String methodName,
                                                   String signature) {
            String returnType = getReturnType(signature);
            String simpleTypeName = getSimpleTypeName();

            return ((typeName.endsWith(simpleTypeName)) && (methodName.equals(_methodName))
                && (returnType.endsWith(_methodReturnType)));
        }

        /**
         * @see  java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[type=METHOD, filePath="); //$NON-NLS-1$
            builder.append(getFilePath().toPortableString());
            builder.append(", simpleTypeName="); //$NON-NLS-1$
            builder.append(getSimpleTypeName());
            builder.append(", methodName="); //$NON-NLS-1$
            builder.append(_methodName);
            builder.append(", methodReturnType="); //$NON-NLS-1$
            builder.append(_methodReturnType);
            builder.append("]"); //$NON-NLS-1$

            return builder.toString();
        }

    }
}
