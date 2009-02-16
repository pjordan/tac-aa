package edu.umich.eecs.tac;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.props.RetailCatalog;

/**
 * <code>BankStatusParser</code> is a simple example of a TAC AA
 * parser that prints out all advertiser's BankStatus received in
 * a simulation from the simulation log file.<p>
 *
 * The class <code>Parser</code> is inherited to
 * provide base functionality for TAC AA log processing.
 *
 * @see edu.umich.eecs.tac.Parser
 */
public class BankStatusParser extends Parser {
  private int day = 0;
  private String[] participantNames;
  private boolean[] is_Advertiser;
  private ParticipantInfo[] participants;

  public BankStatusParser(LogReader reader){
    super(reader);

    //Print agent indexes/gather names
    System.out.println("****AGENT INDEXES****");
    participants = reader.getParticipants();
		if (participants == null) {
			throw new IllegalStateException("no participants");
		}
    int agent;
    participantNames = new String[participants.length];
		is_Advertiser = new boolean[participants.length];
    for(int i = 0, n = participants.length; i < n; i++){
			ParticipantInfo info = participants[i];
			agent = info.getIndex();
			System.out.println(info.getName()+": "+agent);
			participantNames[agent] = info.getName();
			if(info.getRole() == TACAAConstants.ADVERTISER){
				is_Advertiser[agent] = true;
			}else
				is_Advertiser[agent] = false;
		}

    System.out.println("****BANK STATUS DATA****");
    System.out.println("Agent, Day, Bank Status");

  }

  // -------------------------------------------------------------------
  // Callbacks from the parser.
  // Please see the class edu.umich.eecs.tac.Parser for more callback
  // methods.
  // -------------------------------------------------------------------


  /**
	 * Invoked when a message to a specific receiver is encountered in the log
	 * file. Example of this is the offers sent by the manufacturers to the
	 * customers.
	 *
	 * @param sender
	 *            the sender of the message
	 * @param receiver
	 *            the receiver of the message
	 * @param content
	 *            the message content
	 */
  protected void message(int sender, int receiver, Transportable content) {
    if (content instanceof BankStatus &&
          participants[receiver].getRole() == TACAAConstants.ADVERTISER){

      BankStatus status = (BankStatus) content;
      System.out.println(participantNames[receiver]+","+day+","+(int)status.getAccountBalance());

    }else if(content instanceof SimulationStatus){
			SimulationStatus ss = (SimulationStatus) content;
			day = ss.getCurrentDate();
	  }
  }

  protected void dataUpdated(int type, Transportable content) {
    if (content instanceof StartInfo){
      StartInfo info = (StartInfo) content;

      //Do stuff with StartInfo
    }else if(content instanceof RetailCatalog){
      RetailCatalog catalog = (RetailCatalog) content;

      //Do stuff with RetailCatalog
    }
  }
}
