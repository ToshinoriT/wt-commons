/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.exceptions;

import org.eclipse.core.runtime.IPath;

/**
 * Error that provides information about a failure during the source folder creation
 * operation.
 * 
 * @since 3.8.0
 */
public class SourceFolderCreationError extends WTCHelperError {
    private static final long serialVersionUID = -1316897683022057522L;

    /**
     * Save the data members.
     */
    public SourceFolderCreationError(IPath sourceFolderPath) {
        super(buildMessage(sourceFolderPath));
    }

    /**
     * Save the data members.
     */
    public SourceFolderCreationError(IPath sourceFolderPath,
                                     Throwable cause) {
        super(buildMessage(sourceFolderPath), cause);
    }

    /**
     * @return  String - Return a readable exception message
     */
    private static String buildMessage(IPath sourceFolderPath) {
        StringBuilder builder = new StringBuilder();
        builder.append("THE SOURCE FOLDER ("); //$NON-NLS-1$
        builder.append(sourceFolderPath.toPortableString());
        builder.append(") COULD NOT BE CREATED"); //$NON-NLS-1$

        return builder.toString();
    }
}
