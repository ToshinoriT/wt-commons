/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.locators;

import org.eclipse.swt.widgets.Widget;

/**
 * @since 3.8.0
 */
public class HackableSWTWidgetReference extends HackableSWTWidgetLocator {
    private static final long serialVersionUID = -4386416257140138370L;

    private final Widget _widget;

    public HackableSWTWidgetReference(Widget widget) {
        super(widget.getClass());
        _widget = widget;
    }

    @Override
    public boolean matches(Object obj) {
        // Reference equality
        return _widget == obj;
    }
}
