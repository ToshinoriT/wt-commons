/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.condition.ICondition;
import junit.framework.TestCase;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import java.lang.ref.WeakReference;

/**
 * Condition that waits for incoming resource changed events to stop. For each resource
 * changed event that is broadcast, this condition will reset a timer. This condition will
 * be true if that timer expires. In other words, wait for all resource change events to
 * stop then wait the given timeout value after the last resource changed event to make
 * sure no new events are broadcast.
 */
public class NoResourceChangedEventsCondition implements ICondition {
    // How long to wait after the last resource changed event before
    // we're convinced no new events will be coming in
    private int _timeout;

    // The last time a resource change event was broadcast
    private volatile long _lastEventTime;

    /**
     * Save the data members.
     */
    public NoResourceChangedEventsCondition(int timeout) {
        TestCase.assertTrue(timeout > 0);
        _timeout = timeout;
    }

    /**
     * Reset the timeout counter.
     */
    private synchronized void eventReceived() {
        _lastEventTime = System.currentTimeMillis();
    }

    /**
     * @see  com.windowtester.runtime.condition.ICondition#test()
     */
    public synchronized boolean test() {
        long now = System.currentTimeMillis();

        return (now - _lastEventTime) >= _timeout;
    }

    /**
     * Listens for resource changed events and knows how to remove itself as a resource
     * change listener.
     */
    public static class StoppingResourceChangeEventListener implements IResourceChangeListener {
        // We'll use this to know when to remove ourselves as a listener
        private WeakReference<NoResourceChangedEventsCondition> _parent;

        /**
         * Save the data members.
         */
        public StoppingResourceChangeEventListener(NoResourceChangedEventsCondition parent) {
            _parent = new WeakReference<NoResourceChangedEventsCondition>(parent);
        }

        /**
         * @see  org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
         */
        public void resourceChanged(IResourceChangeEvent arg0) {
            // First, let's clean up after ourselves
            NoResourceChangedEventsCondition condition = _parent.get();

            if (_parent == null) {
                IWorkspace ws = ResourcesPlugin.getWorkspace();
                ws.removeResourceChangeListener(this);
            } else {
                condition.eventReceived();
            }
        }
    }
}
