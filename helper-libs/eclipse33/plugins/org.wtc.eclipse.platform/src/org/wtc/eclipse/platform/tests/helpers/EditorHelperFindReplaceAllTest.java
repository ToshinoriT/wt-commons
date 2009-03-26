
package org.wtc.eclipse.platform.tests.helpers;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.helpers.IWorkbenchHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

import com.windowtester.runtime.IUIContext;

/**
 * Quick test to make sure that the find and replace all helper
 *    method works
 */
public class EditorHelperFindReplaceAllTest extends EclipseUITest
{
    /**
     * Import a file, open it up, replace the strings, compare the output
     */
    public void testFindReplaceAll()
    {
        IUIContext ui = getUI();
        
        String projectName = "FindReplaceAllTest"; //$NON-NLS-1$
        
        ISimpleProjectHelper simple 
            = EclipseHelperFactory.getSimpleProjectHelper();
        simple.createProject(ui, projectName);
        
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        IPath fullFilePath = new Path(projectName + "/testFindReplaceAll.txt"); //$NON-NLS-1$
        resources.createFileFromInput(ui,
                                      PlatformActivator.getDefault(),
                                      new Path("resources/testfiles/FindReplaceAllTest/testFindReplaceAll.txt"), //$NON-NLS-1$
                                      fullFilePath);
        
        IEditorHelper editor = EclipseHelperFactory.getEditorHelper();
        editor.findAndReplaceAll(ui, 
                                 fullFilePath,  
                                 "REPLAC..E", //$NON-NLS-1$
                                 "REGEXTES[!T]", //$NON-NLS-1$
                                 true);
        
        editor.findAndReplaceAll(ui, 
                                 fullFilePath, 
                                 "REGEXTES[!T]", //$NON-NLS-1$
                                 "NEWTEXT", //$NON-NLS-1$
                                 false);
        
        IWorkbenchHelper workbench = EclipseHelperFactory.getWorkbenchHelper();
        workbench.saveAndWait(ui);
        
        resources.verifyFileByLine(ui,
                                   PlatformActivator.getDefault(),
                                   new Path("resources/testfiles/FindReplaceAllTest/testFindReplaceAll.bl"), //$NON-NLS-1$
                                   fullFilePath);
    }
}