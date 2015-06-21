package nl.tudelft.dnainator.graph.impl.nodes;

import nl.tudelft.dnainator.annotation.Annotation;
import nl.tudelft.dnainator.core.EnrichedSequenceNode;
import nl.tudelft.dnainator.core.SequenceNode;
import nl.tudelft.dnainator.graph.impl.RelTypes;
import nl.tudelft.dnainator.graph.impl.properties.SequenceProperties;
import nl.tudelft.dnainator.graph.impl.properties.SourceProperties;
import nl.tudelft.dnainator.graph.interestingness.ScoreIdentifier;
import nl.tudelft.dnainator.graph.interestingness.Scores;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link SequenceNode} which delegates to a Neo4j Node containing
 * the information.
 */
public class Neo4jSequenceNode implements EnrichedSequenceNode {
	private GraphDatabaseService service;
	private Node node;

	private String id;
	private int start;
	private int end;
	private String sequence;
	private int basedist;
	private int rank;
	private List<Annotation> annotations;
	private Set<String> sources;
	private List<String> outgoing;
	private Map<ScoreIdentifier, Integer> scores;
	private int interestingness;

	private boolean loaded;

	/**
	 * Construct a new {@link Neo4jSequenceNode} which wraps the given
	 * Neo4j {@link Node}.
	 * @param service The Neo4j service, for accessing the database.
	 * @param node The Neo4j node.
	 */
	public Neo4jSequenceNode(GraphDatabaseService service, Node node) {
		loaded = false;

		this.service = service;
		this.node = node;
		this.annotations = new ArrayList<>();
		this.outgoing = new ArrayList<>();
		this.sources = new HashSet<>();
		this.scores = new HashMap<>();

		try (Transaction tx = service.beginTx()) {
			this.id         = (String) node.getProperty(SequenceProperties.ID.name());
			basedist        = (int) node.getProperty(SequenceProperties.BASE_DIST.name());
			interestingness = (int) node.getProperty(SequenceProperties.INTERESTINGNESS.name(), 0);

			node.getRelationships(RelTypes.NEXT, Direction.OUTGOING).forEach(e -> {
				outgoing.add((String) e.getEndNode().getProperty(SequenceProperties.ID.name()));
			});
			node.getRelationships(RelTypes.SOURCE, Direction.OUTGOING).forEach(e -> {
				sources.add((String) e.getEndNode().getProperty(SourceProperties.SOURCE.name()));
			});

			tx.success();
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getBaseDistance() {
		return basedist;
	}

	@Override
	public List<String> getOutgoing() {
		return outgoing;
	}

	@Override
	public Set<String> getSources() {
		return sources;
	}

	@Override
	public List<Annotation> getAnnotations() {
		load();
		return annotations;
	}

	@Override
	public int getStartRef() {
		load();
		return start;
	}

	@Override
	public int getEndRef() {
		load();
		return end;
	}

	@Override
	public String getSequence() {
		load();
		return sequence;
	}

	@Override
	public int getRank() {
		load();
		return rank;
	}

	@Override
	public int getScore(ScoreIdentifier id) {
		load();
		return scores.get(id);
	}

	@Override
	public Map<ScoreIdentifier, Integer> getScores() {
		load();
		return scores;
	}

	@Override
	public int getInterestingnessScore() {
		return interestingness;
	}

	private void load() {
		if (loaded) {
			return;
		}

		System.out.println("--- loading sequence node " + getId() + " ---");

		try (Transaction tx = service.beginTx()) {
			start    = (int)    node.getProperty(SequenceProperties.STARTREF.name());
			end      = (int)    node.getProperty(SequenceProperties.ENDREF.name());
			sequence = (String) node.getProperty(SequenceProperties.SEQUENCE.name());
			rank     = (int)    node.getProperty(SequenceProperties.RANK.name());
			node.getRelationships(RelTypes.ANNOTATED, Direction.OUTGOING).forEach(e -> {
				annotations.add(new Neo4jAnnotation(service, e.getEndNode()));
			});
			for (ScoreIdentifier id : Scores.values()) {
				scores.put(id, (Integer) node.getProperty(id.name(), 0));
			}
			tx.success();
		}

		loaded = true;
	}

	@Override
	public String toString() {
		return "SequenceNode<" + getId() + "," + getSequence().length() + ">";
	}
}
