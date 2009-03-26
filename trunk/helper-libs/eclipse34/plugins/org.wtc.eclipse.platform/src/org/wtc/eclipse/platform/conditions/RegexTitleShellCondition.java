/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.swt.condition.shell.ShellCondition;
import org.eclipse.swt.widgets.Shell;
import org.wtc.eclipse.platform.PlatformActivator;
import java.text.MessageFormat;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Like a regular ShellCondition that checks for modality and title, but use the expected
 * title as a regex pattern.
 */
public class RegexTitleShellCondition extends ShellCondition {
    private Pattern _titlePattern;
    private String _rawTitle;

    /**
     * Save the data members.
     */
    public RegexTitleShellCondition(String title, boolean modality) {
        super(title, modality);

        _rawTitle = title;

        try {
            _titlePattern = Pattern.compile(title);
        } catch (PatternSyntaxException pse) {
        }
    }

    /**
     * @see  com.windowtester.runtime.swt.condition.shell.ShellCondition#test(org.eclipse.swt.widgets.Shell)
     */
    @Override
    public boolean test(Shell shell) {
        boolean matches = super.testModal(shell);

        if (matches) {
            boolean textMatch = false;
            String shellTitle = shell.getText();

            if (_titlePattern != null) {
                matches = _titlePattern.matcher(shellTitle).matches();
                textMatch = matches;
            }

            if (!matches) {
                matches = _rawTitle.equals(shellTitle);
                textMatch = matches;
            }

            String message = MessageFormat.format("---> EXPECTED TITLE<{0}>; ACTUAL TITLE<{1}>; MATCHES<{2}:{3}>", //$NON-NLS-1$
                                                  new Object[] {
                    _rawTitle, shellTitle, Boolean.toString(textMatch), Boolean.toString(matches)
                });
            PlatformActivator.logDebug(message);
        }

        return matches;
    }
}
