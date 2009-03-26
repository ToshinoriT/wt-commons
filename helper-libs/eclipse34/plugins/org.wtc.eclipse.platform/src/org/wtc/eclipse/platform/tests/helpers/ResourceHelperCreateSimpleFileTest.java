/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * DESCRIPTION:
 */
public class ResourceHelperCreateSimpleFileTest extends EclipseUITest {
    /**
     */
    public void test() {
        IUIContext ui = getUI();

        String projectName = "ResourceHelperCreateSimpleFileTest"; //$NON-NLS-1$
        String fileName = "SimpleFile.txt"; //$NON-NLS-1$

        ISimpleProjectHelper simpleProjectHelper = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProjectHelper.createProject(ui, projectName);

        // -------- 2 -------- //
        IResourceHelper resources = EclipseHelperFactory.getResourceHelper();
        resources.createSimpleFile(ui, new Path(projectName), fileName);
    }
}
