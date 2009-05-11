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
import java.io.StringReader;
import java.text.Collator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import junit.framework.TestCase;

import org.wtc.eclipse.platform.util.ExceptionHandler;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.eclipse.FileExistsCondition;

/**
 * LineByLineRegexDiffer - File comparison utility that compiles the lines of a given
 * baseline file into a regular expression before comparing them against the source file.
 */
public class LineByLineRegexDiffer implements IFileDiffer {
    /**
     * compare - Compile each line of the given expected file as a regular expression then
     * test the same line (by number) in the given actual contents string to see if it
     * matches the compiled regular expression pattern.
     *
     * @param   ui              - Driver for UI generated input
     * @param   expectedFile    - Baseline file whose lines are to be compiled as a
     *                          regular expression
     * @param   actualContents  - Source string whose lines are to be compared against the
     *                          compiled regular expressions
     * @throws  DifferenceException  - When at least one line in the actual contents does
     *                               not match the pattern of the same line (by line
     *                               number) in the baseline file
     */
    public void compare(IUIContext ui, File expectedFile, String actualContents)
                 throws DifferenceException {
        TestCase.assertNotNull(expectedFile);
        TestCase.assertNotNull(actualContents);

        ui.wait(new FileExistsCondition(expectedFile, true));

        try {
            InputStream expectedStream = new FileInputStream(expectedFile);
            InputStreamReader expectedReader = new InputStreamReader(expectedStream);
            BufferedReader expected = new BufferedReader(expectedReader);

            try {
                StringReader actualReader = new StringReader(actualContents);
                BufferedReader actual = new BufferedReader(actualReader);

                try {
                    try {
                        compare(ui,
                                expected,
                                actual,
                                true);
                    } catch (StreamDifferenceException sde) {
                        throw new LineDifferenceException(expectedFile, sde.getExpectedLine(), actualContents, sde.getActualLine(), sde.getLineNumber());
                    }
                } finally {
                    if (actualReader != null) {
                        actualReader.close();
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

    /**
     * compare - Compile each line of the given expected file as a regular expression then
     * test the same line (by number) in the given actual file to see if it matches the
     * compiled regular expression pattern.
     *
     * @param   ui            - Driver for UI generated input
     * @param   expectedFile  - Baseline file whose lines are to be compiled as a regular
     *                        expression
     * @param   actualFile    - Source file whose lines are to be compared against the
     *                        compiled regular expressions
     * @throws  DifferenceException  - When at least one line in the actual file does not
     *                               match the pattern of the same line (by line number)
     *                               in the baseline file
     */
    public void compare(IUIContext ui, File expectedFile, File actualFile)
                 throws DifferenceException {
        compare(ui, expectedFile, actualFile, true);
    }

    /**
     * compare - If specified, compile each line of the given expected file as a regular
     * expression then test the same line (by number) in the given actual file to see if
     * it matches the compiled regular expression pattern.
     *
     * @param   ui            - Driver for UI generated input
     * @param   expectedFile  - Baseline file whose lines are to be compiled as a regular
     *                        expression
     * @param   actualFile    - Source file whose lines are to be compared against the
     *                        compiled regular expressions
     * @param   useRegex      - True if the lines are to be compiled into regex before
     *                        comparing
     * @throws  DifferenceException  - When at least one line in the actual file does not
     *                               match the pattern of the same line (by line number)
     *                               in the baseline file
     */
    protected void compare(IUIContext ui,
                           File expectedFile,
                           File actualFile,
                           boolean useRegex) throws DifferenceException {
        TestCase.assertNotNull(expectedFile);
        TestCase.assertNotNull(actualFile);

        ui.wait(new FileExistsCondition(expectedFile, true));
        ui.wait(new FileExistsCondition(actualFile, true));

        try {
            InputStream expectedStream = new FileInputStream(expectedFile);

            try {
                InputStream actualStream = new FileInputStream(actualFile);

                try {
                    try {
                        InputStreamReader expectedReader = new InputStreamReader(expectedStream);
                        BufferedReader expected = new BufferedReader(expectedReader);

                        InputStreamReader actualreader = new InputStreamReader(actualStream);
                        BufferedReader actual = new BufferedReader(actualreader);

                        compare(ui,
                                expected,
                                actual,
                                useRegex);
                    } catch (StreamDifferenceException sde) {
                        throw new LineDifferenceException(expectedFile, sde.getExpectedLine(), actualFile, sde.getActualLine(), sde.getLineNumber());
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

    /**
     * compare - If specified, compile each line of the given expected file as a regular
     * expression then test the same line (by number) in the given actual file to see if
     * it matches the compiled regular expression pattern.
     *
     * @param   ui              - Driver for UI generated input
     * @param   expectedStream  - Don't cross the streams
     * @param   actualStream    - Total protonic reversal the compiled regular
     *                          expressions.
     * @param   useRegex        - True if the lines are to be compiled into regex before
     *                          comparing
     * @throws  DifferenceException  - When at least one line in the actual file does not
     *                               match the pattern of the same line (by line number)
     *                               in the baseline file
     */
    private void compare(IUIContext ui,
                         BufferedReader expected,
                         BufferedReader actual,
                         boolean useRegex) throws StreamDifferenceException, IOException {
        TestCase.assertNotNull(expected);
        TestCase.assertNotNull(actual);

        String nextExpectedLine = expected.readLine();
        String nextActualLine = actual.readLine();

        int line = 1;

        Collator collator = Collator.getInstance();

        while ((nextExpectedLine != null) || (nextActualLine != null)) {
            if (((nextExpectedLine != null) && (nextActualLine == null)) || ((nextExpectedLine == null) && (nextActualLine != null))) {
                throw new StreamDifferenceException(nextExpectedLine,
                                                    nextActualLine,
                                                    line);
            }

            try {
                boolean match = true;

                if (useRegex) {
                    Pattern expectedPattern = Pattern.compile(nextExpectedLine);
                    Matcher actualMatcher = expectedPattern.matcher(nextActualLine);

                    match = actualMatcher.matches();
                } else {
                    match = collator.equals(nextExpectedLine, nextActualLine);
                }

                if (!match) {
                    throw new StreamDifferenceException(nextExpectedLine,
                                                        nextActualLine,
                                                        line);
                }

                nextExpectedLine = expected.readLine();
                nextActualLine = actual.readLine();
                line++;
            } catch (PatternSyntaxException pse) {
            	ExceptionHandler.handle(pse, "====> " + " LINE(" + line + "): "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
    }

    /**
     * Convert this into LineDifferenceExceptions.
     */
    private static class StreamDifferenceException extends Exception {
        private static final long serialVersionUID = -1155060378283000808L;

        private String _expectedLine;
        private String _actualLine;
        private int _lineNumber;

        /**
         * Save the data members.
         */
        public StreamDifferenceException(String expectedLine,
                                         String actualLine,
                                         int lineNumber) {
            _expectedLine = expectedLine;
            _actualLine = actualLine;
            _lineNumber = lineNumber;
        }

        public String getActualLine() {
            return _actualLine;
        }

        public String getExpectedLine() {
            return _expectedLine;
        }

        public int getLineNumber() {
            return _lineNumber;
        }
    }

}
