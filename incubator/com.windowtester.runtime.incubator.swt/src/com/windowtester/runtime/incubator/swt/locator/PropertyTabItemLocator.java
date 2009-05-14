package com.windowtester.runtime.incubator.swt.locator;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.incubator.swt.matcher.BaseByClassNameMatcher;
import com.windowtester.runtime.locator.IWidgetMatcher;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.util.StringComparator;

public class PropertyTabItemLocator extends SWTWidgetLocator {
	
	
	private static class PropertyTabItemMatcher extends BaseByClassNameMatcher {

		private final String tabLabel;
		
		public PropertyTabItemMatcher(String tabLabel) {
			super("org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList$ListElement");
			this.tabLabel = tabLabel;
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
		 */
		public boolean matches(Object widget) {
			return super.matches(widget) && textMatches(widget);
		}


		private boolean textMatches(Object widget) {
			String text = widget.toString();
			return StringComparator.matches(text, tabLabel);
		}

	}
	
		//TODO: consider adding scoping by view
		
		private static final long serialVersionUID = 1L;
		
		private final String tabLabel;
//		private final ViewLocator view;

		public PropertyTabItemLocator(String tabLabel/*, ViewLocator view*/) {
			super(Widget.class);
			this.tabLabel = tabLabel;
//			this.view = view;
		}
		
//		public PropertyTabLocator(String tabLabel) {
//			this(tabLabel, new ViewLocator("org.eclipse.ui.views.PropertySheet"));
//		}
		
		
		@Override
		protected IWidgetMatcher buildMatcher() {
			return new PropertyTabItemMatcher(tabLabel);
		}

		
	}