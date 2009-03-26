/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util.diff;

import com.windowtester.runtime.IUIContext;
import java.io.File;

/**
 * IFileDiffer - File comparison utility that compares the lines of a file.
 */
public interface IFileDiffer {
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
                 throws DifferenceException;
}
