/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.reset;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IEditorHelper;
import org.wtc.eclipse.platform.helpers.IResourceHelper;
import org.wtc.eclipse.platform.helpers.ISimpleProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * DESCRIPTION:
 */
public class CloseEditorsResetDaemonTest extends EclipseUITest {
    /**
     */
    public void test() {
        IUIContext ui = getUI();

        java.lang.String PROJECT_NAME = "CloseEditorsRDProject"; //$NON-NLS-1$

        ISimpleProjectHelper simpleProjectHelper = EclipseHelperFactory.getSimpleProjectHelper();
        simpleProjectHelper.createProject(ui, PROJECT_NAME);

        IResourceHelper resourceHelper = EclipseHelperFactory.getResourceHelper();
        IPath filePath = resourceHelper.createSimpleFile(ui,
                                                         new Path(PROJECT_NAME),
                                                         "editedfile.txt"); //$NON-NLS-1$

        IEditorHelper iEditorHelper3 = EclipseHelperFactory.getEditorHelper();
        iEditorHelper3.insertString(ui,
                                    "We will not save this file", //$NON-NLS-1$
                                    filePath,
                                    1,
                                    1,
                                    false);

    }
}
