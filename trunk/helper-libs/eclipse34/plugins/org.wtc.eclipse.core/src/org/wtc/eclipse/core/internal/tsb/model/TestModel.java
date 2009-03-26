/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.internal.tsb.model;

import org.wtc.eclipse.core.CoreActivator;
import java.util.Set;

/**
 * Describes a test object defined in one of the registry file types.
 */
public class TestModel extends ModularModel {
    // The loaded test class instance
    private final Class<?> _testClass;
    private final String _testClassName;
    private String _testOwner;

    // The plugin that contains this test
    private final String _pluginName;

    /**
     * Save the data members.
     */
    public TestModel(String testClassName, Set<String> testModules,
                     String testOwner) {
        super(testModules, "TEST CLASS", testClassName); //$NON-NLS-1$

        _testClass = null;
        _testClassName = testClassName;
        _pluginName = null;
        _testOwner = testOwner;
    }

    /**
     * Save the data members.
     */
    public TestModel(Class<?> testClass,
                     String pluginName,
                     Set<String> testModules,
                     String testOwner) {
        super(testModules, "TEST CLASS", testClass.getName()); //$NON-NLS-1$

        _testClass = testClass;
        _testClassName = testClass.getName();
        _pluginName = pluginName;
        _testOwner = testOwner;
    }

    /**
     * @return  String - The parent plugin's name
     */
    public String getPluginName() {
        return _pluginName;
    }

    /**
     * @return  Class<?> - The loaded test class
     */
    public Class<?> getTestClass() {
        return _testClass;
    }

    /**
     * @return  String - The name of the test class
     */
    public String getTestClassName() {
        return _testClassName;
    }

    /**
     * @return  String - The name of the test owner
     */
    public String getTestOwner() {
        return _testOwner;
    }

    /**
     * @param  testOwner  - The username of the owner of this test
     */
    public void setTestOwner(String testOwner) {
        _testOwner = testOwner;
    }

    /**
     * @return  boolean - True if at least one of the given modules is contained this
     *          test's criteria
     */
    public LoadState shouldLoad(Set<String> modules,
                                Set<String> testsToLoad,
                                Set<String> pluginsToLoad,
                                boolean isLinux) {
        assert (_testClass != null);

        LoadState state = super.shouldLoad(modules, isLinux);

        if (state != LoadState.DONT_KNOW) {
            return state;
        }

        if ((testsToLoad != null) && !testsToLoad.isEmpty()) {
            String fqName = _testClass.getName();

            if (testsToLoad.contains(fqName)) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("INFO - [LOAD-1] THE TEST CLASS <"); //$NON-NLS-1$
                buffer.append(_testClassName);
                buffer.append("> WILL *LOAD* BECAUSE OF TEST ARGUMENT <"); //$NON-NLS-1$
                buffer.append(fqName);
                buffer.append(">"); //$NON-NLS-1$
                CoreActivator.logDebug(buffer.toString());

                return LoadState.LOAD;
            }

            String shortName = _testClass.getSimpleName();

            if (testsToLoad.contains(shortName)) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("INFO - [LOAD-1] THE TEST CLASS <"); //$NON-NLS-1$
                buffer.append(_testClassName);
                buffer.append("> WILL *LOAD* BECAUSE OF TEST ARGUMENT <"); //$NON-NLS-1$
                buffer.append(shortName);
                buffer.append(">"); //$NON-NLS-1$
                CoreActivator.logDebug(buffer.toString());

                return LoadState.LOAD;
            }

            StringBuilder buffer = new StringBuilder();
            buffer.append("INFO - [LOAD-0] THE TEST CLASS <"); //$NON-NLS-1$
            buffer.append(_testClassName);
            buffer.append("> WILL *NOT* LOAD BECAUSE NONE OF THE GIVEN TEST NAMES APPLY"); //$NON-NLS-1$
            CoreActivator.logDebug(buffer.toString());

            return LoadState.DONT_LOAD;
        }

        if ((pluginsToLoad != null) && !pluginsToLoad.isEmpty()) {
            if (pluginsToLoad.contains(_pluginName)) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("INFO - [LOAD-1] THE TEST CLASS <"); //$NON-NLS-1$
                buffer.append(_testClassName);
                buffer.append("> WILL *LOAD* BECAUSE OF PLUGIN ARGUMENT <"); //$NON-NLS-1$
                buffer.append(_pluginName);
                buffer.append(">"); //$NON-NLS-1$
                CoreActivator.logDebug(buffer.toString());

                return LoadState.LOAD;
            }

            StringBuilder buffer = new StringBuilder();
            buffer.append("INFO - [LOAD-0] THE TEST CLASS <"); //$NON-NLS-1$
            buffer.append(_testClassName);
            buffer.append("> WILL *NOT* LOAD BECAUSE NONE OF THE GIVEN PLUGIN NAMES APPLY"); //$NON-NLS-1$
            CoreActivator.logDebug(buffer.toString());

            return LoadState.DONT_LOAD;
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append("INFO - [LOAD-0] THE TEST CLASS <"); //$NON-NLS-1$
        buffer.append(_testClassName);
        buffer.append("> WILL *NOT* LOAD BECAUSE NO TEST LOAD CRITERIA WAS GIVEN"); //$NON-NLS-1$
        CoreActivator.logDebug(buffer.toString());

        return LoadState.DONT_LOAD;
    }
}
