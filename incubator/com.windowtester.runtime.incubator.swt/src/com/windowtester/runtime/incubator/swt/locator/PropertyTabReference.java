package com.windowtester.runtime.incubator.swt.locator;

import com.windowtester.runtime.locator.WidgetReference;

public class PropertyTabReference extends WidgetReference {

	public PropertyTabReference(Object widget) {
		super(widget);
	}

	public String getText() {
		return getWidget().toString();
	}

}