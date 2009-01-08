/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util.diff;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.eclipse.FileExistsCondition;
import junit.framework.TestCase;
import org.wtc.eclipse.platform.PlatformActivator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Line differ that will search line by line for a given String.
 */
public class StringExistsFileDiffer {
    /**
     * compare - Scan each line of the given file and search for the given String in that
     * line. The String *must not* span multiple lines since this comparison will search
     * line by line in the given file. The given String does not need to be a full line of
     * text and will be considered a match if a line of text in the given file contains
     * the String.
     *
     * @param   ui              - Driver for UI generated input
     * @param   baselineString  - A single line of text (does not need to be a complete
     *                          line) to search for in the given file.
     * @param   actualFile      - Source file whose lines are to be searched for the given
     *                          String
     * @param   exists          - True if the text should exist in the target file for the
     *                          verification to succeed; False if the text should not
     *                          exist for the verification to succeed
     * @throws  DifferenceException  - Thrown when the String is not found in any of the
     *                               lines in the file and it was expected to be found, or
     *                               when the String was found in at least one of the
     *                               lines in the file and it was expected to not be found
     */
    public void compare(IUIContext ui,
                        String baselineString,
                        File actualFile,
                        boolean exists) throws DifferenceException {
        TestCase.assertNotNull(baselineString);
        TestCase.assertNotNull(actualFile);

        ui.wait(new FileExistsCondition(actualFile, true));

        boolean matchFound = false;

        try {
            InputStream actualStream = new FileInputStream(actualFile);

            try {
                InputStreamReader actualreader = new InputStreamReader(actualStream);
                BufferedReader actual = new BufferedReader(actualreader);

                String nextActualLine = actual.readLine();

                int line = 1;

                while ((nextActualLine != null) && !matchFound) {
                    matchFound = nextActualLine.contains(baselineString);

                    nextActualLine = actual.readLine();
                    line++;
                }
            } finally {
                if (actualStream != null) {
                    actualStream.close();
                }
            }
        } catch (IOException ioe) {
            PlatformActivator.logException(ioe);
            TestCase.fail(ioe.getMessage());
        }

        if (matchFound != exists) {
            throw new LineDifferenceException(baselineString,
                                              actualFile,
                                              exists);
        }
    }
}
