package wt.commons.samples.gmf.taipan.tests;

import wt.commons.samples.gmf.taipan.BaseTaipanTest;



/**
 * A test that drives the GMF Taipan example through some basic paces.
 * <p>
 * To run this test, be sure you have the <a href="http://wiki.eclipse.org/index.php/GMF_Tutorial">Taipan example</a> installed.  
 *
 * <p>
 * <b>Note:</b> this sample code is provided <em>as is</em>.  If you would like 
 * to contribute fixes or extensions, please send a note to wintest-support@instantiations.com.  
 * Thanks!
 * 
 * @author Phil Quitslund
 *
 */
public class TaipanDrivingTest extends BaseTaipanTest {

 
	public void testExampleOne() throws Exception {
		
		createNewProject();
		createTaskletFile();
		setZoom();
		
		drawPort("Port1", 50, 100);
		drawPort("Port2", 350, 100);
		drawPort("Port3", 200, 300);

		drawRouteFromPortToPort("Route1", "Port1", "Port2");
		assertReliabilityEquals(0.8);
		
		setReliabity(0.5);
		assertReliabilityEquals(0.5);
		
		setDestinationPort("Port1");
			
		drawRouteFromPortToPort("Route2", "Port2", "Port3");
		setSourcePort("Port3");
		
		drawRouteFromPortToPort("Route3", "Port3", "Port1");
		drawShipDestination("Invalid destination", 220, 320, 50, 100);
     
		validateDiagram();
		
	}





}
