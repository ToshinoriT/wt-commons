package com.windowtester.runtime.incubator.swt.locator;

import com.windowtester.runtime.locator.WidgetReference;

public class PropertyTabItemReference extends WidgetReference {

	public PropertyTabItemReference(Object widget) {
		super(widget);
	}

	public String getText() {
		return getWidget().toString();
	}

}