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
import org.eclipse.swt.widgets.Label;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Locates a widget by getting its parent, looking for the control's previous sibling,
 * then (if that sibling is a Label) comparing the sibling's text value against the given
 * text value.
 * 
 * @since 3.8.0
 */
public class AdjacentLabeledControlLocator extends SWTWidgetLocator {
    private static final long serialVersionUID = 1L;

    private Class<? extends Control> _controlClass;
    private String _rawText;
    private Pattern _labelPattern;

    /**
     * Save the data members.
     *
     * @param  controlClass   - The class of the control to find
     * @param  adjacentLabel  - The value of the control's previous Label sibling text.
     *                        May optionally be a regex pattern
     */
    public AdjacentLabeledControlLocator(Class<? extends Control> controlClass,
                                         String adjacentLabel) {
        super(controlClass);

        _controlClass = controlClass;
        _rawText = adjacentLabel;

        try {
            _labelPattern = Pattern.compile(adjacentLabel);
        } catch (PatternSyntaxException pse) {
        }
    }

    /**
     * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#matches(java.lang.Object)
     */
    @Override
    public boolean matches(final Object obj) {
        final boolean[] matches = new boolean[1];
        matches[0] = false;

        if ((obj != null) && (_controlClass.isAssignableFrom(obj.getClass()))) {
            final Control control = _controlClass.cast(obj);
            Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        if (control.isVisible() && !control.isDisposed()) {
                            Composite parent = control.getParent();
                            Control[] allChildren = parent.getChildren();
                            Control previousSibling = null;

                            for (Control nextChild : allChildren) {
                                if (nextChild == obj) {
                                    break;
                                }

                                previousSibling = nextChild;
                            }

                            if (previousSibling instanceof Label) {
                                Label siblingLabel = (Label) previousSibling;
                                String text = siblingLabel.getText();

                                if (text != null) {
                                    if (_labelPattern != null) {
                                        Matcher m = _labelPattern.matcher(text);
                                        matches[0] = m.matches();
                                    }

                                    if (!matches[0]) {
                                        matches[0] = _rawText.equals(text);
                                    }
                                }
                            }
                        }
                    }
                });
        }

        return matches[0];

    }
}
