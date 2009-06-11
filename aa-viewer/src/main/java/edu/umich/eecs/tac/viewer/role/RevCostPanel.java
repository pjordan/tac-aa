package edu.umich.eecs.tac.viewer.role;

import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.role.advertiser.AdvertiserRatioPanel;
import edu.umich.eecs.tac.TACAAConstants;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.ui.RectangleInsets;
import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.viewer.TickListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;


/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 9, 2009
 * Time: 12:31:24 PM
 * To change this template use File | Settings | File Templates.
 */

public class RevCostPanel extends SimulationTabPanel {

    private Map<String, AgentRevCostPanel> agentPanels;


    public RevCostPanel(TACAASimulationPanel simulationPanel) {
        super(simulationPanel);

        agentPanels = new HashMap<String, AgentRevCostPanel>();

        simulationPanel.addViewListener(new ParticipantListener());

        initialize();
    }

    private void initialize() {
        setLayout(new GridLayout(2, 4));
        Font font = new Font("font", Font.BOLD, 18);
        setBorder(BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder(),
                "Daily Revenue and Cost", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, font));
    }

    private class ParticipantListener implements ViewListener {

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
        }

        public void dataUpdated(int type, Transportable value) {
        }

        public void participant(int agent, int role, String name,
                int participantID) {
            if (!agentPanels.containsKey(name)
                && role == TACAAConstants.ADVERTISER) {
                AgentRevCostPanel agentRevCostPanel = new AgentRevCostPanel(
                                                  agent, name, getSimulationPanel());

                agentPanels.put(name, agentRevCostPanel);

                add(agentRevCostPanel);
            }
        }
    }
}
