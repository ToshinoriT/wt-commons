/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.handlers;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.IConditionHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.wtc.eclipse.platform.internal.handlers.FocusFixingConditionRegistry;
import java.util.HashSet;
import java.util.Set;

/**
 * A special handler whose job it is to fix drifting focus issues in GTK.
 *
 * <pre>
 * NOTES:
 *
 *      1. This handler should be added to the ConditionMonitor before any associated shell
 *         condition handlers
 *      2. Add it directly via the ConditionMonitor:
 *
 *             ConditionMonitor.getInstance().add(new FocusFixingConditionHandler());
 *
 *      3. This is a HACK.  Buyer beware...
 *
 *      4. LET'S TRY NOT TO USE THIS IN 9.5+ BECAUSE RED HAT 4 SHOULD
 *         HAVE THESE ISSUES FIXED
 *
 * </pre>
 *
 * <p>Copyright (c) 2006, Instantiations, Inc.<br>
 * All Rights Reserved</p>
 *
 * @author  Phil Quitslund
 * @since 3.8.0
 */
public class FocusFixingConditionHandler implements IConditionHandler {
    // The shared instance
    private static FocusFixingConditionHandler _instance;
    private final Set<String> ALWAYS_ON_TOP_DEFAULT;
    private Set<String> _alwaysOnTop;
    private boolean _enabled;

    private Shell _focusShell;

    /**
     * Initialize the data members.
     */
    public FocusFixingConditionHandler() {
        ALWAYS_ON_TOP_DEFAULT = FocusFixingConditionRegistry.getFocusFixingShells();
        _alwaysOnTop = new HashSet<String>(ALWAYS_ON_TOP_DEFAULT);
        _enabled = true;
        _instance = this;
    }

    public static void addShell(String title) {
        if (_instance != null) {
            _instance._alwaysOnTop.add(title);
        }
    }

    /**
     * @see  com.windowtester.swt.condition.IHandler#handle(IUIContext)
     */
    public void handle(IUIContext ui) {
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    if ((_focusShell == null) || _focusShell.isDisposed())
                        return; // fast fail if it's null

                    try {
                        System.out.println("forcing active: " + _focusShell); //$NON-NLS-1$
                        _focusShell.forceActive();
                        _focusShell.forceFocus();
                    } catch (Exception e) {
                        // do something?
                    }

                    // reset so we don't keep doing it unnecessarily
                    _focusShell = null;
                }
            });
    }

    public static void removeShell(String title) {
        if (_instance != null) {
            _instance._alwaysOnTop.remove(title);
        }
    }

    public static void reset() {
        _instance._alwaysOnTop = new HashSet<String>(_instance.ALWAYS_ON_TOP_DEFAULT);
        setEnabled(true);

    }

    public static void setEnabled(boolean enable) {
        _instance._enabled = enable;
    }

    private boolean shouldBeInFront(Shell shell, Shell[] shells) {
        if (shell.isDisposed())
            return false;

        Shell active = Display.getDefault().getActiveShell();

        if (active == shell)
            return false;

        String title = null;

        try {
            title = shell.getText();
        } catch (Exception e) {
            // might be disposed
        }

        if (title == null)
            return false;

        for (String specialCase : _alwaysOnTop) {
            if (title.equals(specialCase))
                return true;
        }

        return false;
    }

    /**
     * @see  com.windowtester.swt.condition.ICondition#test()
     */
    public boolean test() {
        if (!_enabled) {
            return false;
        }

        _focusShell = null;
        final Display display = Display.getDefault();
        display.syncExec(new Runnable() {
                public void run() {
                    Shell[] shells = display.getShells();

                    for (Shell shell : shells) {
                        if (shouldBeInFront(shell, shells))
                            _focusShell = shell;
                    }
                }

            });

        return _focusShell != null;
    }

}
