package edu.umich.eecs.tac.props;

import java.util.*;

/**
 * This class provides a skeletal implementation of a list containing
 * {@link edu.umich.eecs.tac.props.KeyedEntry keyed entries} with supporting
 * methods for interacting entries specified by a given key.
 * 
 * @author Patrick Jordan
 */
public abstract class AbstractKeyedEntryList<T, S extends KeyedEntry<T>>
		extends AbstractTransportableEntryListBacking<S> implements Iterable<T> {

	/**
	 * Returns the index for the entry specified by the key.
	 * 
	 * @param key
	 *            the key for the entry to be found.
	 * @return the index for the entry specified by the key. <code>-1</code> if
	 *         the <code>key</code> is not in the list.
	 */
	public final int indexForEntry(T key) {

		for (int i = 0; i < size(); i++) {

			if (getEntry(i).getKey().equals(key)) {

				return i;

			}

		}

		return -1;
	}

	/**
	 * Returns an iterator over the keys in the list.
	 * 
	 * @return an iterator over the keys in the list.
	 */
	public Iterator<T> iterator() {
		return new KeyIterator<T>(entries.iterator());
	}

	/**
	 * Returns <code>true</code> if the key is in the list and
	 * <code>false</code> otherwise.
	 * 
	 * @param key
	 *            the key to check for containment.
	 * 
	 * @return <code>true</code> if the key is in the list and
	 *         <code>false</code> otherwise.
	 */
	public final boolean containsKey(T key) {
		return indexForEntry(key) > -1;
	}

	/**
	 * Adds a new key to the list. The {@link #createEntry} method creates the
	 * new entry with the specified key.
	 * 
	 * @param key
	 *            the key used to add the new entry.
	 * 
	 * @return the index of the newly generated entry.
	 * 
	 * @throws NullPointerException
	 *             if the <code>key</code> is <code>null</code>.
	 */
	protected final int addKey(T key) {

		if (key == null) {
			throw new NullPointerException("Key cannot be null");
		}

		return addEntry(createEntry(key));
	}

	/**
	 * Creates a new entry with the given key.
	 * 
	 * @param key
	 *            the key for the created entry.
	 * 
	 * @return the created entry with the given key.
	 */
	protected abstract S createEntry(T key);

	/**
	 * Returns the set of keys for the entries. A new set is created each time
	 * the method is called.
	 * 
	 * @return the set of keys for the entries.
	 */
	public Set<T> keys() {
		Set<T> keys = new HashSet<T>();

		for (int i = 0; i < size(); i++) {
			keys.add(getEntry(i).getKey());
		}

		return keys;
	}

	/**
	 * Returns the key for the entry at the <code>index</code>.
	 * 
	 * @param index
	 *            the index for the entry.
	 * 
	 * @return the key for the entry at the <code>index</code>.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (index < 0 || index >= size()).
	 */
	protected T getKey(int index) {
		return getEntry(index).getKey();
	}

	/**
	 * Returns the entry with the specified key.
	 * 
	 * @param key
	 *            the key used to identify the entry.
	 * 
	 * @return the entry with the specified key or <code>null</code> if the key
	 *         is not found.
	 */
	protected S getEntry(T key) {
		int index = indexForEntry(key);

		if (index < 0) {
			return null;
		} else {
			return getEntry(index);
		}
	}
}
