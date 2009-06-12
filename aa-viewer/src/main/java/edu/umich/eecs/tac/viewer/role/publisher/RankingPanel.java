package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.TACAAConstants;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class RankingPanel extends JPanel {

	private Query query;
	private int currentDay;
    private RankingTabPanel rankingTabPanel;
    private JTable table;
    private XYSeriesCollection seriescollection;
    private Map<String, XYSeries> bidSeries;
    private SortedMap auctionResults;
    private int reportsSeenToday;

	public RankingPanel(Query query, RankingTabPanel rankingTabPanel) {
        super(new GridLayout(1,0));

		this.query = query;
        this.rankingTabPanel = rankingTabPanel;
		currentDay = 0;
        bidSeries = new HashMap<String, XYSeries>();
        seriescollection = new XYSeriesCollection();
        auctionResults = new TreeMap();
        reportsSeenToday = 0;
        
		initialize();

		rankingTabPanel.getSimulationPanel().addViewListener(new AuctionListener());
		rankingTabPanel.getSimulationPanel().addTickListener(new DayListener());
    }

	protected void initialize() {
         initializeTable();
         int count = rankingTabPanel.getAgentCount();
		 for (int index = 0; index < count; index++) {
			if (rankingTabPanel.getRole(index) == TACAAConstants.ADVERTISER) {
				XYSeries series = new XYSeries(rankingTabPanel
						.getAgentName(index));
				bidSeries.put(rankingTabPanel.getAgentName(index), series);
				seriescollection.addSeries(series);
			}
		 }
	}
  
    protected void initializeTable(){
         table = new JTable(new MyTableModel());
         ((MyTableModel)table.getModel()).refreshTable();
         //table.setPreferredScrollableViewportSize(new Dimension(50, 70));
         //table.setFillsViewportHeight(true);
         table.setDefaultRenderer(String.class,
                                  new RankingRenderer(Color.white, Color.black));
         table.setDefaultRenderer(Double.class,
                                  new RankingRenderer(Color.white, Color.black));
        //table.getColumnModel().getColumn(1).setCellRenderer(new ImageRenderer());
         table.setGridColor(Color.white);
         //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
         initColumnSizes(table);
         JScrollPane scrollPane = new JScrollPane(table);
         scrollPane.setBorder(BorderFactory.createTitledBorder(
                                     "Auction For (" + query.getManufacturer() + " , "
                                                     + query.getComponent() + ")"));
         add(scrollPane);
    }

	public Query getQuery() {
		return query;
	}

	private class AuctionListener implements ViewListener {

		public void dataUpdated(int agent, int type, int value) {

		}

		public void dataUpdated(int agent, int type, long value) {
		}

		public void dataUpdated(int agent, int type, float value) {
		}

		public void dataUpdated(int agent, int type, double value) {
		}

		public void dataUpdated(int agent, int type, String value) {
		}

		public void dataUpdated(int agent, int type, Transportable value) {
             if(type == TACAAConstants.DU_QUERY_REPORT &&
                value.getClass().equals(QueryReport.class)){
                reportsSeenToday++;
                int index = rankingTabPanel.indexOfAgent(agent);

				String name = index < 0 ? null : rankingTabPanel.getAgentName(index);

                QueryReport queryReport = (QueryReport) value;

                if(name!=null){
                    Ad ad = queryReport.getAd(query,name);
                    if(ad!=null){
                        XYSeries timeSeries = bidSeries.get(name);

                        if(timeSeries != null &&
                           timeSeries.indexOf(currentDay-1) > 0){
                            
                            double bid = timeSeries.getY(timeSeries.
                                                         indexOf(currentDay-1)).doubleValue();
                            if(!Double.isNaN(bid)){
                                double position = queryReport.getPosition(query,name);
                                if(position >= 0 && !Double.isNaN(position)){
                                   Boolean targeted = !ad.isGeneric();
                                   auctionResults.put(position,
                                                     new AgentAuctionResult(name, bid, targeted));
                                }
                            }
                        }
                    }

                    if(reportsSeenToday == rankingTabPanel.getAgentCount() - 2){
                           reportsSeenToday = 0;
                           displayResults();
                    }
                } 
             }

             if (type == TACAAConstants.DU_BIDS
					&& value.getClass().equals(BidBundle.class)) {
                int index = rankingTabPanel.indexOfAgent(agent);
				String name = index < 0 ? null : rankingTabPanel
						.getAgentName(index);

				if (name != null) {
					XYSeries timeSeries = bidSeries.get(name);

					if (timeSeries != null) {

						BidBundle bundle = (BidBundle) value;

						double bid = bundle.getBid(query);
						if (!Double.isNaN(bid)) {

							timeSeries.addOrUpdate(currentDay, bid);
						}
					}
				}
			}
        }

		public void dataUpdated(int type, Transportable value) {

		}

		public void participant(int agent, int role, String name,
				int participantID) {
		}
	}

	protected class DayListener implements TickListener {

		public void tick(long serverTime) {
			RankingPanel.this.tick(serverTime);
		}

		public void simulationTick(long serverTime, int simulationDate) {
			RankingPanel.this.simulationTick(serverTime, simulationDate);
		}
	}

	protected void tick(long serverTime) {
	}

	protected void simulationTick(long serverTime, int simulationDate) {
        currentDay = simulationDate;
   	}

    private void displayResults(){
       ((MyTableModel)table.getModel()).refreshTable();
       Iterator iterator = auctionResults.keySet().iterator();
       int position = 0;
       while(iterator.hasNext()){
        	Object key = iterator.next();
            table.getModel().setValueAt(
                    ((AgentAuctionResult)auctionResults.get(key)).getName(), position, 1);
            table.getModel().setValueAt(
                    ((AgentAuctionResult)auctionResults.get(key)).getBid(), position, 2);
            table.getModel().setValueAt(
                    ((AgentAuctionResult)auctionResults.get(key)).getAdType(), position, 3);
            table.getModel().setValueAt(key, position, 0);
            position++;
       } 

    }
    private void initColumnSizes(JTable table) {
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            comp = headerRenderer.getTableCellRendererComponent(
                                   null, column.getHeaderValue(),
                                   false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            column.setPreferredWidth(headerWidth);
        }
    }

    private class AgentAuctionResult {
        String name;
        Double bid;
        Boolean targeted;

        public AgentAuctionResult(String name, Double bid, Boolean targeted){
            this.name = name;
            this.bid = bid;
            this.targeted = targeted;
        }
        private String getName(){
            return this.name;
        }
        private Double getBid(){
            return this.bid;
        }
        private Boolean getAdType(){
            return this.targeted;
        }
    }

    private class MyTableModel extends AbstractTableModel {

        String[] columnNames = {"Avg. Position","    Advertiser    ","  Bid ($)  ","Targeted"};
        Object[][] data;

        public void refreshTable(){
          Object[][]initData = {{" ", " ", " ",new Boolean(false)},
                                {" ", " ", " ",new Boolean(false)},
                                {" ", " ", " ",new Boolean(false)},
                                {" ", " ", " ",new Boolean(false)},
                                {" ", " ", " ",new Boolean(false)},
                                {" ", " ", " ",new Boolean(false)},
                                {" ", " ", " ",new Boolean(false)},
                                {" ", " ", " ",new Boolean(false)}};
          data = initData;
          fireTableDataChanged();
        }
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            return data.length;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableDataChanged();
        }
   }

}

