

package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.viewer.role.AgentSupport;
import edu.umich.eecs.tac.viewer.role.SimulationTabPanel;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.TACAAConstants;
import se.sics.isl.transport.Transportable;

import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItem;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: May 30, 2009
 * Time: 12:40:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesTabPanel extends SimulationTabPanel{
    private RetailCatalog catalog;
	private Map<Query, SeriesPanel> seriesPanels;
	private AgentSupport agentSupport;
    private LegendPanel legendPanel;

    
    public SeriesTabPanel(TACAASimulationPanel simulationPanel) {
		super(simulationPanel);

		agentSupport = new AgentSupport();

		simulationPanel.addViewListener(new BidBundleListener());
		simulationPanel.addViewListener(agentSupport);

		initialize();
	}

	private void initialize() {
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
		seriesPanels = new HashMap<Query, SeriesPanel >();
	}

	private void handleRetailCatalog(RetailCatalog retailCatalog) {
		this.catalog = retailCatalog;

		this.removeAll();
		seriesPanels.clear();

		for (Product product : retailCatalog) {
			// Create f0
			Query f0 = new Query();

			// Create f1's
			Query f1_manufacturer = new Query(product.getManufacturer(), null);
			Query f1_component = new Query(null, product.getComponent());

			// Create f2
			Query f2 = new Query(product.getManufacturer(), product
					.getComponent());

			if (!seriesPanels.containsKey(f0)) {
				seriesPanels.put(f0,  new SeriesPanel(f0, this, TACAAViewerConstants.LEGEND_COLORS));
			}
			if (!seriesPanels.containsKey(f1_manufacturer)) {
				seriesPanels.put(f1_manufacturer,
                                 new SeriesPanel(f1_manufacturer, this, TACAAViewerConstants.LEGEND_COLORS));
			}
			if (!seriesPanels.containsKey(f1_component)) {
			    seriesPanels.put(f1_component, new SeriesPanel(f1_component,this, TACAAViewerConstants.LEGEND_COLORS));
			}
			if (!seriesPanels.containsKey(f2)) {
				seriesPanels.put(f2, new SeriesPanel(f2, this, TACAAViewerConstants.LEGEND_COLORS));
			}
		}

		int panelCount = seriesPanels.size();
		int sideCount = (int) Math.ceil(Math.sqrt(panelCount));

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;
        c.ipady = 200;

        Iterator iterator = seriesPanels.keySet().iterator();
     
        for(int i = 0; i < 4 ; i++){
            for(int j = 0; j < 4; j++){
                SeriesPanel temp = seriesPanels.get(iterator.next());
                c.gridx = j;
                c.gridy = i;
                add(temp, c);
            }
        }

        legendPanel = new LegendPanel(this, TACAAViewerConstants.LEGEND_COLORS);

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 4;
        c.ipadx = 500;
        c.ipady = 0;
        c.gridwidth = 4;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(5,0,0,0);
        
        legendPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        c.anchor = GridBagConstraints.PAGE_END;
        add(legendPanel, c);
  	}

  	private class BidBundleListener implements ViewListener {

		public void dataUpdated(int agent, int type, int value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, long value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, float value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, double value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, String value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int agent, int type, Transportable value) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void dataUpdated(int type, Transportable value) {
			Class valueType = value.getClass();
			if (valueType == RetailCatalog.class) {
				handleRetailCatalog((RetailCatalog) value);
			}
		}

		public void participant(int agent, int role, String name,
				int participantID) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}
	}

	public int getAgentCount() {
		return agentSupport.size();
	}

	public int getAgent(int index) {
		return agentSupport.agent(index);
	}

	public int getRole(int index) {
		return agentSupport.role(index);
	}

	public int getParticipant(int index) {
		return agentSupport.participant(index);
	}

	public int indexOfAgent(int agent) {
		return agentSupport.indexOfAgent(agent);
	}

	public String getAgentName(int index) {
		return agentSupport.name(index);
	}
   


}

