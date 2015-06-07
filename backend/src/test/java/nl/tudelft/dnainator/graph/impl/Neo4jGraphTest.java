package nl.tudelft.dnainator.graph.impl;

import nl.tudelft.dnainator.core.SequenceNode;
import nl.tudelft.dnainator.core.impl.Edge;
import nl.tudelft.dnainator.core.impl.SequenceNodeFactoryImpl;
import nl.tudelft.dnainator.core.impl.SequenceNodeImpl;
import nl.tudelft.dnainator.graph.impl.command.RankCommand;
import nl.tudelft.dnainator.graph.query.GraphQueryDescription;
import nl.tudelft.dnainator.parser.EdgeParser;
import nl.tudelft.dnainator.parser.NodeParser;
import nl.tudelft.dnainator.parser.exceptions.InvalidEdgeFormatException;
import nl.tudelft.dnainator.parser.exceptions.ParseException;
import nl.tudelft.dnainator.parser.impl.EdgeParserImpl;
import nl.tudelft.dnainator.parser.impl.NodeParserImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.io.fs.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import static nl.tudelft.dnainator.graph.impl.PropertyTypes.ID;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test Neo4j graph implementation.
 */
public class Neo4jGraphTest {
	private static final String DB_PATH = "target/neo4j-junit";
	private static Neo4jGraph db;
	private static InputStream nodeFile;
	private static InputStream edgeFile;

	/**
	 * Setup the database and construct the graph.
	 */
	@BeforeClass
	public static void setUp() {
		try {
			FileUtils.deleteRecursively(new File(DB_PATH));
			nodeFile = Neo4jGraphTest.class.getResourceAsStream("/strains/topo.node.graph");
			edgeFile = Neo4jGraphTest.class.getResourceAsStream("/strains/topo.edge.graph");
			//nodeFile = new File("10_strains_graph/simple_graph.node.graph");
			//edgeFile = new File("10_strains_graph/simple_graph.edge.graph");
			NodeParser np = new NodeParserImpl(new SequenceNodeFactoryImpl(),
					new BufferedReader(new InputStreamReader(nodeFile, "UTF-8")));
			EdgeParser ep = new EdgeParserImpl(new BufferedReader(
							new InputStreamReader(edgeFile, "UTF-8")));
			new Neo4jBatchBuilder(DB_PATH).constructGraph(np, ep);
			db = new Neo4jGraph(DB_PATH);
		} catch (IOException e) {
			fail("Couldn't initialize DB");
		} catch (ParseException e) {
			fail("Couldn't parse file: " + e.getMessage());
		}
	}

	/**
	 * Test looking up a single node.
	 */
	@Test
	public void testNodeLookup() {
		// CHECKSTYLE.OFF: MagicNumber
		SequenceNode node1 = new SequenceNodeImpl("2", Arrays.asList("ASDF"), 1, 5, "TATA");
		SequenceNode node2 = new SequenceNodeImpl("3", Arrays.asList("ASDF"), 5, 9, "TATA");
		SequenceNode node3 = new SequenceNodeImpl("5", Arrays.asList("ASDF"), 4, 8, "TATA");
		assertEquals(node1, db.getNode("2"));
		assertEquals(node2, db.getNode("3"));
		assertEquals(node3, db.getNode("5"));
		// CHECKSTYLE.ON: MagicNumber
	}

	/**
	 * Test looking up one of the roots of the tree.
	 */
	@Test
	public void testRootLookup() {
		// CHECKSTYLE.OFF: MagicNumber
		SequenceNode root = new SequenceNodeImpl("5", Arrays.asList("ASDF"), 4, 8, "TATA");
		assertEquals(root, db.getRootNode());
		// CHECKSTYLE.ON: MagicNumber
	}

	/**
	 * Unit-test the topological ordering.
	 */
	@Test
	public void testTopologicalOrder() {
		LinkedList<Integer> order = new LinkedList<>();
		try {
			EdgeParser ep = new EdgeParserImpl(new BufferedReader(
							new InputStreamReader(edgeFile, "UTF-8")));

			db.execute(e -> {
				for (Node n : new RankCommand(db.rootIterator()).topologicalOrder(e)) {
					order.add(Integer.parseInt((String) n.getProperty(ID.name())));
				}
			});
			while (ep.hasNext()) {
				Edge<String> next = ep.next();
				int source = Integer.parseInt(next.getSource());
				int dest = Integer.parseInt(next.getDest());
				assertThat(order.indexOf(source), lessThan(order.indexOf(dest)));
			}
		} catch (NumberFormatException e) {
			fail("Number format incorrect " + e.getMessage());
			e.printStackTrace();
		} catch (InvalidEdgeFormatException e) {
			fail("Edge format incorrect " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			fail("Error during I/O " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Tests the rank attributes for correctness.
	 */
	@Test
	public void testRanks() {
		Set<String> rank0Expect = new HashSet<>();
		Collections.addAll(rank0Expect, "7", "5", "3");
		assertUnorderedIDEquals(rank0Expect, db.getRank(0));

		Set<String> rank1Expect = new HashSet<>();
		Collections.addAll(rank1Expect, "11", "8");
		assertUnorderedIDEquals(rank1Expect, db.getRank(1));

		Set<String> rank2Expect = new HashSet<>();
		Collections.addAll(rank2Expect, "2", "9", "10");
		assertUnorderedIDEquals(rank2Expect, db.getRank(2));
	}

	/**
	 * Test querying of ranks.
	 */
	@Test
	public void testQueryRanks() {
		// Query for ranks from 0 up to but not including rank 2.
		GraphQueryDescription qd = new GraphQueryDescription()
			.fromRank(0)
			.toRank(2);
		Set<String> expect = new HashSet<>();
		Collections.addAll(expect, "7", "5", "3", "11", "8");
		assertUnorderedIDEquals(expect, db.queryNodes(qd));
	}

	/**
	 * Test querying of IDs.
	 */
	@Test
	public void testQueryIds() {
		GraphQueryDescription qd = new GraphQueryDescription()
			.hasId("2");
		Set<String> expect = new HashSet<>();
		Collections.addAll(expect, "2");
		assertUnorderedIDEquals(expect, db.queryNodes(qd));

		// Also test for multiple ids (reusing the old one)
		qd = qd.hasId("3");
		Collections.addAll(expect, "3");
		assertUnorderedIDEquals(expect, db.queryNodes(qd));

		// Search for non-existent id.
		qd = new GraphQueryDescription()
			.hasId("42");
		expect = new HashSet<>(); // Empty result.
		assertUnorderedIDEquals(expect, db.queryNodes(qd));
	}

	/**
	 * Test the filtering functionality.
	 */
	@Test
	public void testQueryFilter() {
		// CHECKSTYLE.OFF: MagicNumber
		GraphQueryDescription qd = new GraphQueryDescription()
			.filter((sn) -> Integer.parseInt(sn.getId()) > 8);
		// CHECKSTYLE.ON: MagicNumber
		Set<String> expect = new HashSet<>();
		Collections.addAll(expect, "9", "10", "11");
		assertUnorderedIDEquals(expect, db.queryNodes(qd));
	}

	/**
	 * Test querying the source string.
	 */
	@Test
	public void testQuerySources() {
		GraphQueryDescription qd = new GraphQueryDescription()
			.containsSource("ASDF");
		Set<String> expect = new HashSet<>();
		Collections.addAll(expect, "2", "5", "3", "7", "8");
		assertUnorderedIDEquals(expect, db.queryNodes(qd));

		// Also test for multiple sources (reusing the old one)
		qd = qd.containsSource("ASD");
		Collections.addAll(expect, "9", "10", "11");
		assertUnorderedIDEquals(expect, db.queryNodes(qd));

		// Search non-existing source.
		qd = new GraphQueryDescription()
			.containsSource("FDSA");
		// Expect an empty result
		expect = new HashSet<>();
		assertUnorderedIDEquals(expect, db.queryNodes(qd));
	}

	private static void assertUnorderedIDEquals(Collection<String> expected,
			Collection<SequenceNode> actual) {
		assertEquals(expected.stream().collect(Collectors.toSet()),
				actual.stream().map(sn -> sn.getId()).collect(Collectors.toSet()));
	}
	/**
	 * Clean up after ourselves.
	 * @throws IOException when the database could not be deleted
	 */
	@AfterClass
	public static void cleanUp() throws IOException {
		db.shutdown();
	}
}
