package nl.tudelft.dnainator.graph.impl.nodes;

import nl.tudelft.dnainator.annotation.Annotation;
import nl.tudelft.dnainator.annotation.Range;
import nl.tudelft.dnainator.graph.impl.NodeLabels;
import nl.tudelft.dnainator.graph.impl.RelTypes;
import nl.tudelft.dnainator.graph.impl.properties.AnnotationProperties;
import nl.tudelft.dnainator.graph.impl.properties.SequenceProperties;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An {@link Annotation} which is stored in the Neo4j database.
 */
public class Neo4jAnnotation implements Annotation {
	private String geneName;
	private Range range;
	private boolean isMutation;
	private boolean isSense;
	private Collection<String> annotatedNodes;

	/**
	 * Constructs a new Neo4jAnnotation.
	 * @param service the service for accessing the database
	 * @param delegate the node to delegate to.
	 */
	public Neo4jAnnotation(GraphDatabaseService service, Node delegate) {
		try (Transaction tx = service.beginTx()) {
			this.geneName = (String) delegate.getProperty(AnnotationProperties.ID.name());
			this.range = new Range((int) delegate.getProperty(AnnotationProperties.STARTREF.name()),
					(Integer) delegate.getProperty(AnnotationProperties.ENDREF.name()));
			this.isMutation = delegate.hasLabel(NodeLabels.DRMUTATION);
			this.isSense = (Boolean) delegate.getProperty(AnnotationProperties.SENSE.name());
			this.annotatedNodes = new ArrayList<>();
			delegate.getRelationships(Direction.INCOMING, RelTypes.ANNOTATED).forEach(e ->
				annotatedNodes.add(
					(String) e.getStartNode().getProperty(SequenceProperties.ID.name())
				)
			);
			tx.success();
		}
	}

	@Override
	public String getGeneName() {
		return geneName;
	}

	@Override
	public Range getRange() {
		return range;
	}

	@Override
	public boolean isMutation() {
		return isMutation;
	}

	@Override
	public boolean isSense() {
		return isSense;
	}

	@Override
	public Collection<String> getAnnotatedNodes() {
		return annotatedNodes;
	}
}
