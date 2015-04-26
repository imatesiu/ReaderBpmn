package models.semantics.petrinet;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import models.utils.Cast;
import framework.util.collection.AbstractMultiSet;
import framework.util.collection.MultiSet;
import models.graphbased.directed.petrinet.elements.Place;

/**
 * This class represent a Marking with an addition of omega pointer. Omega is a
 * place which has unlimited token. The class is used to represent a
 * coverability graph.
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version 1 September 2008
 */
public class CTMarking extends Marking {

	/**
	 * changed Set<Place>
	 * 
	 */
	private final Set<Place> omegaPlaces = new LinkedHashSet<Place>(); // array to store

	/**
	 * Constructor with marking parameter
	 * 
	 * @param collection
	 */
	public CTMarking(CTMarking collection) {
		super(collection);
		transformToOmega(collection.getOmegaPlaces());
	}

	/**
	 * Constructor with collection parameter
	 * 
	 * @param collection
	 */
	public CTMarking(Collection<Place> collection) {
		super(collection);
	}

	/**
	 * Default constructor
	 */
	public CTMarking() {
		super();
	}

	/**
	 * Return true if the CTMarking has omega place
	 * 
	 * @return
	 */
	public boolean hasOmegaPlace() {
		return omegaPlaces.size() > 0;
	}

	/**
	 * Keeps all elements of the given collection in this multiset.
	 * Multiplicities are taken into account.
	 * 
	 * @return true if the CTMarking changed from calling this method.
	 */
	public synchronized boolean retainAll(CTMarking c) {
		boolean changed = false;
		for (Place p : baseSet()) {
			// if c has p marked as an OMEGA place, then all
			// occurrences should be kept.
			if (!c.omegaPlaces.contains(p)) {
				Integer occToRetain = c.occurrences(p);
				Integer occInThis = occurrences(p);
				if (occInThis >= occToRetain) {
					// keep occToRetain
					size -= (occInThis - occToRetain);
					if (occToRetain == 0) {
						map.remove(p);
					} else {
						assert (occToRetain > 0);
						map.put(p, occToRetain);
					}
					changed = true;
				}
			}
		}
		return changed;
	}

	/**
	 * Add a place which should be presented with omega
	 * 
	 * @param p
	 *            place which should be presented with omega
	 * @return true if the place added successfully
	 */
	public boolean addOmegaPlace(Place p) {
		return omegaPlaces.add(p);
	}

	/**
	 * Remove a place from the list of omega places
	 * 
	 * @param removedItem
	 * @return true if the place is successfully removed from the list of omega
	 *         places
	 */
	public boolean removeOmegaPlace(Place removedItem) {
		return omegaPlaces.remove(removedItem);
	}

	/**
	 * Get a set of omega places
	 * 
	 * @return
	 */
	public Set<Place> getOmegaPlaces() {
		return Collections.unmodifiableSet(omegaPlaces);
	}

	/**
	 * @override equals method
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CTMarking)) {
			return false;
		}
		CTMarking otherCTMarking = Cast.<CTMarking>cast(o);

		// quick check
		if (omegaPlaces.size() != otherCTMarking.omegaPlaces.size()) {
			return false;
		}

		return super.equals(otherCTMarking);
	}

	/**
	 * transform all places in this CTMarking into omega. Omega is represented
	 * as a place CTMarking.OMEGA in place, and also listed as an item in
	 * arraylist omegaPlace
	 * 
	 * @param reference
	 * @return
	 */
	public CTMarking transformToOmega(Collection<Place> reference) {
		Iterator<Place> li = reference.iterator();
		while (li.hasNext()) {
			Place temp = li.next();
			map.put(temp, Integer.MAX_VALUE);
			size = Integer.MAX_VALUE;
			addOmegaPlace(temp);
		}
		return this;
	}

	/**
	 * New toString, to represent elements of inifinite occurrence with "w"
	 */
	@Override
	public String toString() {
		String s = "[";
		for (Place p : baseSet()) {
			if (!s.equals("[")) {
				s += " ";
			}
			if (getOmegaPlaces().contains(p)) {
				s += "(" + p + ", w )";
			} else {
				s += "(" + p + "," + occurrences(p) + ")";
			}
		}
		return s + "]";
	}

	@Override
	public String toHTMLString(boolean includeHTMLTags) {

		String s = (includeHTMLTags ? "<html>" : "") + "[";
		for (Place p : baseSet()) {
			if (!s.endsWith("[")) {
				s += ",";
			}
			s += p;
			if (getOmegaPlaces().contains(p)) {
				s += "<sup>&#8734;</sup>";
			} else if (occurrences(p) > 1) {
				s += "<sup>" + occurrences(p) + "</sup>";
			}
		}
		return s + "]" + (includeHTMLTags ? "</html>" : "");

	}

	/**
	 * returns true if this multiset is less or equal to the given multiset,
	 * i.e. all objects in this multiset should be contained in the given set
	 * and the number of occurrences in the given set is at least the number of
	 * occurrences in this multiset.
	 * 
	 * @param multiSet
	 *            the multiset to test
	 * @return true if the given multiset is less or equal.
	 */
	@Override
	public synchronized boolean isLessOrEqual(MultiSet<Place> multiSet) {
		if (multiSet instanceof CTMarking) {
			CTMarking marking = Cast.<CTMarking>cast(multiSet);
			synchronized (marking) {
				for (Place element : baseSet()) {
					if ((getOmegaPlaces().contains(element) && !marking.getOmegaPlaces().contains(element))
							|| (marking.occurrences(element) < occurrences(element))) {
						return false;
					}
				}
				return true;
			}
		} else {
			return super.isLessOrEqual(multiSet);
		}

	}

	/**
	 * removes the given object from this multiset, if it is in there. Only one
	 * occurrence is removed, i.e. contains(o) can still be true after calling
	 * remove(o)
	 */
	@Override
	public synchronized boolean remove(Object o) {
		if (omegaPlaces.contains(o)) {
			return false;
		} else {
			return super.remove(o);
		}
	}

	@Override
	protected synchronized <S extends MultiSet<Place>> S removeAllMultiSet(AbstractMultiSet<?, ?> mset, S removed) {
		for (Object o : mset.baseSet()) {
			if (!map.containsKey(o) || omegaPlaces.contains(o)) {
				// Do not remove anything if o is not in this set, or if
				// o is an omega place
				continue;
			}
			// Since map.containsKey(entry.getKey()), this is a safe cast
			Place key = Cast.<Place>cast(o);
			Integer val = map.get(key);
			// What's the minimum of the amount I have and the amount I have to remove
			Integer toRemove = Math.min(mset.occurrences(key), val);
			removed.add(key, toRemove);

			size -= toRemove;
			if (val - toRemove == 0) {
				map.remove(key);
			} else {
				assert (val - toRemove > 0);
				map.put(key, val - toRemove);
			}
		}
		return removed;

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
	public Marking minus(CTMarking m) {
		return removeAllMultiSet(m, new CTMarking());
	}

}
