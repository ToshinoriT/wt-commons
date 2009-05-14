package com.windowtester.runtime.incubator.swt.locator;


import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.incubator.swt.matcher.BaseByClassNameMatcher;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

public class TabbedPropertyListLocator extends SWTWidgetLocator {
	
	
	static class TabbedPropertyListMatcher extends BaseByClassNameMatcher {
		
		public TabbedPropertyListMatcher() {
			super("org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList");
		}

	}
	
	private static final long serialVersionUID = 1L;
	
	public TabbedPropertyListLocator() {
		super(Widget.class);		
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	@Override
	protected IWidgetMatcher buildMatcher() {
		//NOTE: matching could be more robust -- e.g., include parent info
		return new TabbedPropertyListMatcher();
	}

	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	@Override
	public IWidgetLocator[] findAll(IUIContext ui) {
		IWidgetLocator[] refs = super.findAll(ui);
		TabbedPropertyListReference[] tabs = new TabbedPropertyListReference[refs.length];
		for (int i = 0; i < tabs.length; i++) {
			tabs[i] = new TabbedPropertyListReference((IWidgetReference)refs[i]);
		}
		return tabs;
	}
	
}