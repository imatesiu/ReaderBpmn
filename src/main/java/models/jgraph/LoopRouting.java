package models.jgraph;

import java.util.List;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphLayoutCache;

public class LoopRouting extends DefaultEdge.DefaultRouting {

	public static LoopRouting ROUTER = new LoopRouting();

	private static final long serialVersionUID = -1502015269578934172L;

	public List<?> route(GraphLayoutCache cache, EdgeView edge) {
		// No routing is performed "on the fly", i.e. all routing information
		// is introduced during the layout phase where all internal control points
		// of edges are set. Hence, the route should just return null.
		return null;
	}
}
