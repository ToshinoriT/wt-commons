/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.swt.util.WaitForIdle;
import junit.framework.TestCase;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IEditorHelper.Placement;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.internal.helpers.impl.HelperImplAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * SourceEditorBlockInserter - Internal utility for inserting blocks of text into the
 * active source file. This class will read in a file and issue corresponding keystrokes
 * in the active source editor to type as a user would.
 */
public class SourceEditorBlockInserter {
    private static final String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$

    /**
     * insertBlock - Use keystrokes to enter a block of text into the file at the given
     * workspace location. Get the block of text from the plugin resource at the given
     * plugin path. Insert the block into the active editor at the given line and column
     * position. This test will issue a TestCase failure if any of the resources cannot be
     * accessed for any reason
     *
     * @param  ui                 - Driver for UI generated input
     * @param  editorHelper       - Implementation of a helper used in text manipulation
     * @param  insertBlockPlugin  - Plugin containing the resource that defines the text
     *                            to be inserted
     * @param  pathToInsertBlock  - The plugin-relative path to the resource of the text
     *                            to insert
     * @param  fullPathToFile     - Full path (project included) to the file in the
     *                            workspace that is to be edited
     * @param  line               - Line number to insert the block of text into. Indexing
     *                            starts at 1
     * @param  column             - Column (character in the given line) to insert the
     *                            block of text into. Indexing starts at 1
     */
    public void insertBlock(IUIContext ui,
                            IEditorHelper editorHelper,
                            Plugin insertBlockPlugin,
                            IPath pathToInsertBlock,
                            IPath fullPathToFile,
                            int line,
                            int column) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(editorHelper);
        TestCase.assertNotNull(insertBlockPlugin);
        TestCase.assertNotNull(pathToInsertBlock);
        TestCase.assertTrue(!pathToInsertBlock.isEmpty());
        TestCase.assertNotNull(fullPathToFile);
        TestCase.assertTrue(!fullPathToFile.isEmpty());

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();

        resources.openFile(ui, fullPathToFile);
        setCursorPosition(ui, editorHelper, line, column);

//        insertBlock(ui, editorHelper, insertBlockPlugin, pathToInsertBlock, fullPathToFile);
        pasteFromFile(ui, editorHelper, insertBlockPlugin, pathToInsertBlock, fullPathToFile);
    }

    /**
     * insertBlock - Use keystrokes to enter a block of text into the file at the given
     * workspace location. Get the block of text from the plugin resource at the given
     * plugin path. Insert the block into the active editor at a position relative to the
     * given search string. This test will issue a TestCase failure if any of the
     * resources cannot be accessed for any reason
     *
     * @param  ui                 - Driver for UI generated input
     * @param  editorHelper       - Implementation of an editor to insert text into
     * @param  insertBlockPlugin  - Plugin containing the resource that defines the text
     *                            to be inserted
     * @param  pathToInsertBlock  - The plugin-relative path to the resource of the text
     *                            to insert
     * @param  fullPathToFile     - Full path (project included) to the file in the
     *                            workspace that is to be edited
     * @param  text               - Text to search in the active editor. This method will
     *                            find the first occurrence of this string in the active
     *                            editor
     * @param  placement          - Position relative to the search string in the active
     *                            file (i.e. - before, after, instead of)
     */
    public void insertBlock(IUIContext ui,
                            IEditorHelper editorHelper,
                            Plugin insertBlockPlugin,
                            IPath pathToInsertBlock,
                            IPath fullPathToFile,
                            String text,
                            Placement placement) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(editorHelper);
        TestCase.assertNotNull(insertBlockPlugin);
        TestCase.assertNotNull(pathToInsertBlock);
        TestCase.assertTrue(!pathToInsertBlock.isEmpty());
        TestCase.assertNotNull(fullPathToFile);
        TestCase.assertTrue(!fullPathToFile.isEmpty());
        TestCase.assertNotNull(text);
        TestCase.assertNotNull(placement);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.openFile(ui, fullPathToFile);

        setCursorPosition(ui, editorHelper, text, placement);

//        insertBlock(ui, editorHelper, insertBlockPlugin, pathToInsertBlock, fullPathToFile);
        pasteFromFile(ui, editorHelper, insertBlockPlugin, pathToInsertBlock, fullPathToFile);
    }

    /**
     * insertBlock - Use keystrokes to enter a block of text into the file at the given
     * workspace location. Get the block of text from the plugin resource at the given
     * plugin path. Insert the block into the active editor at the current character
     * position. This test will issue a TestCase failure if any of the resources cannot be
     * accessed for any reason
     *
     * @param  ui                 - Driver for UI generated input
     * @param  editorHelper       - Implementation of a helper used in text manipulation
     * @param  insertBlockPlugin  - Plugin containing the resource that defines the text
     *                            to be inserted
     * @param  pathToInsertBlock  - The plugin-relative path to the resource of the text
     *                            to insert
     * @param  fullPathToFile     - Full path (project included) to the file in the
     *                            workspace that is to be edited
     */
    private void insertBlock(IUIContext ui,
                             IEditorHelper editorHelper,
                             Plugin insertBlockPlugin,
                             IPath pathToInsertBlock,
                             IPath fullPathToFile) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(editorHelper);
        TestCase.assertNotNull(insertBlockPlugin);
        TestCase.assertNotNull(pathToInsertBlock);
        TestCase.assertTrue(!pathToInsertBlock.isEmpty());
        TestCase.assertNotNull(fullPathToFile);
        TestCase.assertTrue(!fullPathToFile.isEmpty());

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        File insertFromFile = resources.getFileFromPlugin(ui, insertBlockPlugin, pathToInsertBlock);
        IFile toFile = resources.getFileFromWorkspace(ui, fullPathToFile);

        try {
            InputStream blockStream = new FileInputStream(insertFromFile);

            try {
                InputStreamReader blockReader = new InputStreamReader(blockStream);
                BufferedReader block = new BufferedReader(blockReader);

                String nextLine = block.readLine();

                // Condition: This while goes through the rest of the lines
                // entering them
                while (nextLine != null) {
                    safeKeyString(ui, editorHelper, nextLine, toFile);

                    nextLine = block.readLine();

                    if (nextLine != null) {
                        safeNewLine(ui, editorHelper, toFile);
                    }
                }
            } finally {
                if (blockStream != null) {
                    blockStream.close();
                }
            }
        } catch (IOException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getLocalizedMessage());
        }
    }

    /**
     * insertString - Use keystrokes to enter the given text into the file at the given
     * workspace location. Insert the text into the active editor at the current character
     * position. This test will issue a TestCase failure if any of the resources cannot be
     * accessed for any reason
     *
     * @param  ui              - Driver for UI generated input
     * @param  editorHelper    - Implementation of a helper used in text manipulation
     * @param  insertString    - String of characters to insert into the active editor
     * @param  fullPathToFile  - Full path (project included) to the file in the workspace
     *                         that is to be edited
     */
    public void insertString(IUIContext ui,
                             IEditorHelper editorHelper,
                             String insertString,
                             IPath fullPathToFile) {
        pasteIntoFile(ui, fullPathToFile, insertString);
    }

    /**
     * insertString - Use keystrokes to enter the given insertText into the file at the
     * given workspace location. Insert the block into the active editor at a position
     * given by a character offset. This test will issue a TestCase failure if any of the
     * resources cannot be accessed for any reason
     *
     * @param  ui            - Driver for UI generated input
     * @param  editorHelper  - Implementation of a helper used in text manipulation
     * @param  insertText    - the text to insert
     * @param  toFilePath    - Full path (project included) to the file in the workspace
     *                       that is to be edited. Ex: <i>
     *                       RuntimeTestProject/src/folder/ToFile.jsp</i>
     * @param  offset        - The character offset at which the text will be inserted
     */
    public void insertString(IUIContext ui,
                             IEditorHelper editorHelper,
                             String insertText,
                             IPath toFilePath,
                             int offset) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(insertText);
        TestCase.assertNotNull(toFilePath);
        TestCase.assertFalse(toFilePath.isEmpty());
        TestCase.assertTrue(offset >= 0);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.openFile(ui, toFilePath);

        setCursorPosition(ui, editorHelper, offset);

        insertString(ui, editorHelper, insertText, toFilePath);
    }

    /**
     * insertString - Use keystrokes to enter the given text into the file at the given
     * workspace location. Insert the text into the active editor at the current character
     * position. This test will issue a TestCase failure if any of the resources cannot be
     * accessed for any reason
     *
     * @param  ui              - Driver for UI generated input
     * @param  editorHelper    - Implementation of a helper used in text manipulation
     * @param  insertString    - String of characters to insert into the active editor
     * @param  fullPathToFile  - Full path (project included) to the file in the workspace
     *                         that is to be edited
     * @param  text            - Text to search in the active editor. This method will
     *                         find the first occurrence of this string in the active
     *                         editor
     * @param  placement       - Position relative to the search string in the active file
     *                         (i.e. - before, after, instead of)
     */
    public void insertString(IUIContext ui,
                             IEditorHelper editorHelper,
                             String insertString,
                             IPath fullPathToFile,
                             String placementText,
                             Placement placement) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(editorHelper);
        TestCase.assertNotNull(insertString);
        TestCase.assertNotNull(fullPathToFile);
        TestCase.assertTrue(!fullPathToFile.isEmpty());
        TestCase.assertNotNull(placementText);
        TestCase.assertNotNull(placement);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.openFile(ui, fullPathToFile);

        setCursorPosition(ui, editorHelper, placementText, placement);

        insertString(ui, editorHelper, insertString, fullPathToFile);
    }

    /**
     * insertString - Use keystrokes to enter the given insertText into the file at the
     * given workspace location. Insert the block into the active editor at a position
     * given by a line-column position. This test will issue a TestCase failure if any of
     * the resources cannot be accessed for any reason
     *
     * @param  ui              - Driver for UI generated input
     * @param  editorHelper    - Implementation of a helper used in text manipulation
     * @param  insertText      - the text to insert
     * @param  fullPathToFile  - Full path (project included) to the file in the workspace
     *                         that is to be edited. Ex: <i>
     *                         RuntimeTestProject/src/folder/ToFile.jsp</i>
     * @param  line            - The line number on which the new text shall be inserted
     * @param  column          - The column number on the given line to which the new text
     *                         shall be inserted
     */
    public void insertString(IUIContext ui,
                             IEditorHelper editorHelper,
                             String insertString,
                             IPath fullPathToFile,
                             int line,
                             int column) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(editorHelper);
        TestCase.assertNotNull(insertString);
        TestCase.assertNotNull(fullPathToFile);
        TestCase.assertTrue(!fullPathToFile.isEmpty());

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();

        resources.openFile(ui, fullPathToFile);
        setCursorPosition(ui, editorHelper, line, column);

        pasteIntoFile(ui, fullPathToFile, insertString);
    }

    /**
     * Read a file from disk, copy the contents into the clipboard, and paste the contents
     * into the file at the given location.
     */
    private void pasteFromFile(IUIContext ui,
                               IEditorHelper editorHelper,
                               Plugin insertBlockPlugin,
                               IPath pathToInsertBlock,
                               IPath fullPathToFile) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(editorHelper);
        TestCase.assertNotNull(insertBlockPlugin);
        TestCase.assertNotNull(pathToInsertBlock);
        TestCase.assertTrue(!pathToInsertBlock.isEmpty());
        TestCase.assertNotNull(fullPathToFile);
        TestCase.assertTrue(!fullPathToFile.isEmpty());

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        File insertFromFile = resources.getFileFromPlugin(ui, insertBlockPlugin, pathToInsertBlock);

        final StringBuilder buffer = new StringBuilder();

        try {
            InputStream blockStream = new FileInputStream(insertFromFile);

            try {
                InputStreamReader blockReader = new InputStreamReader(blockStream);
                BufferedReader block = new BufferedReader(blockReader);

                String nextLine = block.readLine();

                // Condition: This while goes through the rest of the lines
                // entering them
                while (nextLine != null) {
                    buffer.append(nextLine);
                    nextLine = block.readLine();

                    if (nextLine != null) {
                        buffer.append(NEWLINE);
                    }
                }
            } finally {
                if (blockStream != null) {
                    blockStream.close();
                }
            }
        } catch (IOException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getLocalizedMessage());
        }

        pasteIntoFile(ui, fullPathToFile, buffer.toString());
    }

    /**
     * Paste the given string into the given file. Assume the cursor position has already
     * been set
     */
    private void pasteIntoFile(IUIContext ui,
                               IPath fullPathToFile,
                               final String pasteContents) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(fullPathToFile);
        TestCase.assertTrue(!fullPathToFile.isEmpty());

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        IFile toFile = resources.getFileFromWorkspace(ui, fullPathToFile);

        String oldContents = resources.getFileContents(toFile);
        int oldLength = oldContents.length();
//        System.err.println("--> OLD LENGTH:" + oldLength);

        ui.handleConditions();
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    Clipboard clipboard = new Clipboard(Display.getDefault());

                    try {
                        TextTransfer transfer = TextTransfer.getInstance();
                        clipboard.setContents(new Object[] { pasteContents.toString() }, new Transfer[] {
                                transfer
                            });
                    } finally {
                        clipboard.dispose();
                    }
                }
            });

        try {
            ui.click(new MenuItemLocator("&Edit/&Paste.*")); //$NON-NLS-1$
        } catch (WidgetSearchException e) {
            e.printStackTrace();
            TestCase.fail();
        }

        String newContents = resources.getFileContents(toFile);
        int newLength = newContents.length();
//        System.err.println("--> BUFFER LENGTH:" + buffer.length());
//        System.err.println("--> LINE COUNT:" + lineCount);
//        System.err.println("--> l:" + l);
//        System.err.println("--> NEW LENGTH:" + newLength);

//        TestCase.assertEquals(newLength, oldLength + pasteContents.length());
    }

    /**
     * safeKeyString - For some editors, auto-formatting will insert characters to
     * complete a formatting rule as characters are typed. For example, auto-formatting
     * may insert a closing brace when an opening brace is entered into a java file. This
     * method ensures that only the characters in the given string are entered.
     *
     * @param  ui            - Driver for UI generated input
     * @param  editorHelper  - Implementation of a helper used in text manipulation
     * @param  keyString     - Characters to type into the given target file
     * @param  targetFile    - Enter characters into the current character position of
     *                       this file
     */
    private void safeKeyString(final IUIContext ui,
                               IEditorHelper editorHelper,
                               final String keyString,
                               IFile targetFile) {
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        String oldContents = resources.getFileContents(targetFile);
        int oldLength = oldContents.length();

        int oldLocation = editorHelper.getCursorLocation(ui);

        com.windowtester.swt.IUIContext ui_old = HelperImplAdapter.getUIContext(ui);

        char[] ch = keyString.toCharArray();

        for (int i = 0; i < ch.length; i++) {
            ui.keyClick(ch[i]);
            ui_old.waitForIdle();

            int newLocation = editorHelper.getCursorLocation(ui);

            int locationDifference = newLocation - oldLocation;

            while (locationDifference > 1) {
                ui.keyClick(SWT.ARROW_LEFT);
                ui_old.waitForIdle();
                locationDifference--;
            }

            oldLocation++;

            TestCase.assertTrue(resources.getFileLength(targetFile) > 0);

            while ((resources.getFileLength(targetFile) - oldLength) > 1) {
                ui.keyClick(SWT.DEL);
                ui_old.waitForIdle();
            }

            oldLength++;
        }
    }

    /**
     * safeNewLine - For some editors, auto-formatting will insert characters to complete
     * a formatting rule as characters are typed. For example, auto-formatting may insert
     * a closing brace when an opening brace is entered into a java file. This method
     * ensures that only a newline in the given string is entered.
     *
     * @param  ui            - Driver for UI generated input
     * @param  editorHelper  - Implementation of a helper used in text manipulation
     * @param  targetFile    - Enter characters into the current character position of
     *                       this file
     */
    private void safeNewLine(IUIContext ui,
                             IEditorHelper editorHelper,
                             IFile targetFile) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(editorHelper);
        TestCase.assertNotNull(targetFile);

        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        String oldContents = resources.getFileContents(targetFile);
        int oldLength = oldContents.length();

        int oldLocation = editorHelper.getCursorLocation(ui);

        ui.keyClick(SWT.CR);
        new WaitForIdle().waitForIdle();

        int newLocation = editorHelper.getCursorLocation(ui);

        String newContents = resources.getFileContents(targetFile);

        int tempLocation = oldLocation;
        int difference = 1;

        while (newContents.charAt(tempLocation) != '\n') {
            tempLocation++;
            difference++;
        }

        while (newLocation > (tempLocation + 1)) {
            ui.keyClick(SWT.ARROW_LEFT);
            new WaitForIdle().waitForIdle();

            newLocation = editorHelper.getCursorLocation(ui);
        }

        TestCase.assertTrue(resources.getFileLength(targetFile) > 0);

        while ((resources.getFileLength(targetFile) - oldLength) > difference) {
            ui.keyClick(SWT.DEL);
            new WaitForIdle().waitForIdle();
        }
    }

    /**
     * setCursorPosition - Internal method to set the position of the cursor in the active
     * editor.
     *
     * @param  ui            - Driver for UI generated input
     * @param  editorHelper  - Implementation of a helper used in text manipulation
     * @param  text          - Text to search in the active editor. This method will find
     *                       the first occurrence of this string in the active editor
     * @param  placement     - Position relative to the search string in the active file
     *                       (i.e. - before, after, instead of)
     */
    public void setCursorPosition(IUIContext ui,
                                  IEditorHelper editorHelper,
                                  String text,
                                  Placement placement) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(editorHelper);
        TestCase.assertNotNull(text);
        TestCase.assertNotNull(placement);

        editorHelper.findFirst(ui, text);

        if (placement.equals(Placement.BEFORE)) {
            ui.keyClick(SWT.ARROW_LEFT);
            ui.keyClick(SWT.CR);
            ui.keyClick(SWT.ARROW_UP);
        } else if (placement.equals(Placement.AFTER)) {
            ui.keyClick(SWT.ARROW_RIGHT);
        } else {
            ui.keyClick(SWT.DEL);
        }
    }

    /**
     * setCursorPosition - Internal method to set the position of the cursor in the active
     * editor.
     *
     * @param  ui            - Driver for UI generated input
     * @param  editorHelper  - Implementation of a helper used in text manipulation
     * @param  offset        - The character offset at which the cursor will be placed
     */
    private void setCursorPosition(IUIContext ui,
                                   IEditorHelper editorHelper,
                                   int offset) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(editorHelper);
        TestCase.assertTrue(offset >= 0);

        editorHelper.placeCursor(ui, offset);
    }

    /**
     * setCursorPosition - Internal method to set the position of the cursor in the active
     * editor.
     *
     * @param  ui            - Driver for UI generated input
     * @param  editorHelper  - Implementation of a helper used in text manipulation
     * @param  line          - Line number to insert the block of text into. Indexing
     *                       starts at 1
     * @param  column        - Column (character in the given line) to insert the block of
     *                       text into. Indexing starts at 1
     */
    private void setCursorPosition(IUIContext ui,
                                   IEditorHelper editorHelper,
                                   int line,
                                   int column) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(editorHelper);
        TestCase.assertTrue("The line number must be greater than 0", line > 0); //$NON-NLS-1$
        TestCase.assertTrue("The column number must be greater than 0", column > 0); //$NON-NLS-1$

        editorHelper.placeCursor(ui, line, column);
    }

}
