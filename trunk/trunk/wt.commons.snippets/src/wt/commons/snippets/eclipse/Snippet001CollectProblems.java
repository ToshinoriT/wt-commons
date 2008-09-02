package wt.commons.snippets.eclipse;

import static com.windowtester.runtime.swt.locator.SWTLocators.treeItem;
import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.eclipse.ProjectExistsCondition;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.experimental.condition.eclipse.ActiveEditorCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;

/**
 * A snippet that demonstrates accessing problems in the Eclipse 
 * problems view.
 * <p>
 * {@link #oneTimeSetup()} creates a java class with errors and {@link #testGetAllProblems()} 
 * demonstrates accessing the resulting errors.
 * <p>
 * To run this snippet, you may need to increase your heap size.  It was developed using the following VM
 * args:
 * <pre>
 * -Xms256m
 * -Xmx512m
 * </pre>
 * 
 * 
 * @author Phil Quitslund
 *
 */
public class Snippet001CollectProblems extends UITestCaseSWT {

		
	//NOTE: modification required for Eclipse 3.2
	private static final String SEP_SRC_FOLDER_CREATION_LABEL = /* EclipseUtil.isVersion_32() ? "&Create separate source and output folders" : */ "&Create separate folders for sources and class files";

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.common.UITestCaseCommon#oneTimeSetup()
	 */
	@Override
	protected void oneTimeSetup() throws Exception {
		closeWelcomePageIfNecessary();
		createProject();
		createClass();
		addContents();
		save();
		openProblemsView();
		expandProblemTree();
	}
	
	private void closeWelcomePageIfNecessary() throws WidgetSearchException {
		IWidgetLocator[] welcomeTab = getUI().findAll(new CTabItemLocator("Welcome"));
		if (welcomeTab.length == 0)
			return;
		getUI().close(welcomeTab[0]);
	}
	

	private void createProject() throws WidgetSearchException {
		createJavaProject(getProjectName());
	}
	
	private void createJavaProject(String projectName) throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("Next >"));
		ui.enterText(projectName);
		ui.click(new ButtonLocator(SEP_SRC_FOLDER_CREATION_LABEL));
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		ui.wait(new ProjectExistsCondition(projectName, true));
	}
	
	private String getProjectName() {
		return getClass().getSimpleName() + "Project";
	}

	private void createClass() throws WidgetSearchException {
		createJavaClass(getProjectName() + "/src", getJavaClassName());
	}
	
	
	//NOTE: assumes target project is selected...
	private void createJavaClass(String sourceFolder, String className) throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Class"));
		ui.wait(new ShellShowingCondition("New Java Class"));
		ui.click(2, new LabeledTextLocator("Source fol&der:"));
		ui.enterText(sourceFolder);
		ui.click(2, new LabeledTextLocator("Na&me:"));
		ui.enterText(className);
		ui.assertThat(new ButtonLocator("Finish").isEnabled());
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Java Class"));
		ui.wait(ActiveEditorCondition.forName(className + ".java"));
	}
	
	
	private String getJavaClassName() {
		return "TestClass";
	}
	
	private void addContents() {
		getUI().enterText("bang!");
	}
	
	private void save() throws WidgetSearchException {
		getUI().click(new MenuItemLocator("File/Save"));
	}
	
	private void openProblemsView() throws WidgetSearchException {
		IUIContext ui = getUI();
		if (view("Problems").isVisible().testUI(ui))
			return;
		ui.click(new MenuItemLocator("&Window/Show &View/&Other.*")); //3.* safe path
		ui.wait(new ShellShowingCondition("Show View"));
		ui.click(new TreeItemLocator("(General|Basic)/Problems"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(view("Problems").isVisible());
	}

	private void expandProblemTree() throws WidgetSearchException {
		IWidgetReference errorNode = ((IWidgetReference) getUI().find(treeItem("Errors .*").in(view("Problems"))));
		TreeItem errorItem = (TreeItem) errorNode.getWidget();
		if (!isExpanded(errorItem))
			expand(errorNode);
	}

	private void expand(final IWidgetReference errorNode) throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(errorNode);
		ui.keyClick(WT.ARROW_RIGHT);
		ui.wait(new ICondition() {
			public boolean test() {
				return isExpanded((TreeItem) errorNode.getWidget());
			}
		});
	}

	private boolean isExpanded(final TreeItem item) {
		final boolean[] expanded = new boolean[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				expanded[0] = item.getExpanded();
			}
		});
		return expanded[0];
	}



	/**
	 * Get all of the problem items in the problems view.
	 * @throws WidgetSearchException 
	 */
	public void testGetAllErrors() throws WidgetSearchException {
		IUIContext ui = getUI();
		IWidgetLocator[] locators = ui.findAll(treeItem("Errors .*/.*").in(view("Problems")));		
		for (IWidgetLocator locator : locators) {
		  TreeItem item = (TreeItem) ((IWidgetReference)locator).getWidget();
		  inspectItem(item);
		}
	}


	//NOTE: be sure to do this from the UI thread
	private void inspectItem(final TreeItem item) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				System.out.println(item);
			}
		});
	}
	
	
	
	
	
	
	
}
