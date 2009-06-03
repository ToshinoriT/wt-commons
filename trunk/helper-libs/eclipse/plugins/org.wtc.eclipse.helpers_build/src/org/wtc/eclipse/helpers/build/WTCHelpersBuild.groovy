package org.wtc.eclipse.helpers.build;

import com.instantiations.pde.build.FeatureBuild;

/**
 * A builder for the Helper libs.
 */
public class WTCHelpersBuild extends FeatureBuild {

	public static void main(String[] args) {
		WTCHelpersBuild wtlb = new WTCHelpersBuild();
		wtlb.build();
	}
	
}
