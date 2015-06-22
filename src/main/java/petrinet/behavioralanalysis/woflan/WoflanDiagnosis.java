package petrinet.behavioralanalysis.woflan;

import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;


import framework.util.HTMLToString;
import models.utils.Pair;
import framework.util.collection.MultiSet;
import framework.util.collection.TreeMultiSet;


import models.graphbased.directed.petrinet.Petrinet;
import models.graphbased.directed.petrinet.PetrinetNode;
import petrinet.analysis.AbstractInvariantSet;
import petrinet.analysis.DeadTransitionsSet;
import petrinet.analysis.NonExtendedFreeChoiceClustersSet;
import petrinet.analysis.NonLiveSequences;
import petrinet.analysis.NonLiveTransitionsSet;
import petrinet.analysis.NotPCoveredNodesSet;
import petrinet.analysis.NotSCoveredNodesSet;
import petrinet.analysis.PTHandles;
import petrinet.analysis.SinkPlacesSet;
import petrinet.analysis.SourcePlacesSet;
import petrinet.analysis.TPHandles;
import petrinet.analysis.UnboundedPlacesSet;
import petrinet.analysis.UnboundedSequences;
import petrinet.analysis.UnconnectedNodesSet;
import models.graphbased.directed.petrinet.elements.Place;
import models.graphbased.directed.petrinet.elements.Transition;
import models.semantics.Semantics;
import models.semantics.petrinet.Marking;

/**
 * WoflanDiagnosis
 * 
 * Diagnostic information obtained by Woflan. Contains a visualizer.
 * 
 * @author hverbeek
 * 
 *         Changes: 1.0 20100507: Removed some redundant imports. No need to
 *         update package.
 */
/**
 * @author hverbeek
 * 
 */
/**
 * @author hverbeek
 * 
 */
public class WoflanDiagnosis implements HTMLToString {
	/**
	 * Possible final verdicts.
	 */
	private final int UNKNOWN = -1;
	private final int NOWFNET = 0;
	private final int UNBOUNDED = 1;
	private final int DEAD = 2;
	private final int NOTLIVE = 3;
	private final int SOUND = 4;
	private int diagnosis;

	/**
	 * Diagnostic information.
	 */
	protected Petrinet net;
	protected Petrinet shortCNet;
	protected Semantics<Marking, Transition> semantics;
	private SourcePlacesSet sourcePlacesSet;
	private SinkPlacesSet sinkPlacesSet;
	private UnconnectedNodesSet unconnectedNodesSet;
	private NotSCoveredNodesSet notSCoveredNodesSet;
	private NotPCoveredNodesSet notPCoveredNodesSet;
	private UnboundedPlacesSet unboundedPlacesSet;
	private NonLiveTransitionsSet nonLiveTransitionsSet;
	private NonLiveSequences nonLiveSequences;
	private UnboundedSequences unboundedSequences;
	private DeadTransitionsSet deadTransitionsSet;
	private NonExtendedFreeChoiceClustersSet nonFreeChoiceClusters;
	private PTHandles pTHandles;
	private TPHandles tPHandles;

	/**
	 * Public constructor. Initialize the diagnostic information.
	 * 
	 * @param context
	 *            The plug-in context.
	 * @param net
	 *            The Petri net under diagnosis.
	 */
	public WoflanDiagnosis(Petrinet net) {
		this.net = net;
		/**
		 * Woflan uses regular semantics.
		 */
//		semantics = PetrinetSemanticsFactory
//				.regularPetrinetSemantics(Petrinet.class);
		semantics = new WoflanSemantics();
		/**
		 * Initialize the diagnostic information.
		 */
		diagnosis = UNKNOWN;
		sourcePlacesSet = null;
		sinkPlacesSet = null;
		unconnectedNodesSet = null;
		nonLiveTransitionsSet = null;
		nonLiveSequences = null;
		unboundedPlacesSet = null;
		unboundedSequences = null;
		deadTransitionsSet = null;
		notSCoveredNodesSet = null;
		notPCoveredNodesSet = null;
		nonFreeChoiceClusters = null;
		pTHandles = null;
		tPHandles = null;
	}

	/**
	 * Sets the final verdict.
	 * 
	 * @param diagnosis
	 *            The final verdict.
	 */
	private void setDiagnosis(int diagnosis) {
		if (this.diagnosis == UNKNOWN) {
			this.diagnosis = diagnosis;
		}
	}

	/**
	 * Sets the set of source places, and the final verdict to NOWFNET.
	 * 
	 * @precondition places.size() != 1
	 * 
	 * @param places
	 *            The set of source places.
	 */
	protected void setSourcePlaces(
			SortedSet<Place> places) {
		sourcePlacesSet = new SourcePlacesSet();
		sourcePlacesSet.add(places);
		//context.addConnection(new SourcePlacesConnection(net, sourcePlacesSet));
		setDiagnosis(NOWFNET);
	}

	/**
	 * Sets the set of sink places, and the final verdict to NOWFNET.
	 * 
	 * @precondition places.size() != 1
	 * 
	 * @param places
	 *            The set of sink places.
	 */
	protected void setSinkPlaces( SortedSet<Place> places) {
		sinkPlacesSet = new SinkPlacesSet();
		sinkPlacesSet.add(places);
		//context.addConnection(new SinkPlacesConnection(net, sinkPlacesSet));
		setDiagnosis(NOWFNET);
	}

	/**
	 * Sets the set of unconnected nodes, and the final verdict to NOWFNET.
	 * 
	 * @precondition !nodes.isEmpty()
	 * 
	 * @param nodes
	 *            The set of unconnected nodes.
	 */
	protected void setUnconnectedNodes(
			SortedSet<PetrinetNode> nodes) {
		unconnectedNodesSet = new UnconnectedNodesSet();
		unconnectedNodesSet.add(nodes);
	//	context.addConnection(new UnconnectedNodesConnection(net,
	//			unconnectedNodesSet));
		setDiagnosis(NOWFNET);
	}

	/**
	 * Sets the set of nodes that are not covered by any S-component.
	 * 
	 * @precondition nodesSet.size() == 1 &&
	 *               !nodesSet.iterator().next().isEmpty()
	 * 
	 * @param nodesSet
	 *            A set of set of nodes, containing the set of nodes that are
	 *            not covered by any S-component as its only member.
	 */
	protected void setNotSCoveredNodes(NotSCoveredNodesSet nodesSet) {
		notSCoveredNodesSet = nodesSet;
	}

	/**
	 * Sets the set of places that are not covered by any positive place
	 * invariant.
	 * 
	 * @precondition nodesSet.size() == 1 &&
	 *               !nodesSet.iterator().next().isEmpty()
	 * 
	 * @param nodes
	 *            A set of set of places, containing the set of places that are
	 *            not covered by any positive place invariant as its only
	 *            member.
	 */
	protected void setNotPCoveredNodes(NotPCoveredNodesSet nodesSet) {
		notPCoveredNodesSet = nodesSet;
	}

	/**
	 * Sets the set of unbounded places and the set of unbounded sequences, and
	 * sets the final verdict to UNBOUNDED.
	 * 
	 * @precondition places.size() == 1 && !places.iterator().next().isEmpty()
	 *               && !sequences.isEmpty()
	 * 
	 * @param places
	 *            A set of set of places, containing the set of unbounded places
	 *            as its only member.
	 * @param sequences
	 *            The set of unbounded sequences. Each member of this set
	 *            contains either: - only transitions, in which case
	 *            unboundedness is inevitable, or - one transition and a
	 *            multiset of places, in which case the given transition should
	 *            be disabled at the marking given by the multiset of places.
	 */
	protected void setUnboundedPlaces(UnboundedPlacesSet places,
			UnboundedSequences sequences) {
		unboundedPlacesSet = places;
		unboundedSequences = sequences;
		setDiagnosis(UNBOUNDED);
	}

	/**
	 * Sets the set of non-live transitions and the set of non-live sequences,
	 * and sets the final verdict to NOTLIVE.
	 * 
	 * @precondition transitions.size() == 1 &&
	 *               !transitions.iterator().next().isEmpty() &&
	 *               !sequences.isEmpty()
	 * 
	 * @param transitions
	 *            A set of set of transitions, containing the set of non-live
	 *            transitions as its only member.
	 * @param sequences
	 *            The set of non-live sequences. Each member of this set
	 *            contains either: - only transitions, in which case the final
	 *            state is not reachable, or - one transition and a multiset of
	 *            places, in which case the given transition should be disabled
	 *            at the marking given by the multiset of places.
	 */
	protected void setNotLiveTransitions(NonLiveTransitionsSet transitions,
			NonLiveSequences sequences) {
		nonLiveTransitionsSet = transitions;
		nonLiveSequences = sequences;
		setDiagnosis(NOTLIVE);
	}

	/**
	 * Sets the set of dead transitions and the final verdict to DEAD.
	 * 
	 * @precondition transitions.size() == 1 &&
	 *               !transitions.iterator().next().isEmpty()
	 * 
	 * @param transitions
	 *            A set of set of transition, containing the set of dead
	 *            transitions as its only member.
	 */
	protected void setDeadTransitions(DeadTransitionsSet transitions) {
		deadTransitionsSet = transitions;
		setDiagnosis(DEAD);
	}

	/**
	 * Sets the final verdict to SOUND.
	 */
	protected void setSound() {
		setDiagnosis(SOUND);
	}

	/**
	 * Sets the set of non-extended-free-choice clusters.
	 * 
	 * @param clusters
	 *            The set of non-free-choice clusters.
	 */
	protected void setNotFreeChoice(NonExtendedFreeChoiceClustersSet clusters) {
		nonFreeChoiceClusters = clusters;
	}

	/**
	 * Sets the set of PT-handles.
	 * 
	 * @param handles
	 *            The set of PT-handles.
	 */
	protected void setPTHandles(PTHandles handles) {
		pTHandles = handles;
	}

	/**
	 * Sets the set of TP-handles.
	 * 
	 * @param handles
	 *            The set of TP-handles.
	 */
	protected void setTPHandles(TPHandles handles) {
		tPHandles = handles;
	}

	/**
	 * Adds HTML tags to the given text indicating that the given text relates
	 * to an error.
	 * 
	 * @param text
	 *            The given text.
	 * @return The given text with HTML tags added.
	 */
	private String error(String text) {
		return "<font color=\"FF0000\"><b>" + text + "</b></font>";
	}

	/**
	 * Adds HTML tags to the given text indicating that the given text relates
	 * to a warning.
	 * 
	 * @param text
	 *            The given text.
	 * @return The given text with HTML tags added.
	 */
	private String warning(String text) {
		return "<font color=\"FF8800\"><b>" + text + "</b></font>";
	}

	/**
	 * Adds HTML tags to the given text indicating that the given text relates
	 * to something good.
	 * 
	 * @param text
	 *            The given text.
	 * @return The given text with HTML tags added.
	 */
	private String okay(String text) {
		return "<font color=\"008800\"><b>" + text + "</b></font>";
	}

	/**
	 * Adds HTML tags to the given text indicating that the given text relates
	 * to something yet undecided.
	 * 
	 * @param text
	 *            The given text.
	 * @return The given text with HTML tags added.
	 */
	private String strong(String text) {
		return "<b>" + text + "</b>";
	}

	/**
	 * Adds the HTML report to the given buffer, provided that the net is not a
	 * WF net.
	 * 
	 * @param buffer
	 *            The given buffer.
	 */
	private void addNoWFNetDiagnosis(StringBuffer buffer) {
		buffer.append("<h2>" + error("The net is not a workflow net.")
				+ "</h2>");
		buffer
				.append("<p>The soundness property has been defined on workflow nets. ");
		buffer
				.append("As this net is not a workflow net, soundness is not defined on it.</p>");
		buffer.append("<h3>Workflow net requirements</h3>");
		buffer.append("<dl>");
		if (sourcePlacesSet == null) {
			buffer.append("<dt>" + okay("Source place") + "</dt>");
		} else {
			buffer.append("<dt>" + error("Source place") + "</dt>");
		}
		buffer.append("<dd>There exists a single source place.</dd>");
		if (sinkPlacesSet == null) {
			buffer.append("<dt>" + okay("Sink place") + "</dt>");
		} else {
			buffer.append("<dt>" + error("Sink place") + "</dt>");
		}
		buffer.append("<dd>There exists a single sink place.</dd>");
		if (unconnectedNodesSet == null) {
			buffer.append("<dt>" + okay("Unconnected nodes") + "</dt>");
		} else {
			buffer.append("<dt>" + error("Unconnected nodes") + "</dt>");
		}
		buffer
				.append("<dd>Every node is on some path from the source place to the sink place.</dd>");
		buffer.append("</dl>");
		buffer
				.append("<p>Note that the <b>Unconnected nodes</b> requirement assumes ");
		buffer
				.append("that the <b>Source place</b> and <b>Sink place</b> requirements are met.</p>");
		if (sourcePlacesSet != null) {
			SortedSet<Place> places = sourcePlacesSet.iterator().next();
			int size = places.size();
			buffer
					.append("<p>The net does not satisfy the <b>Source place</b> requirement, ");
			buffer.append("as it contains " + (size == 0 ? "no" : size)
					+ " source places:");
			buffer.append("<ol>");
			for (Place place : places) {
				buffer.append("<li>" + error(place.getLabel()) + "</li>");
			}
			buffer.append("</ol></p>");
		}
		if (sinkPlacesSet != null) {
			SortedSet<Place> places = sinkPlacesSet.iterator().next();
			int size = places.size();
			if (sourcePlacesSet != null) {
				buffer.append("<p>Furthermore, the ");
			} else {
				buffer.append("<p>The ");
			}
			buffer
					.append("net does not satisfy the <b>Sink place</b> requirement, ");
			buffer.append("as it contains " + (size == 0 ? "no" : size)
					+ " sink places:");
			buffer.append("<ol>");
			for (Place place : places) {
				buffer.append("<li>" + error(place.getLabel()) + "</li>");
			}
			buffer.append("</ol></p>");
		}
		if (unconnectedNodesSet != null) {
			SortedSet<PetrinetNode> nodes = unconnectedNodesSet.iterator()
					.next();
			int size = nodes.size();
			buffer
					.append("<p>The net does not satisfy the <b>Unconnected nodes</b> requirement, ");
			buffer.append("as it contains " + size + " unconnected "
					+ (size > 1 ? "nodes:" : "node:"));
			buffer.append("<ol>");
			for (PetrinetNode node : nodes) {
				buffer.append("<li>" + error(node.getLabel()) + "</li>");
			}
			buffer.append("</ol></p>");
		}
	}

	/**
	 * Adds the HTML report to the given buffer, provided that the net is sound.
	 * 
	 * @param buffer
	 *            The given buffer.
	 */
	private void addSoundDiagnosis(StringBuffer buffer) {
		buffer.append("<h2>" + okay("The net is a sound workflow net.")
				+ "</h2>");
		if (notSCoveredNodesSet != null) {
			buffer
					.append("<p>The following diagnostic information presents places that are ");
			buffer
					.append("not covered by any S-component. An S-component strongly relates ");
			buffer
					.append("to an aspect (say, a data field) of a case. Therefore, it is ");
			buffer
					.append("strongly recommended to have all places covered, and any ");
			buffer
					.append("uncovered place cannot be related to any aspect of the case, ");
			buffer
					.append("which seems odd. Note, however, that the net is sound even ");
			buffer.append("though some places are not covered.");
			buffer.append("<ol>");
			SortedSet<PetrinetNode> nodes = notSCoveredNodesSet.iterator()
					.next();
			for (PetrinetNode node : nodes) {
				if (node instanceof Place) {
					buffer.append("<li>" + warning(node.getLabel()) + "</li>");
				}
			}
			buffer.append("</ol>");
		}
	}

	/**
	 * Adds the HTML report to the given buffer, provided that the
	 * short-circuited net is unbounded.
	 * 
	 * @param buffer
	 *            The given buffer.
	 */
	private void addUnboundedDiagnosis(StringBuffer buffer) {
		buffer.append("<h2>" + error("The net is not a sound workflow net.")
				+ "</h2>");
		buffer.append("<h3>Soundness requirements</h3>");
		buffer.append("<dl>");
		buffer.append("<dt>" + strong("Option to complete") + "</dt>");
		buffer
				.append("<dd>Whatever happens, an instance can always mark the sink place</dd>");
		buffer.append("<dt>" + error("Proper completion") + "</dt>");
		buffer
				.append("<dd>On completion, only the sink place is marked, and it is marked only once</dd>");
		buffer.append("<dt>" + strong("No dead tasks") + "</dt>");
		buffer.append("<dd>No transition is dead</dd>");
		buffer.append("</dl>");
		buffer
				.append("<p>The short-circuited net is unbounded. As a result, completion cannot be proper.</p>");
		if (unboundedPlacesSet != null) {
			buffer
					.append("<p>The following places are unbounded in the short-circuited net:<ol>");
			SortedSet<Place> places = unboundedPlacesSet.iterator().next();
			for (Place place : places) {
				buffer.append("<li>" + error(place.getLabel()) + "</li>");
			}
			buffer.append("</ol></p>");
		}
		if (unboundedSequences != null) {
			buffer
					.append("<p>The following diagnostic information assumes that there exists a bounded safe haven ");
			buffer.append("(a bounded strongly connected component ");
			buffer
					.append("which includes the initial marking). Clearly, to avoid unbounded behavior, ");
			buffer
					.append("behavior should be restricted to this component. Thus, any transition leaving ");
			buffer.append("the component should be disabled.</p>");
			boolean all = (unboundedSequences.size() == 1);
			if (all) {
				MultiSet<PetrinetNode> nodes = unboundedSequences.iterator()
						.next();
				for (PetrinetNode node : nodes) {
					all = all && (node instanceof Transition);
				}
			}
			if (all) {
				buffer.append("<p>" + error("No bounded safe haven exist.")
						+ "</p>");
			} else {
				buffer
						.append("<p>Disabling the following transitions at the following (reachable) markings effectively would restrict ");
				buffer.append("the behavior to the bounded safe haven:<ol>");
				for (MultiSet<PetrinetNode> nodes : unboundedSequences) {
					MultiSet<Place> marking = new TreeMultiSet<Place>();
					Transition transition = null;
					for (PetrinetNode node : nodes) {
						if (node instanceof Transition) {
							transition = (Transition) node;
						} else {
							marking.add((Place) node);
						}
					}
					buffer.append("<li>Transition "
							+ error(transition.getLabel()));
					/**
					 * Strip any <html> or </html> from the HTML representation
					 * of the marking.
					 */
					String markingHTML = marking.toHTMLString(false);
					buffer.append(" at marking " + error(markingHTML));
					buffer.append("</li>");
				}
				buffer.append("</ol></p>");
			}
		}
		if (notSCoveredNodesSet != null) {
			buffer
					.append("<p>The following diagnostic information presents places that are ");
			buffer
					.append("not covered by any S-component. An S-component strongly relates ");
			buffer
					.append("to an aspect (say, a data field) of a case. Therefore, it is ");
			buffer
					.append("strongly recommended to have all places covered, and any ");
			buffer
					.append("uncovered place cannot be related to any aspect of the case, ");
			buffer
					.append("which seems odd. Note, however, that a net may be sound even ");
			buffer.append("if some places are not covered.");
			buffer.append("<ol>");
			SortedSet<PetrinetNode> nodes = notSCoveredNodesSet.iterator()
					.next();
			for (PetrinetNode node : nodes) {
				if (node instanceof Place) {
					buffer.append("<li>" + warning(node.getLabel()) + "</li>");
				}
			}
			buffer.append("</ol></p>");
			if (nonFreeChoiceClusters == null) {
				buffer
						.append("<p>A bounded, live, and free-choice net has to be S-coverable. ");
				buffer
						.append("The short-circuited net is not S-coverable, as some ");
				buffer.append("places are not covered by the S-components. ");
				buffer
						.append("As a result, the net is either unbounded, not live, or not free-choice. ");
				buffer
						.append("As the short-circuited net is free-choice, it cannot be live and bounded. ");
				buffer.append(error("Hence, the net cannot be sound. "));
				buffer
						.append("Possibly, the facts that the short-circuited net is free-choice but ");
				buffer.append("not S-coverable helps to diagnose the net.");
			} else if ((pTHandles == null) && (tPHandles == null)) {
				buffer
						.append("<p>A bounded, live, and well-handled net has to be S-coverable. ");
				buffer
						.append("The short-circuited net is not S-coverable, as some ");
				buffer.append("places are not covered by the S-components. ");
				buffer
						.append("As a result, the net is either unbounded, non live, or not well-handled. ");
				buffer
						.append("As the short-circuited net is well-handled (it contains PT handles nor TP handles), it cannot be live and bounded. ");
				buffer.append(error("Hence, the net cannot be sound. "));
				buffer
						.append("To make the net sound, cover the abovementioned places by S-components, or make the net not free-choice.");
			}
		}
		if (notPCoveredNodesSet != null) {
			buffer
					.append("<p>The following diagnostic information presents places that are ");
			buffer.append("not covered by any positive place invariant. ");
			buffer.append("Note, however, that a net may be sound even ");
			buffer.append("if some places are not covered.");
			buffer.append("<ol>");
			SortedSet<Place> places = notPCoveredNodesSet.iterator().next();
			for (Place place : places) {
				buffer.append("<li>" + strong(place.getLabel()) + "</li>");
			}
			buffer.append("</ol></p>");
		}
		if (tPHandles != null) {
			buffer
					.append("<p>The following diagnostic information presents transition-place ");
			buffer
					.append("pairs for which multiple disjoint paths exist from the transition to ");
			buffer
					.append("the place. Such pairs relate strongly to unbounded behavior, and, hence ");
			buffer
					.append("listing them might help detecting the root cause for the unboundedness. ");
			buffer.append("<ol>");
			for (Pair<Transition, Place> tPPair : tPHandles) {
				Transition transition = tPPair.getFirst();
				Place place = tPPair.getSecond();
				buffer.append("<li>Transition " + strong(transition.getLabel())
						+ ", place " + strong(place.getLabel()) + "</li>");
			}
			buffer.append("</ol></p>");
		}
	}

	/**
	 * Adds the HTML report to the given buffer, provided that the
	 * short-circuited net is not live.
	 * 
	 * @param buffer
	 *            The given buffer.
	 */
	private void addNonLiveDiagnosis(StringBuffer buffer) {
		buffer.append("<h2>" + error("The net is not a sound workflow net.")
				+ "</h2>");
		buffer.append("<h3>Soundness requirements</h3>");
		buffer.append("<dl>");
		buffer.append("<dt>" + error("Option to complete") + "</dt>");
		buffer
				.append("<dd>Whatever happens, an instance can always mark the sink place</dd>");
		buffer.append("<dt>" + okay("Proper completion") + "</dt>");
		buffer
				.append("<dd>On completion, only the sink place is marked, and it is marked only once</dd>");
		buffer.append("<dt>" + okay("No dead tasks") + "</dt>");
		buffer.append("<dd>No transition is dead</dd>");
		buffer.append("</dl>");
		buffer
				.append("<p>The short-circuited net is bounded, contains no dead transitions, ");
		buffer
				.append("but is not live. As a result, completion is not always possible.</p>");
		if (nonLiveTransitionsSet != null) {
			buffer
					.append("<p>The following transitions are not live in the short-circuited net</h3>");
			buffer.append("<ol>");
			SortedSet<Transition> transitions = nonLiveTransitionsSet
					.iterator().next();
			for (Transition transition : transitions) {
				buffer.append("<li>" + error(transition.getLabel()) + "</li>");
			}
			buffer.append("</ol>");
		}
		if (nonLiveSequences != null) {
			buffer
					.append("<p>The following diagnostic information assumes that there exists a part ");
			buffer.append("of the state space ");
			buffer
					.append("from which completion is still possible. Clearly, to avoid losing the ");
			buffer
					.append("option to complete, behavior should be restricted to this part. Thus, any transition leaving ");
			buffer.append("the part should be disabled.</p>");
			boolean all = nonLiveSequences.size() == 1;
			if (all) {
				MultiSet<PetrinetNode> nodes = nonLiveSequences.iterator()
						.next();
				for (PetrinetNode node : nodes) {
					all = all && (node instanceof Transition);
				}
			}
			if (all) {
				buffer
						.append("<p>"
								+ error("Such a part of the state space does not exist."));
				buffer
						.append("Most likely, the sink place cannot be marked.</p>");
			} else {
				buffer
						.append("<p>Disabling the following transitions at the following (reachable) markings effectively would restrict ");
				buffer
						.append("the behavior to the part from which completion is possible:<ol>");
				for (MultiSet<PetrinetNode> nodes : nonLiveSequences) {
					MultiSet<Place> marking = new TreeMultiSet<Place>();
					Transition transition = null;
					for (PetrinetNode node : nodes) {
						if (node instanceof Transition) {
							transition = (Transition) node;
						} else {
							marking.add((Place) node);
						}
					}
					buffer.append("<li>Transition "
							+ error(transition.getLabel()));
					/**
					 * Strip any <html> or </html> from the HTML representation
					 * of the marking.
					 */
					String markingHTML = marking.toHTMLString(false);
					buffer.append(" at marking " + error(markingHTML));
					buffer.append(".</li>");
				}
				buffer.append("</ol></p>");
			}
		}
		if (notSCoveredNodesSet != null) {
			buffer
					.append("<p>The following diagnostic information presents places that are ");
			buffer
					.append("not covered by any S-component. An S-component strongly relates ");
			buffer
					.append("to an aspect (say, a data field) of a case. Therefore, it is ");
			buffer
					.append("strongly recommended to have all places covered, and any ");
			buffer
					.append("uncovered place cannot be related to any aspect of the case, ");
			buffer
					.append("which seems odd. Note, however, that a net may be sound even ");
			buffer.append("if some places are not covered.");
			buffer.append("<ol>");
			SortedSet<PetrinetNode> nodes = notSCoveredNodesSet.iterator()
					.next();
			for (PetrinetNode node : nodes) {
				if (node instanceof Place) {
					buffer.append("<li>" + warning(node.getLabel()) + "</li>");
				}
			}
			buffer.append("</ol></p>");
			if (nonFreeChoiceClusters == null) {
				buffer
						.append("<p>A bounded, live, and free-choice net has to be S-coverable. ");
				buffer
						.append("The short-circuited net is not S-coverable, as some ");
				buffer.append("places are not covered by the S-components. ");
				buffer
						.append("As a result, the net is either unbounded, non live, or not free-choice. ");
				buffer
						.append("As the short-circuited net is bounded and free-choice, it cannot be live. ");
				buffer.append(error("Hence, the net cannot be sound. "));
				buffer
						.append("Possibly, the facts that the short-circuited net is free-choice but ");
				buffer.append("not S-coverable helps to diagnose the net.");
			} else if ((pTHandles == null) && (tPHandles == null)) {
				buffer
						.append("<p>A bounded, live, and well-handled net has to be S-coverable. ");
				buffer
						.append("The short-circuited net is not S-coverable, as some ");
				buffer.append("places are not covered by the S-components. ");
				buffer
						.append("As a result, the net is either unbounded, non live, or not well-handled. ");
				buffer
						.append("As the short-circuited net is bounded and  well-handled (it contains PT handles nor TP handles), it cannot be live. ");
				buffer.append(error("Hence, the net cannot be sound. "));
				buffer
						.append("Possibly, the facts that the short-circuited net is well-handles but ");
				buffer.append("not S-coverable helps to diagnose the net.");
			}
		}
		if (pTHandles != null) {
			buffer
					.append("<p>The following diagnostic information presents place-transition ");
			buffer
					.append("pairs for which multiple disjoint paths exist from the place to ");
			buffer
					.append("the transition. Such pairs relate strongly to non-live behavior, and, hence ");
			buffer
					.append("listing them might help detecting the root cause for the non-liveness. ");
			buffer.append("<ol>");
			for (Pair<Place, Transition> pTPair : pTHandles) {
				Place place = pTPair.getFirst();
				Transition transition = pTPair.getSecond();
				buffer.append("<li>Place " + strong(place.getLabel())
						+ ", transition " + strong(transition.getLabel())
						+ "</li>");
			}
			buffer.append("</ol></p>");
		}
	}

	/**
	 * Adds the HTML report to the given buffer, provided that the
	 * short-circuited net contains dead transitions.
	 * 
	 * @param buffer
	 *            The given buffer.
	 */
	private void addDeadDiagnosis(StringBuffer buffer) {
		buffer.append("<h2>" + error("The net is not a sound workflow net.")
				+ "</h2>");
		buffer.append("<h3>Soundness requirements</h3>");
		buffer.append("<dl>");
		buffer.append("<dt>" + strong("Option to complete") + "</dt>");
		buffer
				.append("<dd>Whatever happens, an instance can always mark the sink place</dd>");
		buffer.append("<dt>" + okay("Proper completion") + "</dt>");
		buffer
				.append("<dd>On completion, only the sink place is marked, and it is marked only once</dd>");
		buffer.append("<dt>" + error("No dead tasks") + "</dt>");
		buffer.append("<dd>No transition is dead</dd>");
		buffer.append("</dl>");
		buffer
				.append("<p>The short-circuited net is bounded, but contains dead transitions. ");
		buffer.append("As a result, the net contains dead tasks.</p>");
		if (deadTransitionsSet != null) {
			buffer.append("<ol>");
			SortedSet<Transition> transitions = deadTransitionsSet.iterator()
					.next();
			for (Transition transition : transitions) {
				buffer.append("<li>" + error(transition.getLabel()) + "</li>");
			}
			buffer.append("</ol>");
		}
	}

	/**
	 * Generates the HTML Woflan diagnosis report.
	 * 
	 * @return The Woflan diagnosis report in HTML format.
	 */
	public String toHTMLString(boolean includeHTMLTags) {
		StringBuffer buffer = new StringBuffer();

		if (includeHTMLTags) {
			buffer.append("<html>");
		}
		buffer.append("<head>");
		buffer.append("<title>Woflan Diagnosis on net \"" + net.getLabel()
				+ "\"</title>");
		buffer.append("</head><body><font fact=\"Arial\">");
		buffer.append("<h1>Woflan Diagnosis on Net \"" + net.getLabel()
				+ "\"</h1>");
		switch (diagnosis) {
		case NOWFNET: {
			addNoWFNetDiagnosis(buffer);
			break;
		}
		case SOUND: {
			addSoundDiagnosis(buffer);
			break;
		}
		case UNBOUNDED: {
			addUnboundedDiagnosis(buffer);
			break;
		}
		case NOTLIVE: {
			addNonLiveDiagnosis(buffer);
			break;
		}
		case DEAD: {
			addDeadDiagnosis(buffer);
			break;
		}
		}
		buffer.append("</font></body>");
		if (includeHTMLTags) {
			buffer.append("</html>");
		}
		return buffer.toString();
	}

	/**
	 * Returns a condense report in some format. Used for testing Woflan.
	 */
	public String toString() {
		String diagnosisText = "\nWoflan diagnosis of net \"" + net.getLabel()
				+ "\"\n";
		switch (diagnosis) {
		case NOWFNET: {
			diagnosisText += "- The net is not a WF-net\n";
			if (sourcePlacesSet != null) {
				SortedSet<Place> places = sourcePlacesSet.iterator().next();
				diagnosisText += "  - The number of source places is not correct (is "
						+ places.size() + ", should be 1)\n";
				for (Place place : places) {
					diagnosisText += "    - " + place.getLabel() + "\n";
				}
			}
			if (sinkPlacesSet != null) {
				SortedSet<Place> places = sinkPlacesSet.iterator().next();
				diagnosisText += "  - The number of sink places is not correct (is "
						+ places.size() + ", should be 1)\n";
				for (Place place : places) {
					diagnosisText += "    - " + place.getLabel() + "\n";
				}
			}
			if (unconnectedNodesSet != null) {
				SortedSet<PetrinetNode> nodes = unconnectedNodesSet.iterator()
						.next();
				diagnosisText += "  - The number of unconnected nodes is not correct (is "
						+ nodes.size() + ", should be 0)\n";
				for (PetrinetNode node : nodes) {
					diagnosisText += "    - " + node.getLabel() + "\n";
				}
			}
			break;
		}
		case SOUND: {
			diagnosisText += "- The net is sound\n";
			break;
		}
		case NOTLIVE: {
			diagnosisText += "- The net is not live\n";
			SortedSet<Transition> transitions = nonLiveTransitionsSet
					.iterator().next();
			for (Transition transition : transitions) {
				diagnosisText += "  - " + transition.getLabel() + "\n";
			}
			break;
		}
		case DEAD: {
			diagnosisText += "- The net contains dead transitions\n";
			SortedSet<Transition> transitions = deadTransitionsSet.iterator()
					.next();
			for (Transition transition : transitions) {
				diagnosisText += "  - " + transition.getLabel() + "\n";
			}
			break;
		}
		case UNBOUNDED: {
			diagnosisText += "- The net is not bounded\n";
			SortedSet<Place> places = unboundedPlacesSet.iterator().next();
			for (Place place : places) {
				diagnosisText += "  - " + place.getLabel() + "\n";
			}
			break;
		}
		}
		diagnosisText += "End of Woflan diagnosis\n";
		return diagnosisText;
	}

	public Collection<Place> getSinkPlaces() {
		return sinkPlacesSet.iterator().next();
	}

	/*
	 * Methods for querying this diagnosis.
	 */

	/**
	 * @return Whether the net is bounded (null = unknown, true = bounded, false
	 *         = unbounded).
	 */
	public Boolean isBounded() {
		switch (diagnosis) {
		case NOWFNET:
			return null;
		case UNBOUNDED:
			return false;
		default:
			return true;
		}
	}

	/**
	 * @return Whether the net contains no dead transitions (null = unknown,
	 *         true = no dead transitions, false = dead transitions).
	 */
	public Boolean isNotDead() {
		switch (diagnosis) {
		case NOWFNET:
		case UNBOUNDED:
			return null;
		case DEAD:
			return false;
		default:
			return true;
		}
	}

	/**
	 * @return Whether the net is live (null = unknown, true = live, false = not
	 *         live).
	 */
	public Boolean isLive() {
		switch (diagnosis) {
		case NOWFNET:
		case UNBOUNDED:
		case DEAD:
			return null;
		case NOTLIVE:
			return false;
		default:
			return true;
		}
	}

	/**
	 * @return The collection of unbounded places. Empty if boundedness is
	 *         unknown or true.
	 */
	public Collection<Place> getUnboundedPlaces() {
		return isBounded().equals(Boolean.FALSE) ? unboundedPlacesSet
				.iterator().next() : new HashSet<Place>();
	}

	/**
	 * @return The collection of dead transitions. Empty if existence of dead
	 *         transition is unknown or false.
	 */
	public Collection<Transition> getDeadTransitions() {
		return isNotDead().equals(Boolean.FALSE) ? deadTransitionsSet
				.iterator().next() : new HashSet<Transition>();
	}

	/**
	 * @return The collection of non-live transitions. Empty if liveness is
	 *         unknown or true.
	 */
	public Collection<Transition> getNonLiveTransitions() {
		return isLive().equals(Boolean.FALSE) ? nonLiveTransitionsSet
				.iterator().next() : new HashSet<Transition>();
	}

	/**
	 * Converts unboundedSequences or nonLiveSequences to collections of
	 * markings and transitions.
	 * 
	 * @param multisets
	 *            unboundedSequences or nonLiveSequences
	 * @return unboundedSequences or nonLiveSequences converted to a collection
	 *         of markings and transitions.
	 */
	private Collection<Pair<Marking, Transition>> getSequences(
			AbstractInvariantSet<PetrinetNode> multisets) {
		Collection<Pair<Marking, Transition>> sequences = new HashSet<Pair<Marking, Transition>>();
		for (MultiSet<PetrinetNode> nodes : multisets) {
			Marking marking = new Marking();
			Transition transition = null;
			for (PetrinetNode node : nodes) {
				if (node instanceof Transition) {
					transition = (Transition) node;
				} else {
					marking.add((Place) node);
				}
			}
			sequences.add(new Pair<Marking, Transition>(marking, transition));
		}
		return sequences;
	}

	/**
	 * @return The unbounded sequences. Every sequence consists of a marking and
	 *         a transition (enabled in this marking). From the marking,
	 *         unbounded behavior can still be avoided. After having fired the
	 *         transition in this marking, unbounded behavior has become
	 *         unavoidable.
	 */
	public Collection<Pair<Marking, Transition>> getUnboundedSequences() {
		return getSequences(unboundedSequences);
	}

	/**
	 * Precondition: net is bounded.
	 * 
	 * @return The non-live sequences. Every sequence consists of a marking and
	 *         a transition (enabled in this marking). From the marking, the
	 *         final marking can still be reached. After having fired the
	 *         transition in this marking, the final marking has become
	 *         unreachable.
	 */
	public Collection<Pair<Marking, Transition>> getNonLiveSequences() {
		return getSequences(nonLiveSequences);
	}

}
