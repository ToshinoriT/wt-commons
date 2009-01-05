/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util.diff;

import com.windowtester.runtime.IUIContext;
import junit.framework.TestCase;
import org.wtc.eclipse.platform.PlatformActivator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;

/**
 * FileBlockDiffer - Utility that searches a file for the existence of a block of text (by
 * the first matching line) then compares found blocks for differences between the
 * expected block and actual block. Useful for testing the existence of an element in an
 * XML file where the order of the element may not be important
 */
public class FileBlockDiffer implements IFileDiffer {
    /**
     * compare - Compare the lines of the given files. See implementation for comparison
     * algorithms
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
        TestCase.assertNotNull(expectedFile);
        TestCase.assertNotNull(actualFile);
        TestCase.assertTrue(expectedFile.exists());
        TestCase.assertTrue(actualFile.exists());

        try {
            InputStream sourceStream = new FileInputStream(actualFile);

            try {
                InputStream blockStream = new FileInputStream(expectedFile);

                try {
                    InputStreamReader sourceReader = new InputStreamReader(sourceStream);
                    BufferedReader source = new BufferedReader(sourceReader);

                    InputStreamReader blockReader = new InputStreamReader(blockStream);
                    BufferedReader block = new BufferedReader(blockReader);

                    Collator collator = Collator.getInstance();

                    String nextBlockLine = block.readLine();
                    boolean comparing = false;

                    String nextSourceLine = source.readLine();
                    int currentSourceLine = 1;

                    while ((nextSourceLine != null) && (nextBlockLine != null)) {
                        // If the lines are equal
                        if (collator.compare(nextSourceLine, nextBlockLine) == 0) {
                            // Make sure we're comparing
                            comparing = true;

                            // Now start indexing the blocks
                            nextBlockLine = block.readLine();
                        }
                        // Otherwise, the lines aren't equal. If we're comparing
                        // then there was a difference and an error should be
                        // reported
                        else if (comparing) {
                            StringBuffer buffer = new StringBuffer();
                            buffer.append("** A DIFFERENCE WAS FOUND ON SOURCE LINE ("); //$NON-NLS-1$
                            buffer.append(currentSourceLine);
                            buffer.append(") OF ("); //$NON-NLS-1$
                            buffer.append(actualFile.getAbsolutePath());
                            buffer.append(") COMPARING BLCOKS:\n"); //$NON-NLS-1$
                            buffer.append("    EXPECTED:"); //$NON-NLS-1$
                            buffer.append(nextBlockLine);
                            buffer.append("\n"); //$NON-NLS-1$
                            buffer.append("      ACTUAL:"); //$NON-NLS-1$
                            buffer.append(nextSourceLine);
                            buffer.append("\n"); //$NON-NLS-1$

                            throw new BlockDifferenceFoundException(buffer.toString());
                        }

                        // If we got to here then we're either still searching for the first
                        // block or we're continuing to compare
                        nextSourceLine = source.readLine();
                        currentSourceLine++;

                    } // endwhile

                    // Now that the loop is complete, let's make sure that
                    // we found the first line from the block comparison
                    if (!comparing) {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("** DID NOT FIND AN OCCURRENCE OF THE BLOCK ("); //$NON-NLS-1$
                        buffer.append(expectedFile.getAbsolutePath());
                        buffer.append(") IN ("); //$NON-NLS-1$
                        buffer.append(actualFile.getAbsolutePath());
                        buffer.append(")"); //$NON-NLS-1$

                        throw new BlockNotFoundException(buffer.toString());
                    }

                } finally {
                    if (blockStream != null) {
                        blockStream.close();
                    }
                }
            } finally {
                if (sourceStream != null) {
                    sourceStream.close();
                }
            }
        } catch (IOException ioe) {
            PlatformActivator.logException(ioe);
            TestCase.fail(ioe.getMessage());
        }

    }

    /**
     * BlockDifferenceFoundException - Thrown when a given block is found in the actual
     * file (by the first line of the block file) but the block contains differences.
     */
    public static class BlockDifferenceFoundException extends DifferenceException {
        private static final long serialVersionUID = 1L;

        /**
         * @see  java.lang.Exception#Exception(java.lang.String)
         */
        public BlockDifferenceFoundException(String message) {
            super(message);
        }
    }

    /**
     * BlockNotFoundException - Thrown when a given block is not found in the actual file
     * (by the first line of the block file).
     */
    public static class BlockNotFoundException extends DifferenceException {
        private static final long serialVersionUID = 1L;

        /**
         * @see  java.lang.Exception#Exception(java.lang.String)
         */
        public BlockNotFoundException(String message) {
            super(message);
        }
    }
}
