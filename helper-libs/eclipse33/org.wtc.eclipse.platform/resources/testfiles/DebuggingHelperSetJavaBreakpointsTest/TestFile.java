/**
 * B E A   S Y S T E M S
 *
 * Copyright (c) 2005  BEA Systems, Inc.
 *
 * All Rights Reserved. Unpublished rights reserved under the copyright laws
 * of the United States. The software contained on this media is proprietary
 * to and embodies the confidential technology of BEA Systems, Inc. The
 * possession or receipt of this information does not convey any right to
 * disclose its contents, reproduce it, or use,  or license the use, for
 * manufacture or sale, the information or anything described therein. Any
 * use, disclosure, or reproduction without BEA System's prior written
 * permission is strictly prohibited.
 *
 */

/**
 * Abstract error from which all specific WLW test errors
 *     should extend
 */
public abstract class TestFile extends RuntimeException
{
    /**
     * Constructs a new error with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
    public TestFile(String message) 
    {
        super(message);
    }
    
    /**
     * Constructs a new error with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this error's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public TestFile(String message, Throwable cause) 
    {
        super(message, cause);
    }
    
    /**
     * Constructs a new error with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for errors that are little more than
     * wrappers for other throwables.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public TestFile(Throwable cause)
    {
        super(cause);
    }
    
    /**
     *  
     * @return String - a display value for the given array  
     */
    protected static String getDisplayValue( Object[] things )
    {
        if ( things == null )
            return "[null]";

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        boolean isFirst = true;
        
        for ( Object thing : things )
        {
            if (!isFirst)
            {
                builder.append(", ");
            }
            builder.append( thing.toString() );
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    
    /**
     * some method that returns void
     */
    public void aVoidMethod()
    {
        System.out.println("test");
    }
}
