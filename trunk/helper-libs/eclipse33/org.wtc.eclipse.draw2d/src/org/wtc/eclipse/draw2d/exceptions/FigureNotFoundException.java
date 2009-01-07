/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.draw2d.exceptions;

/**
 * Represents exception thrown when a search for a Draw2D figure did not resolve a figure.
 */
public class FigureNotFoundException extends Exception {
    private static final long serialVersionUID = -5452800126478046999L;

    /**
     * @see  Exception#Exception(java.lang.String)
     */
    public FigureNotFoundException(String message) {
        super(message);
    }
}
