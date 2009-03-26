/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.conditions;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.condition.ICondition;
import junit.framework.TestCase;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Condition that waits for the open editors to (not) contain an editor for the given file
 * path.
 */
public class FileOpenCondition implements ICondition {
    private final IPath _filePath;
    private final boolean _open;

    /**
     * Save the data members.
     *
     * @param  ui        - Driver for UI generated input. Needed because any time we start
     *                   doing synExec calls outside of the WindowTester framework, we'll
     *                   need to call handleConditions ourselves
     * @param  filePath  - The full path (project included) of the file to verify editors
     *                   for
     * @param  open      - True if an editor for the given file should be open for the
     *                   given file for this condition to be met; false otherwise
     */
    public FileOpenCondition(IUIContext ui,
                             IPath filePath,
                             boolean open) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        ui.handleConditions();

        _filePath = filePath;
        _open = open;
    }

    /**
     * @see  com.windowtester.runtime2.condition.ICondition#test()
     */
    public boolean test() {
        final boolean[] found = new boolean[1];
        found[0] = false;

        final IPath absoluteFilePath = _filePath.makeAbsolute();
        final Exception[] exceptions = new Exception[1];
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

                    if (window != null) {
                        IWorkbenchPage page = window.getActivePage();

                        if (page != null) {
                            IEditorReference[] references = page.getEditorReferences();

                            for (IEditorReference nextRef : references) {
                                try {
                                    IEditorInput input = nextRef.getEditorInput();

                                    if (input instanceof IFileEditorInput) {
                                        IFileEditorInput fileInput = (IFileEditorInput) input;
                                        IFile file = fileInput.getFile();
                                        IPath actualFilePath = file.getFullPath();

                                        if (actualFilePath.equals(absoluteFilePath)) {
                                            found[0] = true;

                                            break;
                                        }
                                    }
                                } catch (PartInitException e) {
                                    exceptions[0] = e;
                                }
                            }
                        }
                    }
                }
            });

        return found[0] == _open;
    }
}
