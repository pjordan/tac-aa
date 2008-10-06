package edu.umich.eecs.tac.props;

import java.io.Serializable;
import java.text.ParseException;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class BankStatus implements Transportable, Serializable {

	/**Update this when class is complete.  This number is version dependent.
	 * 
	 */
	private static final long serialVersionUID = -8046738774880804420L;

	float balance;

	public BankStatus(float b){
		balance = b;
	}
	
	public double getAccountBalance(){
		return balance;
	}
	
	@Override
	public String getTransportName() {
		return "bankStatus";
	}

	@Override
	public void read(TransportReader reader) throws ParseException {
		balance = reader.getAttributeAsFloat("balance", 0.00F);
	}

	@Override
	public void write(TransportWriter writer) {
		writer.attr("balance", balance);
	}

}
