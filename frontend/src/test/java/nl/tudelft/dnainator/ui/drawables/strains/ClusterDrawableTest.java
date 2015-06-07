package nl.tudelft.dnainator.ui.drawables.strains;

import nl.tudelft.dnainator.core.SequenceNode;
import nl.tudelft.dnainator.ui.ColorServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Test the drawable class that is used for clusters.
 * This class contains a certain amount of clustered sequencenodes,
 * and will dynamically change its size based on that amount. 
 */
@RunWith(MockitoJUnitRunner.class)
public class ClusterDrawableTest {
	@Mock private ColorServer colorserver;
	@Mock private SequenceNode node;
	
	private ClusterDrawable cluster;
	
	/**
	 * Set up common mocks and variables.
	 */
	@Before
	public void setup() {
		Set<String> sources = new HashSet<>();
		sources.add("1");
		Mockito.when(node.getSources()).thenReturn(sources);
	}
	
	/**
	 * Test creation of a cluster with a single node.
	 */
	@Test
	public void testCreate() {
		cluster = new ClusterDrawable(colorserver, Arrays.asList(node));
		
		// CHECKSTYLE.OFF: MagicNumber
		Mockito.verify(colorserver, Mockito.times(1)).getColor("1");
		assertEquals(ClusterDrawable.SINGLE_RADIUS, cluster.getRadius(), .001);
		assertEquals(1, cluster.getClustered().size());
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Test creation of a cluster with a small cluster.
	 */
	@Test
	public void testCreateSmall() {
		cluster = new ClusterDrawable(colorserver, Arrays.asList(node, node, node));
		
		// CHECKSTYLE.OFF: MagicNumber
		Mockito.verify(colorserver, Mockito.times(1)).getColor("1");
		assertEquals(ClusterDrawable.SMALL_RADIUS, cluster.getRadius(), .001);
		assertEquals(3, cluster.getClustered().size());
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Test creation of a cluster with a medium cluster.
	 */
	@Test
	public void testCreateMedium() {
		cluster = new ClusterDrawable(colorserver, Arrays.asList(node, node, node, node));
		
		// CHECKSTYLE.OFF: MagicNumber
		Mockito.verify(colorserver, Mockito.times(1)).getColor("1");
		assertEquals(ClusterDrawable.MEDIUM_RADIUS, cluster.getRadius(), .001);
		assertEquals(4, cluster.getClustered().size());
		// CHECKSTYLE.ON: MagicNumber
	}
	
	/**
	 * Test creation of a cluster with a large cluster.
	 */
	@Test
	public void testCreateLarge() {
		cluster = new ClusterDrawable(colorserver, Arrays.asList(node, node, node, node, node,
									node, node, node, node, node, node));
		
		// CHECKSTYLE.OFF: MagicNumber
		Mockito.verify(colorserver, Mockito.times(1)).getColor("1");
		assertEquals(ClusterDrawable.LARGE_RADIUS, cluster.getRadius(), .001);
		assertEquals(11, cluster.getClustered().size());
		// CHECKSTYLE.ON: MagicNumber
	}
}
