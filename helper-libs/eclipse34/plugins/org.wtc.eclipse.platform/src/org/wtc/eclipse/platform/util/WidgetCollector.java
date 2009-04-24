/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * Collects widgets of interest for testing.
 *
 * @since 3.8.0
 */
public class WidgetCollector {

	
	private final IUIContext ui;
	
	public WidgetCollector(IUIContext ui) {
		this.ui = ui;
	}
	
	/**
	 * Gets all widgets of a certain type.
	 * @param <T> - the type of the suspect widget
	 * @param cls - the class of the suspect widget
	 * @return a list of widgets of a given type
	 * @since 3.8.1
	 */
	@SuppressWarnings("unchecked")
	public <T extends Widget> List<T> all(Class<T> cls) {
		Arguments.assertNotNull(cls);
		List<T> widgets = new ArrayList<T>();
		IWidgetLocator[] refs = ui.findAll(new SWTWidgetLocator(cls));
		for (IWidgetLocator ref : refs) {
			widgets.add((T)((IWidgetReference)ref).getWidget());
		}
		return widgets;
	}
	
	/**
	 * Gets the nth widget of a certain type.
	 * @param <T> - the type of the suspect widget
	 * @param cls - the class of the suspect widget
	 * @param index - the index of the suspect widget
	 * @return the widget of interest
	 * @throws WidgetNotFoundException if no widget is found
	 * @since 3.8.1
	 */
	public <T extends Widget> T indexed(Class<T> cls, int index) throws WidgetNotFoundException {
		Arguments.assertNotNull(cls, index);
		List<T> all = all(cls);
		if (index >= all.size())
			throw new WidgetNotFoundException("No Widget of class <" + cls.getName() + "> found at index: " + index); //$NON-NLS-1$ //$NON-NLS-2$
		return all.get(index);
	}
	
	
	/**
	 * Finds the widget that comes after the given widget in
	 * it's parent's list of children.
	 * @param <T> - the type of the returned widget
	 * @param control - the control whose successor you want to collect
	 * @return the successor of the given widget
	 * @throws WidgetNotFoundException if no succesor is found
	 */
	@SuppressWarnings("unchecked")
	public <T extends Widget> T successorOf(final Control control) throws WidgetNotFoundException {
		Arguments.assertNotNull(control);
		final Widget[] successor = new Widget[1];
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				Composite parent = control.getParent();
				Control[] children = parent.getChildren();
				boolean isSuccessor = false;
				for (Control child : children) {
					if (isSuccessor) {
						successor[0] = child;
						return;
					}
					if (control == child)
						isSuccessor = true;
				}
			}
		});
		Widget result = successor[0];
		if (result == null)
			throw new WidgetNotFoundException("No successor found for Control: < " + control + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		return (T) result;
	}
	
	
	
}
