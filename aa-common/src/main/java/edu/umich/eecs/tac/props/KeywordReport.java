package edu.umich.eecs.tac.props;
import java.io.Serializable;
import java.text.ParseException;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;


public class KeywordReport implements Serializable, Transportable {

	/**Update when this file is updated.
	 * 
	 */
	private static final long serialVersionUID = -4366560152538990286L;

	@Override
	public String getTransportName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void read(TransportReader reader) throws ParseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(TransportWriter writer) {
		// TODO Auto-generated method stub

	}

}
