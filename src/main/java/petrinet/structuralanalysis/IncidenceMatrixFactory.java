/**
 * 
 */
package petrinet.structuralanalysis;

import java.util.ArrayList;
import java.util.Iterator;

import models.graphbased.directed.petrinet.PetrinetEdge;
import models.graphbased.directed.petrinet.PetrinetGraph;
import models.graphbased.directed.petrinet.PetrinetNode;
import models.graphbased.directed.petrinet.elements.InhibitorArc;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.ResetArc;
import models.graphbased.directed.petrinet.elements.Transition;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * This class generate incidence matrix from a net
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Oct 30, 2008
 */
public class IncidenceMatrixFactory {
	private IncidenceMatrixFactory() {
	}

	/**
	 * Generate transposed incidence matrix from petrinet (and its variants)
	 * Inhibitor net and ResetNet arc are ignored Rows indicate transitions
	 * columns indicate places
	 * 
	 * @param net
	 * @return
	 */
	public static DoubleMatrix2D getTransposedIncidenceMatrix(PetrinetGraph net) {
		DoubleMatrix2D matrix = DoubleFactory2D.sparse.make(net.getTransitions().size(), net.getPlaces().size(), 0);
		ArrayList<Place> placeList = new ArrayList<Place>(net.getPlaces());
		ArrayList<Transition> transitionList = new ArrayList<Transition>(net.getTransitions());

		Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> it = net.getEdges().iterator();
		while (it.hasNext()) {
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e = it.next();
			//	System.out.println("Connection between " + e.getSource().getLabel() + " to " + e.getTarget().getLabel() + " is founded");

			if (!(e instanceof InhibitorArc) && !(e instanceof ResetArc)) { // inhibitor and reset arc are ignored
				if (e.getSource() instanceof Transition) {
					/*
					 * System.out.println("Connection between Transition " +
					 * e.getSource().getLabel() + "(" +
					 * transitionList.indexOf(e.getSource()) + ") to Place " +
					 * e.getTarget().getLabel() + "(" +
					 * placeList.indexOf(e.getTarget()) +
					 * ") is founded with weight " + net.getArc(e.getSource(),
					 * e.getTarget()).getWeight());
					 */
					int x = transitionList.indexOf(e.getSource());
					int y = placeList.indexOf(e.getTarget());
					matrix.set(x, y, matrix.get(x, y) + net.getArc(e.getSource(), e.getTarget()).getWeight());
				} else {
					if (e.getSource() instanceof Place) {
						/*
						 * System.out.println("Connection between Place " +
						 * e.getSource().getLabel() + " (" +
						 * placeList.indexOf(e.getSource()) + ") to Transition "
						 * + e.getTarget().getLabel() + " (" +
						 * transitionList.indexOf(e.getTarget())
						 * +") is founded with weight -1 * " +
						 * net.getArc(e.getSource(),
						 * e.getTarget()).getWeight());
						 */
						int x = transitionList.indexOf(e.getTarget());
						int y = placeList.indexOf(e.getSource());
						matrix.set(x, y, matrix.get(x, y) - net.getArc(e.getSource(), e.getTarget()).getWeight());
					}
				}
			}
		}
		return matrix;
	}

	/**
	 * Generate incidence matrix from petrinet (and its variants) Inhibitor net
	 * and ResetNet arc are ignored Rows indicate places columns indicate
	 * transition
	 * 
	 * @param net
	 * @return
	 */
	public static DoubleMatrix2D getIncidenceMatrix(PetrinetGraph net) {
		return IncidenceMatrixFactory.getTransposedIncidenceMatrix(net).viewDice();
	}

	/**
	 * Generate transposed signed incidence matrix from petrinet (and its
	 * variants) Inhibitor net and ResetNet arc are ignored Rows indicate
	 * transitions Columns indicate places
	 * 
	 * @param net
	 * @return
	 */
	public static DoubleMatrix2D getTransposedSignedIncidenceMatrix(PetrinetGraph net) {
		DoubleMatrix2D matrix = DoubleFactory2D.sparse.make(net.getTransitions().size(), net.getPlaces().size(), 0);
		ArrayList<Place> placeList = new ArrayList<Place>(net.getPlaces());
		ArrayList<Transition> transitionList = new ArrayList<Transition>(net.getTransitions());

		Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> it = net.getEdges().iterator();
		while (it.hasNext()) {
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e = it.next();

			if (!(e instanceof InhibitorArc) && !(e instanceof ResetArc)) { // both inhibitor and reset arcs are ignored
				if (e.getSource() instanceof Transition) {
					int x = transitionList.indexOf(e.getSource());
					int y = placeList.indexOf(e.getTarget());

					matrix.set(x, y, SignIncidenceMatrixOperator.add(matrix.get(x, y), net.getArc(e.getSource(),
							e.getTarget()).getWeight()));
				} else {
					if (e.getSource() instanceof Place) {
						int x = transitionList.indexOf(e.getTarget());
						int y = placeList.indexOf(e.getSource());

						matrix.set(x, y, SignIncidenceMatrixOperator.add(matrix.get(x, y), (-1 * net.getArc(
								e.getSource(), e.getTarget()).getWeight())));
					}
				}
			}
		}
		return matrix;
	}

	/**
	 * Generate incidence matrix from petrinet (and its variants) Inhibitor net
	 * and ResetNet arc are ignored Rows indicate places columns indicate
	 * transition
	 * 
	 * @param net
	 * @return
	 */
	public static DoubleMatrix2D getSignedIncidenceMatrix(PetrinetGraph net) {
		return IncidenceMatrixFactory.getTransposedSignedIncidenceMatrix(net).viewDice();
	}

}
