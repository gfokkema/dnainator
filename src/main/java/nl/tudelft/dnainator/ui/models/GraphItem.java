package nl.tudelft.dnainator.ui.models;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;
import nl.tudelft.dnainator.graph.Graph;
import nl.tudelft.dnainator.graph.impl.Neo4jSingleton;

/**
 * The {@link GraphItem} class represents the top level object in the viewable model.
 * It is a {@link CompositeItem}, that can hold both content and children.
 */
public class GraphItem extends CompositeItem {
	private static final int FOUR = 4;

	/**
	 * Construct a new top level {@link GraphItem} using the default graph.
	 */
	public GraphItem() {
		this(Neo4jSingleton.getInstance().getDatabase());
	}

	/**
	 * Construct a new top level {@link GraphItem} using the specified graph.
	 * @param graph	the specified graph
	 */
	public GraphItem(Graph graph) {
		super(graph);

		localToRootProperty().set(new Translate());

		Group g = new Group();
		for (int i = 0; i < FOUR; i++) {
			int width = NO_CLUSTERS * NO_RANKS * RANK_WIDTH / FOUR;
			Rectangle r = new Rectangle(width, CLUSTER_SIZE, Color.BLACK);
			r.setTranslateX(i * width);
			g.getChildren().add(r);
		}
		getContent().getChildren().add(g);

		// FIXME: These should be lazily instantiated!
		for (int i = 0; i < NO_CLUSTERS; i++) {
			ClusterItem ci = new ClusterItem(getGraph(), localToRootProperty());
			ci.setTranslateX(i * NO_RANKS * RANK_WIDTH);
			getChildItems().add(ci);
		}
	}

	@Override
	public void update(Bounds b) {
		update(b, Thresholds.GRAPH);
	}
}