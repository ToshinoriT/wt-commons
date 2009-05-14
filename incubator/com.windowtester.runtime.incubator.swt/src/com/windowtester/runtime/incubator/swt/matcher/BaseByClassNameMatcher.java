package com.windowtester.runtime.incubator.swt.matcher;

import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.internal.matcher.VisibilityMatcher;

@SuppressWarnings("restriction")
public class BaseByClassNameMatcher implements IWidgetMatcher {


	IWidgetMatcher visibilityMatcher = VisibilityMatcher.create(true);

	private final String className;
	

	public BaseByClassNameMatcher(String className) {
		this.className = className;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return classMatches(widget) && isVisible(widget);
	}

	private boolean isVisible(Object widget) {
		return visibilityMatcher.matches(widget);
	}

	private boolean classMatches(Object widget) {
		return widget.getClass().getName().equals(className);
	}

}