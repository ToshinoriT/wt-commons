/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.wtc.eclipse.platform.exceptions.SourceFolderCreationError;

/**
 * Helper for java-specific actions.
 * 
 * @since 3.8.0
 */
public interface IJavaHelper {
    /**
     * addClassFolder - Using the given full path, create the folder in the project if it
     * does not already exist, then add that folder to the project's list of class
     * folders.
     *
     * @since 3.8.0
     * @param  ui          - Driver for UI generated input
     * @param  sourcePath  - The full path (project name included) of the class folder to
     *                     (create and) add. Ex: <i>RuntimeTestProject/classes</i>
     */
    public void addClassFolder(IUIContext ui, IPath classFolder);

    /**
     * addExternalJARToClasspathViaAPI - Create a classpath entry for the given JAR path
     * and add it to the given project's classpath. This is meant to be used for external
     * jars, that do not live under the workspace root. For this to be safe on cruise
     * control runs, this should be limited to paths from known temp directories, i.e.
     * project metadata, etc.
     *
     * @since 3.8.0
     * @param  ui               - Driver for UI generated input
     * @param  javaProjectName  - The name of the project to add the jar to
     * @param  jarPath          - The absolute file system location path of the jar to be
     *                          added to the project's classpath
     * @param  isExported       - True if the JAR is to be added to the exported classpath
     *                          list
     */
    public void addExternalJARToClasspathViaAPI(IUIContext ui,
                                                String javaProjectName,
                                                IPath jarPath,
                                                boolean isExported);

    /**
     * addProjectFolderToClasspathViaAPI - Create a classpath entry for the given folder
     * path and add it to the given project's classpath.
     *
     * @since 3.8.0
     * @param  ui          - Driver for UI generated input
     * @param  folderPath  - The full path (project name included) to be added to it's
     *                     parent project's classpath
     */
    public void addProjectFolderToClasspathViaAPI(IUIContext ui,
                                                  IPath folderPath);

    /**
     * addProjectJARToClasspath - Use the project properties dialog to add the JAR at the
     * given location to a project's build path. The specified JAR should already exist in
     * the target project's resources (a project- to-project build dependency should use
     * addProjectBuildDependency)
     *
     * @since 3.8.0
     * @param  ui       - Driver for UI generated input
     * @param  jarPath  - Full path (project included) of the JAR to add to the containing
     *                  project's build path
     */
    public void addProjectJARToClasspath(IUIContext ui, IPath jarPath);

    /**
     * addProjectJARToClasspathViaAPI - Create a classpath entry for the given JAR path
     * and add it to the given project's classpath.
     *
     * @since 3.8.0
     * @param  ui          - Driver for UI generated input
     * @param  jarPath     - The full path (project name included) to be added to it's
     *                     parent project's classpath
     * @param  isExported  - True if the JAR is to be added to the exported classpath list
     */
    public void addProjectJARToClasspathViaAPI(IUIContext ui,
                                               IPath jarPath,
                                               boolean isExported);

    /**
     * addSourceFolder - Using the given full path, create the folder in the project if it
     * does not already exist, then add that folder to the project's list of source
     * folders.
     *
     * @param   ui          - Driver for UI generated input
     * @param   sourcePath  - The full path (project name included) of the source folder
     *                      to (create and) add. Ex: <i>
     *                      RuntimeTestProject/src/yourFolder</i>
     * @throws  SourceFolderCreationError  - When the given project is not a Java project,
     *                                     the given folder is already nested in another
     *                                     source folder, or the source folder could not
     *                                     be specified for any other reason
     */
    public void addSourceFolder(IUIContext ui, IPath sourceFolder) throws SourceFolderCreationError;

    /**
     * createClass - Create a java class in the given source path, with the given package
     * and class name.
     *
     * @param   ui           - Driver for UI generated input
     * @param   outputPath   - Full path (project included) of the source root to contain
     *                       the package.  Ex: <i>RuntimeTestProject/src/yourFolder</i>
     * @param   packageName  - The dot-separated name of the package to create
     * @param   className    - The name of the class to create
     * @return  IPath the full path project included of the newly created file
     */
    public IPath createClass(IUIContext ui,
                             IPath outputPath,
                             String packageName,
                             String className);

    /**
     * createClass - Create a java class in the given source path, with the given package
     * and class name. Make this new class extend the given class
     *
     * @param   ui                  - Driver for UI generated input
     * @param   outputPath          - Full path (project included) of the source root to
     *                              contain the package.  Ex: <i>
     *                              RuntimeTestProject/src/yourFolder</i>
     * @param   packageName         - The dot-separated name of the package to create
     * @param   className           - The name of the class to create
     * @param   superTypeClassName  - The fully qualified class name of the class that
     *                              this class should extend
     * @return  IPath the full path project included of the newly created file
     */
    public IPath createClass(IUIContext ui,
                             IPath outputPath,
                             String packageName,
                             String className,
                             String superTypeClassName);

    /**
     * createPackage - Create a java package in the given source path, with the given
     * package name.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  outputPath   - Full path (project included) of the source root to contain
     *                      the package.  Ex: <i>RuntimeTestProject/src/yourFolder</i>
     * @param  packageName  - The dot-separated name of the package to create
     */
    public void createPackage(IUIContext ui,
                              IPath outputPath,
                              String packageName);

    /**
     * exportProjectJAROnClasspath - Add the JAR at the given path to the parent project's
     * list of exported classpath entries. The JAR file must exist and must already be on
     * the classpath.
     *
     * @since 3.8.0
     * @param  ui       - Driver for UI generated input
     * @param  jarPath  - Full path (project included) of the JAR to export
     */
    public void exportProjectJAROnClasspath(IUIContext ui,
                                            IPath jarPath);

    /**
     * getJavaType - Retrieves the IType for the specified Java type. Issues a TestCase
     * failure if the specified type does not exist.
     *
     * @param   projectName         Name of project that should contain the Java type.
     *                              Cannot be null and must be accessible.
     * @param   fullyQualifiedName  Fully qualified name of the type. Cannot be null.
     * @return  IType representing the discovered Java type.
     */
    public IType getJavaType(String projectName, String fullyQualifiedName);

    /**
     * Get the Java package name for a container.
     *
     * @param   ui          - Driver for UI-generated input
     * @param   folderPath  - The full path (project included) to a path of a folder in
     *                      the workspace
     * @return  String - The corresponding Java package name, or <code>null</code> if the
     *          container is not a package
     */
    public String getPackageName(IUIContext ui, IPath folderPath);

    /**
     * removeAllSourceFolders - Use the API to remove all source folders from the project
     * with the given name.
     *
     * @since 3.8.0
     * @param  ui           - Driver for UI generated input
     * @param  projectName  - Name of the project whose source folders are to be removed
     */
    public void removeAllSourceFolders(IUIContext ui,
                                       String projectName);

    /**
     * renameClass - Rename a java class through a refactor operation.
     *
     * @param   ui              - Driver for UI generated input
     * @param   sourceFilePath  - The full path to the java source file containing the
     *                          class to rename
     * @param   newClassName    - The new name of the class
     * @return  The full path to the new file
     */
    public IPath renameClass(IUIContext ui,
                             IPath sourceFilePath,
                             String newClassName);

    /**
     * verifyJavaType - Uses an API call to check for the existence of the specified Java
     * type. Issues a TestCase failure if the specified type does not exist.
     *
     * <p>IMPORTANT: Callers must ensure that the build has completed prior to making this
     * call.</p>
     *
     * @param  projectName         Name of project that should contain the Java type.
     *                             Cannot be null and must be accessible.
     * @param  fullyQualifiedName  Fully qualified name of the type. Cannot be null.
     */
    public void verifyJavaType(String projectName, String fullyQualifiedName);

    /**
     * checkForJavaType - Uses an API call to check for the existence of the specified
     * Java type. Issues a TestCase failure if the specified type either exists or does
     * not exist, as determined by the "exists" parameter.
     *
     * <p>IMPORTANT: Callers must ensure that the build has completed prior to making this
     * call.</p>
     *
     * @param   projectName         Name of project that should contain the Java type.
     *                              Cannot be null and must be accessible.
     * @param   fullyQualifiedName  Fully qualified name of the type. Cannot be null.
     * @param   exists              True if the type should exist, false if it should not
     *                              exist.
     * @return  IType representing the discovered Java type. Will be null if exists is
     *          false.
     */
    public void verifyJavaType(String projectName, String fullyQualifiedName, boolean exists);
}
