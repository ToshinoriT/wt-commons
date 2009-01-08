/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util.diff;

import com.windowtester.runtime.IUIContext;
import java.io.File;

/**
 * LineByLineDiffer - File comparison utility that compares an expected file against an
 * actual file as exact matches.
 */
public class LineByLineDiffer extends LineByLineRegexDiffer implements IFileDiffer {
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
    @Override
    public void compare(IUIContext ui, File expectedFile, File actualFile)
                 throws DifferenceException {
        compare(ui, expectedFile, actualFile, false);
    }
}
