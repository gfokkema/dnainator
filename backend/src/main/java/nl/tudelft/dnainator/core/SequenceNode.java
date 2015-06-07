package nl.tudelft.dnainator.core;

import java.util.List;
import java.util.Set;

/**
 * Interface that all sequences should implement.
 */
public interface SequenceNode {
	/**
	 * The id of this nucleotide sequence, for example it's node number.
	 * @return	the id of this sequence
	 */
	String getId();

	/**
	 * The associated source strains.
	 * @return	the sources
	 */
	Set<String> getSources();

	/**
	 * The associated start ref.
	 * @return	the start reference
	 */
	int getStartRef();

	/**
	 * The associated end ref.
	 * @return	the end reference
	 */
	int getEndRef();

	/**
	 * The assiocated nucleotide sequence.
	 * @return	the base sequence
	 */
	String getSequence();

	/**
	 * The associated rank.
	 * @return	the rank
	 */
	int getRank();

	@Override
	boolean equals(Object other);

	@Override
	int hashCode();

	/**
	 * The associated incoming neighbours.
	 * @return	a list of neighbour id's
	 */
	List<String> getOutgoing();

}
