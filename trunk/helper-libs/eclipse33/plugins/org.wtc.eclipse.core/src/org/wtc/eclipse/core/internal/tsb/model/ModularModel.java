/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.internal.tsb.model;

import org.wtc.eclipse.core.CoreActivator;
import org.wtc.eclipse.core.tsb.ITestSuiteBuilderConstants;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Base class for a test project model object that contains modules.
 */
public abstract class ModularModel {
    // For comparing modules
    private static Comparator<String> _moduleComparator = new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        };

    public static enum LoadState {
        DONT_KNOW,
        DONT_LOAD,
        DONT_LOAD_FAILURE,
        DONT_LOAD_NOLINUX,
        LOAD
    }

    // The modules that apply to this element
    private Set<String> _modules;

    // Used in debug information
    private final String _dataType;
    private final String _dataTypeName;

    /**
     * Save the data members.
     */
    public ModularModel(Set<String> modules,
                        String dataType,
                        String dataTypeName) {
        _modules = new TreeSet<String>(_moduleComparator);
        _modules.addAll(modules);
        _dataType = dataType;
        _dataTypeName = dataTypeName;
    }

    /**
     * @return  Set<String> - The modules to load or <code>null</code> if the modules
     *          property wasn't given
     */
    public Set<String> getModules() {
        return _modules;
    }

    /**
     * Reset the modules of this test to the given module values.
     */
    public void setModules(Set<String> modules) {
        _modules = new TreeSet<String>(_moduleComparator);
        _modules.addAll(modules);
    }

    /**
     * @return  boolean - True if at least one of the given modules is contained this
     *          test's criteria
     */
    public LoadState shouldLoad(Set<String> modules,
                                boolean isLinux) {
        LoadState loadState = LoadState.DONT_KNOW;

        if ((modules != null) && !modules.isEmpty()) {
            String message = "[ !! UNKNOWN STATE !! ]"; //$NON-NLS-1$

            for (String nextModule : modules) {
                if (_modules.contains(nextModule)) {
                    if (_modules.contains(ITestSuiteBuilderConstants.MODULE_FAILURE)) {
                        StringBuilder buffer = new StringBuilder();
                        buffer.append("INFO - [LOAD-0] THE "); //$NON-NLS-1$
                        buffer.append(_dataType);
                        buffer.append(" <"); //$NON-NLS-1$
                        buffer.append(_dataTypeName);
                        buffer.append("> WILL *NOT* LOAD BECAUSE IT IS MARKED WITH THE FAILURE MODULE <"); //$NON-NLS-1$
                        buffer.append(ITestSuiteBuilderConstants.MODULE_FAILURE);
                        buffer.append(">"); //$NON-NLS-1$

                        message = buffer.toString();
                        loadState = LoadState.DONT_LOAD_FAILURE;
                    } else if (isLinux && _modules.contains(ITestSuiteBuilderConstants.MODULE_NOLINUX)) {
                        StringBuilder buffer = new StringBuilder();
                        buffer.append("INFO - [LOAD-0] THE "); //$NON-NLS-1$
                        buffer.append(_dataType);
                        buffer.append(" <"); //$NON-NLS-1$
                        buffer.append(_dataTypeName);
                        buffer.append("> WILL *NOT* LOAD BECAUSE IT IS MARKED WITH THE NO-LINUX MODULE <"); //$NON-NLS-1$
                        buffer.append(ITestSuiteBuilderConstants.MODULE_FAILURE);
                        buffer.append(">"); //$NON-NLS-1$

                        message = buffer.toString();
                        loadState = LoadState.DONT_LOAD_NOLINUX;
                    } else {
                        StringBuilder buffer = new StringBuilder();
                        buffer.append("INFO - [LOAD-1] THE "); //$NON-NLS-1$
                        buffer.append(_dataType);
                        buffer.append(" <"); //$NON-NLS-1$
                        buffer.append(_dataTypeName);
                        buffer.append("> WILL *LOAD* BECAUSE OF MODULE <"); //$NON-NLS-1$
                        buffer.append(nextModule);
                        buffer.append(">"); //$NON-NLS-1$

                        message = buffer.toString();
                        loadState = LoadState.LOAD;
                    }

                    break;
                }
            }

            if (loadState == LoadState.DONT_KNOW) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("INFO - [LOAD-0] THE "); //$NON-NLS-1$
                buffer.append(_dataType);
                buffer.append(" <"); //$NON-NLS-1$
                buffer.append(_dataTypeName);
                buffer.append("> WILL *NOT* LOAD BECAUSE NONE OF ITS MODULES <"); //$NON-NLS-1$
                boolean isFirst = true;

                for (String nextModule : _modules) {
                    if (!isFirst) {
                        buffer.append("  "); //$NON-NLS-1$
                    }

                    buffer.append(nextModule);
                    isFirst = false;
                }

                buffer.append("> APPLY"); //$NON-NLS-1$
                message = buffer.toString();
                loadState = LoadState.DONT_LOAD;
            }

            CoreActivator.logDebug(message, ITestSuiteBuilderConstants.LOGGING_TESTSUITEMODEL);
        }

        return loadState;
    }
}
