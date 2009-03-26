/**
 * WT Commons Project 2008-2009
 *
 * http://code.google.com/p/wt-commons/wiki/WTSamples
 */
package org.wtc.eclipse.draw2d.helpers;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.XYLocator;
import junit.framework.TestCase;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.wtc.eclipse.draw2d.Draw2DActivator;
import org.wtc.eclipse.draw2d.tester.FigureTester;

public class DragAndDropHelperImpl {
    /**
     * First scroll the source and target FigureCanvases as needed to ensure the source
     * and target IFigures are showing. Then move the mouse over the source IFigure and
     * drag and drop it on to the target IFigure
     *
     * @param  ui            - driver for ui generated input
     * @param  sourceCanvas  - the FigureCanvas containing the source IFigure
     * @param  source        - the IFigure to be dragged and dropped
     * @param  targetCanvas  - the FigureCanvas containing the target IFigure
     * @param  target        - the IFigure on which to drop the source
     */
    public void dragAndDrop(IUIContext ui,
                            FigureCanvas sourceCanvas,
                            IFigure source,
                            FigureCanvas targetCanvas,
                            IFigure target) {
        TestCase.assertNotNull(sourceCanvas);
        TestCase.assertNotNull(source);
        TestCase.assertNotNull(targetCanvas);
        TestCase.assertNotNull(target);

        // first scroll the source and target canvases
        // as necessary to show the figures
        FigureTester figureTester = new FigureTester();
        figureTester.scrollForClick(sourceCanvas, source);
        figureTester.scrollForClick(targetCanvas, target);

        // compute the source screen location
        Rectangle sourceRect = source.getBounds().getCopy();
        source.translateToAbsolute(sourceRect);

        // compute the target screen location
        Rectangle targetRect = source.getBounds().getCopy();
        target.translateToAbsolute(targetRect);

        try {
            // move the mouse over the source
            ui.mouseMove(new XYLocator(sourceRect.x, sourceRect.y));

            //drag and drop on the target
            ui.dragTo(new XYLocator(targetRect.x, targetRect.y));
        }
        // SHOULD NEVER HAPPEN BECAUSE WE'RE USING THE XYLOCATOR
        catch (WidgetSearchException ex) {
            Draw2DActivator.logException(ex);
            TestCase.fail(ex.getLocalizedMessage());
        }
    }
}
