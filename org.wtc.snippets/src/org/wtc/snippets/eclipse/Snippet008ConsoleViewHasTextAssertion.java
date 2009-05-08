package org.wtc.snippets.eclipse;


import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;


/**
 * A snippet that demonstrates one approach to accessing console context in the Eclipse 
 * Console view.
 * <p>
 * To run this snippet, you may need to increase your heap size.  It was developed using the following VM
 * args:
 * <pre>
 * -Xms256m
 * -Xmx512m
 * </pre>
 * 
 * @author Phil Quitslund
 * 
 * @since 3.8.1
 */
public class Snippet008ConsoleViewHasTextAssertion extends UITestCaseSWT {

	private static final String CONSOLE_TEXT = "Hello console!";


	private class StyledTextAccessor implements HasText {

		private final IWidgetLocator textLocator;
		private String text;

		StyledTextAccessor(IWidgetLocator text) {
			this.textLocator = text;
		}
		
		public String getText(IUIContext ui) throws WidgetSearchException {
			final StyledText textWidget = (StyledText) ((IWidgetReference)ui.find(textLocator)).getWidget();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					text =  textWidget.getText().trim();
				}				
			});
			return text;
		}
		
		public ICondition hasText(String text) {
			return new HasTextCondition(this, text);
		}
		
	}
	
	

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IUIContext ui = getUI();
		ui.ensureThat(view("Welcome").isClosed());
		ui.ensureThat(view("Console").isShowing());
		doWriteToConsole();
		
	}
	
	public void testConsole() throws Exception {
		IUIContext ui = getUI();
		IWidgetLocator consoleLocator = new SWTWidgetLocator(StyledText.class, new ViewLocator("org.eclipse.ui.console.ConsoleView"));
		ui.assertThat(new StyledTextAccessor(consoleLocator).hasText(CONSOLE_TEXT));
	}


	private void doWriteToConsole() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					writeToConsole();
				} catch (PartInitException e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
			}
		});
	}


	private void writeToConsole() throws PartInitException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		String id = "org.eclipse.ui.console.ConsoleView"; // IConsoleConstants.ID_CONSOLE_VIEW;
		page.showView(id);

		MessageConsole myConsole = findConsole(id);
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println(CONSOLE_TEXT);
	}
	
	
	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
}
