/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.locators;

import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Find a control with the given class that has a first level parent of type Group and
 * given text.
 * 
 * @since 3.8.0
 */
public class ControlInGroupLocator extends SWTWidgetLocator {
    private static final long serialVersionUID = -4086494387384448701L;

    private Class<? extends Control> _controlClass;
    private String _rawText;
    private Pattern _groupTextPattern;

    /**
     * Save the data members.
     *
     * @param  controlClass  - The control within the group to find
     * @param  groupText     - Either the exact text of the control's parent group or a
     *                       regex pattern of the control's parent group
     */
    public ControlInGroupLocator(Class<? extends Control> controlClass,
                                 String groupText) {
        super(controlClass);

        _controlClass = controlClass;
        _rawText = groupText;

        try {
            _groupTextPattern = Pattern.compile(groupText);
        } catch (PatternSyntaxException pse) {
        }
    }

    /**
     * @see  com.windowtester.runtime.swt.locator.SWTWidgetLocator#matches(java.lang.Object)
     */
    @Override
    public boolean matches(Object obj) {
        final boolean[] matches = new boolean[1];
        matches[0] = false;

        if ((obj != null) && (_controlClass.isAssignableFrom(obj.getClass()))) {
            final Control control = _controlClass.cast(obj);
            Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        Composite parent = control.getParent();

                        if (control.isVisible() && !control.isDisposed() && (parent instanceof Group)) {
                            Group group = (Group) parent;
                            String text = group.getText();

                            if (text != null) {
                                if (_groupTextPattern != null) {
                                    Matcher m = _groupTextPattern.matcher(text);
                                    matches[0] = m.matches();
                                }

                                if (!matches[0]) {
                                    matches[0] = _rawText.equals(text);
                                }
                            }
                        }
                    }
                });
        }

        return matches[0];
    }
}
