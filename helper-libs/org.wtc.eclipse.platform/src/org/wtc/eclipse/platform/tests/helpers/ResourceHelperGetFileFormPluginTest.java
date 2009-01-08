
package org.wtc.eclipse.platform.tests.helpers;

import java.io.File;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

import com.windowtester.runtime.IUIContext;

/**
 * INFRASTRUCTURE TEST ONLY! DO NOT ADD THIS TO THE BVTs!
 */
public class ResourceHelperGetFileFormPluginTest extends EclipseUITest
{
    /**
     * testFailure
     */
    public void testFailure()
    {
        IUIContext ui = getUI();
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        
        try
        {
            resources.getFileFromPlugin(ui,
                                        PlatformActivator.getDefault(),
                                        new Path("totally/bogus/path.txt")); //$NON-NLS-1$
            
            TestCase.fail("WE EXPECTED getFileFromPlugin TO FAIL IF THE FILE DOESN'T EXIST"); //$NON-NLS-1$
        }
        catch(AssertionFailedError afe)
        {
            // This is the expected result
        }
    }
    
    /**
     * testFile
     */
    public void testFile()
    {
        IUIContext ui = getUI();
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        
        File file
             = resources.getFileFromPlugin(ui,
                                           PlatformActivator.getDefault(),
                                           new Path("resources/testfiles/ResourceHelperGetFileFormPluginTest/resource1.txt")); //$NON-NLS-1$
        TestCase.assertNotNull(file);
        TestCase.assertTrue(file.exists());
        TestCase.assertTrue(file.isFile());
    }
    
    /**
     * testFolder
     */
    public void testFolder()
    {
        IUIContext ui = getUI();
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        
        File folder
             = resources.getFileFromPlugin(ui,
                                           PlatformActivator.getDefault(),
                                           new Path("resources/testfiles/ResourceHelperGetFileFormPluginTest")); //$NON-NLS-1$
        TestCase.assertNotNull(folder);
        TestCase.assertTrue(folder.exists());
        TestCase.assertTrue(folder.isDirectory());
    }
}
