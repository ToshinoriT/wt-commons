package wt.commons.samples.gmf.taipan;

import static com.windowtester.runtime.swt.locator.SWTLocators.column;
import static com.windowtester.runtime.swt.locator.SWTLocators.treeCell;
import static com.windowtester.runtime.swt.locator.SWTLocators.treeItem;
import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;
import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.helpers.GEFDebugHelper;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.locator.MenuItemLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CComboItemLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.util.StringComparator;
import com.windowtester.swt.util.DebugHelper;

/**
 * A helper for creating and interacting with TaiPan Diagrams.
 * <p>
 * <b>Note:</b> this sample code is provided <em>as is</em>.  If you would like 
 * to contribute fixes or extensions, please send a note to wintest-support@instantiations.com.  
 * Thanks!
 *
 * @author Phil Quitslund
 *
 */
public class TaipanHelper {

    private final IUIContext ui;
	
	public TaipanHelper(IUIContext ui) {
		this.ui = ui;
	}
    
  
	public void setReliabity(double reliability) throws Exception {
		setTextProperty("Reliability", Double.toString(reliability));
	}

	public void dumpFigures() throws WidgetSearchException {
		FigureCanvas canvas = (FigureCanvas) ((IWidgetReference)ui.find(new FigureCanvasLocator())).getWidget();
		new GEFDebugHelper().printFigures(canvas);
	}
	


	public void assertReliabilityEquals(double expectedReliability) throws WidgetSearchException {
		assertPropertyEquals("Reliability", Double.toString(expectedReliability));
	}

	public void assertPropertyEquals(String propertyName, String expectedValue) throws WidgetSearchException {
		//TODO[pq]: TreeItemLocator should support hasText; once it does this should work:
		//ui.assertThat((treeCell(propertyName).at(column(1)).in(view("Properties"))).hasText("0.8"));
		
		//*sigh*  ... in the meantime, we have to do something ugly:
		IWidgetReference treeItemRef = (IWidgetReference) ui.find(treeItem(propertyName).in(view("Properties")));
		TreeItem item = (TreeItem) treeItemRef.getWidget();
		assertEquals(expectedValue, getTreeItemColumnText(item, 1));
	}


	private String getTreeItemColumnText(final TreeItem item, final int columnIndex) {
		final String[] text = new String[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				text[0] = item.getText(columnIndex);
			}
		});
		return text[0];
	}

	public void createNewProject() throws Exception {
        ui.click(new MenuItemLocator("File/New/Project..."));
        ui.wait(new ShellShowingCondition("New Project"));
        ui.click(new FilteredTreeItemLocator(
            "General/Project"));
        ui.click(new ButtonLocator("&Next >"));
        
        ui.click(new LabeledTextLocator("&Project name:"));
        ui.enterText("Taipan");
        
        ui.click(new ButtonLocator("&Finish"));
        ui.wait(new ShellDisposedCondition("New Project"));
    }

    public void createTaskletFile() 
    throws Exception {
        ui.contextClick(new TreeItemLocator(
                "Taipan", 
                new ViewLocator("org.eclipse.jdt.ui.PackageExplorer")), 
                "New/Other...");
        ui.wait(new ShellShowingCondition("New"));
        ui.click(1, new FilteredTreeItemLocator("Examples/TaiPan Diagram"), WT.SHIFT);
        ui.click(new ButtonLocator("&Next >"));
        ui.click(new ButtonLocator("&Finish"));
        ui.wait(new ShellDisposedCondition("New TaiPan Diagram"));
        //Show Properties view
        ui.contextClick(
            new FigureCanvasXYLocator(10, 10), "S&how Properties View");

    }

    public void drawPort(String name,
    						int x, int y) throws Exception {
    	ui.click(new PaletteItemLocator("Port"));
    	ui.click(new FigureCanvasXYLocator(x, y));
    	if (name != null) {
    		ui.enterText(name);
    		ui.keyClick(WT.CR);
    	}

    }
    
    
    public void drawRouteFromPortToPort(String name, String fromPortName,
			String toPortName) throws Exception {

		// TODO - *This would be far better, if we could draw a route between
		// 2 ports using the port names to find the from and to locations
		// how could we use the NamedEditPartFigureLocator or NamedFigureLocator

    	

    	
		ui.click(new PaletteItemLocator("Reliable Route"));
		ui.pause(2000);
		ui.mouseMove(xy(port(fromPortName), 10, 10));
		ui.pause(2000);
		ui.dragTo(xy(port(toPortName), 10, 10));

		if (name != null) {
			ui.enterText(name);
			ui.keyClick(WT.CR);
		}
	}
    

	public ILocator xy(ILocator port, int x, int y) {
		return new XYLocator(port, x, y);
	}


    //TODO[pq] :: GMF Figures require coordinate mapping???
	public ILocator port(final String portName) {
		
		return new FigureLocator(new IFigureMatcher() {
			IFigureMatcher classMatcher = new ByClassNameFigureMatcher("org.eclipse.gmf.runtime.diagram.ui.internal.figures.BorderItemContainerFigure");
			@Override
			public boolean matches(IFigureReference figureRef) {
				
				//return isWrappingLabelWithText(figureRef.getFigure(), portName);
				return isBorderContainerFigure(figureRef) && childFigureHasText(figureRef, portName);
				//TODO[pq]: could further refine by inspecting the editpart
			}
			@SuppressWarnings("unchecked")
			private boolean childFigureHasText(IFigureReference figureRef, String portName) {
				IFigure figure = figureRef.getFigure();
				List children = figure.getChildren();
				if (children.isEmpty())
					return false;
				IFigure child = (IFigure) children.get(0);
				return isWrappingLabelWithText(child, portName);
			}
			private boolean isWrappingLabelWithText(
					IFigure figure, String portName) {
				if (!(figure instanceof WrappingLabel))
					return false;
				WrappingLabel label = (WrappingLabel)figure;
				return StringComparator.matches(label.getText(), portName);
			}
			protected boolean isBorderContainerFigure(IFigureReference figureRef) {
				return classMatcher.matches(figureRef);
			}
		});
	}



	public void drawShipDestination(String name,
			 int fromX, int fromY,
			 int toX, int toY) throws Exception {

    	//TODO - a ship destination can only be drawn from a ship to a port. This is
    	//       done in GMF using constraints. In the only call to this method, we are
    	//       attempting to draw from a port. Can we assert that 
    	//       this is not being allowed? Notice no link is drawn as a result 

    	ui.click(new PaletteItemLocator("Ship Destination"));
    	ui.mouseMove(new FigureCanvasXYLocator(fromX, fromY));
    	ui.dragTo(new FigureCanvasXYLocator(toX, toY));

    	if (name != null) {
    		ui.enterText(name);
    		ui.keyClick(WT.CR);
    	}
    }
    
    public void setZoom() throws Exception {

    	//TODO By changing the zoom, throws off the x/y of above methods
    	//     we are currently compensating for this, but is there a better way?
        ui.click(new PaletteItemLocator("Zoom Out"));
        ui.click(new FigureCanvasXYLocator(10, 10)); //75%
        ui.click(new FigureCanvasXYLocator(10, 10)); //50%
        ui.click(new PaletteItemLocator("Select"));
        
        //TODO Is there a better way to set the zoom to an actual value. We tried
        //     entering a value in the zoom % box on the tool bar when a diagram 
        //     is being edited, but were unable to use windowTesterto enter value 
        //     for zoom
        
    }
    
    public void setStringProperty(String propertName, 
            String propertyValue) throws Exception {

    	ui.click(new CTabItemLocator("Properties"));
    	ui.click(new TreeItemLocator(propertName, new ViewLocator(
    		"org.eclipse.ui.views.PropertySheet")));

    	ui.enterText(propertyValue);
    	ui.keyClick(WT.CR);
    	ui.keyClick(WT.CR);
    }

    /**
     * Set a combo list property in the property view.
     * 
     * @param propertyName - name of property
     * @param propertyValue - value in list to set to
     * @throws WidgetSearchException 
     * @throws WidgetSearchException - exception
     */
    public void setComboProperty(String propertyName, 
                                   String propertyValue) throws WidgetSearchException  {

        ui.click(new CTabItemLocator("Properties"));
        ui.click(new TreeItemLocator(propertyName, 
            new ViewLocator("org.eclipse.ui.views.PropertySheet")));

        
        ui.click(treeCell(propertyName).at(column(1)).in(view("Properties")));

        
        new DebugHelper().printWidgets();  
        
        //TODO[pq]: removing the CCombo parent should work but does not...        
        ui.click(new CComboItemLocator(propertyValue, new SWTWidgetLocator(CCombo.class, new ViewLocator("org.eclipse.ui.views.PropertySheet"))));

    }
    
    
    public void setTextProperty(String propertyName, String propertyValue)
			throws Exception {

		ui.click(new CTabItemLocator("Properties"));
		ui.click(new TreeItemLocator(propertyName, new ViewLocator(
				"org.eclipse.ui.views.PropertySheet")));

		ui.click(treeCell(propertyName).at(column(1)).in(view("Properties")));

		new DebugHelper().printWidgets();

		ui.enterText(propertyValue);
		ui.keyClick(WT.CR);
	}
    
    
    public void validateDiagram() throws Exception {
		ui.click(new MenuItemLocator("Diagram/Validate"));
		ui.click(new CTabItemLocator("Problems"));

		//TODO Is there a way to assert the port object with no name has a error marker on it?
		
		//TODO Not GEF, but how can we assert the error below exists before
		//     clicking on it

		ui.click(2, new TreeItemLocator(
				"Errors (1 item)/Port location should not be empty",
				new ViewLocator("org.eclipse.ui.views.ProblemView")));
		
		//TODO after the double click above on the error, how do we assert the 
		//     correct object in diagram was selected

        ui.click(new CTabItemLocator("Properties"));
        
    }



	public void setDestinationPort(String portName) throws WidgetSearchException {
		setComboProperty("Destination", prefixedPort(portName));
	}

	public void setSourcePort(String portName) throws WidgetSearchException {
		setComboProperty("Source", prefixedPort("Port3"));
	}
	

	private String prefixedPort(String portName) {
		return "Port " + portName;
	}

}
