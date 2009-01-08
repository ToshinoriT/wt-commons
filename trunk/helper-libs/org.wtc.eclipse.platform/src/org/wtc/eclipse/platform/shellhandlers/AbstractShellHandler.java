/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.shellhandlers;

import com.windowtester.runtime.IUIContext;
import junit.framework.TestCase;

/**
 * Shell handler for the Shell Monitor that will handle a shell that may or may not pop
 * and is to be disposed without failing the test.
 */
public abstract class AbstractShellHandler implements IWorkbenchShellHandler {
    private final String _title;
    private final boolean _isModal;

    private final int _hashCode;

    /**
     * Save the UI context.
     *
     * @param  ui  - Driver for UI generated input
     */
    protected AbstractShellHandler(IUIContext ui,
                                   String title,
                                   boolean isModal) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(title);

        _title = title;
        _isModal = isModal;

        _hashCode = getClass().hashCode();
    }

    /**
     * @see  java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;

        if (obj instanceof AbstractShellHandler) {
            equals = ((AbstractShellHandler) obj).hashCode() == hashCode();
        }

        return equals;
    }

    /**
     * @return  String - The title of the dialog that this handler reacts to
     */
    public String getTitle() {
        return _title;
    }

    /**
     * @see  java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _hashCode;
    }

    /**
     * @return  boolean - True if this dialog is modal when it is shown; False otherwise
     */
    public boolean isModal() {
        return _isModal;
    }
}
