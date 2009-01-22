//@author- Ben Cassell, Lee Callender

package edu.umich.eecs.tac.props;

import java.text.ParseException;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

public class BankStatus extends AbstractTransportable {

    /**
     * If BankStatus is updated, update the ID
     */
    private static final long serialVersionUID = -6576269032652384128L;

    private double balance;

    public BankStatus() {
        balance = 0.0;
    }

    public BankStatus(double b) {
        balance = b;
    }

    public double getAccountBalance() {
        return balance;
    }

    public void setAccountBalance(double b) {
        lockCheck();
        balance = b;
    }

    public String toString() {
        return String.format("%s[%f]", getTransportName(), balance);
    }

    // -------------------------------------------------------------------
    // Transportable (externalization support)
    // -------------------------------------------------------------------

    protected void readWithLock(TransportReader reader) throws ParseException {
        balance = reader.getAttributeAsDouble("balance", 0.0);
    }

    protected void writeWithLock(TransportWriter writer) {
        writer.attr("balance", balance);
    }

}
