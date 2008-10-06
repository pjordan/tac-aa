package edu.umich.eecs.tac.props;

import java.io.Serializable;
import java.text.ParseException;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

public class BankStatus implements Transportable, Serializable {

	/**If BankStatus is updated, update the ID
	 * 
	 */
	private static final long serialVersionUID = -6576269032652384128L;
	
	float balance;

	public BankStatus(float b){
		balance = b;
	}
	
	public double getAccountBalance(){
		return balance;
	}
	
	public String getTransportName() {
		return "bankStatus";
	}

	public void read(TransportReader reader) throws ParseException {
		balance = reader.getAttributeAsFloat("balance", 0.00F);
	}

	public void write(TransportWriter writer) {
		writer.attr("balance", balance);
	}

}
