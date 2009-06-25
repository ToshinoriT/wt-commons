/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.platform.helpers.adapters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.osgi.framework.Bundle;
import org.wtc.eclipse.platform.PlatformActivator;
import org.wtc.eclipse.platform.conditions.RegexTitleShellCondition;
import org.wtc.eclipse.platform.shellhandlers.IWorkbenchShellHandler;
import org.wtc.eclipse.platform.util.ExceptionHandler;

import abbot.finder.swt.SWTHierarchy;
import abbot.finder.swt.TestHierarchy;
import abbot.tester.swt.ControlTester;
import abbot.tester.swt.TableItemTester;
import abbot.tester.swt.TableTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.monitor.IUIThreadMonitor;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.condition.shell.IShellCondition;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.internal.condition.shell.ShellMonitor;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.tester.swt.TreeItemTester;

/**
 * Generic methods shared in Helper implementation subclasses.
 * 
 * @since 3.8.0
 */
public abstract class HelperImplAdapter {
    protected static final String DEBUG_OPTION_ENTRY_EXIT = "/logging/entry_exit"; //$NON-NLS-1$
    protected static final String DEBUG_OPTION_SHELLHANDLERREG = "/logging/shellHandlerRegistraion"; //$NON-NLS-1$
    protected static final String DEBUG_OPTION_WIDGET_SEARCH = "/logging/widgetSearch"; //$NON-NLS-1$

    // We'll map handler class name to the shell condition. The
    // shell condition is needed to remove a shell handler from
    // the active shell monitor
    private static Map<IWorkbenchShellHandler, IShellCondition> _registeredHandlers =
        new HashMap<IWorkbenchShellHandler, IShellCondition>();

    /**
     * Simple utility for clicking a button.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void clickButton(IUIContext ui, String buttonLabel) {
        try {
            IWidgetLocator buttonLoc = new ButtonLocator(buttonLabel);
            waitForControlEnabled(ui, buttonLoc, Button.class);
            ui.click(buttonLoc);
        } catch (WidgetSearchException e) {
        	ExceptionHandler.handle(e);
        }
    }

    /**
     * Simple utility for clicking a button by name (not label).
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void clickButtonByName(IUIContext ui, String buttonName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(buttonName);

        try {
            ui.click(new NamedWidgetLocator(buttonName));
        } catch (WidgetSearchException e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Simple utility for clicking a Cancel button.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void clickCancel(IUIContext ui) {
        clickButton(ui, "Cancel"); //$NON-NLS-1$
    }

    /**
     * Simple utility for clicking a Close button.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void clickClose(IUIContext ui) {
        clickButton(ui, "Close"); //$NON-NLS-1$
    }

    /**
     * Simple utility for clicking a finish button.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void clickFinish(IUIContext ui) {
        clickButton(ui, "&Finish"); //$NON-NLS-1$
    }

    /**
     * Simple utility for clicking a next button.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void clickNext(IUIContext ui) {
        clickButton(ui, "&Next >"); //$NON-NLS-1$
    }

    /**
     * Simple utility for clicking a next button.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void clickNo(IUIContext ui) {
        clickButton(ui, "&No"); //$NON-NLS-1$
    }

    /**
     * Simple utility for clicking a OK button.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void clickOK(IUIContext ui) {
        clickButton(ui, "OK"); //$NON-NLS-1$
    }

    /**
     * Utility to click a Tree column where the first column has the specified text value.
     * In other words, click a multi-columned tree item where Y-position is determined by
     * the tree node text and the X-position is determined by the given column index
     *
     * @since 3.8.0
     * @param  ui        - Driver for UI generated input
     * @param  tree      - The multi-columned tree
     * @param  nodePath  - The label of the tree node to find where nested nodes are
     *                   separated with forward slashes
     * @param  column    - The index of the column in the nodeText's tree item rox
     */
    protected void clickTreeColumn(final IUIContext ui,
                                   final Tree tree,
                                   String nodePath,
                                   final int column) throws WidgetSearchException {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(tree);
        TestCase.assertNotNull(nodePath);
        TestCase.assertFalse(nodePath.length() == 0);
        TestCase.assertTrue(column >= 0);

        // First, make sure the node is visible
        IWidgetLocator treeRef = new WidgetReference(tree);
        ui.click(new TreeItemLocator(nodePath, treeRef));

        // Let everything repaint
        new SWTIdleCondition().waitForIdle();

        try {
            TreeItemTester treeItemTester = new TreeItemTester();
            final TreeItem treeItem = treeItemTester.getTreeItemByPath(nodePath, tree);

            final int[] itemCenterXY = new int[2];
            Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        Rectangle itemBounds = treeItem.getBounds(column);

                        itemCenterXY[0] = itemBounds.x + (itemBounds.width / 2);
                        itemCenterXY[1] = itemBounds.y + (itemBounds.height / 2) + tree.getHeaderHeight();
                    }
                });

            // Now determine the click point. First, make sure the facet is
            // selected and scrolled into view [handled by ui.click() above]
            ui.click(new XYLocator(treeRef, itemCenterXY[0], itemCenterXY[1]));
            new SWTIdleCondition().waitForIdle();
        } catch (abbot.finder.swt.WidgetNotFoundException ex) {
            throw new WidgetSearchException(ex);
        } catch (abbot.finder.swt.MultipleWidgetsFoundException ex) {
            throw new WidgetSearchException(ex);
        }
    }

    /**
     * Simple utility for clicking a Yes button.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void clickYes(IUIContext ui) {
        clickButton(ui, ".*Yes.*"); //$NON-NLS-1$
    }

    /**
     * Utility method for debugging purpses only. Take a snapshot of all the wigdets in
     * the workbench, and print the widget hierarchy to the console
     */
    protected void dbPrintWidgets() {
        SWTHierarchy h = new SWTHierarchy(Display.getDefault());
        h.dbPrintWidgets();
    }

    /**
     * Find a control by name.
     */
    public static <T extends Control> T findControlByName(IUIContext ui,
                                                          Class<T> controlClass,
                                                          final String controlName) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(controlClass);
        TestCase.assertNotNull(controlName);

        NamedWidgetLocator hackedLocator = new NamedWidgetLocator(controlName) {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean matches(final Object obj) {
                    boolean matches = false;

                    if (obj instanceof Control) {
                        final String[] actualName = new String[1];
                        final boolean[] visible = new boolean[1];
                        Display.getDefault().syncExec(new Runnable() {
                                public void run() {
                                    actualName[0] = (String) ((Control) obj).getData("name"); //$NON-NLS-1$
                                    visible[0] = ((Control) obj).isVisible();
                                }
                            });
                        matches = visible[0] && controlName.equals(actualName[0]);
                        String message = MessageFormat.format("exptected<{0}> actual<{1}> visible<{2}> matches<{3}>", //$NON-NLS-1$
                                                              new Object[] {
                                controlName, actualName[0], visible[0], matches
                            });
                        PlatformActivator.logDebug(message, DEBUG_OPTION_WIDGET_SEARCH);
                    }

                    return matches;
                }
            };

        return waitForControl(ui,
                              hackedLocator,
                              controlClass);
    }

    /**
     * Find a widget by a recursive, breadth-first, search of the given widget's children.
     * This method should only be used when a Window Tester Widget Locator bug exists and
     * all possible Window Tester location strategies have been exhausted.
     *
     * <p>IMPORTANT! - This method must be called from the UI thread. Ex: <code>
     * Display.getDefault().syncExec(new Runnable() { findWidget() });</code> Whenever a
     * helper must use the UI thread, that operation is called an 'atomic operation.'
     * Every atomic operation *must* be called between a pair of <code>
     * IUIContext.handleConditions()</code> calls. Example:<br/>
     * <code>ui.handleConditions(); Display.getDefault().syncExec(new Runnable() {
     * findWidget() }); ui.handleConditions();</code><br/>
     * Without these calls conditions handlers are not run and unexpected UI states
     * (unexpected dialogs, for example) will cause the test run to hang.</p>
     *
     * @param   parentWidget  - The parent widget to search. Must not be null
     * @param   widgetClass   - The class of the Widget to find
     * @return  the widget - The first instance of a Widget of the given type or <code>
     *          null</code> if no Widget is found
     */
    @SuppressWarnings("unchecked")
	protected <T extends Widget> T findFirstWidget(Widget parentWidget, Class<T> widgetClass) {
        Display display = Display.getDefault();
        TestHierarchy hierarchy = new TestHierarchy(display);

        Collection<Widget> widgets = hierarchy.getWidgets(parentWidget);

        for (Widget nextWidget : widgets) {
            if (widgetClass.isAssignableFrom(nextWidget.getClass())) {
                return widgetClass.cast(nextWidget);
            } else {
                T widget = findFirstWidget(nextWidget, widgetClass);

                if (widget != null) {
                    return widget;
                }
            }
        }

        return null;
    }

    /**
     * @return  T - Find the unique element in the before and after lists
     */
    protected <T> T findUniqueElement(List<T> before, List<T> after) {
        TestCase.assertNotNull(before);
        TestCase.assertNotNull(after);
        int difference = (after.size() - before.size());
        TestCase.assertEquals(1, difference);

        List<T> beforeCopy = new ArrayList<T>(before);
        List<T> afterCopy = new ArrayList<T>(after);

        for (T nextThing : beforeCopy) {
            afterCopy.remove(nextThing);
        }

        TestCase.assertEquals(afterCopy.size(), 1);

        return afterCopy.get(0);
    }

    /**
     * Get the bundle for a plugin ID or fail the test.
     */
    protected Bundle getBundleForPluginID(String pluginID) {
        TestCase.assertNotNull(pluginID);
        Bundle bundle = Platform.getBundle(pluginID);
        TestCase.assertNotNull("THE PLUGIN FOR ID <" + pluginID + "> COULD NOT BE FOUND", //$NON-NLS-1$ //$NON-NLS-2$
                               bundle);

        return bundle;
    }

    /**
     * @return  Stack<StackTraceElement> - The call stack of the current thread including
     *          the method name given or an empty stack if the method was not found on the
     *          call stack
     */
    private List<StackTraceElement> getCallStackBefore(String targetMethodName) {
        List<StackTraceElement> callStack = new ArrayList<StackTraceElement>();

        Thread current = Thread.currentThread();
        StackTraceElement[] frames = current.getStackTrace();
        boolean found = false;
        int index = 0;

        for (; !found && (index < frames.length); index++) {
            StackTraceElement target = frames[index];
            String actualMethodName = target.getMethodName();
            found = actualMethodName.equals(targetMethodName);
        }

        TestCase.assertTrue("THE TARGET METHOD NAME <" + targetMethodName + "> WAS NOT FOUND ON THE STACK", //$NON-NLS-1$ //$NON-NLS-2$
                            found);

        for (; index < frames.length; index++) {
            callStack.add(frames[index]);
        }

        return callStack;
    }

    /**
     * @return  String - a display value for the given array
     */
    protected String getDisplayValue(Object[] things) {
        if (things == null)
            return "[null]"; //$NON-NLS-1$

        StringBuilder builder = new StringBuilder();
        builder.append("["); //$NON-NLS-1$
        boolean isFirst = true;

        for (Object thing : things) {
            if (!isFirst) {
                builder.append(", "); //$NON-NLS-1$
            }

            builder.append(thing.toString());
            isFirst = false;
        }

        builder.append("]"); //$NON-NLS-1$

        return builder.toString();
    }

    /**
     * @return  String - a display value for the given object
     */
    protected String getDisplayValue(Object obj) {
        return (obj == null) ? "null" //$NON-NLS-1$
            : obj.toString();
    }

    /**
     * @return  String - a display value for the given object
     */
    protected String getDisplayValue(Set<?> things) {
        return getDisplayValue(things.toArray());
    }

    /**
     * @return  String - If the given file name has the ".java" extension, then return the
     *          given string; Otherwise, append ".java" and return the new string
     */
    protected String getSafeJavaFileName(String name) {
        if (!name.endsWith(".java")) //$NON-NLS-1$
        {
            name = name + ".java"; //$NON-NLS-1$
        }

        return name;
    }

    /**
     * Get an instance of the shell monitor. The shell monitor can be used to handle
     * unexpected dialogs
     *
     * @param   ui  - Driver for UI generated input
     * @return  IShellMonitor - Instance of shell monitor for this
     *          {@link IUIContext}
     */
    protected IShellMonitor getShellMonitor(IUIContext ui) {
    	/*
    	 * FIXME: This is how we *want* to access the shell monitor but doing so breaks some
    	 * test lifecycle assumptions.
    	 * 
    	 * http://code.google.com/p/wt-commons/issues/detail?id=25
    	 */
//    	IShellMonitor sm = (IShellMonitor) ui.getAdapter(IShellMonitor.class);
//    	return sm;
    	return ShellMonitor.getInstance();
    }

    /**
     * Whenever the state of the workbench changes at the API level, this method must be
     * called prior to that call. The reason is that the state of the workbench can be
     * changed through the API in a way that is otherwise impossible through the UI (for
     * example, making a modal shell's parent the active window).
     *
     * <p>If you're not 100% sure that you need to be calling this method, then you
     * shouldn't be calling this method.</p>
     */
    protected void handleConditions(IUIContext ui) {
    	ui.handleConditions();
    }

    /**
     * You probably shouldn't be calling this. OS-Specific workarounds are a last-ditch
     * thing
     */
    protected boolean isLinux() {
        return Platform.getOS().equals(Platform.OS_LINUX);
    }

    /**
     * Start listening for the the given dialog and use the given dialog handler to react
     * to the dialog when it is shown. THIS IS FOR UNEXPACTED DIALOGS.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    public void listenForDialog(IUIContext ui, IWorkbenchShellHandler handler) {
        IShellCondition condition = new RegexTitleShellCondition(handler.getTitle(), handler.isModal());
        listenForDialog(ui, handler, condition);
    }

    /**
     * Start listening for the the given dialog and use the given dialog handler to react
     * to the dialog when it is shown. THIS IS FOR UNEXPACTED DIALOGS.
     *
     * @since 3.8.0
     * @param  ui         - Driver for UI generated input
     * @param  handler    - How to react to the shell
     * @param  condition  - How to tell if the given handler applies to a popped shell
     */
    public void listenForDialog(IUIContext ui,
                                IWorkbenchShellHandler handler,
                                IShellCondition condition) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(handler);
        TestCase.assertNotNull(condition);

        PlatformActivator.logDebug("[SHELL MONITOR] ADDING: " + handler.getClass().getCanonicalName(), //$NON-NLS-1$
                                   DEBUG_OPTION_SHELLHANDLERREG);

        if (!_registeredHandlers.keySet().contains(handler)) {
            PlatformActivator.logDebug("[SHELL MONITOR] +1 SHELL HANDLER: " + handler.getClass().getCanonicalName(), DEBUG_OPTION_SHELLHANDLERREG); //$NON-NLS-1$
            IShellMonitor shellMonitor = getShellMonitor(ui);
            shellMonitor.add(condition, handler);
            _registeredHandlers.put(handler, condition);
        }
    }

    /**
     * logEntry - Log a statement that indicates a method entry has occurred.
     *
     * @param       methodName  - The method name entered
     * @param       params      - The string representations of the parameters passed on
     *                          method entry
     * @deprecated  Use logEntry2 instead
     */
    protected void logEntry(String methodName, String... params) {
        if (PlatformActivator.isOptionEnabled(DEBUG_OPTION_ENTRY_EXIT)) {
            List<StackTraceElement> callStack = getCallStackBefore("logEntry"); //$NON-NLS-1$
            logEntry(methodName, callStack, params);
        }
    }

    /**
     * Overloaded to handle the different callstacks.
     */
    private void logEntry(String methodName, List<StackTraceElement> callStack,
                          String... params) {
        if (PlatformActivator.isOptionEnabled(DEBUG_OPTION_ENTRY_EXIT)) {
            StringBuilder message = new StringBuilder();
            message.append("[[ ENTERING: "); //$NON-NLS-1$
            message.append(getClass().getSimpleName());
            message.append("."); //$NON-NLS-1$
            message.append(methodName);
            message.append("("); //$NON-NLS-1$
            boolean first = true;

            for (String nextParam : params) {
                if (!first) {
                    message.append(", "); //$NON-NLS-1$
                }

                message.append(nextParam);
                first = false;
            }

            message.append(") ]]"); //$NON-NLS-1$

            PlatformActivator.logDebug(message.toString());
        }
    }

    /**
     * logEntry2 - Log a statement that indicates a method entry has occurred.
     *
     * @param  paramValues  - The string representations of the parameter values passed on
     *                      method entry
     */
    protected void logEntry2(String... paramValues) {
        if (PlatformActivator.isOptionEnabled(DEBUG_OPTION_ENTRY_EXIT)) {
            List<StackTraceElement> callStack = getCallStackBefore("logEntry2"); //$NON-NLS-1$
            String message = MessageFormat.format("THE GIVEN DEPTH <1> IS GREATER THAN THE TOTAL NUMBER OF FRAMES <{0}>", //$NON-NLS-1$
                                                  new Object[] {
                    Integer.toString(callStack.size())
                });
            TestCase.assertFalse(message, callStack.isEmpty());
            String methodName = callStack.get(0).getMethodName();
            logEntry(methodName, callStack, paramValues);
        }
    }

    /**
     * logExit - Log a statement that indicates a method exit has occurred.
     *
     * @param       methodName  - The method name exited
     * @deprecated  - Use logExit2 instead
     */
    protected void logExit(String methodName, String... returnValues) {
        if (PlatformActivator.isOptionEnabled(DEBUG_OPTION_ENTRY_EXIT)) {
            List<StackTraceElement> callStack = getCallStackBefore("logExit"); //$NON-NLS-1$
            logExit(methodName,
                    callStack,
                    returnValues);
        }
    }

    /**
     * Overload for call stack differences.
     */
    private void logExit(String methodName,
                         List<StackTraceElement> callStack,
                         String... returnValues) {
        if (PlatformActivator.isOptionEnabled(DEBUG_OPTION_ENTRY_EXIT)) {
            StringBuilder message = new StringBuilder();
            message.append("[[ EXITING: "); //$NON-NLS-1$
            message.append(getClass().getSimpleName());
            message.append("."); //$NON-NLS-1$
            message.append(methodName);
            message.append("--> "); //$NON-NLS-1$
            boolean isFirst = true;

            for (String nextRV : returnValues) {
                if (!isFirst) {
                    message.append(", "); //$NON-NLS-1$
                }

                message.append(nextRV);
                isFirst = false;
            }

            message.append("]]"); //$NON-NLS-1$
            PlatformActivator.logDebug(message.toString());
        }
    }

    /**
     * logExit2 - Log a statement that indicates a method exit has occurred.
     */
    protected void logExit2(String... returnValues) {
        if (PlatformActivator.isOptionEnabled(DEBUG_OPTION_ENTRY_EXIT)) {
            List<StackTraceElement> callStack = getCallStackBefore("logExit2"); //$NON-NLS-1$
            String message = MessageFormat.format("THE GIVEN DEPTH <1> IS GREATER THAN THE TOTAL NUMBER OF FRAMES <{0}>", //$NON-NLS-1$
                                                  new Object[] {
                    Integer.toString(callStack.size())
                });
            TestCase.assertFalse(message, callStack.isEmpty());
            String methodName = callStack.get(0).getMethodName();
            logExit(methodName, callStack, returnValues);
        }
    }

    /**
     * pressTab - Simple helper for pressing the enter key.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void pressEnter(IUIContext ui) {
        ui.keyClick(SWT.CR);
    }

    /**
     * pressTab - Simple helper for pressing the tab key.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void pressTab(IUIContext ui) {
        ui.keyClick(SWT.TAB);
    }

    /**
     * safeEnterText - Select all of the text in the entry field at the given
     * com.windowtester.swt.IUIContext key, then type the given String value into that
     * field.
     *
     * @param   ui                - Driver for UI generated input
     * @param   textFieldLocator  - The text field must be found with this locator. See
     *                            WidgetLocator or SWTWdigetLocator
     * @param   value             - The text to type into the text field
     * @throws  com.windowtester.swt.WidgetNotFoundException;       - If the text field
     *                                                              could not be uniquely
     *                                                              found
     * @throws  com.windowtester.swt.MultipleWidgetsFoundException  - If the text field
     *                                                              could not be uniquely
     *                                                              found
     */
    protected void safeEnterText(IUIContext ui,
                                 IWidgetLocator textFieldLocator,
                                 String value) throws WidgetSearchException {
        Control c = waitForControlEnabled(ui, textFieldLocator, Control.class);
        WidgetReference ref = new WidgetReference(c);

        ui.click(ref);

        selectAll(ui);

        //added duplicate for EAR project field on project creation, sometimes it's selected sometimes not
        ///duplicate select all does not affect any other scenario but fixes this inconsistancy
        selectAll(ui);

        if ((value != null) && (value.length() > 0)) {
            ui.enterText(value);
        } else {
            ui.keyClick(SWT.DEL);
        }
    }

    /**
     * selectAll - Simple helper for sending the keystroke to select all.
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI generated input
     */
    protected void selectAll(IUIContext ui) {
        int modKey = abbot.Platform.isOSX() ? WT.COMMAND : WT.CTRL;
        ui.keyClick(modKey, 'a');
    }

    /**
     * Utility for selecting a menu item in the file menu.
     *
     * @since 3.8.0
     * @param  ui        - Driver for UI generated input
     * @param  menuText  - The escaped menu text to select. Sub menus are accessed using a
     *                   forward slash to delimit the menu items
     */
    protected void selectFileMenuItem(IUIContext ui, String menuItem) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(menuItem);

        selectMenuItem(ui, "File", menuItem); //$NON-NLS-1$
    }

    /**
     * Utility for selecting a menu items.
     *
     * @since 3.8.0
     * @param  ui        - Driver for UI generated input
     * @param  root      - The top level menu text. For example, "&File"
     * @param  menuText  - The escaped menu text to select. Sub menus are accessed using a
     *                   forward slash to delimit the menu items
     */
    protected void selectMenuItem(IUIContext ui, String root, String menuItem) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(root);
        TestCase.assertNotNull(menuItem);

        // NEVER DO THIS!! WE DO THIS HERE BECAUSE WE WANT TO MAKE SURE
        // THE WORKBENCH HAS REPAINTED BEFORE SELECTING THE MENU ITEM
        new SWTIdleCondition().waitForIdle();
        ui.pause(2000); // For good measure

        try {
            ui.click(new MenuItemLocator(root + "/" + menuItem)); //$NON-NLS-1$
        } catch (WidgetSearchException wse) {
        	ExceptionHandler.handle(wse);
        }
    }

    /**
     * Utility for selecting a menu item in the project menu.
     *
     * @since 3.8.0
     * @param  ui        - Driver for UI generated input
     * @param  menuText  - The escaped menu text to select. Sub menus are accessed using a
     *                   forward slash to delimit the menu items
     */
    protected void selectProjectMenuItem(IUIContext ui, String menuItem) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(menuItem);

        selectMenuItem(ui, "&Project", menuItem); //$NON-NLS-1$
    }

    /**
     * Utility for selecting a table item by row and column.
     */
    protected void selectTableItem(IUIContext ui,
                                   Table table,
                                   int index,
                                   int column) {
        TestCase.assertNotNull(table);

        TableTester tableTester = new TableTester();
        TableItem[] items = tableTester.getItems(table);
        tableTester.actionSelectTableItem(table, items[index]);

        TableItemTester tableItemTester = new TableItemTester();
        tableItemTester.actionClickTableItem(items[index], column);

        new SWTIdleCondition().waitForIdle();
    }

    /**
     * setExpectedDelay - For certain operations that may cause a long running busy cursor
     * (keeping the UI thread from responding) all test harness safety nets need to be
     * notified that an operation will take a long time. The default for the UI thread
     * safety nets is 2 minutes. An example usage is the upgrade dialog where the test
     * thread waits on a condition until the upgrade is complete (upgrades can take much
     * longer than 2 minutes)
     *
     * @since 3.8.0
     * @param  ui      - Driver for UI generated input
     * @param  millis  - The number of milliseconds that the UI thread safety  nets should
     *                 expect to wait before failing the test. In other words, if the test
     *                 is still waiting on a single operation after this many millis then
     *                 fail the test.
     */
    protected void setExpectedDelay(IUIContext ui, long millis) {
        TestCase.assertNotNull(ui);

        IUIThreadMonitor monitor = (IUIThreadMonitor) ui.getAdapter(IUIThreadMonitor.class);
        TestCase.assertNotNull(monitor);

        monitor.expectDelay(millis);
    }

    /**
     * Stop listening for all unexpected dialogs. This is the complimentary method to the
     * listenForDialog method
     *
     * @since 3.8.0
     * @param  ui  - Driver for UI input
     */
    public void stopListeningForAllDialogs(IUIContext ui) {
        PlatformActivator.logDebug("[SHELL MONITOR] STOPPING *ALL* SHELLS!!", //$NON-NLS-1$
                                   DEBUG_OPTION_SHELLHANDLERREG);

        IShellMonitor shellMontitor = getShellMonitor(ui);
        shellMontitor.removeAll();
        _registeredHandlers.clear();
    }

    /**
     * stopListeningForDialog - Stop listening for the given dialog.
     *
     * @since 3.8.0
     * @param  ui       - Driver for UI generated input
     * @param  handler  - The shell handler that should no longer be handled through the
     *                  Shell Monitor. Should have been registered through the
     *                  listenForDialog method
     */
    public void stopListeningForDialog(IUIContext ui,
                                       IWorkbenchShellHandler handler) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(handler);

        IShellCondition condition = _registeredHandlers.get(handler);
        PlatformActivator.logDebug("[SHELL MONITOR] STOPPING: " + handler.getClass().getCanonicalName(), DEBUG_OPTION_SHELLHANDLERREG); //$NON-NLS-1$

        if (condition != null) {
            PlatformActivator.logDebug("[SHELL MONITOR] -1 SHELL HANDLER: " + handler.getClass().getCanonicalName(), DEBUG_OPTION_SHELLHANDLERREG); //$NON-NLS-1$
            IShellMonitor shellMontitor = getShellMonitor(ui);
            shellMontitor.remove(condition);
            _registeredHandlers.remove(handler);
        }
    }

    /**
     * Utility to calculate the given component's x,y coordinates relative to the first
     * parent shell on the widget's hierarchy.
     *
     * @return  int[] - int[0] = x coordinate; int[1] = y coordinate
     */
    protected int[] toShell(final IUIContext ui, final Control control) {
        if (control instanceof Shell) {
            return new int[] { 0, 0 };
        }

        final int[] bounds = new int[2];
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    Composite parent = control.getParent();
                    int[] toParent = toShell(ui, parent);

                    Rectangle cBounds = control.getBounds();
                    bounds[0] = toParent[0] + cBounds.x;
                    bounds[1] = toParent[1] + cBounds.y;
                }
            });

        return bounds;
    }

    /**
     * Wait for a control to be found. Do not assume a failure if the control is not
     * immediately found (do not close open dialogs, do not take a screenshot, etc). If
     * the control is not found after 30 seconds, THEN close all dialogs, take a
     * screenshot, fail the test, etc
     *
     * @since 3.8.0
     * @param  ui              - Driver for UI generated input
     * @param  controlLocator  - How to find the control
     * @param  controlClass    - The widget class to find
     */
    public static <T extends Control> T waitForControl(final IUIContext ui,
                                                       final IWidgetLocator controlLocator,
                                                       final Class<T> controlClass) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(controlLocator);
        TestCase.assertNotNull(controlClass);

        final List<T> control = new ArrayList<T>(1);
        ui.wait(new ICondition() {
                @SuppressWarnings("unchecked")
				public boolean test() {
                    boolean found = false;
                    IWidgetLocator[] references = ui.findAll(controlLocator);

                    if ((references != null) && (references.length == 1) && (references[0] instanceof WidgetReference)) {
                        Object asObj = ((WidgetReference) references[0]).getWidget();
                        found = controlClass.isAssignableFrom(asObj.getClass());

                        String message = MessageFormat.format("######## expected<{0}> actual<{1}> assignable<{2}>", //$NON-NLS-1$
                                                              new Object[] {
                                controlClass.getName(),
                                asObj.getClass(),
                                Boolean.toString(found)
                            });
                        PlatformActivator.logDebug(message, DEBUG_OPTION_WIDGET_SEARCH);

                        if (found) {
                            control.add((T) asObj);
                        }
                    } else {
                        try {
                            String message = MessageFormat.format("######## expected<{0}> BUT <{1}> WERE FOUND", //$NON-NLS-1$
                                                                  new Object[] {
                                    controlClass.getName(),
                                    ((references != null) ? Integer.toString(references.length)
                                                          : "0") //$NON-NLS-1$
                                });
                            PlatformActivator.logDebug(message, DEBUG_OPTION_WIDGET_SEARCH);
                        } catch (RuntimeException ex) {
                            ex.printStackTrace();
                            throw ex;
                        }
                    }

                    return found;
                }

                @Override
                public String toString() {
                    String message = MessageFormat.format("WAITING FOR THE CONTROL OF TYPE <{0}> TO BE FOUND WITH LOCATOR <{1}>", //$NON-NLS-1$
                                                          new Object[] {
                            controlClass.getName(), controlLocator.toString()
                        });

                    return message;
                }
            }, 15000, 250);

        return control.get(0);
    }

    /**
     * waitForControlEnabled - Wait for the control registered to the given key can be
     * found and is enabled. Do not fail the test if the control is not immediately found
     *
     * @param   ui              - Driver for UI generated input
     * @param   controlLocator  - How to find the control
     * @param   controlClass    - The control class to find
     * @return  T - The control instance found
     */
    public static <T extends Control> T waitForControlEnabled(final IUIContext ui,
                                                              final IWidgetLocator controlLocator,
                                                              final Class<T> controlClass) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(controlLocator);
        TestCase.assertNotNull(controlClass);

        // First, make sure its visible
        final T control = waitForControlVisible(ui, controlLocator, controlClass);

        final ControlTester tester = new ControlTester();
        ui.wait(new ICondition() {
                public boolean test() {
                    return tester.getEnabled(control);
                }

                @Override
                public String toString() {
                    String message = MessageFormat.format("WAITING FOR THE CONTROL OF TYPE <{0}> AND LOCATOR <{1}> TO BE ENABLED", //$NON-NLS-1$
                                                          new Object[] {
                            controlClass.getName(), controlLocator.toString()
                        });

                    return message;
                }
            });

        return control;
    }

    /**
     * waitForControlVisible - Wait for the control with the given info can be found and
     * is visible. Do not fail the test if the control is not immediately found
     *
     * @param   ui         - Driver for UI generated input
     * @param   isLocator  - How to find the is
     * @param   isClass    - The is class to find
     * @return  T - The is instance found
     */
    public static <T extends Control> T waitForControlVisible(IUIContext ui,
                                                              final IWidgetLocator isLocator,
                                                              Class<T> isClass) {
        TestCase.assertNotNull(ui);
        TestCase.assertNotNull(isLocator);
        TestCase.assertNotNull(isClass);

        final T control = waitForControl(ui, isLocator, isClass);

        final ControlTester tester = new ControlTester();
        ui.wait(new ICondition() {
                public boolean test() {
                    return tester.isVisible(control);
                }
            });

        return control;
    }
    
	/**
	 * Wait for SWT Idle.
	 * @since 3.8.0
	 */
	protected static void waitForIdle(IUIContext ui) throws WaitTimedOutException {
		/*
		 * This condition replaces earlier deprecated wait for idle strategies.
		 * Since idle waits have historically been a hotspot for timing issues
		 * this is a good place to look if we see timing-related regressions.
		 */
		ui.wait(new SWTIdleCondition());
	}

}
