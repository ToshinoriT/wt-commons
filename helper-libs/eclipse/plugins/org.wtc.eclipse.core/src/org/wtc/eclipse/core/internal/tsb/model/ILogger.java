/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.core.internal.tsb.model;

/**
 * Wrapper for logging in the parsers.
 */
public interface ILogger {
    /**
     * Is the tracing option enabled?
     */
    public boolean isOptionEnabled(String optionKey);

    /**
     * log a debug message.
     */
    public void logDebug(String message);

    /**
     * log an error message.
     */
    public void logError(String message);

    /**
     * log an exception.
     */
    public void logException(Throwable exception);

    /**
     * log a warning message.
     */
    public void logWarning(String message);
}
