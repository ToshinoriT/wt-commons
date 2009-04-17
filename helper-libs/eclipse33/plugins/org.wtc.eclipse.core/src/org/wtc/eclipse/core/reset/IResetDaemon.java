/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.reset;

import com.windowtester.runtime.IUIContext;
import org.wtc.eclipse.core.util.Timestamp;

/**
 * Called between Test classes (not test methods) to reset the workspace to a fresh state.
 * A type of universal oneTimeTearDown where plugins that provide helpers can also provide
 * the logic to verify that the actions from the provided helper methods are properly
 * cleaned. A reset daemon is scoped by a logical entity within the workspace (ie -
 * projects, preferences, servers, etc) as opposed to a 1:1 relationship with a helper
 * 
 * @since 3.8.0
 */
public interface IResetDaemon {
    /**
     * After this method is called, the state of the current workspace should be
     * considered a clean workspace in the scope of which this reset daemon is concerned.
     * For example, a project reset daemon may delete projects from the workspace but
     * should not try to reset preferences, for example. All actions performed within this
     * reset daemon should use Eclipse API calls instead of UI calls if possible
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input and owner of the "wait"
     *                       method
     * @param  resetContext  - Information about the test that was running and needs to be
     *                       reset
     */
    public void resetWorkspace(IUIContext ui, ResetContext context);

    /**
     * Data access object for context information when resetting the workspace.
     */
    public static class ResetContext {
        private final String _testClassName;
        private final String _timestamp;

        /**
         * Save the data members.
         */
        public ResetContext(String testClassName) {
            _testClassName = testClassName;
            _timestamp = new Timestamp().toString();
        }

        /**
         * @return  String - The name of the test class that ran just before the workspace
         *          should be reset
         */
        public String getTestClassName() {
            return _testClassName;
        }

        /**
         * @return  String - The time this reset context was created
         */
        public String getTimeStamp() {
            return _timestamp;
        }
    }
}
