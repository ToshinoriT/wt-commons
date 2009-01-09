/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IAntHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;
import java.io.File;

/**
 * Smoke test for running ant files.
 */
public class AntHelperTest extends EclipseUITest {
    /**
     * Run an ant script that copies a file.
     */
    public void testSimpleAnt() {
        IUIContext ui = getUI();

        String projectName = "AntHelperTest"; //$NON-NLS-1$
        ISimpleProjectHelper simpleProject = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProject.createProject(ui, projectName);

        IPath outputFolderPath = new Path(projectName + "/outputFolder"); //$NON-NLS-1$
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.createFolder(ui, outputFolderPath);

        String fileName = "newFile.txt"; //$NON-NLS-1$
        resources.createSimpleFile(ui, outputFolderPath, fileName);
        IFile newFile = resources.getFileFromWorkspace(ui, outputFolderPath.append("/" + fileName)); //$NON-NLS-1$
        IPath absoluteOutputPath = newFile.getLocation();
        absoluteOutputPath = absoluteOutputPath.removeLastSegments(1).append("/copiedFile.txt"); //$NON-NLS-1$

        IPath testAntPath = new Path("/resources/testfiles/AntHelperTest/build.xml"); //$NON-NLS-1$
        File antFile = resources.getFileFromPlugin(ui, PlatformActivator.getDefault(), testAntPath);

        IAntHelper antHelper = EclipseHelperFactory.getAntHelper();
        antHelper.executeExternalAntFile(ui, antFile, null, "-Ddestination=" + absoluteOutputPath.toPortableString()); //$NON-NLS-1$

        resources.verifyFileExists(ui, outputFolderPath.append("/copiedFile.txt"), true); //$NON-NLS-1$
    }

    /**
     * Run an ant script from the workspace that copies a file.
     */
    public void testSimpleWorkspaceAnt() {
        IUIContext ui = getUI();

        String projectName = "AntHelperTest2"; //$NON-NLS-1$
        ISimpleProjectHelper simpleProject = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProject.createProject(ui, projectName);

        IPath antFolderPath = new Path(projectName + "/antFolder"); //$NON-NLS-1$
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.createFolder(ui, antFolderPath);

        IPath antImportPath = new Path("/resources/testfiles/AntHelperTest"); //$NON-NLS-1$
        resources.importFiles(ui, PlatformActivator.getDefault(), antImportPath, antFolderPath);

        IPath antToExecutePath = antFolderPath.append(new Path("build.xml")); //$NON-NLS-1$

        IPath outputFolderPath = new Path(projectName + "/outputFolder"); //$NON-NLS-1$
        resources.createFolder(ui, outputFolderPath);

        String fileName = "newFile.txt"; //$NON-NLS-1$
        resources.createSimpleFile(ui, outputFolderPath, fileName);
        IFile newFile = resources.getFileFromWorkspace(ui, outputFolderPath.append("/" + fileName)); //$NON-NLS-1$
        IPath absoluteOutputPath = newFile.getLocation();
        absoluteOutputPath = absoluteOutputPath.removeLastSegments(1).append("/copiedFile.txt"); //$NON-NLS-1$

        IAntHelper antHelper = EclipseHelperFactory.getAntHelper();
        antHelper.executeWorkspaceAntFile(ui, antToExecutePath, null, "-Ddestination=" + absoluteOutputPath.toPortableString()); //$NON-NLS-1$

        resources.verifyFileExists(ui, outputFolderPath.append("/copiedFile.txt"), true); //$NON-NLS-1$
    }
}
