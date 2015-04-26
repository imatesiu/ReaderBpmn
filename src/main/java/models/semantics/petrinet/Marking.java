package models.semantics.petrinet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import framework.util.collection.TreeMultiSet;
import models.graphbased.directed.petrinet.elements.Place;

public class Marking extends TreeMultiSet<Place> implements Comparable<Marking> {

	public Marking(Collection<Place> collection) {
		super(collection);
	}

	public Marking() {
		super();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Marking)) {
			return false;
		}
		return (compareTo((Marking) o) == 0);
	}

	/**
	 * removes the elements in the given multiset from this multiset and returns
	 * a multiset indicating what was removed.
	 * 
	 * @param mset
	 *            the multiset of elements needing to be removed.
	 * @return a new multiset where the occurrences are the occurrences in this
	 *         multiset, minus the occurrences in the given multiset
	 */
	public Marking minus(Marking m) {
		return removeAllMultiSet(m, new Marking());
	}

	public int compareTo(Marking mset) {
		Collection<Map.Entry<Place, Integer>> s1 = map.entrySet();
		Collection<Map.Entry<Place, Integer>> s2 = mset.map.entrySet();
		if (s1.size() < s2.size()) {
			return -1;
		} else if (s1.size() > s2.size()) {
			return 1;
		}
		// Same base size;
		Iterator<Map.Entry<Place, Integer>> it1 = s1.iterator();
		Iterator<Map.Entry<Place, Integer>> it2 = s2.iterator();
		while (it1.hasNext()) {
			Map.Entry<Place, Integer> o1 = it1.next();
			Map.Entry<Place, Integer> o2 = it2.next();

			int c = o1.getKey().compareTo(o2.getKey());
			if (c != 0) {
				return c;
			}

			c = o1.getValue() - o2.getValue();
			if (c != 0) {
				return c;
			}

		}
		// same base set and same occurrence count.
		return 0;
	}

}
