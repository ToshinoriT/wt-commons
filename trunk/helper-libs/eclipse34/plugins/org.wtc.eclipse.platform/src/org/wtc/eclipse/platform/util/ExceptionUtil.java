/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * ExceptionUtil - Utility class for methods that handle extensions of Throwable.
 */
public class ExceptionUtil {
    /**
     * formatThrowable - Extract the stack trace from the given Throwable and return it as
     * a formatted String.
     *
     * @param   throwable  - Can have chained causes
     * @return  String - Formatted stack trace or the empty string if the given throwable
     *          is null
     */
    public static String formatThrowable(Throwable throwable) {
        if (throwable == null) {
            return ""; //$NON-NLS-1$
        }

        Throwable temp = throwable;
        int i = 0;
        StringWriter sw = new StringWriter();

        while (temp != null) {
            i++;

            if (i != 1) {
                sw.write("\nCaused By: "); //$NON-NLS-1$
            }

            sw.write("Exception: \nmessage( " + temp.getMessage() + ")\n"); //$NON-NLS-1$ //$NON-NLS-2$
            sw.write("Stack Trace:\n"); //$NON-NLS-1$
            temp.printStackTrace(new PrintWriter(sw));
            sw.write("\n"); //$NON-NLS-1$
            temp = temp.getCause();
        }

        return sw.toString();
    }
}
