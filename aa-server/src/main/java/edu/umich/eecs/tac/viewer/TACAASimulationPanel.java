package edu.umich.eecs.tac.viewer;

import se.sics.tasim.viewer.TickListener;
import se.sics.tasim.viewer.ViewerPanel;
import se.sics.isl.util.ConfigManager;

import javax.swing.*;

import com.botbox.util.ArrayUtils;

import java.awt.*;

/**
 * @author Patrick Jordan
 */
public class TACAASimulationPanel extends JPanel implements TickListener {

    private Icon[] backgroundIcons;
    private int[] backgroundIconInfo;
    private int iconCount;

    private TACAAAgentView[] agentViews = new TACAAAgentView[10];
    private int participants;
    private int lastTimeUnit = 0;


    private ViewerPanel viewerPanel;

    private boolean isRunning;

    public TACAASimulationPanel(ViewerPanel viewerPanel) {
        super(null);
        this.viewerPanel = viewerPanel;
        setLayout(new CardLayout());
        //setLayout(new SimulationLayout(this, SimulationLayout.Y_AXIS, 50, 2));
        //setBackground(Color.black);
    }

    public TACAAAgentView getAgentView(int agentID) {
        return agentID < participants ? agentViews[agentID] : null;
    }

    public String getAgentName(int agentIndex) {
        TACAAAgentView view = getAgentView(agentIndex);
        return view != null ? view.getName() : Integer.toString(agentIndex);
    }

    public int getHighestAgentIndex() {
        return participants;
    }

    public void addAgentView(TACAAAgentView view, int index, String name,
                             int role, String roleName, int container) {
        if (agentViews.length <= index) {
            agentViews = (TACAAAgentView[]) ArrayUtils.setSize(agentViews, index + 10);
        }
        if (participants <= index) {
            participants = index + 1;
        }
        view.init(this, index, name, role, roleName);
        agentViews[index] = view;
        add(view,view.getName());
    }

    public void removeAgentView(TACAAAgentView view) {
        int id = view.getIndex();
        if (id < participants) {
            agentViews[id] = null;
        }
        remove(view);
    }

    /**
     * ******************************************************************
     * setup and time handling
     * ********************************************************************
     */

    public void simulationStarted(long startTime, long endTime,
                                  int timeUnitCount) {
        // Clear any old items before start a new simulation
        clear();

        if (timeUnitCount < 1) timeUnitCount = 1;


        long currentTime = viewerPanel.getServerTime();

        if (!isRunning) {
            viewerPanel.addTickListener(this);
            isRunning = true;
        }
    }

    public void simulationStopped() {
        isRunning = false;
        viewerPanel.removeTickListener(this);

        repaint();
    }

    public void clear() {
        int participants = this.participants;
        this.participants = 0;
        for (int i = 0, n = participants; i < n; i++) {
            agentViews[i] = null;
        }
        // This must be done with event dispatch thread. FIX THIS!!!
        removeAll();
        repaint();
    }

    public void nextTimeUnit(int timeUnit) {

    }


    /**
     * ******************************************************************
     * TickListener interface
     * ********************************************************************
     */

    public void tick(long serverTime) {

    }

    public void simulationTick(long serverTime, int timeUnit) {
        if (timeUnit != lastTimeUnit) {
            lastTimeUnit = timeUnit;
            for (int i = 0; i < participants; i++) {
                TACAAAgentView view = agentViews[i];
                if(view!=null)
                    view.nextTimeUnit(serverTime, timeUnit);
            }
        }
    }


    /**
     * ******************************************************************
     * API towards the agent views
     * ********************************************************************
     */

    ConfigManager getConfig() {
        return viewerPanel.getConfig();
    }

    Icon getIcon(String name) {
        return viewerPanel.getIcon(name);
    }

    void showDialog(JComponent dialog) {
        viewerPanel.showDialog(dialog);
    }


}
