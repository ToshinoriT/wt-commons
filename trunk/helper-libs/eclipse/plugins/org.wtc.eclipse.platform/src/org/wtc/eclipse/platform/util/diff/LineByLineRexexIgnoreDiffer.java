/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util.diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import junit.framework.TestCase;

import org.wtc.eclipse.platform.util.ExceptionHandler;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.eclipse.FileExistsCondition;

/**
 * LineByLineRexexIgnoreDiffer - File comparison utility that checks that two files are
 * the same line by line and ignores any lines that match a given regex pattern.
 */
public class LineByLineRexexIgnoreDiffer implements IFileDiffer {
    private final String _ignorePattern;

    /**
     * Save the data members.
     */
    public LineByLineRexexIgnoreDiffer() {
        this(null);
    }

    /**
     * Save the data members.
     */
    public LineByLineRexexIgnoreDiffer(String ignorePattern) {
        _ignorePattern = ignorePattern;
    }

    /**
     * compare - Compare each line of the actual file against the lines in the expected
     * file. Compile the known regex and ignore any lines (don't diff) in the actual file
     * that match the given regex
     *
     * @param   ui            - Driver for UI generated input
     * @param   expectedFile  - Baseline file that is expected to be the expected results
     * @param   actualFile    - Source file whose lines are to be compared against the
     *                        given baseline (expected) file
     * @throws  DifferenceException  - When at least one line in the actual file does not
     *                               match the pattern of the same line (by line number)
     *                               in the baseline file
     */
    public void compare(IUIContext ui, File expectedFile, File actualFile)
                 throws DifferenceException {
        compare(ui, expectedFile, actualFile, _ignorePattern);
    }

    /**
     * compare - Compare each line of the actual file against the lines in the expected
     * file. Compile the known regex and ignore any lines (don't diff) in the actual file
     * that match the given regex
     *
     * @param   ui             - Driver for UI generated input
     * @param   expectedFile   - Baseline file that is expected to be the expected results
     * @param   actualFile     - Source file whose lines are to be compared against the
     *                         given baseline (expected) file
     * @param   ignorePattern  - Regex pattern of the lines to ignore
     * @throws  DifferenceException  - When at least one line in the actual file does not
     *                               match the pattern of the same line (by line number)
     *                               in the baseline file
     */
    public void compare(IUIContext ui,
                        File expectedFile,
                        File actualFile,
                        String ignorePattern) throws DifferenceException {
        TestCase.assertNotNull(expectedFile);
        TestCase.assertNotNull(actualFile);

        ui.wait(new FileExistsCondition(expectedFile, true));
        ui.wait(new FileExistsCondition(actualFile, true));

        Pattern ignore = null;

        if (ignorePattern != null) {
            try {
                ignore = Pattern.compile(ignorePattern);
            } catch (PatternSyntaxException pse) {
            	ExceptionHandler.handle(pse, "====> " + expectedFile.getAbsolutePath() + " LINE(" + ignorePattern + "): "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }

        try {
            InputStream expectedStream = new FileInputStream(expectedFile);

            try {
                InputStream actualStream = new FileInputStream(actualFile);

                try {
                    InputStreamReader expectedReader = new InputStreamReader(expectedStream);
                    BufferedReader expected = new BufferedReader(expectedReader);

                    InputStreamReader actualreader = new InputStreamReader(actualStream);
                    BufferedReader actual = new BufferedReader(actualreader);

                    String nextExpectedLine = expected.readLine();
                    String nextActualLine = actual.readLine();

                    int line = 1;

                    Collator collator = Collator.getInstance();

                    while ((nextExpectedLine != null) || (nextActualLine != null)) {
                        if (((nextExpectedLine != null) && (nextActualLine == null)) || ((nextExpectedLine == null) && (nextActualLine != null))) {
                            throw new LineDifferenceException(expectedFile,
                                                              nextExpectedLine,
                                                              actualFile,
                                                              nextActualLine,
                                                              line);
                        }

                        boolean doDiff = true;

                        if (ignore != null) {
                            Matcher actualMatcher = ignore.matcher(nextActualLine);
                            doDiff = !actualMatcher.matches();
                        }

                        if (doDiff && !collator.equals(nextExpectedLine, nextActualLine)) {
                            throw new LineDifferenceException(expectedFile,
                                                              nextExpectedLine,
                                                              actualFile,
                                                              nextActualLine,
                                                              line);
                        }

                        nextExpectedLine = expected.readLine();
                        nextActualLine = actual.readLine();
                        line++;
                    }
                } finally {
                    if (actualStream != null) {
                        actualStream.close();
                    }
                }
            } finally {
                if (expectedStream != null) {
                    expectedStream.close();
                }
            }
        } catch (IOException ioe) {
        	ExceptionHandler.handle(ioe);
        }
    }

}
