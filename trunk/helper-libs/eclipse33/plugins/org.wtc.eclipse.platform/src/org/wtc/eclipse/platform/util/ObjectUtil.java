/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectUtil {
    /**
     * Determines is two objects are equal, accounting for one or both objects being null
     * or the two objects being array types.
     *
     * @param   o1  The first object to compare.
     * @param   o2  The second object to compare.
     * @return  True if the two objects are equal.
     */
    public static final boolean areEqual(Object o1, Object o2) {
        boolean objectsAreEqual = false;

        if (o1 == o2) {
            objectsAreEqual = true;
        } else if ((o1 != null) && (o2 != null)) {
            if (o1.getClass().isArray() && o2.getClass().isArray()) {
                objectsAreEqual = Arrays.equals((Object[]) o1, (Object[]) o2);
            } else {
                objectsAreEqual = o1.equals(o2);
            }
        }

        return objectsAreEqual;
    }

    /**
     * Get a list of objects in the model of a particular type.
     */
    public final <T> List<T> getObjectsByClass(List list, Class<T> clazz) {
        List<T> mos = new ArrayList<T>();

        for (Object o : list) {
            if (clazz.isAssignableFrom(o.getClass())) {
                T casted = clazz.cast(o);
                mos.add(casted);
            }
        }

        return mos;
    }

}
