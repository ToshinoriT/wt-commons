/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.IEditorPart;

/**
 * Helper for manipulating files through the source editor.
 * 
 * @since 3.8.0
 */
public interface IEditorHelper {
    // ------------------------------------------------------------------------
    // Placement types relative to the search string in text edits
    // ------------------------------------------------------------------------
    public enum Placement {
        AFTER,
        BEFORE,
        INSTEADOF
    }

    /**
     * findAndReplaceAll - Open the given file, search for the provided string and replace
     * it with the given replacement value.
     *
     * @since 3.8.0
     * @param  ui             - Driver for UI generated input
     * @param  filePath       - Full path (project included) of the file to edit. Ex: <i>
     *                        RuntimeTestProject/src/folder/MyFile.jsp</i>
     * @param  searchString   - Search for this string
     * @param  replaceString  - Replace the search string with this string
     */
    public void findAndReplaceAll(IUIContext ui,
                                  IPath filePath,
                                  String searchString,
                                  String replaceString);

    /**
     * findAndReplaceAll - Open the given file, search for the provided string and replace
     * it with the given replacement value.
     *
     * @since 3.8.0
     * @param  ui             - Driver for UI generated input
     * @param  filePath       - Full path (project included) of the file to edit. Ex: <i>
     *                        RuntimeTestProject/src/folder/MyFile.jsp</i>
     * @param  searchString   - Search for this string
     * @param  replaceString  - Replace the search string with this string
     * @param  useRegex       - True if the search string is to check the regular
     *                        expression checkbox on the find and replace dialog; false if
     *                        that checkbox is to be unchecked
     */
    public void findAndReplaceAll(IUIContext ui,
                                  IPath filePath,
                                  String searchString,
                                  String replaceString,
                                  boolean useRegex);

    /**
     * findAndReplaceFirst - Open the given file, search for the provided string and
     * replace it with the given replacement value.
     *
     * @since 3.8.0
     * @param  ui             - Driver for UI generated input
     * @param  filePath       - Full path (project included) of the file to edit. Ex: <i>
     *                        RuntimeTestProject/src/folder/MyFile.jsp</i>
     * @param  searchString   - Search for this string
     * @param  replaceString  - Replace the search string with this string
     */
    public void findAndReplaceFirst(IUIContext ui,
                                    IPath filePath,
                                    String searchString,
                                    String replaceString);

    /**
     * findAndReplaceOccurrences- Open the given file, search for the provided string and
     * replace it the specified number of times with the given replacement value.
     *
     * @since 3.8.0
     * @param  ui                   - Driver for UI generated input
     * @param  filePath             - Full path (project included) of the file to edit.
     *                              Ex: <i>RuntimeTestProject/src/folder/MyFile.jsp</i>
     * @param  searchString         - Search for this string
     * @param  replaceString        - Replace the search string with this string
     * @param  numberOfOccurrences  - the number of times to replace the pattern
     */
    public void findAndReplaceOccurrences(IUIContext ui,
                                          IPath filePath,
                                          String searchString,
                                          String replaceString,
                                          int numberOfOccurrences);

    /**
     * findFirst - Use the "Find/Replace" dialog to find the first occurrence of the given
     * search string in the active editor.
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  searchString  - String to find in the active text editor
     */
    public void findFirst(IUIContext ui, String searchString);

    /**
     * getActiveEditor - Internal use only method to get the active editor in the
     * workbench. This method will issue a TestCase failure if the active editor cannot be
     * determined for any reason
     *
     * @param   ui  - Driver for UI-generated input
     * @return  IEditorPart - The view part of the active editor
     */
    public IEditorPart getActiveEditor(IUIContext ui);

    /**
     * getCursorLocation - Return the cursor offset of the active text editor. This method
     * will issue a TestCase failure if there is no active ITextEditor
     *
     * @param   ui  - Driver for UI generated input
     * @return  int - Cursor offset of current text editor
     */
    public int getCursorLocation(IUIContext ui);

    /**
     * gotoLine - Use the "Go to Line" to place the cursor at a line location in the
     * active editor.
     *
     * @since 3.8.0
     * @param  ui    - Driver for UI generated input
     * @param  line  - The line number to place the cursor at. Should be within the bounds
     *               of the length of the file
     */
    public void gotoLine(IUIContext ui, int line);

    /**
     * insertBlock - Use keystrokes to enter a block of text into the file at the given
     * workspace location. Get the block of text from the plugin resource at the given
     * plugin path. Insert the block into the active editor at a position relative to the
     * given search string. This test will issue a TestCase failure if any of the
     * resources cannot be accessed for any reason
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  sourcePlugin  - Plugin containing the resource that defines the text to be
     *                       inserted
     * @param  fromFilePath  - The plugin-relative path (plugin NOT included) to the
     *                       resource of the text to insert. Ex: <i>
     *                       resources/testfiles/FromFile.jsp</i>
     * @param  toFilePath    - Full path (project included) to the file in the workspace
     *                       that is to be edited. Ex: <i>
     *                       RuntimeTestProject/src/folder/ToFile.jsp</i>
     * @param  text          - Text to search in the active editor. This method will find
     *                       the first occurrence of this string in the active editor
     * @param  placement     - Position relative to the search string in the active file
     *                       (i.e. - before, after, instead of)
     */
    public void insertBlock(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath fromFilePath,
                            IPath toFilePath,
                            String text,
                            Placement placement);

    /**
     * insertBlock - Use keystrokes to enter a block of text into the file at the given
     * workspace location. Get the block of text from the plugin resource at the given
     * plugin path. Insert the block into the active editor at a fixed line, column
     * position. This test will issue a TestCase failure if any of the resources cannot be
     * accessed for any reason
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  sourcePlugin  - Plugin containing the resource that defines the text to be
     *                       inserted
     * @param  fromFilePath  - The plugin-relative path (plugin NOT included) to the
     *                       resource of the text to insert. Ex: <i>
     *                       resources/testfiles/FromFile.jsp</i>
     * @param  toFilePath    - Full path (project included) to the file in the workspace
     *                       that is to be edited. Ex: <i>
     *                       RuntimeTestProject/src/folder/ToFile.jsp</i>
     * @param  line          - The line number on which the new text shall be inserted
     * @param  column        - The column number on the given line to which the new text
     *                       shall be inserted
     */
    public void insertBlock(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath fromFilePath,
                            IPath toFilePath,
                            int line,
                            int column);

    /**
     * insertBlock - Use keystrokes to enter a block of text into the file at the given
     * workspace location. Get the block of text from the plugin resource at the given
     * plugin path. Insert the block into the active editor at a position relative to the
     * given search string. This test will issue a TestCase failure if any of the
     * resources cannot be accessed for any reason
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  sourcePlugin  - Plugin containing the resource that defines the text to be
     *                       inserted
     * @param  fromFilePath  - The plugin-relative path (plugin NOT included) to the
     *                       resource of the text to insert. Ex: <i>
     *                       resources/testfiles/FromFile.jsp</i>
     * @param  toFilePath    - Full path (project included) to the file in the workspace
     *                       that is to be edited. Ex: <i>
     *                       RuntimeTestProject/src/folder/ToFile.jsp</i>
     * @param  text          - Text to search in the active editor. This method will find
     *                       the first occurrence of this string in the active editor
     * @param  placement     - Position relative to the search string in the active file
     *                       (i.e. - before, after, instead of)
     * @param  save          - True if this edit should save the editor after insert;
     *                       false if the editor should not be saved
     */
    public void insertBlock(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath fromFilePath,
                            IPath toFilePath,
                            String text,
                            Placement placement,
                            boolean save);

    /**
     * insertBlock - Use keystrokes to enter a block of text into the file at the given
     * workspace location. Get the block of text from the plugin resource at the given
     * plugin path. Insert the block into the active editor at a fixed line, column
     * position. This test will issue a TestCase failure if any of the resources cannot be
     * accessed for any reason
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  sourcePlugin  - Plugin containing the resource that defines the text to be
     *                       inserted
     * @param  fromFilePath  - The plugin-relative path (plugin NOT included) to the
     *                       resource of the text to insert. Ex: <i>
     *                       resources/testfiles/FromFile.jsp</i>
     * @param  toFilePath    - Full path (project included) to the file in the workspace
     *                       that is to be edited. Ex: <i>
     *                       RuntimeTestProject/src/folder/ToFile.jsp</i>
     * @param  line          - The line number on which the new text shall be inserted
     * @param  column        - The column number on the given line to which the new text
     *                       shall be inserted
     * @param  save          - True if this edit should save the editor after insert;
     *                       false if the editor should not be saved
     */
    public void insertBlock(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath fromFilePath,
                            IPath toFilePath,
                            int line,
                            int column,
                            boolean save);

    /**
     * insertString - Use keystrokes to enter the given insertText into the file at the
     * given workspace location. Insert the block into the active editor at a position
     * relative to the given search string. This test will issue a TestCase failure if any
     * of the resources cannot be accessed for any reason
     *
     * @since 3.8.0
     * @param  ui          - Driver for UI generated input
     * @param  insertText  - the text to insert
     * @param  toFilePath  - Full path (project included) to the file in the workspace
     *                     that is to be edited. Ex: <i>
     *                     RuntimeTestProject/src/folder/ToFile.jsp</i>
     * @param  text        - Text to search in the active editor. This method will find
     *                     the first occurrence of this string in the active editor
     * @param  placement   - Position relative to the search string in the active file
     *                     (i.e. - before, after, instead of)
     */
    public void insertString(IUIContext ui,
                             String insertText,
                             IPath toFilePath,
                             String text,
                             Placement placement);

    /**
     * insertString - Use keystrokes to enter the given insertText into the file at the
     * given workspace location. Insert the block into the active editor at a position
     * given by a character offset. This test will issue a TestCase failure if any of the
     * resources cannot be accessed for any reason
     *
     * @since 3.8.0
     * @param  ui          - Driver for UI generated input
     * @param  insertText  - the text to insert
     * @param  toFilePath  - Full path (project included) to the file in the workspace
     *                     that is to be edited. Ex: <i>
     *                     RuntimeTestProject/src/folder/ToFile.jsp</i>
     * @param  offset      - The character offset at which the text will be inserted
     * @param  save        - True if this edit should save the editor after insert; false
     *                     if the editor should not be saved
     */
    public void insertString(IUIContext ui,
                             String insertText,
                             IPath toFilePath,
                             int offset,
                             boolean save);

    /**
     * insertString - Use keystrokes to enter the given insertText into the file at the
     * given workspace location. Insert the block into the active editor at a position
     * relative to the given search string. This test will issue a TestCase failure if any
     * of the resources cannot be accessed for any reason
     *
     * @since 3.8.0
     * @param  ui          - Driver for UI generated input
     * @param  insertText  - the text to insert
     * @param  toFilePath  - Full path (project included) to the file in the workspace
     *                     that is to be edited. Ex: <i>
     *                     RuntimeTestProject/src/folder/ToFile.jsp</i>
     * @param  text        - Text to search in the active editor. This method will find
     *                     the first occurrence of this string in the active editor
     * @param  placement   - Position relative to the search string in the active file
     *                     (i.e. - before, after, instead of)
     * @param  save        - True if this edit should save the editor after insert; false
     *                     if the editor should not be saved
     */
    public void insertString(IUIContext ui,
                             String insertText,
                             IPath toFilePath,
                             String text,
                             Placement placement,
                             boolean save);

    /**
     * insertString - Use keystrokes to enter the given insertText into the file at the
     * given workspace location. Insert the block into the active editor at a position
     * given by a line-column position. This test will issue a TestCase failure if any of
     * the resources cannot be accessed for any reason
     *
     * @since 3.8.0
     * @param  ui          - Driver for UI generated input
     * @param  insertText  - the text to insert
     * @param  toFilePath  - Full path (project included) to the file in the workspace
     *                     that is to be edited. Ex: <i>
     *                     RuntimeTestProject/src/folder/ToFile.jsp</i>
     * @param  line        - The line number on which the new text shall be inserted
     * @param  column      - The column number on the given line to which the new text
     *                     shall be inserted
     * @param  save        - True if this edit should save the editor after insert; false
     *                     if the editor should not be saved
     */
    public void insertString(IUIContext ui,
                             String insertText,
                             IPath toFilePath,
                             int line,
                             int column,
                             boolean save);

    /**
     * Opens a file in a Text Editor view.
     *
     * @since 3.8.0
     * @param  ui        - Driver for UI-generated input
     * @param  filePath  - Full path (project included) to the file in the workspace that
     *                   is to be opened. Ex: <i>
     *                   RuntimeTestProject/src/folder/ToFile.jsp</i>
     */
    public void openFileInTextEditor(IUIContext ui,
                                     IPath filePath);

    /**
     * placeCursor - Set the cursor of the active text editor to the given offset. This
     * method will issue a TestCase failure if there is no active ITextEditor
     *
     * @since 3.8.0
     * @param  ui      - Driver for UI generated input
     * @param  offset  - the offset position from the beginning of the file
     */
    public void placeCursor(IUIContext ui, int offset);

    /**
     * placeCursor - Set the cursor of the active text editor to the given line and
     * column. This method will issue a TestCase failure if there is no active ITextEditor
     *
     * @since 3.8.0
     * @param  ui      - Driver for UI generated input
     * @param  line    - Line number to place the cursor at. Should be within the bounds
     *                 of the current file length
     * @param  column  - Column number to place the cursor at. Should be within the bounds
     *                 of the current file length
     */
    public void placeCursor(IUIContext ui, int line, int column);

    /**
     * placeCursorAfter - Set the cursor of the active text editor to the position after
     * the given search string. This method will issue a TestCase failure if there is no
     * active ITextEditor
     *
     * @since 3.8.0
     * @param  ui            - Driver for UI generated input
     * @param  searchString  - Text to find in the active source editor
     */
    public void placeCursorAfter(IUIContext ui, String searchString);
}
