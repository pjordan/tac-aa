package edu.umich.eecs.tac.sim;

/**
 * @author Ben Cassell
 */

import java.util.HashMap;
import java.util.Map;

import se.sics.tasim.sim.SimulationAgent;

import edu.umich.eecs.tac.agents.DefaultPublisher;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.SlotInfo;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.SalesReport;

public class DummyTACAASimulation implements BankStatusSender {

	private RetailCatalog rc;
	private SlotInfo ai;
	private SalesAnalyst sa;
    private Map<String, AdvertiserInfo> advertiserInfo = new HashMap<String, AdvertiserInfo>();
    private SimulationAgent[] ps;
    
	public void sendBankStatus(String accountName, BankStatus status) {
		return;
	}

	public final void setup() {
		rc = new RetailCatalog();
		rc.addProduct(new Product("man", "com"));
		ai = new SlotInfo();
        SimpleAgentRepository repository = new SimpleAgentRepository();
        SimpleSalesReportSender salesReportSender = new SimpleSalesReportSender();
		sa = new DefaultSalesAnalyst(repository, salesReportSender, 1);
		ps = new SimulationAgent[1];
		ps[0] = new SimulationAgent(new DefaultPublisher(), "dp");
	}
	
	public RetailCatalog getRetailCatalog() {
		return rc;
	}

	public SlotInfo getAuctionInfo() {
		return ai;
	}

	public Map<String, AdvertiserInfo> getAdvertiserInfo() {
		return advertiserInfo;
	}

	public SimulationAgent[] getPublishers() {
		return ps;
	}

	public SimulationAgent[] getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	public SalesAnalyst getSalesAnalyst() {
		return sa;
	}

	public int getNumberOfAdvertisers() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String[] getAdvertiserAddresses() {
		// TODO Auto-generated method stub
		return null;
	}
    public class SimpleAgentRepository implements AgentRepository {
        public RetailCatalog getRetailCatalog() {
            return null;
        }

        public Map<String, AdvertiserInfo> getAdvertiserInfo() {
            return advertiserInfo;
        }

        public SimulationAgent[] getPublishers() {
            return new SimulationAgent[0];
        }

        public SimulationAgent[] getUsers() {
            return new SimulationAgent[0];
        }

        public SalesAnalyst getSalesAnalyst() {
            return sa;
        }

        public SlotInfo getAuctionInfo() {
            return ai;
        }


        public int getNumberOfAdvertisers() {
            return advertiserInfo.size();
        }


        public String[] getAdvertiserAddresses() {
            return advertiserInfo.keySet().toArray(new String[0]);
        }
    }

    public class SimpleSalesReportSender implements SalesReportSender {
        public void sendSalesReport(String advertiser, SalesReport report) {
        }

       public void broadcastConversions(String advertiser, int conversions) {
        }
    }
}
