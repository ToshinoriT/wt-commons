/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for creating a timestamp string.
 * 
 * @since 3.8.0
 */
public class Timestamp {
    private final String _timestamp;

    /**
     * Save the data members.
     */
    public Timestamp() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmssSSS"); //$NON-NLS-1$
        _timestamp = format.format(now);
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _timestamp;
    }
}
