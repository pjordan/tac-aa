package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.props.*;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.TACAAConstants;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import se.sics.isl.transport.Transportable;


public class RankingPanel extends JPanel {

    private Query query;
    private JTable table;
    private Map<Integer, String> names;
    private MyTableModel model;

    public RankingPanel(Query query, RankingTabPanel rankingTabPanel) {
        super(new GridLayout(1, 0));

        this.query = query;
        this.names = new HashMap<Integer, String>();

        initialize();

        rankingTabPanel.getSimulationPanel().addViewListener(new AuctionListener());

    }

    protected void initialize() {
        model = new MyTableModel();
        table = new JTable(model);

        table.setDefaultRenderer(String.class, new RankingRenderer(Color.white, Color.black));
        table.setDefaultRenderer(Double.class, new RankingRenderer(Color.white, Color.black));
        table.setGridColor(Color.white);

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

    private void handleQueryReport(int agent, QueryReport queryReport) {
        String name = names.get(agent);

        if (name != null) {
            Ad ad = queryReport.getAd(query);
            double position = queryReport.getPosition(query);
            model.handleQueryReportItem(name, ad, position);
        }
    }

    private void handleBidBundle(int agent, BidBundle bundle) {
        String name = names.get(agent);

        if (name != null) {

            double bid = bundle.getBid(query);

            if (!(BidBundle.PERSISTENT_BID == bid || Double.isNaN(BidBundle.PERSISTENT_BID) && Double.isNaN(bid))) {

                model.handleBidBundleItem(name, bid);
            }
        }
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
            if (type == TACAAConstants.DU_QUERY_REPORT && value.getClass().equals(QueryReport.class)) {

                handleQueryReport(agent, (QueryReport) value);

            } else if (type == TACAAConstants.DU_BIDS && value.getClass().equals(BidBundle.class)) {

                handleBidBundle(agent, (BidBundle) value);

            }
        }

        public void dataUpdated(int type, Transportable value) {

        }

        public void participant(int agent, int role, String name, int participantID) {
            if (role == TACAAConstants.ADVERTISER) {
                RankingPanel.this.names.put(agent, name);
            }
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


    private class MyTableModel extends AbstractTableModel {

        String[] columnNames = {"Avg. Position", "    Advertiser    ", "  Bid ($)  ", "Targeted"};
        List<ResultsItem> data;
        Map<String, ResultsItem> map;

        private MyTableModel() {
            data = new ArrayList<ResultsItem>();
            map = new HashMap<String, ResultsItem>();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return data.get(row).getPosition();
            } else if (col == 1) {
                return data.get(row).getAdvertiser();
            } else if (col == 2) {
                return data.get(row).getBid();
            } else if (col == 3) {
                return data.get(row).isTargeted();
            } else {
                return null;
            }
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public void handleQueryReportItem(String name, Ad ad, double position) {

            ResultsItem item = map.get(name);

            if (item != null) {
                data.remove(item);
            } else {
                item = new ResultsItem(name);
                map.put(name, item);
            }


            item.setAd(ad);
            item.setPosition(position);

            if (!Double.isNaN(position)) {
                data.add(item);
                Collections.sort(data);
            }

            fireTableDataChanged();
        }

        public void handleBidBundleItem(String name, double bid) {
            ResultsItem item = map.get(name);

            if (item != null) {
                data.remove(item);
            } else {
                item = new ResultsItem(name);
                map.put(name, item);
            }

            item.setBid(bid);

            if (!Double.isNaN(item.getPosition())) {
                data.add(item);
                Collections.sort(data);
            }


            fireTableDataChanged();
        }
    }

    private static class ResultsItem implements Comparable<ResultsItem> {
        private String advertiser;
        private Ad ad;
        private double position;
        private double bid;

        public ResultsItem(String advertiser) {
            this.advertiser = advertiser;
            this.position = Double.NaN;
            this.bid = Double.NaN;
        }

        public void setAd(Ad ad) {
            this.ad = ad;
        }

        public void setPosition(double position) {
            this.position = position;
        }

        public void setBid(double bid) {
            this.bid = bid;
        }

        public double getBid() {
            return bid;
        }

        public String getAdvertiser() {
            return advertiser;
        }

        public Ad getAd() {
            return ad;
        }

        public double getPosition() {
            return position;
        }

        public boolean isTargeted() {
            return getAd().getProduct() != null;
        }

        public int compareTo(ResultsItem o) {
            return Double.compare(position, o.position);
        }
    }
}

