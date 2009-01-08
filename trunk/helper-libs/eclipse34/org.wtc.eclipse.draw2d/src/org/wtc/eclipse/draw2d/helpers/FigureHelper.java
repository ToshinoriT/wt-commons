/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.draw2d.helpers;

import junit.framework.TestCase;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.wtc.eclipse.draw2d.Draw2DActivator;
import org.wtc.eclipse.draw2d.exceptions.FigureNotFoundException;
import org.wtc.eclipse.draw2d.exceptions.MultipleFiguresFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Automated test support for Draw2D objects.
 */
public class FigureHelper {
    /**
     * Get the Draw 2D figure object that matches the given text. Recurse through the tree
     * of figures from the given figure to find the matching figure. If the figure does
     * not have a getText method, then it is considered to be not a match.
     *
     * @param   parent  - Parent figure of the Draw2D figures to find
     * @param   name    - Text of the figures (as determined by the getText implementation
     *                  of the figure) to return
     * @return  IFigure - Draw 2D figure whose text matches the given text
     * @throws
     */
    public IFigure findFigureByLabel(IFigure parent, String text)
                              throws FigureNotFoundException, MultipleFiguresFoundException {
        TestCase.assertNotNull(parent);
        TestCase.assertNotNull(text);

        List<IFigure> figures = findFiguresByLabel(parent, text);

        int size = figures.size();

        if (size == 0) {
            throw new FigureNotFoundException("Did not find a figure for: " + text); //$NON-NLS-1$
        } else if (size > 1) {
            throw new MultipleFiguresFoundException("Expected a single figure for (" //$NON-NLS-1$
                                                    + text + ") but found (" //$NON-NLS-1$
                                                    + size + ") instead"); //$NON-NLS-1$
        }

        return figures.get(0);
    }

    /**
     * Get all Draw 2D figure objects that match the given text. Recurse through the tree
     * of figures from the given figure to find the matching figures. If the figure does
     * not have a getText method, then it is considered to be not a match
     *
     * @param   parent  - Parent figure of the Draw2D figures to find
     * @param   name    - Text of the figures (as determined by the getText implementation
     *                  of the figure) to return
     * @return  List<IFigure> - List of Draw 2D figures whose text matches the given text
     *          or an empty list if no figures were found
     */
    public List<IFigure> findFiguresByLabel(IFigure parent,
                                            String text) {
        TestCase.assertNotNull(parent);
        TestCase.assertNotNull(text);

        ArrayList<IFigure> matches = new ArrayList<IFigure>();

        // Try to get the text property through the getText method
        Class figureClass = parent.getClass();

        try {
            Method m = figureClass.getMethod("getText", (Class[]) null); //$NON-NLS-1$
            String sourceText = (String) m.invoke(parent, (Object[]) null);

            if (sourceText.equals(text)) {
                matches.add(parent);
            }
            // If the equals didn't match, then try a regex
            else {
                //System.err.println("-- ATTEMPTING FIGURE MATCH BY REGEX --");
                try {
                    Pattern pattern = Pattern.compile(text);
                    Matcher matcher = pattern.matcher(sourceText);

                    if (matcher.matches()) {
                        matches.add(parent);
                    }
                } catch (PatternSyntaxException pse) {
                }
            }
        }
        // Not all figures have a getText method because not all figures
        // have text. Catch the reflection exceptions and do nothing.
        // Don't just catch Exception so any other unexpected exceptions
        // are caught
        catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        // Make sure the children are added
        @SuppressWarnings(value = { "unchecked" })
        List<IFigure> kids = parent.getChildren();

        for (IFigure nextKid : kids) {
            matches.addAll(findFiguresByLabel(nextKid, text));
        }

        return matches;
    }

    /**
     * Get all Draw 2D figure objects that match the given text. Recurse through the tree
     * of figures from the root figure in the given figure canvas to find the figures
     *
     * @param   canvas  - Parent SWT container for the Draw2D figures to find
     * @param   name    - Text of the figures (as determined by the toString
     *                  implementation of the figure) to return
     * @return  List<IFigure> - List of Draw 2D figures whose text matches the given text
     *          or an empty list if no figures were found
     */
    public List<IFigure> findFiguresByText(FigureCanvas canvas, String text) {
        TestCase.assertNotNull(canvas);
        TestCase.assertNotNull(text);

        IFigure contentPane = canvas.getContents();

        return findFiguresByText(contentPane, text);
    }

    /**
     * Get all Draw 2D figure objects that match the given text. Recurse through the tree
     * of figures from the given figure to find the matching figures
     *
     * @param   parent  - Parent figure of the Draw2D figures to find
     * @param   name    - Text of the figures (as determined by the toString
     *                  implementation of the figure) to return
     * @return  List<IFigure> - List of Draw 2D figures whose text matches the given text
     *          or an empty list if no figures were found
     */
    private List<IFigure> findFiguresByText(IFigure parent, String text) {
        TestCase.assertNotNull(parent);
        TestCase.assertNotNull(text);

        ArrayList<IFigure> matches = new ArrayList<IFigure>();

        List children = parent.getChildren();

        for (Object child : children) {
            IFigure figChild = (IFigure) child;
            matches.addAll(findFiguresByText(figChild, text));

            if (child.toString().equals(text)) {
                matches.add(figChild);
            }
        }

        return matches;
    }

    /**
     * Get the first child figure of a given type. This will do a breadth-first search of
     * the children of the given parent figure and return the first child it finds that is
     * the right type. That is, if there are multiple figures of the same type in the
     * hierarchy, it will return the one found at the shallowest level. If multiple
     * figures of that type are found at the same level, the first one returned from the
     * parent's getChildren() call will be returned. If the given parent figure is itself
     * the right type, it will be returned.
     *
     * @param   figure  The parent figure to search
     * @param   c       The type of figure to search for
     * @return  The first child of that type, or null if none were found
     */
    @SuppressWarnings("unchecked")
    public <T> T getFirstFigureOfType(IFigure figure, Class<T> c) {
        TestCase.assertNotNull(c);

        List<IFigure> children = new ArrayList<IFigure>();
        children.add(figure);

        for (int i = 0; i < children.size(); i++) {
            IFigure child = children.get(i);

            if (child != null) {
                if (c.isAssignableFrom(child.getClass())) {
                    return c.cast(child);
                }

                children.addAll(child.getChildren());
            }
        }

        return null;
    }

    /**
     * Issue a message with debug severity that prints information about the provided Draw
     * 2D figure and its children.
     */
    public void printFigures(IFigure figure) {
        printFigures(figure, 0);
    }

    /**
     * Issue a message with debug severity that prints information about the provided Draw
     * 2D figure and its children.
     */
    private void printFigures(IFigure figure, int indent) {
        StringBuffer output = new StringBuffer();

        for (int i = 0; i < indent; i++) {
            output.append(" "); //$NON-NLS-1$
        }

        String text = ""; //$NON-NLS-1$

        try {
            Method getText = figure.getClass().getMethod("getText", new Class[] {}); //$NON-NLS-1$

            if (getText != null) {
                Object obj = getText.invoke(figure, new Object[] {});

                if (obj instanceof String) {
                    text = " - " + (String) obj; //$NON-NLS-1$
                }
            }
        } catch (Exception e) { /* do nothing */
        }

        output.append("<"); //$NON-NLS-1$
        output.append(figure.toString());
        output.append(text);
        output.append(">"); //$NON-NLS-1$

        Draw2DActivator.logDebug(output.toString());

        @SuppressWarnings(value = { "unchecked" })
        List<IFigure> children = figure.getChildren();

        for (IFigure next : children) {
            printFigures(next, indent + 3);
        }
    }
}
