package wt.commons.snippets.eclipse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * An example custom browser locator implementation.
 * <p>
 * The included tests provide references for how this might be used in a test.
 * <p>
 * <ul>
 *   <li> {@link #testTutorialsLinkExists()} tests to see that the WindowTester "getting started" link exists</li>
 * 	 <li> {@link #testWhatsNewLinkExists()} tests to see that the WindowTester "What's New" link exists
 * </ul>
 * <p>
 * 
 * @since Eclipse 3.4
 * @author Phil Quitslund
 */
public class Snippet006CustomBrowserLocator extends UITestCaseSWT /* intentionally NOT subclassing BASETEST! */ {

	
	private static final String TUTORIALS = "http://org.eclipse.ui.intro/showPage?id=tutorials";
	private static final String WHATS_NEW = "http://org.eclipse.ui.intro/showPage?id=whatsnew";
	
	private static final String SHOW_HELP_CMD = "http://org.eclipse.ui.intro/showHelpTopic?id=/";
	private static final String WT_GETTING_STARTED_LINK = 
		SHOW_HELP_CMD + "com.windowtester.eclipse.help/html/gettingStarted.html";
	private static final String WT_WHATS_NEW_LINK = 	
		SHOW_HELP_CMD + "com.windowtester.eclipse.help/html/whatsnew.html";
	
	private BrowserReference browser;


	public static class BrowserReference implements IWidgetReference {

		
		private class HtmlContainsCondition implements ICondition {

			String expectedText;
			
			public HtmlContainsCondition(String expectedText) {
				this.expectedText = expectedText;
			}
			
			/* (non-Javadoc)
			 * @see com.windowtester.runtime.condition.ICondition#test()
			 */
			public boolean test() {
				String html = getHTML();
				if (html == null)
					return false;
				return html.contains(expectedText);
			}
						
		}

		
		private final IWidgetReference browserWidget;

		public BrowserReference(IWidgetReference browserWidget) {
			this.browserWidget = browserWidget;
		}
				
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.locator.IWidgetReference#getWidget()
		 */
		public Object getWidget() {
			return browserWidget.getWidget();
		}

		/**
		 * Text access was introduced in 3.4
		 * TODO: consider throwing an exception here if not 3.4+
         * 
		 * @since Eclipse 3.4 where Browser.getText() is introduced
		 */
		public String getHTML() {
			final String text[] = new String[1];
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					text[0] = getText(getBrowser());
				}			
			});
			return text[0];
		}
		
		/**
		 * Using reflection to access <code>Browser.getText()</code> so that this
		 * will compile pre Eclipse 3.4.
		 */
		private static String getText(Browser browser) {
			if (browser == null)
				return null;
			
			try {
				Method m = browser.getClass().getMethod("getText", (Class[]) null);
				m.setAccessible(true);
				return (String) m.invoke(browser, (Object[]) null);
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			return null;
		}
		
		public void execute(final String script) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getBrowser().execute(script);
				}			
			});
		}
		public void setURL(final String url) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getBrowser().setUrl(url);
				}			
			});
		}
		
		public ICondition htmlContains(String expectedText) {
			return new HtmlContainsCondition(expectedText);
		}
		
		public Browser getBrowser() {
			return (Browser)getWidget();
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
		 */
		public IWidgetLocator[] findAll(IUIContext ui) {
			return browserWidget.findAll(ui);
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
		 */
		public boolean matches(Object widget) {
			return browserWidget.matches(widget);
		}
		
		
	}
	
	
	public static class BrowserLocator extends SWTWidgetLocator {

		private static final long serialVersionUID = 1L;

		public BrowserLocator() {
			super(Browser.class);
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
		 */
		@Override
		public IWidgetLocator[] findAll(IUIContext ui) {
			IWidgetLocator[] refs = super.findAll(ui);
			BrowserReference[] browsers = new BrowserReference[refs.length];
			for (int i = 0; i < browsers.length; i++) {
				browsers[i] = new BrowserReference((IWidgetReference)refs[i]);
			}
			return browsers;
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		ensureWelcomePageIsVisible();
		browser = getBrowser();
	}


	//tests to see that the WindowTester "getting started" link exists
	public void testTutorialsLinkExists() throws Exception {
		openPage(TUTORIALS);
		assertThat(browser.htmlContains(WT_GETTING_STARTED_LINK));
	}

	//tests to see that the WindowTester "What's New" link exists
	public void testWhatsNewLinkExists() throws Exception {
		openPage(WHATS_NEW);
		assertThat(browser.htmlContains(WT_WHATS_NEW_LINK));
	}
		
	private void assertThat(ICondition condition) {
		getUI().assertThat(condition);
	}


	private void openPage(String url) {
		browser.setURL(url);
	}

	private BrowserReference getBrowser() throws WidgetSearchException {
		return (BrowserReference) getUI().find(new BrowserLocator());
	}

	private void ensureWelcomePageIsVisible()
			throws WidgetSearchException, WaitTimedOutException {
		IUIContext ui = getUI();
		if (welcomePage().isVisible().testUI(ui)) //a bit cumbersome
			return;
		ui.click(new MenuItemLocator("Help/Welcome")); 
		ui.wait(welcomePage().isVisible());
	}


	private ViewLocator welcomePage() {
		return ViewLocator.forName("Welcome");
	}
	
	
}
