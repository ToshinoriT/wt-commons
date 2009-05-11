/**
 * WT Commons Project 2009
 *
 * http://code.google.com/p/wt-commons
 */
package org.wtc.eclipse.core.util;

import org.eclipse.core.resources.ResourcesPlugin;
import org.osgi.framework.Constants;

/**
 * Eclipse environment accessors.
 * <p/>
 * Sample use:
 * 
 * <pre>
 *    if (Eclipse.VERSION.isAtLeast(3,2)) {
 *    		...
 *    }
 * </pre>
 * 
 * <p/>
 * 
 * @author Phil Quitslund
 * @author Dan Rubel
 * @author Mark Russell
 * 
 * @since 3.8.0
 */
public class Eclipse {


	public static class Version {
		
		private final int major;
		private final int minor;
		
		Version() {
			this(parseVersion());
		}
		
		Version(org.osgi.framework.Version version) {
			this(version.getMajor(), version.getMinor());
		}
		
		Version(int major, int minor) {
			this.major = major;
			this.minor = minor;
		}
		
		/**
		 * @return the major version number
		 */
		public int getMajor() {
			return major;
		}
		
		/**
		 * @return the minor version number
		 */
		public int getMinor() {
			return minor;
		}
		
		/**
		 * Test if this version is equal to the given major and minor specifier.
		 * @param major the version major number
		 * @param minor the version minor number
		 * @return <code>true</code> if this version is equal and <code>false</code> otherwise
		 */
		public boolean is(int major, int minor) {
			return getMajor() == major && getMinor() == minor;
		}
		
		/**
		 * Test if this version is equal to or greater than the given major and minor specifier.
		 * @param major the version major number
		 * @param minor the version minor number
		 * @return <code>true</code> if this version is equal to or greater than the given major and minor and <code>false</code> otherwise
		 */
		public  boolean isAtLeast(int major, int minor) {
			int actualMajor = getMajor();
			if (actualMajor > major)
				return true;
			if (actualMajor ==  major)
				return getMinor() >= minor;
			return false;
		}
		

		/**
		 * Test if this version is less than the given major and minor specifier.
		 * @param major the version major number
		 * @param minor the version minor number
		 * @return <code>true</code> if this version is less and <code>false</code> otherwise
		 */
		public boolean isLessThan(int major, int minor) {
			return !isAtLeast(major, minor);
		}
		
	}
	
	
	/*
	 * Get the version of the current running Eclipse instance.
	 * (Note that micro versions may not be accurate.)
	 */
	private static org.osgi.framework.Version parseVersion() {
		String versionStr = (String)ResourcesPlugin.getPlugin().getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
		return org.osgi.framework.Version.parseVersion(versionStr);
	}
	
	/**
	 * Version identifier for the current running Eclipse.
	 * <p>
	 * Note that only major and minor numbers are provided (version micro numbers may not be accurate).
	 */
	public static final Version VERSION = new Version();
	

	
}
