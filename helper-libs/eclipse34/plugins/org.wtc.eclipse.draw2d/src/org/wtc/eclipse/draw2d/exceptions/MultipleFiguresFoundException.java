/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.draw2d.exceptions;

/**
 * Represents exception thrown when a search for a Draw2D figure did not resolve a figure.
 * 
 * @since 3.8.0
 */
public class MultipleFiguresFoundException extends Exception {
    private static final long serialVersionUID = 2225548163779567092L;

    /**
     * @see  Exception#Exception(java.lang.String)
     */
    public MultipleFiguresFoundException(String message) {
        super(message);
    }
}
