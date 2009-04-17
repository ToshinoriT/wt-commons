/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.locators;

import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import org.eclipse.swt.widgets.Widget;

/**
 * Generic class to find a widget by index without a parent locator.
 * 
 * @since 3.8.0
 */
public class SWTWidgetByIndexLocator extends SWTWidgetLocator {
    private static final long serialVersionUID = -8628730208864079576L;

    private int _count;
    private Class<? extends Widget> _widgetClass;

    /**
     * Save the data members.
     */
    public SWTWidgetByIndexLocator(Class<? extends Widget> widgetClass,
                                   int index) {
        super(widgetClass);

        _widgetClass = widgetClass;
        _count = index;
    }

    /**
     * @see  com.windowtester.runtime.swt.locator.SWTWidgetLocator#matches(java.lang.Object)
     */
    @Override
    public boolean matches(Object obj) {
        boolean matches = false;

        if ((obj != null) && (_widgetClass.isAssignableFrom(obj.getClass()))) {
            matches = _count == 0;
            _count--;
            System.err.println("---->> " + obj.getClass().getName() + "(" + (_count + 1) + ")::" + (matches)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        return matches;
    }
}
