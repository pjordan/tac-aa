package edu.umich.eecs.tac.viewer.auction;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.TACAAConstants;

import javax.swing.*;

import se.sics.isl.transport.Transportable;
import org.jfree.data.xy.XYSeries;

import java.util.*;
import java.util.List;
import java.awt.*;

import com.sun.tools.javac.util.Pair;

/**
 * @author Patrick Jordan
 */
public class AverageRankingPanel extends JPanel {
    private Query query;
    private int currentDay;
    private JList resultsList;
    private ResultsPageModel model;

    public AverageRankingPanel(Query query, TACAASimulationPanel simulationPanel) {
        this.query = query;
        this.model = new ResultsPageModel(query);
        this.currentDay = 0;
        simulationPanel.addViewListener(this.model);
        initialize();
    }

    private void initialize() {
        setBorder(BorderFactory.createTitledBorder(String.format("(%s,%s) average results", query.getManufacturer(), query.getComponent())));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        
        resultsList = new JList(model);
        resultsList.setCellRenderer(new AdRenderer(query));
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new GridLayout(1, 1));
        add(new JScrollPane(resultsList));
    }

    private static class ResultsItem implements Comparable<ResultsItem> {
        private String advertiser;
        private Ad ad;
        private double position;

        public ResultsItem(String advertiser, Ad ad, double position) {
            this.advertiser = advertiser;
            this.ad = ad;
            this.position = position;
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

        public int compareTo(ResultsItem o) {
            return -Double.compare(position,o.position);
        }
    }

    private static class ResultsPageModel extends AbstractListModel implements ViewListener {
        private Query query;
        private List<ResultsItem> results;

        private ResultsPageModel(Query query) {
            this.query = query;
            this.results = new ArrayList<ResultsItem>();
        }

        public int getSize() {
            return results.size();
        }

        public Object getElementAt(int index) {
            return results.get(index);
        }

        public void dataUpdated(int agent, int type, int value) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dataUpdated(int agent, int type, long value) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dataUpdated(int agent, int type, float value) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dataUpdated(int agent, int type, double value) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dataUpdated(int agent, int type, String value) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void dataUpdated(int agent, int type, Transportable value) {
            if (type == TACAAConstants.DU_QUERY_REPORT &&
                    value.getClass().equals(QueryReport.class)) {

                final QueryReport queryReport = (QueryReport) value;

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        results.clear();

                        for (String advertiser : queryReport.advertisers(query)) {
                            Ad ad = queryReport.getAd(query, advertiser);
                            double position = queryReport.getPosition(query, advertiser);

                            if(ad!=null && !Double.isNaN(position)) {
                                results.add(new ResultsItem(advertiser, ad, position));
                            }

                        }

                        Collections.sort(results);
                        fireContentsChanged(this, 0, getSize());
                    }
                });

            }
        }

        public void dataUpdated(int type, Transportable value) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void participant(int agent, int role, String name, int participantID) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private static class AdRenderer extends JLabel implements ListCellRenderer {
        private static final ImageIcon GENERIC = new ImageIcon(AdRenderer.class.getResource("/generic_regular.gif"));
        private static final Map<Product,ImageIcon> icons;

        static {
            icons = new HashMap<Product,ImageIcon>();
            for(String manufacturer : new String[] {"lioneer","pg","flat"}) {
                for(String component: new String[] {"tv","dvd","audio"}) {
                    icons.put(new Product(manufacturer,component), new ImageIcon(AdRenderer.class.getResource(String.format("/%s_%s_regular.gif",manufacturer,component))));    
                }
            }
        }

        private Query query;
        private String adCopy;

        private AdRenderer(Query query) {

            
            this.query = query;

            switch (query.getType()) {
                case FOCUS_LEVEL_ZERO:
                    adCopy = "products";
                    break;
                case FOCUS_LEVEL_ONE:
                    adCopy = String.format("<b>%s</b> products", query.getManufacturer(),
                                           query.getComponent(),
                                           query.getManufacturer());
                    break;
                case FOCUS_LEVEL_TWO:
                    adCopy = String.format("<b>%s %s</b> units", query.getManufacturer(),query.getComponent());
                    break;
            }
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            setBackground(list.getBackground());
            setForeground(list.getForeground());


            ResultsItem item = (ResultsItem) value;

            ImageIcon icon = icons.get(item.getAd().getProduct());
            if(icon==null) {
                icon = GENERIC;
            }
            setIcon(icon);
            setText(String.format("<html><font color='green'>Sale on %s</font><br><font color='blue'>From <b>%s</b>'s website</font></html>", adCopy, item.getAdvertiser()));
            setFont(list.getFont());
            
            return this;
        }
    }
}
