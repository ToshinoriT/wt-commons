/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Helper for manipulating files and folders at a low level.
 */
public interface IResourceHelper {
    /**
     * closeFile - If the given file is open in an editor, close that editor. If that file
     * is not open, then no-op
     *
     * @param  ui        - Driver for UI generated input
     * @param  filePath  - The full path (project included) of the file to close
     */
    public void closeFile(IUIContext ui, IPath filePath);

    /**
     * copyFile - Copy a file from a location in the workspace to another location in the
     * workspace. Force the copy such that the copied file will overwrite an existing file
     * at the destination location
     *
     * @param  ui                   - Driver for UI-generated input
     * @param  sourceFilePath       - The full path (project name included) of the file to
     *                              copy
     * @param  destinationFilePath  - The full path (project name included) of the
     *                              location in which to copy the source file into
     */
    public void copyFile(IUIContext ui, IPath sourceFilePath, IPath destinationFilePath);

    /**
     * createFileFromInStream - Create a new, writable, file in the active workspace using
     * the contents of the given file in the given plugin.**!! THIS METHOD SHOULD BE USED
     * FOR MODEL TESTS ONLY SINCE IT DOES NOT USE THE UI !!**
     *
     * @param  ui                   - Driver for UI generated input
     * @param  sourcePlugin         - Container plugin for the resources whose contents
     *                              are to be used to create a new file
     * @param  sourcePath           - The plugin relative path of the file whose contents
     *                              are to be used to create a new file
     * @param  fullDestinationPath  - The full path (project included) of the file to
     *                              create
     */
    public IFile createFileFromInput(IUIContext ui,
                                     Plugin sourcePlugin,
                                     IPath sourcePath,
                                     IPath fullDestinationPath);

    /**
     * createFolder - Create a new folder in a project.
     *
     * @param  ui          - Driver for UI generated input
     * @param  folderPath  - Full path (project name and new folder name included) of the
     *                     folder to create Ex: <i>RuntimeTestProject/src/myFolder</i>
     */
    public void createFolder(IUIContext ui, IPath folderPath);

    /**
     * createFolderViaAPI - Use the API to create a new folder in a project.
     *
     * @param   folderPath  - Full path (project name and new folder name included) of the
     *                      folder to create Ex: <i>RuntimeTestProject/src/myFolder</i>
     * @return  IFolder - The created folder. Will return null if the given path is empty
     */
    public IFolder createFolderViaAPI(IPath folderPath);

    /**
     * createSimpleFile - Create a new file in the given location and with the given file
     * name. Use the simple new file wizard to create the file
     *
     * @param   ui          - Driver for UI generated input
     * @param   outputPath  - Full path (project included) of the folder in which to
     *                      create the new file. Ex: <i>
     *                      RuntimeTestProject/src/myFolder</i>
     * @param   fileName    - Name of the file. Should not be null
     * @return  IPath - The full path (project name included) of the file just created
     */
    public IPath createSimpleFile(IUIContext ui,
                                  IPath outputPath,
                                  String fileName);

    /**
     * Create a Zip file containing the contents of the given locations. If the locations
     * are directories, recurse those directories and add all of the files and folders
     * within those locations. Only add files that should be added according to the given
     * file filters. The zip file will be created in the workspace directory under the
     * 'results' sub-directory
     *
     * @param  ui           - Driver for UI generated input
     * @param  zipFileName  - The name of the zip file to create. A timestamp will be
     *                      added to this zip file name. For example, if the given name is
     *                      "filename.zip" the zip file created will be
     *                      "filename_yyyyMMdd_HHmmssSSS.zip"
     * @param  filesToZip   - A list of file base locations to add to the zip file.
     *                      Recurse through folders and add all contents under these root
     *                      locations
     * @param  fileFilter   - Indicates what files to add/ignore when copying files into
     *                      the zip. For example, *.class files can be ignored and not
     *                      added to the zip with a file filter
     */
    public void createZipCopy(IUIContext ui,
                              String zipFileName,
                              File[] filesToZip,
                              FilenameFilter fileFilter);

    /**
     * deleteFileOrFolder - deletes the identified file (or folder) at the given location.
     *
     * @param  ui    - Driver for the UI generated input
     * @param  path  - Full Path (project included) of the file (or folder) to delete Ex:
     *               <i>RuntimeTestProject/src/myFolder</i>
     */
    public void deleteFileOrFolder(IUIContext ui, IPath path);

    /**
     * Deletes the specified file or folder (recursively) at the specified full path using
     * the API.
     *
     * <p>!! THIS METHOD SHOULD BE USED FOR API-DRIVEN TESTS ONLY SINCE IT DOES NOT USE
     * THE UI !!</p>
     *
     * @param  ui    - Driver for the UI generated input
     * @param  path  - Full Path (project included) of the file or folder to delete. If a
     *               folder is specified, the contents are recursively deleted.
     */
    public void deleteFileOrFolderViaAPI(IUIContext ui, IPath path);

    /**
     * getFileContents - Return the contents of the given source as a string. This method
     * issues TestCase failure if the contents cannot be extracted for any reason
     *
     * @param   sourceFile  - The workspace file whose contents are to be extracted.
     *                      Should not be null
     * @return  String - The source file contents
     */
    public String getFileContents(IFile sourceFile);

    /**
     * getFileContents - Return the contents of the given source as a string. This method
     * issues TestCase failure if the contents cannot be extracted for any reason
     *
     * @param   sourceFile  - The workspace file whose contents are to be extracted.
     *                      Should not be null
     * @return  String - The source file contents
     */
    public String getFileContents(File sourceFile);

    /**
     * getFileFromPlugin - A plugin can be loaded into a runtime in many ways. Use the
     * eclipse plugin loader and the given plugin to find a file within the plugin.
     *
     * @param   ui        - Driver for UI generated input
     * @param   pluginID  - ID of a plugin in the workspace
     * @param   filePath  - A plugin relative (plugin name NOT included) path to a
     *                    resource within the plugin Ex: <i>
     *                    resources/testfiles/ProjectName/folder/file.java</i>
     * @return  File - extracted from the loaded plugin's resources
     */
    public File getFileFromPlugin(IUIContext ui, String pluginID, IPath filePath);

    /**
     * getFileFromPlugin - A plugin can be loaded into a runtime in many ways. Use the
     * eclipse plugin loader and the given plugin to find a file within the plugin.
     *
     * @param   ui        - Driver for UI generated input
     * @param   plugin    - A plugin in the workspace. Typically found with the
     *                    getDefault() method on the plugin
     * @param   filePath  - A plugin relative (plugin name NOT included) path to a
     *                    resource within the plugin Ex: <i>
     *                    resources/testfiles/ProjectName/folder/file.java</i>
     * @return  File - extracted from the loaded plugin's resources
     */
    public File getFileFromPlugin(IUIContext ui, Plugin plugin, IPath filePath);

    /**
     * getFileFromWorkspace - Translate the given path into an IFile in the workspace.
     * Wait for the file to exist before returning it. If the file does not exist, then
     * this method will issue a TestCase failure
     *
     * @param   ui        - Driver for UI generated input
     * @param   filePath  - Full path (project included) to a file in the workspace.
     *                    Should not be null. Ex: <i>
     *                    RuntimeTestProject/src/myFolder/MyFile.java</i>
     * @return  IFile - extracted from the current workspace
     */
    public IFile getFileFromWorkspace(IUIContext ui, IPath filePath);

    /**
     * getFileFromWorkspaceRoot - Find the workspace root location on disk, then get a *
     * first order* child file from that location. This method is useful when getting
     * build files generated to the workspace root.
     *
     * @param   ui        - Driver for UI generated input
     * @param   fileName  - The name of the file in the workspace root to retrieve. The
     *                    location of the workspace root is implicit and not included in
     *                    this name.<br/>
     *                    Ex: <i>build.xml</i>
     * @return  File - extracted from the current workspace root
     */
    public File getFileFromWorkspaceRoot(IUIContext ui, String fileName);

    /**
     * getFileLength - Return the length in characters of the contents of the given file.
     * This method issues TestCase failure if the contents cannot be extracted for any
     * reason
     *
     * @param   sourceFile  - The workspace file whose contents are to be extracted.
     *                      Should not be null
     * @return  int - Length in characters of the given file
     */
    public int getFileLength(IFile sourceFile);

    /**
     * getFileTimestamp - Get the timestamp of a file in the current workspace. This test
     * will issue a test case failure if the file could not be found for any reason
     *
     * @param   ui        - Driver for UI-generated input
     * @param   filePath  - The full path (project included) to the file in the current
     *                    workspace whose timestamp is to be retrieved. Ex: <i>
     *                    RuntimeTestProject/src/myFile.txt</i>
     * @return  long - The file's timestamp as milliseconds
     */
    public long getFileTimestamp(IUIContext ui, IPath filePath);

    /**
     * importFiles - Use the File -> Import -> File System dialog to import files from a
     * given plugin's resources into the given workspace path. Will import all files under
     * the given source path
     *
     * @param  ui              - Driver for UI generated input
     * @param  sourcePlugin    - Container plugin for the resources to import
     * @param  importFromPath  - Path to the folder to import from Ex: <i>
     *                         resources/testfiles/ProjectName/folder</i>
     * @param  toPath          - Full path (project name included) to the import to
     *                         directory Ex: <i>RuntimeTestProject/src/myFolder</i>
     */
    public void importFiles(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath importFromPath,
                            IPath toPath);

    /**
     * importFiles - Use the File -> Import -> File System dialog to import files from a
     * given plugin's resources into the given workspace path. Will import all files under
     * the given source path
     *
     * @param  ui              - Driver for UI generated input
     * @param  sourcePluginID  - ID of the container plugin for the resources to import. A
     *                         test case failure will be issued if a plugin for the given
     *                         ID is not found
     * @param  importFromPath  - Path to the folder to import from Ex: <i>
     *                         resources/testfiles/ProjectName/folder</i>
     * @param  toPath          - Full path (project name included) to the import to
     *                         directory Ex: <i>RuntimeTestProject/src/myFolder</i>
     */
    public void importFiles(IUIContext ui,
                            String sourcePluginID,
                            IPath importFromPath,
                            IPath toPath);

    /**
     * importFiles - Use the File -> Import -> File System dialog to import files from a
     * given plugin's resources into the given workspace path. Will import all files under
     * the given source path
     *
     * @param  ui              - Driver for UI generated input
     * @param  sourcePlugin    - Container plugin for the resources to import
     * @param  importFromPath  - Path to the folder to import from Ex: <i>
     *                         resources/testfiles/ProjectName/folder</i>
     * @param  toPath          - Full path (project name included) to the import to
     *                         directory Ex: <i>RuntimeTestProject/src/myFolder</i>
     * @param  overwrite       - overwrite existing files
     */
    public void importFiles(IUIContext ui,
                            Plugin sourcePlugin,
                            IPath importFromPath,
                            IPath toPath,
                            boolean overwrite);

    /**
     * moveFile - Use the resource navigator to move a file in the workspace from the
     * given source location to the given target location. The target location must exist
     * before the move is executed. If the resource exists in the target location it will
     * be overwritten
     *
     * @param  ui          - Driver for UI generated input
     * @param  filePath    - The full path (project included) of the file to move Ex: <i>
     *                     RuntimeTestProject/src/sourceFolder/MyFile.jsp</i>
     * @param  targetPath  - The full path (project included) of the folder to move the
     *                     given file to. This folder must already exist
     * @param  IPath       - The full path (project included) of the file in its new
     *                     location Ex: <i>
     *                     RuntimeTestProject/src/targetFolder/MyFile.jsp</i>
     */
    public IPath moveFile(IUIContext ui, IPath filePath, IPath targetPath);

    /**
     * openFile - Open the file at the given workspace relative path in the
     * file-associated editor. If the file is already open, then this operation will bring
     * that editor to the front
     *
     * @param  ui        - Driver for UI generated input
     * @param  filePath  - Full path (project included) of the file to open Ex: <i>
     *                   RuntimeTestProject/src/myFolder/MyFile.jsp</i>
     */
    public void openFile(IUIContext ui, IPath filePath);

    /**
     * setFileContents - A shotgun approach to setting a file's contents where all the
     * lines are replaced with the lines in the given file without recreating the file on
     * disc.
     *
     * @param  ui              - Driver for UI generated input
     * @param  filePath        - The full path (project name included) to the file whose
     *                         contents are to be replaced Ex: <i>
     *                         RuntimeTestProject/src/myFolder/MyFile.jsp</i>
     * @param  sourcePlugin    - The plugin containing the file whose contents will be
     *                         placed into the workspace file
     * @param  sourceFilePath  - The path in the source plugin of the file whose contents
     *                         are to be used
     */
    public void setFileContents(IUIContext ui,
                                IPath filePath,
                                Plugin sourcePlugin,
                                IPath sourceFilePath);

    /**
     * verifyFileByBlock - Use a block comparison (the baseline file is a block that must
     * exist in the given source file) to compare the given file. If the baseline file
     * could not be found or the source file could not be found then this method will
     * issue a TestCase failure. If the given file has a block difference (the block does
     * not exist or the block contains differences) then this method will issue a TestCase
     * failure
     *
     * @param  ui                - Driver for UI generated input
     * @param  plugin            - The source plugin where the baseline file is located
     * @param  baselineFilePath  - A plugin relative path (plugin NOT included) to the
     *                           baseline file containing a block of text to compare
     *                           against. Ex: <i>
     *                           resources/testfiles/ProjectName/folder/file.txt</i>
     * @param  filePath          - Full path (project included) to the file that is to be
     *                           compared with the given baseline resource Ex: <i>
     *                           RuntimeTestProject/src/myFolder/File.java</i>
     */
    public void verifyFileByBlock(IUIContext ui,
                                  Plugin plugin,
                                  IPath baselineFilePath,
                                  IPath filePath);

    /**
     * verifyFileByLine - Use a line-by-line exact match comparison to compare the given
     * file. If the baseline file could not be found or the source file could not be found
     * then this method will issue a TestCase failure. If the given file has a line
     * difference then this method will issue a TestCase failure
     *
     * @param  ui                - Driver for UI generated input
     * @param  plugin            - The source plugin where the baseline file is located
     * @param  baselineFilePath  - A plugin relative path (plugin NOT included) to the
     *                           baseline file containing a block of text to compare
     *                           against. Ex: <i>
     *                           resources/expected/TestName/ProjectName/myFolder/file.txt</i>
     * @param  filePath          - Full path (project included) to the file that is to be
     *                           compared with the given baseline resource Ex: <i>
     *                           RuntimeTestProject/src/myFolder/File.java</i>
     */
    public void verifyFileByLine(IUIContext ui,
                                 Plugin plugin,
                                 IPath baselineFilePath,
                                 IPath filePath);

    /**
     * verifyFileByLineSet - Compare the lines of the given file as a set ( lines must
     * exist but order is not important). If the baseline file could not be found or the
     * source file could not be found then this method will issue a TestCase failure. If
     * the given file has a line difference then this method will issue a TestCase failure
     *
     * @param  ui                - Driver for UI generated input
     * @param  plugin            - The source plugin where the baseline file is located
     * @param  baselineFilePath  - A plugin relative path (plugin NOT included) to the
     *                           baseline file containing a block of text to compare
     *                           against. Ex: <i>
     *                           resources/expected/TestName/ProjectName/folder/file.txt</i>
     * @param  filePath          - Full path (project included) to the file that is to be
     *                           compared with the given baseline resource Ex: <i>
     *                           RuntimeTestProject/src/myFolder/File.java</i>
     */
    public void verifyFileByLineSet(IUIContext ui,
                                    Plugin plugin,
                                    IPath baselineFilePath,
                                    IPath filePath);

    /**
     * verifyFileByLineSet - Compare the lines of the given file as a set ( lines must
     * exist but order is not important). If the baseline file could not be found or the
     * source file could not be found then this method will issue a TestCase failure. If
     * the given file has a line difference then this method will issue a TestCase failure
     *
     * @param  ui                - Driver for UI generated input
     * @param  plugin            - The source plugin where the baseline file is located
     * @param  baselineFilePath  - A plugin relative path (plugin NOT included) to the
     *                           baseline file containing a block of text to compare
     *                           against. Ex: <i>
     *                           resources/expected/TestName/ProjectName/folder/file.txt</i>
     * @param  actualLines       - A set of lines to be checked
     */
    public void verifyFileByLineSet(IUIContext ui,
                                    Plugin plugin,
                                    IPath baselineFilePath,
                                    String[] actualLines);

    /**
     * verifyFileByLineWithIgnores - Use a line-by-line exact match comparison to compare
     * the given file. If the baseline file could not be found or the source file could
     * not be found then this method will issue a TestCase failure. Ignore differences
     * between the two files for all actual file lines that match the given regular
     * expression. If the given file has a line difference then this method will issue a
     * TestCase failure
     *
     * @param  ui                - Driver for UI generated input
     * @param  plugin            - The source plugin where the baseline file is located
     * @param  baselineFilePath  - A plugin relative path (plugin NOT included) to the
     *                           baseline file containing a block of text to compare
     *                           against. Ex: <i>
     *                           resources/expected/TestName/ProjectName/folder/file.txt</i>
     * @param  filePath          - Full path (project included) to the file that is to be
     *                           compared with the given baseline resource Ex: <i>
     *                           RuntimeTestProject/src/myFolder/File.java</i>
     * @param  ignorePattern     - Ignore lines (do not perform a diff) where the source
     *                           file matches this regular expression
     */
    public void verifyFileByLineWithIgnores(IUIContext ui,
                                            Plugin plugin,
                                            IPath baselineFilePath,
                                            IPath filePath,
                                            String ignorePattern);

    /**
     * verifyFileByRegexLine - Use a line-by-line regular expression comparison to compare
     * the given file. If the baseline file could not be found or the source file could
     * not be found then this method will issue a TestCase failure. If the given files has
     * a line difference then this method will issue a TestCase failure
     *
     * @param  ui                - Driver for UI generated input
     * @param  plugin            - The source plugin where the baseline file is located
     * @param  baselineFilePath  - A plugin relative path (plugin NOT included) to the
     *                           baseline file containing a block of text to compare
     *                           against. Ex: <i>
     *                           resources/expected/TestName/ProjectName/folder/file.txt</i>
     * @param  filePath          - Full path (project included) to the file that is to be
     *                           compared with the given baseline resource Ex: <i>
     *                           RuntimeTestProject/src/myFolder/File.java</i>
     */
    public void verifyFileByRegexLine(IUIContext ui,
                                      Plugin plugin,
                                      IPath baselineFilePath,
                                      IPath filePath);

    /**
     * verifyFileByString - Verify that the given String exists in the given workspace
     * file. The String *must not* span multiple lines since this comparison will search
     * line by line in the given file. The given String does not need to be a full line of
     * text and will be considered a match if a line of text in the given file contains
     * the String.
     *
     * @param  ui              - Driver for UI generated input
     * @param  baselineString  - A single line of text (does not need to be a complete
     *                         line) to search for in the given file.
     * @param  filePath        - Full path (project included) to the file that is to be
     *                         compared with the given String Ex: <i>
     *                         RuntimeTestProject/src/myFolder/File.java</i>
     * @param  exists          - True if the text should exist in the target file for the
     *                         verification to succeed; False if the text should not exist
     *                         for the verification to succeed
     */
    public void verifyFileByString(IUIContext ui,
                                   String baselineString,
                                   IPath filePath,
                                   boolean exists);

    /**
     * verifyFileExists - Wait until the file at the given path exists. If the file still
     * does not exist after 30 seconds, then issue a TestCase failure
     *
     * @param  ui        - Driver for UI generated input
     * @param  filePath  - Full path (project included) to the file whose existence is to
     *                   be verified Ex: <i>RuntimeTestProject/src/myFolder/File.java</i>
     * @param  exists    - True if the file is to exist for this condition to be satisfied
     */
    public void verifyFileExists(IUIContext ui,
                                 IPath filePath,
                                 boolean exists);

    /**
     * verifyFileExists - Wait until the file at the given path exists. If the file still
     * does not exist after 2 seconds, then issue a TestCase failure
     *
     * @param  ui        - Driver for UI generated input
     * @param  plugin    - The test plugin containing the file to check for
     * @param  filePath  - Plugin relative path to the file whose existence is to be
     *                   verified
     * @param  exists    - True if the file is to exist for this condition to be satisfied
     */
    public void verifyFileExists(IUIContext ui,
                                 Plugin plugin,
                                 IPath filePath,
                                 boolean exists);

    /**
     * verifyFileExists - Wait until the file at the given path exists. If the file still
     * does not exist after 2 seconds, then issue a TestCase failure
     *
     * @param  ui        - Driver for UI generated input
     * @param  pluginID  - The ID of the test plugin containing the file to check for
     * @param  filePath  - Plugin relative path to the file whose existence is to be
     *                   verified
     * @param  exists    - True if the file is to exist for this condition to be satisfied
     */
    public void verifyFileExists(IUIContext ui,
                                 String pluginID,
                                 IPath filePath,
                                 boolean exists);

    /**
     * verifyFileUpdated - Wait until the file at the given path exists and has a
     * modifaction timestamp greater than the given timestap. If the file does not meet
     * these criteria after 30 seconds then issue a TestCase failure
     *
     * @param  ui          - Driver for UI generated input
     * @param  filePath    - Full path (project included) to the file whose existence and
     *                     timestamp is to be verified
     * @param  timestamp   - Value of the timestamp that is to be compared against
     * @param  wasUpdated  - True if the file should have been updated since the the last
     *                     time the timestamp was checked; false if the should should not
     *                     have been updated
     */
    public void verifyFileUpdated(IUIContext ui,
                                  IPath filePath,
                                  long timestamp,
                                  boolean wasUpdated);

    /**
     * verifyFolderExists - Wait until the folder at the given path exists. If the folder
     * still does not exist after 30 seconds, then issue a TestCase failure
     *
     * @param  ui          - Driver for UI generated input
     * @param  folderPath  - Full path (project included) to the folder whose existence is
     *                     to be verified Ex: <i>RuntimeTestProject/src/myFolder</i>
     * @param  exists      - True if the folder is to exist for this condition to be
     *                     satisfied
     */
    public void verifyFolderExists(IUIContext ui,
                                   IPath folderPath,
                                   boolean exists);

    /**
     * verifyStringByRegexFileLine - Use a line-by-line regular expression comparison to
     * compare the given String to a baseline file (compile the lines in the baseline file
     * as patterns). If the baseline file could not be found or the source file could not
     * be found then this method will issue a TestCase failure. If the given files has a
     * line difference then this method will issue a TestCase failure
     *
     * @param  ui                - Driver for UI generated input
     * @param  plugin            - The source plugin where the baseline file is located
     * @param  baselineFilePath  - A plugin relative path (plugin NOT included) to the
     *                           baseline file containing a block of text to compare
     *                           against. Ex: <i>
     *                           resources/expected/TestName/ProjectName/folder/file.txt</i>
     * @param  actual            - The contenst to be compared with the given baseline
     *                           resource
     */
    public void verifyStringByRegexFileLine(IUIContext ui,
                                            Plugin plugin,
                                            IPath baselineFilePath,
                                            String contents);

    /**
     * writeFileContents - Write a file to disk and set its contents to the given text.
     *
     * @param  ui        - Driver for UI generated input
     * @param  filePath  - Full path (project included) of the file to create. If the
     *                   project doesn't exist or is not accessible, an assertion error
     *                   will be thrown. Folders that do not exist will be created Ex: <i>
     *                   RuntimeTestProject/src/myFolder/File.java</i>
     * @param  contents  - The contents to write to the file
     */
    public void writeFileContents(IUIContext ui,
                                  IPath filePath,
                                  String contents);
}
