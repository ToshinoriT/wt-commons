
package org.wtc.eclipse.platform.tests.helpers;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;
import org.wtc.eclipse.platform.util.diff.DifferenceException;
import org.wtc.eclipse.platform.util.diff.StringExistsFileDiffer;

import com.windowtester.runtime.IUIContext;

/**
 * Run a series of smoke tests on the StringExistsFileDiffer
 */
public class StringExistsDifferTest extends EclipseUITest
{
    private static final String BASELINE_FILEPATH
        = "resources/testfiles/StringExistsDifferTest/StringExistsDifferBaseline.txt";//$NON-NLS-1$
    
    /**
     * serchingTest
     */
    public void serchingTest(String searchString, 
                             boolean exists,
                             boolean shouldFail)
    {
        IUIContext ui = getUI();
        
        IPath baselinePath = new Path(BASELINE_FILEPATH);
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        File actualFile 
            = resources.getFileFromPlugin(ui,
                                          PlatformActivator.getDefault(), 
                                          baselinePath);
        
        try
        {
            StringExistsFileDiffer differ = new StringExistsFileDiffer();
            differ.compare(ui, searchString, actualFile, exists);
            
            // If we got to here and we were supposed to fail, then
            // the test should fail
            if(shouldFail)
            {
                StringBuilder message = new StringBuilder();
                message.append("THE SEARCH STRING <");//$NON-NLS-1$
                message.append(searchString);
                message.append("> WAS SUPPOSED TO FAIL AND DIDN'T");//$NON-NLS-1$
                TestCase.fail(message.toString());
            }  
        }
        catch(DifferenceException de)
        {
            // If we got to here and we were supposed to pass, then
            // the test should fail
            if(!shouldFail)
            {
                StringBuilder message = new StringBuilder();
                message.append("THE SEARCH STRING <");//$NON-NLS-1$
                message.append(searchString);
                message.append("> WAS SUPPOSED TO PASS BUT THREW:");//$NON-NLS-1$
                message.append(de.getLocalizedMessage());
                
                PlatformActivator.logException(de);
                TestCase.fail(message.toString());
            }
        }        
    }
    
    /**
     * testStringIndexZero
     */
    public void testStringIndexZero()
    {
        serchingTest("SEARCH_INDEX0", true, false);  //$NON-NLS-1$
    }
    
    /**
     * testStringIndexZeroFailure
     */
    public void testStringIndexZeroFailure()
    {
        serchingTest("SEARCH_INDEX0", false, true);  //$NON-NLS-1$
    }
    
    /**
     * testStringMiddle
     */
    public void testStringMiddle()
    {
        serchingTest("SEARCH_MIDDLE", true, false);  //$NON-NLS-1$
    }
    
    /**
     * testStringMiddleFailure
     */
    public void testStringMiddleFailure()
    {
        serchingTest("SEARCH_MIDDLE", false, true);  //$NON-NLS-1$
    }
    
    /**
     * testStringLastIndex
     */
    public void testStringLastIndex()
    {
        serchingTest("SEARCH_LASTINDEX", true, false);  //$NON-NLS-1$
    }
    
    /**
     * testStringLastIndexFailure
     */
    public void testStringLastIndexFailure()
    {
        serchingTest("SEARCH_LASTINDEX", false, true);  //$NON-NLS-1$
    }
    
    /**
     * testStringMissing
     */
    public void testStringMissing()
    {
        serchingTest("SEARCH_MISSING", false, false);  //$NON-NLS-1$
    }
    
    /**
     * testStringMissingFailure
     */
    public void testStringMissingFailure()
    {
        serchingTest("SEARCH_MISSING", true, true);  //$NON-NLS-1$
    }
}
