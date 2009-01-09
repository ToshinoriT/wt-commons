/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util;

import junit.framework.TestCase;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.wtc.eclipse.platform.PlatformActivator;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to convert eclipse build markers (errors, warnings, tasks, etc) into a human
 * readable format.
 */
public class MarkerUtil {
    /**
     * toString - Convert the given marker objects into a human readable format.
     *
     * @param  markers  - Object representations of markers in the workbench. Should not
     *                  be null
     */
    public String toString(IMarker[] markers) {
        TestCase.assertNotNull(markers);

        List<Marker> list = new ArrayList<Marker>(markers.length);

        for (IMarker imarker : markers) {
            list.add(new Marker(imarker));
        }

        String divider = "--------------------------------"; //$NON-NLS-1$
        StringBuffer buff = new StringBuffer();
        int i = 0;

        for (Marker marker : list) {
            i++;
            buff.append(i + ":\n" + divider + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
            buff.append(marker.toString());
            buff.append("\n" + divider); //$NON-NLS-1$
        }

        buff.append("\nFound " + i + " markers."); //$NON-NLS-1$ //$NON-NLS-2$

        return buff.toString();
    }

    /**
     * Marker - Utility class to convert an eclipse IMarker into human readable
     * information.
     */
    private static class Marker {
        private IMarker _marker;

        /**
         * Marker.
         *
         * @param  marker  - The eclipse marker that is to be analyzed
         */
        public Marker(IMarker marker) {
            TestCase.assertNotNull(marker);
            _marker = marker;
        }

        /**
         * getEnd.
         *
         * @return  int - The marker highlights a region in a file. This is the index of
         *          the end of that selection
         */
        public int getEnd() {
            return _marker.getAttribute(IMarker.CHAR_END, -1);
        }

        /**
         * getLocation.
         *
         * @return  String - The location of the resource that contains the reason for
         *          this marker. Typically a project file or a project
         */
        public String getLocation() {
            return _marker.getAttribute(IMarker.LOCATION, ""); //$NON-NLS-1$
        }

        /**
         * getMessage.
         *
         * @return  int - The description of the issue that created the marker
         */
        public String getMessage() {
            return _marker.getAttribute(IMarker.MESSAGE, ""); //$NON-NLS-1$
        }

        /**
         * getResourcePath.
         *
         * @return  String - The location of the resource that contains the reason for
         *          this marker. Typically a project file or a project
         */
        public String getResourcePath() {
            return _marker.getResource().getFullPath().toPortableString();
        }

        /**
         * getStart.
         *
         * @return  int - The marker highlights a region in a file. This is the index of
         *          the start of that selection
         */
        public int getStart() {
            return _marker.getAttribute(IMarker.CHAR_START, -1);
        }

        /**
         * getType.
         *
         * @return  String - The marker type or null if the type could not be extracted
         *          for any reason
         */
        public String getType() {
            try {
                return _marker.getType();
            } catch (CoreException e) {
                PlatformActivator.logException(e);
            }

            return null;
        }

        /**
         * @see  Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder buff = new StringBuilder();

            buff.append("TYPE:" + getType()); //$NON-NLS-1$
            buff.append("\nMESSAGE:" + getMessage()); //$NON-NLS-1$
            buff.append("\nRESOURCE_PATH:" + getResourcePath()); //$NON-NLS-1$
            buff.append("\nLOCATION:" + getLocation()); //$NON-NLS-1$
            buff.append("\nSTART:" + getStart()); //$NON-NLS-1$
            buff.append("\nEND:" + getEnd()); //$NON-NLS-1$

            return buff.toString();
        }
    }
}
