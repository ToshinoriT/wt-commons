/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class contains utility methods for dealing with <code>String</code>s.
 */
public class StringUtil {
    /**
     * This method will take in a string and capitalize its first letter.
     *
     * @param   name  the string to be capitalized
     * @return  a new string with the first letter uppercased
     */
    public static String capitalizeFirstLetter(String name) {
        if ((name == null) || (name.length() == 0))
            return ""; //$NON-NLS-1$

        if (name.length() == 1)
            return "" + Character.toUpperCase(name.charAt(0)); //$NON-NLS-1$

        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * determines if the given source string contains the given pattern based upon the
     * criteria.
     *
     * @param   source
     * @param   pattern
     * @param   exactMatch
     * @param   caseSensitive
     * @return
     */
    public static boolean doesStringContainPattern(String source,
                                                   String pattern,
                                                   boolean exactMatch,
                                                   boolean caseSensitive) {
        // do not match on empty strings
        if (isEmpty(pattern) || isEmpty(source)) {
            return false;
        }

        if (exactMatch) {
            return (caseSensitive) ? source.equals(pattern) : source.equalsIgnoreCase(pattern);
        } else {
            return (caseSensitive) ? source.contains(pattern) : (indexOfIgnoreCase(source, pattern) >= 0);
        }
    }

    /**
     * determines if the given string conatins a whitespace character.
     *
     * @param  source
     */
    public static boolean doesStringContainWhitespace(String source) {
        assert (source != null) && (source.length() > 0) : "null or empty source string"; //$NON-NLS-1$
        char[] chars = source.toCharArray();

        for (char c : chars) {
            if (Character.isWhitespace(c)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the stringbuffer ends with the specified string.
     */
    public static boolean endsWith(StringBuffer buf, String strSuffix) {
        if (buf.length() < strSuffix.length()) {
            return false;
        }

        int offset = buf.length() - strSuffix.length();

        for (int i = 0; i < strSuffix.length(); i++) {
            if (strSuffix.charAt(i) != buf.charAt(offset + i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether the given string ends with the given suffix, ignoring the case
     * of the letters.
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str.length() < suffix.length())
            return false;

        String end = str.substring(str.length() - suffix.length(), str.length());

        return end.equalsIgnoreCase(suffix);
    }

    /**
     * Turn HTML entities into their escaped version.
     *
     * @param   s  a string which may contain HTML-reserved characters.
     * @return  a string which has reserved characters entitized.
     */
    public static String escapeHTML(String s) {
        StringBuffer sb = new StringBuffer();
        int n = s.length();

        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);

            switch (c) {
                case '<': {
                    sb.append("&lt;"); //$NON-NLS-1$

                    break;
                }

                case '>': {
                    sb.append("&gt;"); //$NON-NLS-1$

                    break;
                }

                case '&': {
                    sb.append("&amp;"); //$NON-NLS-1$

                    break;
                }

                case '"': {
                    sb.append("&quot;"); //$NON-NLS-1$

                    break;
                }

                case ' ': {
                    sb.append("&nbsp;"); //$NON-NLS-1$

                    break;
                }

                // This is a nasty. HTML shows newlines as spaces, but we want it to
                // appear as an unprintable character
                case '\n': {
                    sb.append("\b"); //$NON-NLS-1$

                    break;
                }

                case '\r': {
                    sb.append("\b"); //$NON-NLS-1$

                    break;
                }

                // Don't need to escape other characters because Swing will
                // render them correctly in the HTML unescaped

                default: {
                    sb.append(c);

                    break;
                }
            }
        }

        return sb.toString();
    }

    /**
     * Turn a string into something that can be used as a string constant in a java source
     * file.  This includes adding quotes, and escaping special characters like quotes,
     * backslashes, and newlines.
     */
    public static String escapeJava(String s) {
        if (s == null)
            return null;

        StringBuffer sb = new StringBuffer();
        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            switch (c) {
                case '\n': {
                    sb.append("\\n"); //$NON-NLS-1$

                    break;
                }

                case '\r': {
                    sb.append("\\r"); //$NON-NLS-1$

                    break;
                }

                case '\\': {
                    sb.append("\\\\"); //$NON-NLS-1$

                    break;
                }

                case '"': {
                    sb.append("\\\""); //$NON-NLS-1$

                    break;
                }

                case '\t': {
                    sb.append("\\t"); //$NON-NLS-1$

                    break;
                }

                // BUGBUG I've probably missed some.  add 'em.
                default: {
                    sb.append(c);

                    break;
                }
            }
        }

        return sb.toString();
    }

    /**/
    public static String escapeXMLText(String input) {
        return input.replaceAll("\\&", "&amp;").replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    }

    /**
     * Return the index of pattern in source, ignoring case.
     *
     * <pre>
     * Example: Util.indexOfIgnoreCase(" blee Blee BlEe", "BlEe") => 1
     * </pre>
     *
     * @param  source   a string
     * @param  pattern  the pattern string to look for.
     */
    public static int indexOfIgnoreCase(String source, String pattern) {
        return indexOfIgnoreCase(source, pattern, 0);
    }

    /**
     * Return the index of pattern in source, ignoring case.
     *
     * <pre>
     * Example: Util.indexOfIgnoreCase(" blee Blee BlEe", "BlEe", 4) => 6
     * </pre>
     *
     * @param  source   a string
     * @param  pattern  the pattern string to look for.
     * @param  start    where to start searching from
     */
    public static int indexOfIgnoreCase(String source, String pattern, int start) {
        int iLength = pattern.length();
        int iLastStart = source.length() - iLength + 1;

        for (int i = start; i < iLastStart; i++) {
            if (pattern.regionMatches(true, 0, source, i, iLength))
                return i;
        }

        return -1;
    }

    /**
     * Determines whether or not the string is empty or null.
     */
    public static boolean isEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    /**
     * Returns a string containing the given pieces glued together with the given
     * delimiter in between.
     */
    public static String join(List parts, char delim) {
        StringBuffer buf = new StringBuffer(128);
        Iterator iter = parts.iterator();

        while (iter.hasNext()) {
            buf.append(iter.next()).append(delim);
        }

        if (buf.length() > 0) {
            buf.setLength(buf.length() - 1);
        }

        return buf.toString();
    }

    /**
     */
    public static String join(String[] parts, char delim) {
        StringBuffer buf = new StringBuffer(128);

        for (int i = 0; i < parts.length; i++) {
            buf.append(parts[i]).append(delim);
        }

        if (buf.length() > 0) {
            buf.setLength(buf.length() - 1);
        }

        return buf.toString();
    }

    /**
     * Changed the case of the first letter of a string to lower case.
     *
     * @param   name  the string whose first char must be lower cased
     * @return  a new string with the first letter lowercased
     */
    public static String lowerCaseFirstLetter(String name) {
        if ((name == null) || (name.length() == 0))
            return ""; //$NON-NLS-1$

        if (name.length() == 1)
            return "" + Character.toLowerCase(name.charAt(0)); //$NON-NLS-1$

        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * Determine if two paths are the same, disregarding the file extension. i.e.
     * onlyDiffersByExtension("foo.jsp", "foo.html") returns true; but
     * onlyDiffersByExtension("foo.jsp", "bar.jsp") returns false. If either path does not
     * have a file extension, the path is compared as-is, so onlyDiffersByExtension(
     * "foo.jsp", "foo" ) will return true.
     *
     * @param   path1  A path to compare
     * @param   path2  Another path to compare
     * @return  true if they are the same except for the file extension.
     */
    public static boolean onlyDiffersByExtension(String path1, String path2) {
        assert (path1 != null) : "Can't compare a null path"; //$NON-NLS-1$
        assert (path2 != null) : "Can't compare a null path"; //$NON-NLS-1$
        String subPath1 = path1;
        String subPath2 = path2;
        int i = path1.lastIndexOf('.');

        if (i > -1)
            subPath1 = path1.substring(0, i);

        i = path2.lastIndexOf('.');

        if (i > -1)
            subPath2 = path2.substring(0, i);

        return ObjectUtil.areEqual(subPath1, subPath2);
    }

    /**
     * Replaces all of the occurrences of strPattern with strReplace. Note that unlike
     * String.replace, strPattern and strReplace are _not_ regular expressions
     */
    public static String replaceAll(String str, String strPattern, String strReplace) {
        StringBuffer ret = null;
        int ichLast = 0;
        int ichNext;

        while ((ichNext = str.indexOf(strPattern, ichLast)) != -1) {
            if (ret == null) {
                ret = new StringBuffer(str.length() + strReplace.length());
            }

            ret.append(str.substring(ichLast, ichNext));
            ret.append(strReplace);
            ichLast = ichNext + strPattern.length();
        }

        if (ret == null) {
            return str;
        }

        ret.append(str.substring(ichLast));

        return ret.toString();
    }

    /**
     * Returns a list containing the parts of the string that are separated by the given
     * delimiter.
     */
    public static List<String> split(String str, char delim) {
        List<String> parts = new ArrayList<String>();

        // if string empty return empty list
        if (null == str)
            return parts;

        StringTokenizer tokens = new StringTokenizer(str, String.valueOf(delim));

        while (tokens.hasMoreTokens()) {
            parts.add(tokens.nextToken());
        }

        return parts;
    }

    /**
     * Returns a list containing the parts of the string that are separated by the given
     * delimiter. fTrim will skip empty values
     */
    public static List<String> split(String str,
                                     char delim,
                                     boolean fTrim, List<String> parts) {
        StringTokenizer tokens = new StringTokenizer(str, String.valueOf(delim));

        while (tokens.hasMoreTokens()) {
            String s = tokens.nextToken();

            if (fTrim) {
                s = s.trim();

                if (s.length() == 0)
                    continue;
            }

            parts.add(s);
        }

        return parts;
    }

    /**
     * strips the package from the given string.
     */
    public static String stripPackage(String str) {
        assert (str != null);
        int lastDot = str.lastIndexOf("."); //$NON-NLS-1$

        return (lastDot == -1) ? str : str.substring(lastDot + 1);
    }

    public static String toStringStackTrace(Throwable t) {
        assert t != null : "Throwable was null."; //$NON-NLS-1$
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            t.printStackTrace(pw);
            pw.flush();
            sw.flush();

            return sw.toString();
        } finally {
            try {
                sw.close();
            } catch (IOException ioe) {
                assert false; /* Ignore. */
            }

            pw.close();
        }
    }

    /**/
    public static String trimQuotes(String string) {
        if ((string == null) || (string.length() == 0)) {
            return string;
        }

        char[] chars = string.toCharArray();
        int l = 0;
        int r = chars.length - 1;

        if ((chars[l] == '"') || ((chars[l] == '\'') && (chars[l] == chars[r]))) {
            l++;
            r--;

            return new String(chars, l, r);
        }

        //no matching quotes
        return string;
    }

    /**/
    public static String truncate(String str, int len) {
        //remove len characters from the end of the string
        return str.substring(0, str.length() - len);
    }

    /**
     * gets a shorter version of the given string, no wider than width when rendered in
     * c's default font.
     */
    public static String truncateString(String input, int width, java.awt.Component c) {
        return truncateString(input, width, c.getFontMetrics(c.getFont()));
    }

    /**
     * gets a shorter version of the given string, no wider than width when rendered in fm
     * metrics. this will return null only if the original input is null.
     */
    public static String truncateString(String input, int width, java.awt.FontMetrics fm) {
        if (input == null) {
            return null;
        }

        if (fm.stringWidth(input) <= width) {
            return input; // enough room for all text.
        }

        //hex value for ellipsis char
        String ellipsis = "\u2026"; //$NON-NLS-1$

        if (fm.stringWidth(ellipsis) > width) {
            // no room for ellipsis, give up.
            return ""; //$NON-NLS-1$
        }

        // we know that somewhere between 0 and length characters of the input will fit with an ellipsis.
        // find out exactly how many.
        int high = input.length();
        int low = 0;
        int mid;

        while (true) {
            mid = low + ((high - low) / 2);

            if (fm.stringWidth(input.substring(0, mid) + ellipsis) > width) {
                high = mid - 1;
            } else if (fm.stringWidth(input.substring(0, mid + 1) + ellipsis) < width) {
                low = mid + 1;
            } else {
                break;
            }
        }

        return input.substring(0, mid) + ellipsis;
    }

    /**
     * Return whether or not a string is a valid java identifier with an info message.
     *
     * @param   id
     * @param   message
     * @return  whether the passed in string is a valid java identifier
     */
    public static boolean validateJavaIdentifier(String id,
                                                 StringBuffer message) {
        boolean validID = true;

        IStatus result = JavaConventions.validateIdentifier(id);

        if (!result.isOK()) {
            message.append(result.getMessage());
            validID = false;
        }
        // Note, the JDT id checker does not yet consider 'enum' a java keyword
        // This code block should be removed when the JDT tester is fixed
        else if (id.equals("enum")) //$NON-NLS-1$
        {
            message.append("'enum' is not a valid Java identifier"); //$NON-NLS-1$
            validID = false;
        }
        // Note, the JDT id checker does not yet consider 'assert' a java keyword
        // This code block should be removed when the JDT tester is fixed
        else if (id.equals("assert")) //$NON-NLS-1$
        {
            message.append("'assert' is not a valid Java identifier"); //$NON-NLS-1$
            validID = false;
        }

        return validID;
    }

    /**
     * Wraps the given text in HTML, attempting to be between the desired and maximum
     * length on each line. If there aren't any breaks between those two points, it will
     * use the first break before the desired length. If there are no breaks, it will wrap
     * the line at the maximum length. Also escapes HTML values.
     */
    public static String wrapText(String s, int desiredLength, int maxLength) {
        // In most cases, the string will be fairly short, so save
        // a lot of work by not wrapping it
        if (s.length() < maxLength) {
            return escapeHTML(s);
        }

        BreakIterator lineIterator = BreakIterator.getLineInstance();
        lineIterator.setText(s);
        StringBuffer sb = new StringBuffer();
        int lastBreak = 0;
        int position = lineIterator.next();

        int lineCount = 0;

        while ((position != s.length()) || ((position - lastBreak) > maxLength)) {
            while ((position < s.length()) && (position < (lastBreak + desiredLength))) {
                position = lineIterator.following(position);
            }

            if ((position == s.length()) && ((position - lastBreak) < maxLength)) {
                break;
            }

            if ((position - lastBreak) > maxLength) {
                while ((position - lastBreak) > maxLength) {
                    position = lineIterator.previous();
                }

                if (position <= lastBreak) {
                    position = lastBreak + maxLength;
                }
            }

            sb.append(escapeHTML(s.substring(lastBreak, position)));
            lineCount++;
            sb.append("<br>"); //$NON-NLS-1$
            lastBreak = position;

            if (lineCount == 30) {
                sb.append("<b>Truncated "); //$NON-NLS-1$
                sb.append(s.length() - position);
                sb.append(" characters."); //$NON-NLS-1$

                return sb.toString();
            }
        }

        sb.append(escapeHTML(s.substring(lastBreak)));

        return sb.toString();
    }
}

/*% StringUtil
 * assert-not-valid=''assert'' is not a valid Java identifier enum-not-valid=''enum'' is
 * not a valid Java identifier%*/
