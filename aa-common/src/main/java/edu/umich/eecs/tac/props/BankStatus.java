//@author- Ben Cassell, Lee Callender

package edu.umich.eecs.tac.props;

import java.text.ParseException;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

/**
 * The bank status class holds an agent's account balance at a bank.
 */
public class BankStatus extends AbstractTransportable {

    /**
     * The serialization id.
     */
    private static final long serialVersionUID = -6576269032652384128L;

    /**
     * The account balance.
     */
    private double balance;

    /**
     * Create a new bank status object with a balance of zero.
     */
    public BankStatus() {
        balance = 0.0;
    }

    /**
     * Create a new bank status object with the supplied balance.
     *
     * @param b the balance
     */
    public BankStatus(final double b) {
        balance = b;
    }

    /**
     * Returns the account balance.
     * @return the account balance.
     */
    public final double getAccountBalance() {
        return balance;
    }

    /**
     * Sets the account balance.
     * @param b the account balance.
     */
    public final void setAccountBalance(final double b) {
        lockCheck();
        balance = b;
    }

    /**
     * Creates a string with the account balance.
     * @return a string with the account balance.
     */
    @Override
    public final String toString() {
        return String.format("%s[%f]", getTransportName(), balance);
    }

    /**
     * Read the balance parameter.
     * @param reader the reader to read from
     * @throws ParseException if a parse exception occurs reading the balance.
     */
    @Override
    protected final void readWithLock(final TransportReader reader) throws ParseException {
        balance = reader.getAttributeAsDouble("balance", 0.0);
    }

    /**
     * Write the balance parameter.
     * @param writer the writer to write to.
     */
    @Override
    protected final void writeWithLock(final TransportWriter writer) {
        writer.attr("balance", balance);
    }

}
