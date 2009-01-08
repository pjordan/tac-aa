//@author- Ben Cassell, Lee Callender

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

	private boolean isLocked = false;
	private double balance;

	public boolean isLocked(){
		return isLocked;
	}
	
	public void lock(){
		isLocked = true;
	}
	
    public BankStatus(){
		balance = 0.0;
	}
	
	public BankStatus(double b){
		balance = b;
	}
	
	public double getAccountBalance(){
		return balance;
	}

    public void setAccountBalance(double b){
		if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
        balance = b;
    }

    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getTransportName()).append('[')
    	.append(balance).append(']');

    	return sb.toString();
    }
    
    // -------------------------------------------------------------------
    //  Transportable (externalization support)
    // -------------------------------------------------------------------


    //Returns transport name used for externalization
    public String getTransportName() {
		return "bankstatus";
	}

	public void read(TransportReader reader) throws ParseException {
	    if(isLocked){
	    	throw new IllegalStateException("locked");
	    }
	    boolean lock = reader.getAttributeAsInt("lock", 0) > 0;
	    if(reader.nextNode("bankstatus", false)){
	    	this.setAccountBalance(reader.getAttributeAsDouble("balance", 0.0));
	    }
	    if(lock){
	        lock();
	    }
	}

	public void write(TransportWriter writer) {
		if (isLocked) {
			writer.attr("lock", 1);
	    }
		writer.node("bankstatus");
		writer.attr("balance", balance);       //If (balance != 0)? See TAC-SCM version
		writer.endNode("bankstatus");
	}

}
