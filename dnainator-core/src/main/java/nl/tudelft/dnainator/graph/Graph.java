package nl.tudelft.dnainator.graph;

import nl.tudelft.dnainator.annotation.Annotation;
import nl.tudelft.dnainator.annotation.AnnotationCollection;
import nl.tudelft.dnainator.annotation.Range;
import nl.tudelft.dnainator.core.SequenceNode;
import nl.tudelft.dnainator.core.impl.Cluster;
import nl.tudelft.dnainator.graph.interestingness.InterestingnessStrategy;
import nl.tudelft.dnainator.graph.query.GraphQueryDescription;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface for backend agnostic interaction with a graph.
 */
public interface Graph extends AnnotationCollection {
	/**
	 * Get the root node of this graph.
	 * FIXME: This is the node that has no incoming edges.
	 * @return	a SequenceNode
	 */
	SequenceNode getRootNode();

	/**
	 * Get the node with identifier n from this graph.
	 * @param n	the identifier
	 * @return	a SequenceNode
	 */
	SequenceNode getNode(String n);

	/**
	 * @return The {@link AnnotationCollection} containing the annotations.
	 */
	AnnotationCollection getAnnotations();

	/**
	 * Get all the nodes with a specific rank from this graph.
	 * @param rank	the rank
	 * @return		a list of sequence nodes
	 */
	List<SequenceNode> getRank(int rank);

	/**
	 * Get a list of all nodes from this graph.
	 * @return	a list of all nodes, per rank
	 */
	List<List<SequenceNode>> getRanks();

	/**
	 * Return a list of nodes that belong to the same cluster as the given startId.
	 * @param startNodes	the start nodes
	 * @param end		the maximum rank of the cluster
	 * @param threshold	the clustering threshold
	 * @return		a list representing the cluster
	 */
	Map<Integer, List<Cluster>> getAllClusters(List<String> startNodes, int end, int threshold);

	/**
	 * Sets the interestingness strategy which calculates the interestingness when
	 * clustering.
	 * @param is the interestingness strategy.
	 */
	void setInterestingnessStrategy(InterestingnessStrategy is);

	/**
	 * Find the nodes satisfying the given query.
	 * @param q the query for finding the nodes.
	 * @return the result of the query.
	 */
	List<SequenceNode> queryNodes(GraphQueryDescription q);

	/**
	 * Return all annotations covered by the given range of ranks.
	 * @param r The range
	 * @return all annotations covered.
	 */
	Collection<Annotation> getAnnotationByRank(Range r);
}
