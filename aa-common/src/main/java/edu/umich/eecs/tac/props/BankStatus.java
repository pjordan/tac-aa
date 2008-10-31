
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

  private double balance;

	public BankStatus(){
		balance = 0.0d;
	}
	
	public BankStatus(double b){
		balance = b;
	}
	
	public double getAccountBalance(){
		return balance;
	}

  public void setAccountBalance(double b){
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
		return "bankStatus";
	}

	public void read(TransportReader reader) throws ParseException {
		balance = reader.getAttributeAsDouble("balance", 0.0d);
	}

	public void write(TransportWriter writer) {
		writer.attr("balance", balance);       //If (balance != 0)? See TAC-SCM version
	}

}
