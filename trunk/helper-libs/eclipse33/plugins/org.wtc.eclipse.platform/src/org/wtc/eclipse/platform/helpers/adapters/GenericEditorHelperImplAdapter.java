/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers.adapters;

import abbot.tester.swt.ButtonTester;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.LabeledLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import junit.framework.TestCase;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper.View;
import org.wtc.eclipse.platform.util.SourceEditorBlockInserter;

/**
 * Helper for manipulating files through the source editor. Adapter for any text-based
 * editors
 * 
 * @since 3.8.0
 */
public class GenericEditorHelperImplAdapter extends HelperImplAdapter implements IEditorHelper {
    /**
     * make sure the active editor is not an ITextEditor.
     *
     * @return  the active editor as an ITextEditor
     */
    protected ITextEditor ensureTextEditor(IUIContext ui) {
        IEditorPart activeEditor = getActiveEditor(ui);

        TestCase.assertTrue(activeEditor instanceof ITextEditor);

        return (ITextEditor) activeEditor;
    }

    /**
     * findAndReplace- Open the given file, search for the provided string and replace it
     * the specified number of times with the given replacement value.
     *
     * @since 3.8.0
     * @param  ui                   - Driver for UI generated input
     * @param  filePath             - Full path (project included) of the file to edit.
     *                              Ex: <i>RuntimeTestProject/src/folder/MyFile.jsp</i>
     * @param  searchString         - Search for this string
     * @param  replaceString        - Replace the search string with this string
     * @param  numberOfOccurrences  - the number of times to replace the pattern (< 0 to
     *                              replace all)
     */
    private void findAndReplace(IUIContext ui,
                                IPath filePath,
                                String searchString,
                                String replaceString,
                                int numberOfOccurrences) {
        findAndReplace(ui,
                       filePath,
                       searchString,
                       replaceString,
                       numberOfOccurrences,
                       false);
    }

    /**
     * findAndReplace- Open the given file, search for the provided string and replace it
     * the specified number of times with the given replacement value.
     *
     * @since 3.8.0
     * @param  ui                   - Driver for UI generated input
     * @param  filePath             - Full path (project included) of the file to edit.
     *                              Ex: <i>RuntimeTestProject/src/folder/MyFile.jsp</i>
     * @param  searchString         - Search for this string
     * @param  replaceString        - Replace the search string with this string
     * @param  numberOfOccurrences  - the number of times to replace the pattern (< 0 to
     *                              replace all)
     * @param  useRegex             - True if the search string is to check the regular
     *                              expression checkbox on the find and replace dialog;
     *                              false if that checkbox is to be unchecked
     */
    private void findAndReplace(IUIContext ui,
                                IPath filePath,
                                String searchString,
                                String replaceString,
                                int numberOfOccurrences,
                                boolean useRegEx) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertNotNull(searchString);
        TestCase.assertNotNull(replaceString);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.openFile(ui, filePath);

        gotoLine(ui, 1);

        try {
            new SWTIdleCondition().waitForIdle();
            ui.click(new MenuItemLocator("&Edit/&Find.*Replace.*")); //$NON-NLS-1$
            ui.wait(new ShellShowingCondition("Find/Replace")); //$NON-NLS-1$

            try {
                IWidgetLocator searchComboLocator = new LabeledLocator(Combo.class, "&Find:"); //$NON-NLS-1$
                IWidgetLocator searchComboRef = ui.find(searchComboLocator);
                TestCase.assertTrue(searchComboRef instanceof WidgetReference);
                ui.enterText(searchString);

                IWidgetLocator replaceComboLocator = new LabeledLocator(Combo.class,
                                                                        "R&eplace (W|w)ith:"); //$NON-NLS-1$
                IWidgetLocator replaceComboRef = ui.find(replaceComboLocator);
                TestCase.assertTrue(replaceComboRef instanceof WidgetReference);

                safeEnterText(ui, replaceComboLocator, replaceString);

                IWidgetLocator checkRef = (IWidgetLocator) ui.find(new ButtonLocator("Wra&p (S|s)earch")); //$NON-NLS-1$
                TestCase.assertTrue(checkRef instanceof WidgetReference);

                Button wrapButton = (Button) ((WidgetReference) checkRef).getWidget();
                ButtonTester buttonTester = new ButtonTester();

                if (!buttonTester.getSelection(wrapButton)) {
                    ui.click(checkRef);
                }

                IWidgetLocator regexRef = (IWidgetLocator) ui.find(new ButtonLocator("Regular e&xpressions")); //$NON-NLS-1$
                TestCase.assertTrue(regexRef instanceof WidgetReference);

                Button regexButton = (Button) ((WidgetReference) regexRef).getWidget();

                if (buttonTester.getSelection((regexButton)) != useRegEx) {
                    ui.click(regexRef);
                }

                if (numberOfOccurrences < 0) {
                    ui.click(new ButtonLocator("Replace &All")); //$NON-NLS-1$
                } else {
                    IWidgetLocator replaceRef = (IWidgetLocator) ui.find(new ButtonLocator("&Replace")); //$NON-NLS-1$
                    TestCase.assertTrue(replaceRef instanceof WidgetReference);

                    Button replaceButton = (Button) ((WidgetReference) replaceRef).getWidget();

                    for (int i = 0; i < numberOfOccurrences; i++) {
                        ui.click(new ButtonLocator("Fi&nd")); //$NON-NLS-1$
                        TestCase.assertTrue(buttonTester.isEnabled(replaceButton));
                        ui.click(replaceRef);
                    }
                }
            } finally {
                clickClose(ui);
                ui.wait(new ShellDisposedCondition("Find/Replace")); //$NON-NLS-1$
            }
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getMessage());
        }

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.saveAndWait(ui);
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#findAndReplaceAll(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String, java.lang.String)
     */
    public void findAndReplaceAll(IUIContext ui,
                                  IPath filePath,
                                  String searchString,
                                  String replaceString) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertNotNull(searchString);
        TestCase.assertNotNull(replaceString);

        logEntry2(filePath.toString(), searchString, replaceString);

        findAndReplaceAll(ui,
                          filePath,
                          searchString,
                          replaceString,
                          false);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#findAndReplaceAll(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String, java.lang.String, boolean)
     */
    public void findAndReplaceAll(IUIContext ui,
                                  IPath filePath,
                                  String searchString,
                                  String replaceString,
                                  boolean useRegex) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertNotNull(searchString);
        TestCase.assertNotNull(replaceString);

        logEntry2(filePath.toPortableString(), searchString, replaceString, Boolean.toString(useRegex));

        findAndReplace(ui, filePath, searchString, replaceString, -1, useRegex);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#findAndReplaceFirst(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String, java.lang.String)
     */
    public void findAndReplaceFirst(IUIContext ui,
                                    IPath filePath,
                                    String searchString,
                                    String replaceString) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertNotNull(searchString);
        TestCase.assertNotNull(replaceString);

        logEntry2(filePath.toPortableString(), searchString, replaceString);

        findAndReplace(ui, filePath, searchString, replaceString, 1);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#findAndReplaceOccurrences(IUIContext,
     *       org.eclipse.core.runtime.IPath, java.lang.String, java.lang.String, int)
     */
    public void findAndReplaceOccurrences(IUIContext ui,
                                          IPath filePath,
                                          String searchString,
                                          String replaceString,
                                          int numberOfOccurrences) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertNotNull(searchString);
        TestCase.assertNotNull(replaceString);
        TestCase.assertTrue(numberOfOccurrences > 0);

        logEntry2(filePath.toPortableString(), searchString, replaceString, Integer.toString(numberOfOccurrences));

        findAndReplace(ui, filePath, searchString, replaceString, numberOfOccurrences);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#findFirst(com.windowtester.runtime.IUIContext,
     *       java.lang.String)
     */
    public void findFirst(IUIContext ui, final String searchString) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(searchString);

        logEntry2(searchString);

        ensureTextEditor(ui);

        gotoLine(ui, 1);

        try {
            ui.click(new MenuItemLocator("&Edit/&Find.*Replace.*")); //$NON-NLS-1$

            ui.wait(new ShellShowingCondition("Find/Replace")); //$NON-NLS-1$
            ui.enterText(searchString);

            ui.click(new ButtonLocator("Fi&nd")); //$NON-NLS-1$

            // Wait for things to repaint
            new SWTIdleCondition().waitForIdle();

            // If we found the String, then there will be text
            // highlighted and the "Replace" button will be enabled.
            // If we didn't find text, then the "Replace" button
            // will be disabled.
            IWidgetLocator buttonRef = ui.find(new ButtonLocator("&Replace")); //$NON-NLS-1$
            TestCase.assertTrue(buttonRef instanceof WidgetReference);

            final Button replaceButton = (Button) ((WidgetReference) buttonRef).getWidget();
            final ButtonTester buttonTester = new ButtonTester();

            try {
                ui.wait(new ICondition() {
                        public boolean test() {
                            return buttonTester.isEnabled(replaceButton);
                        }

                        @Override
                        public String toString() {
                            return " FOR THE STRING <" + searchString + "> TO BE FOUND IN THE CURRENT EDITOR"; //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }, 2500, 100);
            } finally {
                clickClose(ui);
                ui.wait(new ShellDisposedCondition("Find/Replace")); //$NON-NLS-1$
            }
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getMessage());
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#getActiveEditor(com.windowtester.runtime.IUIContext)
     */
    public IEditorPart getActiveEditor(IUIContext ui) {
        TestCase.assertNotNull(ui);
        logEntry2();

        final IEditorPart[] activeEditor = new IEditorPart[1];

        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    TestCase.assertNotNull(window);

                    IWorkbenchPage page = window.getActivePage();
                    TestCase.assertNotNull(page);

                    activeEditor[0] = page.getActiveEditor();
                }
            });

        TestCase.assertNotNull(activeEditor[0]);
        logExit2(activeEditor[0].toString());

        return activeEditor[0];
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#getCursorLocation(com.windowtester.runtime.IUIContext)
     */
    public int getCursorLocation(IUIContext ui) {
        logEntry2();

        final ITextEditor textEditor = ensureTextEditor(ui);

        final int[] offset = new int[1];
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    ISelectionProvider selectionProvider = textEditor.getSelectionProvider();
                    TestCase.assertNotNull(selectionProvider);

                    ITextSelection selection = (ITextSelection) selectionProvider.getSelection();
                    TestCase.assertNotNull(selection);

                    offset[0] = selection.getOffset();
                }
            });

        TestCase.assertTrue(offset[0] >= 0);

        logExit2();

        return offset[0];
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#gotoLine(com.windowtester.runtime.IUIContext,
     *       int)
     */
    public void gotoLine(IUIContext ui, int line) {
        TestCase.assertNotNull(ui);
        TestCase.assertTrue("The line number must be greater than 0", line > 0); //$NON-NLS-1$

        logEntry2(Integer.toString(line));

        ensureTextEditor(ui);

        try {
            ui.click(new MenuItemLocator("&Navigate/.*Go to Line.*")); //$NON-NLS-1$

            ui.wait(new ShellShowingCondition("Go to Line")); //$NON-NLS-1$
            ui.enterText(Integer.toString(line));

            clickOK(ui);

            ui.wait(new ShellDisposedCondition("Go to Line")); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getMessage());
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#insertBlock(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath, java.lang.String,
     *       org.wtc.eclipse.platform.helpers.IEditorHelper.Placement)
     */
    public void insertBlock(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath fromFilePath,
                            IPath toFilePath,
                            String text,
                            Placement placement) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(fromFilePath);
        TestCase.assertNotNull(toFilePath);
        TestCase.assertNotNull(text);
        TestCase.assertNotNull(placement);

        logEntry2(sourcePlugin.getBundle().getSymbolicName(), fromFilePath.toPortableString(), toFilePath.toPortableString(), text, placement.toString());

        insertBlock(ui,
                    sourcePlugin,
                    fromFilePath,
                    toFilePath,
                    text,
                    placement,
                    true);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#insertBlock(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath, int, int)
     */
    public void insertBlock(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath fromFilePath,
                            IPath toFilePath,
                            int line,
                            int column) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(fromFilePath.toPortableString());
        TestCase.assertNotNull(toFilePath.toPortableString());

        logEntry2(sourcePlugin.getBundle().getSymbolicName(), fromFilePath.toPortableString(), toFilePath.toPortableString(), Integer.toString(line), Integer.toString(column));

        insertBlock(ui,
                    sourcePlugin,
                    fromFilePath,
                    toFilePath,
                    line,
                    column,
                    true);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#insertBlock(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath, java.lang.String,
     *       org.wtc.eclipse.platform.helpers.IEditorHelper.Placement, boolean)
     */
    public void insertBlock(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath fromFilePath,
                            IPath toFilePath,
                            String text,
                            Placement placement,
                            boolean save) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(fromFilePath);
        TestCase.assertNotNull(toFilePath);
        TestCase.assertNotNull(text);
        TestCase.assertNotNull(placement);

        logEntry2(sourcePlugin.getBundle().getSymbolicName(), fromFilePath.toPortableString(), toFilePath.toPortableString(), text, placement.toString(), Boolean.toString(save));

        ensureTextEditor(ui);

        SourceEditorBlockInserter sebi = new SourceEditorBlockInserter();
        sebi.insertBlock(ui,
                         this,
                         sourcePlugin,
                         fromFilePath,
                         toFilePath,
                         text,
                         placement);

        saveOrWait(ui, save);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#insertBlock(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.Plugin, org.eclipse.core.runtime.IPath,
     *       org.eclipse.core.runtime.IPath, int, int, boolean)
     */
    public void insertBlock(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath fromFilePath,
                            IPath toFilePath,
                            int line,
                            int column,
                            boolean save) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(sourcePlugin);
        TestCase.assertNotNull(fromFilePath);
        TestCase.assertNotNull(toFilePath);

        logEntry2(sourcePlugin.getBundle().getSymbolicName(), fromFilePath.toPortableString(), toFilePath.toPortableString(), Integer.toString(line), Integer.toString(column), Boolean.toString(save));

        ensureTextEditor(ui);

        SourceEditorBlockInserter sebi = new SourceEditorBlockInserter();
        sebi.insertBlock(ui,
                         this,
                         sourcePlugin,
                         fromFilePath,
                         toFilePath,
                         line,
                         column);

        saveOrWait(ui, save);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#insertString(com.windowtester.runtime.IUIContext,
     *       java.lang.String, org.eclipse.core.runtime.IPath, java.lang.String,
     *       org.wtc.eclipse.platform.helpers.IEditorHelper.Placement)
     */
    public void insertString(IUIContext ui,
                             String insertText,
                             IPath toFilePath,
                             String searchString,
                             Placement placement) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(insertText);
        TestCase.assertNotNull(toFilePath);
        TestCase.assertNotNull(searchString);
        TestCase.assertNotNull(placement);

        logEntry2(insertText, toFilePath.toPortableString(), searchString, placement.toString());

        insertString(ui,
                     insertText,
                     toFilePath,
                     searchString,
                     placement,
                     true);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#insertString(IUIContext,java.lang.String,
     *       org.eclipse.core.runtime.IPath, int, boolean)
     */
    public void insertString(IUIContext ui,
                             String insertText,
                             IPath toFilePath,
                             int offset,
                             boolean save) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(insertText);
        TestCase.assertNotNull(toFilePath);
        TestCase.assertFalse(toFilePath.isEmpty());
        TestCase.assertTrue(offset >= 0);

        logEntry2(insertText, toFilePath.toPortableString(), Integer.toString(offset), Boolean.toString(save));

        ensureTextEditor(ui);

        SourceEditorBlockInserter sebi = new SourceEditorBlockInserter();
        sebi.insertString(ui,
                          this,
                          insertText,
                          toFilePath,
                          offset);

        saveOrWait(ui, save);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#insertString(IUIContext,java.lang.String,
     *       org.eclipse.core.runtime.IPath, java.lang.String,
     *       org.wtc.eclipse.platform.helpers.IEditorHelper.Placement, boolean)
     */
    public void insertString(IUIContext ui,
                             String insertText,
                             IPath toFilePath,
                             String searchString,
                             Placement placement,
                             boolean save) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(insertText);
        TestCase.assertNotNull(searchString);
        TestCase.assertNotNull(toFilePath);
        TestCase.assertNotNull(placement);

        logEntry2(insertText, toFilePath.toPortableString(), searchString, placement.toString(), Boolean.toString(save));

        ensureTextEditor(ui);

        SourceEditorBlockInserter sebi = new SourceEditorBlockInserter();
        sebi.insertString(ui,
                          this,
                          insertText,
                          toFilePath,
                          searchString,
                          placement);

        saveOrWait(ui, save);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#insertString(com.windowtester.runtime.IUIContext,
     *       java.lang.String, org.eclipse.core.runtime.IPath, int, int, boolean)
     */
    public void insertString(IUIContext ui,
                             String insertText,
                             IPath toFilePath,
                             int line,
                             int column,
                             boolean save) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(insertText);
        TestCase.assertNotNull(toFilePath);

        logEntry2(insertText, toFilePath.toPortableString(), Integer.toString(line), Integer.toString(column), Boolean.toString(save));

        ensureTextEditor(ui);

        SourceEditorBlockInserter sebi = new SourceEditorBlockInserter();
        sebi.insertString(ui,
                          this,
                          insertText,
                          toFilePath,
                          line,
                          column);

        saveOrWait(ui, save);

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#openFileInTextEditor(com.windowtester.runtime.IUIContext,
     *       org.eclipse.core.runtime.IPath)
     */
    public void openFileInTextEditor(IUIContext ui, IPath filePath) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(filePath);
        TestCase.assertFalse(filePath.isEmpty());

        logEntry2(filePath.toPortableString());

        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.openView(ui, View.BASIC_NAVIGATOR);
        IWidgetLocator treeLocator = new SWTWidgetLocator(Tree.class,
                                                          new ViewLocator(IWorkbenchHelper.View.BASIC_NAVIGATOR.getViewID()));
        String treePath = filePath.toPortableString();
        System.err.println("CLICKING: " + treePath); //$NON-NLS-1$

        WidgetSearchException lastWSE = null;

        for (int i = 0; i < 3; i++) {
            try {
                ui.contextClick(new TreeItemLocator(treePath, treeLocator),
                                "Open With/Text Editor"); //$NON-NLS-1$
                lastWSE = null;

                break;
            } catch (WidgetSearchException wse) {
                lastWSE = wse;
            }
        }

        if (lastWSE != null) {
            PlatformActivator.logException(lastWSE);
            TestCase.fail(lastWSE.getLocalizedMessage());
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#placeCursor(com.windowtester.runtime.IUIContext,
     *       int)
     */
    public void placeCursor(IUIContext ui, final int offset) {
        TestCase.assertNotNull(ui);
        TestCase.assertTrue("The offset must be positive", offset >= 0); //$NON-NLS-1$

        logEntry2(Integer.toString(offset));

        final ITextEditor textEditor = ensureTextEditor(ui);

        // tried to use arrow keys here, but code folding causes problems
        ui.handleConditions();
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    textEditor.selectAndReveal(offset, 0);
                }
            });
        ui.handleConditions();

        new SWTIdleCondition().waitForIdle();

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#placeCursor(com.windowtester.runtime.IUIContext,
     *       int, int)
     */
    public void placeCursor(IUIContext ui, int line, int column) {
        TestCase.assertNotNull(ui);
        TestCase.assertTrue("The line number must be greater than 0", line > 0); //$NON-NLS-1$
        TestCase.assertTrue("The column number must be greater than 0", column > 0); //$NON-NLS-1$

        logEntry2(Integer.toString(line), Integer.toString(column));

        ensureTextEditor(ui);

        gotoLine(ui, line);

        ui.pause(100);

        while (column > 1) {
            ui.keyClick(SWT.ARROW_RIGHT);
            column = column - 1;
        }

        logExit2();
    }

    /**
     * @see  org.wtc.eclipse.platform.helpers.IEditorHelper#placeCursorAfter(com.windowtester.runtime.IUIContext,
     *       java.lang.String)
     */
    public void placeCursorAfter(IUIContext ui, String searchString) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(searchString);

        logEntry2(searchString);

        ensureTextEditor(ui);

        SourceEditorBlockInserter sebi = new SourceEditorBlockInserter();
        sebi.setCursorPosition(ui, this, searchString, Placement.AFTER);

        logExit2();
    }

    /**
     * Utility for saving or waiting on insert method exit.
     */
    private void saveOrWait(IUIContext ui, boolean save) {
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();

        if (save) {
            workbench.saveAndWait(ui);
        } else {
            workbench.waitNoJobs(ui);
        }
    }

}
