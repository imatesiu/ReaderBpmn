package framework.connections;

import java.util.UUID;



/**
 * ID of a connection. Need not to be persistent over serialization.
 * 
 * @author bfvdonge
 * 
 */
public interface ConnectionID extends Comparable<ConnectionID> {

	/**
	 * ProMID are used in Collections, so this method has to be implemented in
	 * all its subclasses.
	 * 
	 * @return
	 */
	public boolean equals(Object o);

	/**
	 * ProMID are used in HashMaps, so this method has to be implemented in all
	 * its subclasses.
	 * 
	 * @return
	 */
	public int hashCode();

	/**
	 * Determines equality between this ProMID and any given object. Basically
	 * checks for equality using the toString() methods of both objects.
	 * 
	 * @param o
	 *            the object to compare the string representation with
	 * @return true if the toString() of this provided object ID equals the
	 *         toString() of the given object parameter.
	 */
	public boolean equalsIgnoreType(Object o);

	/**
	 * Returns a UUID that is used for this object.
	 * 
	 * @return
	 */
	public UUID getUUID();

}
