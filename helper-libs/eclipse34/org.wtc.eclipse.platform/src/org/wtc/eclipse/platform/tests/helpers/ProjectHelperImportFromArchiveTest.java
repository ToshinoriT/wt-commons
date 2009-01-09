/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.tests.helpers;

import com.windowtester.runtime.IUIContext;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.helpers.EclipseHelperFactory;
import org.wtc.eclipse.platform.helpers.IProjectHelper;
import org.wtc.eclipse.platform.tests.EclipseUITest;

/**
 * DESCRIPTION:
 */
public class ProjectHelperImportFromArchiveTest extends EclipseUITest {
    /**
     * Import a project from a ZIP file.
     */
    public void test() {
        IUIContext ui2 = getUI();

        java.lang.String PROJECT_NAME = "importTest"; //$NON-NLS-1$

        IProjectHelper projectHelper = EclipseHelperFactory.getProjectHelper();
        IPath path = new Path("resources/testfiles/ProjectHelperImportFromArchiveTest/import.zip"); //$NON-NLS-1$
        projectHelper.importExistingProjectFromArchive(ui2, PlatformActivator.getDefault(), path, PROJECT_NAME);

    }

}
