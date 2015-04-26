package petrinet.analysis;

import java.util.Arrays;
import java.util.TreeSet;

import models.semantics.petrinet.Marking;

public abstract class AbstractMarkingSet<M extends Marking> extends TreeSet<M> {

	private static final long serialVersionUID = -806622561885767396L;
	private final String label;

	public AbstractMarkingSet(String label, M... markings) {
		this.label = label;
		addAll(Arrays.asList(markings));
	}

	public String getLabel() {
		return label;
	}

	public Marking[] getMarkings() {
		return this.toArray(new Marking[0]);
	}
}
