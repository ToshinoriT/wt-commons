/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.internal.tsb.xml;

/**
 * Thrown when a registry XML file could not be parsed because it does not meet the
 * registry schema.
 */
public class RegistryDOMParseException extends Exception {
    private static final long serialVersionUID = 4978374933759940463L;

    /**
     * Save the data members.
     */
    public RegistryDOMParseException(String message) {
        super(message);
    }
}
