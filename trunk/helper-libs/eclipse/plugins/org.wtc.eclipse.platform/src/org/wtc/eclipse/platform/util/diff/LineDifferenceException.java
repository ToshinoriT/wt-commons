/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util.diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.wtc.eclipse.platform.util.ExceptionHandler;

/**
 * LineDifferenceException - Exception thrown when a line-by-line file comparison finds a
 * difference in a line between  two files.
 */
public class LineDifferenceException extends DifferenceException {
    private static final long serialVersionUID = -3779732628318364458L;
    final String _message;

    /**
     * LineDifferenceException.
     *
     * @param  expectedLine       - String representation of the line in the baseline file
     *                            that was different from the source line
     * @param  actualFile         - File handle to the source file compared to the
     *                            baseline file
     * @param  expectedExistence  - True if the line was supposed to be found and wasn't;
     *                            False otherwise
     */
    public LineDifferenceException(String expectedLine,
                                   File actualFile,
                                   boolean expectedExistence) {
        TestCase.assertNotNull(expectedLine);
        TestCase.assertNotNull(actualFile);

        StringBuilder buffer = new StringBuilder();
        buffer.append("The line <"); //$NON-NLS-1$
        buffer.append(expectedLine);
        buffer.append("> should have resulted in a search <"); //$NON-NLS-1$
        buffer.append(expectedExistence);
        buffer.append(">\n"); //$NON-NLS-1$
        addFileToTrace(buffer, "ACTUAL FILE", actualFile); //$NON-NLS-1$

        _message = buffer.toString();
    }

    /**
     * LineDifferenceException.
     *
     * @param  expectedFile  - File handle to the baseline file
     * @param  expectedLine  - String representation of the line in the baseline file that
     *                       was different from the source line
     * @param  actualSet     - Set compared to the baseline file
     * @param  actualLine    - String representation of the line that differed from the
     *                       given baseline file line
     */
    public LineDifferenceException(File expectedFile,
                                   String expectedLine,
                                   String[] actualSet,
                                   String actualLine) {
        TestCase.assertNotNull(expectedFile);
        TestCase.assertNotNull(expectedLine);
        TestCase.assertNotNull(actualSet);
        TestCase.assertNotNull(actualLine);

        StringBuilder buffer = new StringBuilder();
        buffer.append("The file <"); //$NON-NLS-1$
        buffer.append(expectedFile.getAbsolutePath());
        buffer.append("> had differences with the set <"); //$NON-NLS-1$
        buffer.append(actualSet);
        buffer.append("):\n EXPECTED:"); //$NON-NLS-1$
        buffer.append(expectedLine);
        buffer.append("\n    ACTUAL:"); //$NON-NLS-1$
        buffer.append(actualLine);

        addFileToTrace(buffer, "EXPECTED FILE", expectedFile); //$NON-NLS-1$

        buffer.append("\n\n---------------- <"); //$NON-NLS-1$
        buffer.append("LINE SET"); //$NON-NLS-1$
        buffer.append("> -----------------\n"); //$NON-NLS-1$

        for (String nextLine : actualSet) {
            buffer.append(nextLine);
            buffer.append("\n"); //$NON-NLS-1$
        }

        buffer.append("\n\n---------------- <"); //$NON-NLS-1$
        buffer.append("/LINE SET"); //$NON-NLS-1$
        buffer.append("> -----------------\n"); //$NON-NLS-1$

        _message = buffer.toString();
    }

    /**
     * LineDifferenceException.
     *
     * @param  expectedFile  - File handle to the baseline file
     * @param  expectedLine  - String representation of the line in the baseline file that
     *                       was different from the source line
     * @param  actualFile    - File handle to the source file compared to the baseline
     *                       file
     * @param  actualLine    - String representation of the line that differed from the
     *                       given baseline file line
     */
    public LineDifferenceException(File expectedFile,
                                   String expectedLine,
                                   File actualFile,
                                   String actualLine) {
        TestCase.assertNotNull(expectedFile);
        TestCase.assertNotNull(expectedLine);
        TestCase.assertNotNull(actualFile);
        TestCase.assertNotNull(actualLine);

        StringBuilder buffer = new StringBuilder();
        buffer.append("The file <"); //$NON-NLS-1$
        buffer.append(expectedFile.getAbsolutePath());
        buffer.append("> had differences with the file <"); //$NON-NLS-1$
        buffer.append(actualFile.getAbsolutePath());
        buffer.append("):\n EXPECTED:"); //$NON-NLS-1$
        buffer.append(expectedLine);
        buffer.append("\n    ACTUAL:"); //$NON-NLS-1$
        buffer.append(actualLine);

        addFilesToTrace(buffer, expectedFile, actualFile);

        _message = buffer.toString();
    }

    /**
     * LineDifferenceException.
     *
     * @param  expectedFile        - File handle to the baseline file as an input stream
     * @param  expectedLine        - String representation of the line in the baseline
     *                             file that was different from the source line
     * @param  actualFileContents  - Contents of the source file compared to the baseline
     *                             file
     * @param  actualLine          - String representation of the line that differed from
     *                             the given baseline file line
     * @param  lineNumber          - The line number of the baseline file that had the
     *                             difference between the two files
     */
    public LineDifferenceException(File expectedFile,
                                   String expectedLine,
                                   String actualFileContents,
                                   String actualLine,
                                   int lineNumber) {
        TestCase.assertNotNull(expectedFile);
        TestCase.assertNotNull(actualFileContents);

        StringBuilder buffer = new StringBuilder();
        buffer.append("Expected file <"); //$NON-NLS-1$
        buffer.append(expectedFile.getAbsolutePath());
        buffer.append("> had differences with the given contents on line ("); //$NON-NLS-1$
        buffer.append(lineNumber);
        buffer.append("):\n EXPECTED:"); //$NON-NLS-1$
        buffer.append(expectedLine);
        buffer.append("\n    ACTUAL:"); //$NON-NLS-1$
        buffer.append(actualLine);

        addFileToTrace(buffer, "EXPECTED FILE", expectedFile); //$NON-NLS-1$
        addStringToTrace(buffer, "ACTUAL STRING CONTENTS", actualFileContents); //$NON-NLS-1$

        _message = buffer.toString();
    }

    /**
     * LineDifferenceException.
     *
     * @param  expectedFile  - File handle to the baseline file
     * @param  expectedLine  - String representation of the line in the baseline file that
     *                       was different from the source line
     * @param  actualFile    - File handle to the source file compared to the baseline
     *                       file
     * @param  actualLine    - String representation of the line that differed from the
     *                       given baseline file line
     * @param  lineNumber    - The line number of the baseline file that had the
     *                       difference between the two files
     */
    public LineDifferenceException(File expectedFile,
                                   String expectedLine,
                                   File actualFile,
                                   String actualLine,
                                   int lineNumber) {
        TestCase.assertNotNull(expectedFile);
        TestCase.assertNotNull(actualFile);

        StringBuilder buffer = new StringBuilder();
        buffer.append("Expected file <"); //$NON-NLS-1$
        buffer.append(expectedFile.getAbsolutePath());
        buffer.append("> had differences with actual file <"); //$NON-NLS-1$
        buffer.append(actualFile.getAbsolutePath());
        buffer.append("> on line ("); //$NON-NLS-1$
        buffer.append(lineNumber);
        buffer.append("):\n EXPECTED:"); //$NON-NLS-1$
        buffer.append(expectedLine);
        buffer.append("\n    ACTUAL:"); //$NON-NLS-1$
        buffer.append(actualLine);

        addFilesToTrace(buffer, expectedFile, actualFile);

        _message = buffer.toString();
    }

    /**
     * addFilesToTrace - Stream the given files into the given buffer.
     *
     * @param  io_buffer     - I/O param: stream the given files into this buffer
     * @param  expectedFile  - Baseline file to stream
     * @param  actualFile    - Source file to stream
     */
    private void addFilesToTrace(StringBuilder io_buffer,
                                 File expectedFile,
                                 File actualFile) {
        TestCase.assertNotNull(io_buffer);
        TestCase.assertNotNull(expectedFile);
        TestCase.assertNotNull(actualFile);

        addFileToTrace(io_buffer, "EXPECTED FILE", expectedFile); //$NON-NLS-1$
        addFileToTrace(io_buffer, "ACTUAL FILE", actualFile); //$NON-NLS-1$

    }

    /**
     * addFileToTrace - Stream the given file into the given buffer.
     *
     * @param  io_buffer  - I/O param: stream the given file into this buffer
     * @param  label      - Identifier that will tag the file in the given stream
     * @param  file       - File to stream
     */
    private void addFileToTrace(StringBuilder io_buffer,
                                String label,
                                File file) {
        TestCase.assertNotNull(io_buffer);
        TestCase.assertNotNull(label);
        TestCase.assertNotNull(file);

        io_buffer.append("\n\n---------------- <"); //$NON-NLS-1$
        io_buffer.append(label);
        io_buffer.append("> -----------------\n"); //$NON-NLS-1$

        try {
            FileReader fileReader = new FileReader(file);

            BufferedReader reader = new BufferedReader(fileReader);

            try {
                String nextLine = reader.readLine();

                while (nextLine != null) {
                    io_buffer.append(nextLine);
                    io_buffer.append("\n"); //$NON-NLS-1$

                    nextLine = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch (FileNotFoundException fnfe) {
            io_buffer.append("!!!!! "); //$NON-NLS-1$
            io_buffer.append(file.getAbsolutePath());
            io_buffer.append(" WAS NOT FOUND !!!!!\n"); //$NON-NLS-1$
        } catch (IOException ioe) {
        	ExceptionHandler.handle(ioe);
        }

        io_buffer.append("---------------- </"); //$NON-NLS-1$
        io_buffer.append(label);
        io_buffer.append("> -----------------\n"); //$NON-NLS-1$
    }

    /**
     * addStringToTrace - Stream the given contents into the given buffer.
     *
     * @param  io_buffer  - I/O param: stream the given file into this buffer
     * @param  label      - Identifier that will tag the file in the given stream
     * @param  contents   - contents to stream
     */
    private void addStringToTrace(StringBuilder io_buffer,
                                  String label,
                                  String contents) {
        TestCase.assertNotNull(io_buffer);
        TestCase.assertNotNull(label);
        TestCase.assertNotNull(contents);

        io_buffer.append("\n\n---------------- <"); //$NON-NLS-1$
        io_buffer.append(label);
        io_buffer.append("> -----------------\n"); //$NON-NLS-1$
        io_buffer.append(contents);
        io_buffer.append("---------------- </"); //$NON-NLS-1$
        io_buffer.append(label);
        io_buffer.append("> -----------------\n"); //$NON-NLS-1$
    }

    /**
     * @see  Throwable#getLocalizedMessage()
     */
    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    /**
     * @see  Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return _message;
    }

    /**
     * @see  Object#toString()
     */
    @Override
    public String toString() {
        return getMessage();
    }
}
