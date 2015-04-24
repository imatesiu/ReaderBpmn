package models.jgraph.elements;

import java.util.Map;

import org.jgraph.graph.CellView;

public interface CustomGraphElement {

	CellView getView();

	void updateViewsFromMap();

	Map getAttributes();
}
