package com.windowtester.runtime.incubator.swt.locator;

import java.lang.reflect.Field;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;

public class TabbedPropertyListReference implements IWidgetReference {

	private final IWidgetReference tabListRef;

	public TabbedPropertyListReference(IWidgetReference tabListRef) {
		this.tabListRef = tabListRef;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetReference#getWidget()
	 */
	public Object getWidget() {
		return tabListRef.getWidget();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		return tabListRef.findAll(ui);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		return tabListRef.matches(widget);
	}
	
	public PropertyTabReference[] getTabs() {
		Object[] tabs = getTabElements(getWidget());
		PropertyTabReference[] tabRefs = new PropertyTabReference[tabs.length];
		for (int i = 0; i < tabs.length; i++) {
			tabRefs[i] = new PropertyTabReference(tabs[i]);
		}
		return tabRefs;
	}

	private Object[] getTabElements(Object widget) {
		Object[] tabs = new Object[0];
		
		//this is really cheesy but there is no way to get these using API
		try {
			Class<?> cls = widget.getClass();
			Field field = cls.getDeclaredField("elements");
			field.setAccessible(true);
			tabs = (Object[]) field.get(widget);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tabs;
	}
	
	
}