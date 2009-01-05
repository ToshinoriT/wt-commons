/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

import com.windowtester.runtime.IUIContext;

/**
 * Helper for additional UI driver macros and UI generated input that is visible to tools
 * and helper implementations.
 */
public interface IUIHelper {
    /**
     * annoy - Beep the system a few times. A utility method that is useful in debugging
     * (specifically, when I'm debugging a test and want to watch something specific
     * happen, but I don't want to watch the whole test, then I drop this method in right
     * before the interesting part).
     *
     * @param  ui  - Driver for UI generated input
     */
    public void annoy(IUIContext ui);

    /**
     * arrowDown - Tap the down arrow the given number of times.
     *
     * @param  ui             - Driver for UI generated input
     * @param  numberOfTimes  - Number of times to hit the down arrow
     */
    public void arrowDown(IUIContext ui, int numberOfTimes);

    /**
     * arrowUp - Tap the up arrow the given number of times.
     *
     * @param  ui             - Driver for UI generated input
     * @param  numberOfTimes  - Number of times to hit the up arrow
     */
    public void arrowUp(IUIContext ui, int numberOfTimes);

    /**
     * freeze - Stop the UI driver. Do not exist the test. Leave the UI in the current
     * state. May only be called in development (if the prodMode environment variable is
     * set to anything other than "dev", then issue a test case failure)
     *
     * @param  ui  - Driver for UI generated input
     */
    public void freeze(IUIContext ui);

    /**
     * pressEnter - Simple helper for pressing the enter key.
     *
     * @param  ui  - Driver for UI generated input
     */
    public void pressEnter(IUIContext ui);

    /**
     * Take a screenshot with the given title and place it in the harness-specific
     * screenshot output directory.
     *
     * @param  ui     - Driver for UI-generated input
     * @param  title  - Title of the screenshot. The current total screenshot count will
     *                be appended to this title. Extension is not required. Will be output
     *                as a PNG file
     */
    public void screenshot(IUIContext ui, String title);

    /**
     * thaw - If the test is frozen with a freeze() call, then let the test continue
     * execution until the next freeze() call is made.
     */
    public void thaw();
}
