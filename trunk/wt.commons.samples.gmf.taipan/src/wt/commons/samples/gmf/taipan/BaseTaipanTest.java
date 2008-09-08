package wt.commons.samples.gmf.taipan;

import wt.commons.samples.BaseTest;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;

/**
 * A base test for Taipan diagram tests.
 * <p>
 * <b>Note:</b> this sample code is provided <em>as is</em>.  If you would like 
 * to contribute fixes or extensions, please send a note to wintest-support@instantiations.com.  
 * Thanks!
 *
 * @author Phil Quitslund
 *
 */
public class BaseTaipanTest extends BaseTest {

	   //main UI context
    protected IUIContext ui;
	protected TaipanHelper taipan;
	
    /**
     * {@inheritDoc}
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        ui = getUI();
        taipan = new TaipanHelper(ui);
        super.setUp();
    }
	
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
    	saveAllIfNecessary();
    }
    

	protected void setReliabity(double reliability) throws Exception {
		taipan.setTextProperty("Reliability", Double.toString(reliability));
	}


	protected void assertReliabilityEquals(double expectedReliability) throws WidgetSearchException {
		taipan.assertPropertyEquals("Reliability", Double.toString(expectedReliability));
	}

	protected void assertPropertyEquals(String propertyName, String expectedValue) throws WidgetSearchException {
		taipan.assertPropertyEquals(propertyName, expectedValue);
	}


	protected void createNewProject() throws Exception {
       taipan.createNewProject();
    }

    protected void createTaskletFile() 
    throws Exception {
    	taipan.createTaskletFile();
    }

    protected void drawPort(String name,
    						int x, int y) throws Exception {
    	taipan.drawPort(name, x, y);
    }
    
    
    protected void drawRouteFromPortToPort(String name, String fromPortName,
			String toPortName) throws Exception {
    	taipan.drawRouteFromPortToPort(name, fromPortName, toPortName);
	}
    

	protected void drawShipDestination(String name,
			 int fromX, int fromY,
			 int toX, int toY) throws Exception {
		taipan.drawShipDestination(name, fromX, fromY, toX, toY);
    }
    
    protected void setZoom() throws Exception {
    	taipan.setZoom();
    }
    
    protected void setStringProperty(String propertName, 
            String propertyValue) throws Exception {
    	taipan.setStringProperty(propertName, propertyValue);
    }

	protected void setDestinationPort(String portName) throws WidgetSearchException {
		taipan.setDestinationPort(portName);
	}
    
	
	protected void setSourcePort(String portName) throws WidgetSearchException {
		taipan.setSourcePort(portName);
	}

    protected void setTextProperty(String propertyName, String propertyValue)
			throws Exception {
    	taipan.setTextProperty(propertyName, propertyValue);
	}
    
    
    protected void validateDiagram() throws Exception {
    	taipan.validateDiagram();
    }
	
}
