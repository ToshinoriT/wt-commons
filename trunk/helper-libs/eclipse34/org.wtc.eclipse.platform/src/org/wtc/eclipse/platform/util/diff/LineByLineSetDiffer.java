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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LineByLineSetDiffer - File comparison utility that checks that two files are the same
 * line by line even when the lines appear in different orders.
 */
public class LineByLineSetDiffer implements IFileDiffer {
    /**
     * compare - Compile the lines in the baseline file with the given set of lines.
     *
     * @param   ui            - Driver for UI generated input
     * @param   baselineFile  - File whose lines are to be used as baselines
     * @param   actualLines   - Lines to verify
     * @throws  DifferenceException  - Thrown when the lengths of the two line sets are
     *                               different or the lines from the first file do not all
     *                               appear in the list
     */
    public void compare(IUIContext ui, File baselineFile, String[] actualLines)
                 throws DifferenceException {
        TestCase.assertNotNull(baselineFile);
        TestCase.assertNotNull(actualLines);

        ui.wait(new FileExistsCondition(baselineFile, true));

        List<String> file1LineSet = createLineSet(baselineFile);

        List<String> actualLineSet = new ArrayList<String>();
        Collections.addAll(actualLineSet, actualLines);

        compare(file1LineSet,
                actualLineSet,
                new SetExceptionBuilder(baselineFile, actualLines));
    }

    /**
     * compare - Compile the lines in each file then make sure the sets are of equal
     * length. If so, then make sure that the lines from the first file appear in the
     * second
     *
     * @param   ui     - Driver for UI generated input
     * @param   file1  - File whose lines are to be compared
     * @param   file2  - File whose lines are to be compared
     * @throws  DifferenceException  - Thrown when the lengths of the two files are
     *                               different or the lines from the first file do not all
     *                               appear in the second file
     */
    public void compare(IUIContext ui, File file1, File file2) throws DifferenceException {
        TestCase.assertNotNull(file1);
        TestCase.assertNotNull(file2);

        ui.wait(new FileExistsCondition(file1, true));
        ui.wait(new FileExistsCondition(file2, true));

        List<String> file1LineSet = createLineSet(file1);
        List<String> file2LineSet = createLineSet(file2);

        compare(file1LineSet,
                file2LineSet,
                new FileExceptionBuilder(file1, file2));
    }

    /**
     * compare - Compare line sets.
     */
    private void compare(List<String> set1, List<String> set2, ExceptionBuilder builder)
                  throws LineDifferenceException {
        if (!(set1.size() == set2.size())) {
            builder.throwLineDifference("Files are the same length", //$NON-NLS-1$
                                        "Files are of different lengths"); //$NON-NLS-1$
        }

        String file2LastCheckedLine = "No line checked"; //$NON-NLS-1$

        for (String file1CurrentLine : set1) {
            boolean linesMatch = false;

            for (String file2CurrentLine : set2) {
                file2LastCheckedLine = file2CurrentLine;

                if (file2CurrentLine.equals(file1CurrentLine)) {
                    linesMatch = true;

                    break;
                }
            }

            if (linesMatch == false) {
                builder.throwLineDifference(file1CurrentLine,
                                            file2LastCheckedLine);
            }
        }
    }

    /**
     * createLineSet - Compile the lines of the given file into a list of Strings.
     *
     * @param   sourceFile  - File to read. Should not be null
     * @return  List<String> - A string for each line in the given file
     */
    private static List<String> createLineSet(File sourceFile) {
        TestCase.assertNotNull(sourceFile);

        //Create an array of lines using the users file
        ArrayList<String> sourceFileLineSet = new ArrayList<String>();

        try {
            InputStream sourceFileStream = new FileInputStream(sourceFile);

            try {
                InputStreamReader sourceFileInputStreamReader = new InputStreamReader(sourceFileStream);
                BufferedReader sourceFileBufferedReader = new BufferedReader(sourceFileInputStreamReader);

                String nextSourceFileLine = sourceFileBufferedReader.readLine();

                while (nextSourceFileLine != null) {
                    sourceFileLineSet.add(nextSourceFileLine);
                    nextSourceFileLine = sourceFileBufferedReader.readLine();
                }
            } finally {
                if (sourceFileStream != null) {
                    sourceFileStream.close();
                }
            }

        } catch (IOException e) {
            PlatformActivator.logException(e);
            TestCase.fail(e.getMessage());
        }

        return sourceFileLineSet;
    }

    /**
     * Utility for building exceptions in common comparison code.
     */
    private abstract class ExceptionBuilder {
        public abstract void throwLineDifference(String currentLine, String checkedLine)
                                          throws LineDifferenceException;
    }

    /**
     * Utility for file-to-file comparison exception handling.
     */
    private class FileExceptionBuilder extends ExceptionBuilder {
        private final File _file1;
        private final File _file2;

        /**
         * Save the data members.
         */
        public FileExceptionBuilder(File file1, File file2) {
            _file1 = file1;
            _file2 = file2;
        }

        /**
         * toss.
         */
        @Override
        public void throwLineDifference(String currentLine, String checkedLine)
                                 throws LineDifferenceException {
            throw new LineDifferenceException(_file1, currentLine, _file2, checkedLine);
        }
    }

    /**
     * Utility for file-to-set comparison exception handling.
     */
    private class SetExceptionBuilder extends ExceptionBuilder {
        private final File _file1;
        private final String[] _lines;

        /**
         * Save the data members.
         */
        public SetExceptionBuilder(File file1, String[] lines) {
            _file1 = file1;
            _lines = lines;
        }

        /**
         * toss.
         */
        @Override
        public void throwLineDifference(String currentLine, String checkedLine)
                                 throws LineDifferenceException {
            throw new LineDifferenceException(_file1, currentLine, _lines, checkedLine);
        }
    }
}
