/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util.diff;

/**
 * DifferenceException - Exception thrown when a file comparison finds a difference in a
 * between two files.
 */
public class DifferenceException extends Exception {
    private static final long serialVersionUID = 2365414300147769801L;

    /**
     * @see  java.lang.Exception#Exception()
     */
    public DifferenceException() {
        super();
    }

    /**
     * @see  java.lang.Exception#Exception(java.lang.String)
     */
    public DifferenceException(String message) {
        super(message);
    }
}
