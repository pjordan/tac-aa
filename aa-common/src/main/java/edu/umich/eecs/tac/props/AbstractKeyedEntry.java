package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * This class provides a skeletal implementation of the {@link KeyedEntry}
 * interface.
 * 
 * @author Patrick Jordan
 */
public abstract class AbstractKeyedEntry < T > implements KeyedEntry < T > {
	private T key;

	/**
	 * Returns the key for the entry.
	 * 
	 * @return the key for the entry.
	 */
	public T getKey() {
		return key;
	}

	/**
	 * Sets the key for the entry.
	 * 
	 * @param key
	 *            the key for the entry.
	 */
	public void setKey(T key) {
		this.key = key;
	}

	/**
	 * Returns the {@link Class#getSimpleName() simple name} of the implementing
	 * class.
	 * 
	 * @return the {@link Class#getSimpleName() simple name} of the implementing
	 *         class.
	 */
	public String getTransportName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Reads the state from the {@link TransportReader}. The {@link #readEntry}
	 * method will be called first and then the {@link #readKey}.
	 * 
	 * @param reader
	 *            the reader to read the state in from.
	 * 
	 * @throws ParseException
	 *             if a parse exception occurs
	 */
	public void read(TransportReader reader) throws ParseException {
		readEntry(reader);

		readKey(reader);
	}

	/**
	 * Writes the state to the {@link TransportWriter}. The {@link #writeEntry}
	 * method will be called first and then the {@link #writeKey}.
	 * 
	 * @param writer
	 *            the writer to write the state to
	 */
	public void write(TransportWriter writer) {
		writeEntry(writer);

		writeKey(writer);
	}

	/**
	 * Reads the entry state from the {@link TransportReader}. The attributes
	 * should be read in first, then the nodes.
	 * 
	 * @param reader
	 *            the reader to read the state in from.
	 * 
	 * @throws ParseException
	 *             if a parse exception occurs
	 */
	protected abstract void readEntry(TransportReader reader)
			throws ParseException;

	/**
	 * Reads the entry key from the {@link TransportReader}. This method requirs
	 * that the key be in node form.
	 * 
	 * @param reader
	 *            the reader to read the key in from.
	 * 
	 * @throws ParseException
	 *             if a parse exception occurs
	 */
	protected abstract void readKey(TransportReader reader)
			throws ParseException;

	/**
	 * Writes the entry state to the {@link TransportWriter}. The attributes
	 * should be written in first, then the nodes.
	 * 
	 * @param writer
	 *            the writer to write the entry state to
	 */
	protected abstract void writeEntry(TransportWriter writer);

	/**
	 * Writes the entry key to the {@link TransportWriter}. The key must be
	 * written in node form.
	 * 
	 * @param writer
	 *            the writer to write the key to
	 */
	protected abstract void writeKey(TransportWriter writer);

}
