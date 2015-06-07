package nl.tudelft.dnainator.graph.impl;

import org.neo4j.graphdb.RelationshipType;

/**
 * Edge relationship types.
 */
public enum RelTypes implements RelationshipType {
	NEXT,
	SOURCE
}
