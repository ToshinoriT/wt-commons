/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.locators;

import java.awt.Point;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.internal.selector.BasicWidgetSelector;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * An SWTWidgetLocator with a backdoor to specify waitForIdle strategy.
 */
public class HackableSWTWidgetLocator extends SWTWidgetLocator {
    private static final long serialVersionUID = -8377938981861371169L;

    public HackableSWTWidgetLocator(Class cls) {
        super(cls);
    }

    public HackableSWTWidgetLocator(Class cls, String text) {
        super(cls, text);
    }

    public HackableSWTWidgetLocator(Class cls, SWTWidgetLocator parent) {
        super(cls, parent);
    }

    public HackableSWTWidgetLocator(Class cls, int index,
                                    SWTWidgetLocator parent) {
        super(cls, index, parent);
    }

    public HackableSWTWidgetLocator(Class cls, String text,
                                    SWTWidgetLocator parent) {
        super(cls, text, parent);
    }

    public HackableSWTWidgetLocator(Class cls, String text, int index,
                                    SWTWidgetLocator parent) {
        super(cls, text, index, parent);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#doClick(int,
     *      org.eclipse.swt.widgets.Widget, java.awt.Point, int)
     */
    @Override
    protected Widget doClick(int clicks, Widget w, Point offset,
                             int modifierMask) {
        return getSelector().click(w, offset.x, offset.y, modifierMask, clicks);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#doContextClick(org.eclipse.swt.widgets.Widget,
     *      java.awt.Point, java.lang.String)
     */
    @SuppressWarnings("deprecation")
    @Override
    protected Widget doContextClick(Widget w, Point offset, String menuItemPath)
                             throws WidgetNotFoundException, MultipleWidgetsFoundException {
        // don't ask about this ugly wrappering...
        return getSelector().contextClick(w, offset.x, offset.y, menuItemPath);
    }

    private BasicWidgetSelector getSelector() {
        return new BasicWidgetSelector() {
                /*
                 * (non-Javadoc)
                 *
                 * @see com.windowtester.event.selector.swt.BasicWidgetSelector#waitForIdle(org.eclipse.swt.widgets.Display)
                 */
                @Override
                protected void waitForIdle(final Display display) {
                    /*
                     * JOHN: you'll want to hack/pick your poison here...
                     */

                    // waitForIdleClassic(display); //<-- the blocking way...
                    waitForIdleNew(display); // <-- this is what the patch I sent
                                             // did...
                }
            };
    }

    static void waitForIdleClassic(final Display display) {
        /*
         * The OLD way to wait (found not safe in Linux) And giving us fits in the bridged
         * AWT/SWT case https://fogbugz.instantiations.com/default.php?27953
         */
        display.syncExec(new Runnable() {
                public void run() {
                    while (display.readAndDispatch()) {
                        ;
                    }
                }
            });
    }

    static void waitForIdleNew(final Display display) {
        new SWTIdleCondition().waitForIdle();
    }

}
