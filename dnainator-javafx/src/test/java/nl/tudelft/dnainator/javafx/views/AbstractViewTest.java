package nl.tudelft.dnainator.javafx.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import nl.tudelft.dnainator.graph.Graph;
import nl.tudelft.dnainator.javafx.ColorServer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class AbstractViewTest {

	private AbstractView abstractView;
	@Mock private Graph graph;
	@Mock private ColorServer colorServer;
	@Mock private Group group;
	
	/**
	 * Set up common variables.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		// CHECKSTYLE.OFF: MagicNumber
		Mockito.when(graph.getMaxBasePairs()).thenReturn(0);
		// CHECKSTYLE.ON: MagicNumber
		abstractView = new StrainView(colorServer, graph);
	}
	
	/**
	 * Tests the default set scale.
	 */
	@Test
	public void testGetScale() {
		// CHECKSTYLE.OFF: MagicNumber
		assertEquals(0.5, abstractView.getScale().getMxx(), 0.001);
		assertEquals(0.5, abstractView.getScale().getMyy(), 0.001);
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Check whether three transforms were added for zooming, panning.
	 */
	@Test
	public void testSetTransforms() {
		abstractView.setTransforms(group);
		// CHECKSTYLE.OFF: MagicNumber
		assertEquals(3, group.getTransforms().size());
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Check whether panning changes the translation correctly.
	 */
	@Test
	public void testPan() {
		// CHECKSTYLE.OFF: MagicNumber
		abstractView.pan(new Point2D(1.0, 2.0));
		assertEquals(1.0, abstractView.translate.getX(), 0.001);
		assertEquals(2.0, abstractView.translate.getY(), 0.001);
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Tests whether zooming in makes the scale larger.
	 */
	@Test
	public void testZoomIn() {
		double zoomPrevious = abstractView.scale.getMxx();
		// CHECKSTYLE.OFF: MagicNumber
		abstractView.zoomInScroll(1.0, 2.0);
		// CHECKSTYLE.ON: MagicNumber
		assertTrue(abstractView.scale.getMxx() > zoomPrevious);
		assertTrue(abstractView.scale.getMyy() > zoomPrevious);

		double zoomPrevious2 = abstractView.scale.getMxx();
		abstractView.zoomIn();
		assertTrue(abstractView.scale.getMxx() > zoomPrevious2);
		assertTrue(abstractView.scale.getMyy() > zoomPrevious2);
	}
	
	/**
	 * Tests whether zooming out makes the scale smaller.
	 */
	@Test
	public void testZoomOut() {
		double zoomPrevious = abstractView.scale.getMxx();
		// CHECKSTYLE.OFF: MagicNumber
		abstractView.zoomOutScroll(1.0, 2.0);
		// CHECKSTYLE.ON: MagicNumber
		assertTrue(abstractView.scale.getMxx() < zoomPrevious);
		assertTrue(abstractView.scale.getMyy() < zoomPrevious);

		double zoomPrevious2 = abstractView.scale.getMxx();
		abstractView.zoomOut();
		assertTrue(abstractView.scale.getMxx() < zoomPrevious2);
		assertTrue(abstractView.scale.getMyy() < zoomPrevious2);
	}
	
	/**
	 * Check wheter resetting the panning puts the translation back to 0.
	 */
	@Test
	public void resetPan() {
		abstractView.pan(new Point2D(1.0, 2.0));
		assertEquals(1.0, abstractView.translate.getX(), 0.001);
		assertEquals(2.0, abstractView.translate.getY(), 0.001);
		
		abstractView.resetTranslate();
		// CHECKSTYLE.OFF: MagicNumber
		assertEquals(0, abstractView.translate.getX(), 0.001);
		assertEquals(0, abstractView.translate.getY(), 0.001);
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Check wheter resetting the zoom puts the scaling back to its original.
	 */
	@Test
	public void resetZoom() {
		double zoomPrevious = abstractView.scale.getMxx();
		abstractView.zoomIn();
		assertTrue(abstractView.scale.getMxx() > zoomPrevious);
		
		abstractView.resetZoom();
		// CHECKSTYLE.OFF: MagicNumber
		assertEquals(0, abstractView.scale.getTx(), 0.001);
		assertEquals(0, abstractView.scale.getTy(), 0.001);
		// CHECKSTYLE.ON: MagicNumber
	}
}
