/*
 * AdvertiserQueryInfoPanel.java
 * 
 * Copyright (C) 2006-2009 Patrick R. Jordan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.SalesReport;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.TACAAConstants;

import javax.swing.*;
import java.awt.*;

import se.sics.isl.transport.Transportable;

/**
 * @author Patrick R. Jordan
 */
public class AdvertiserQueryInfoPanel extends JPanel {
	private int agent;
	private String advertiser;
    private Query query;

    private int impressions;
	private int clicks;
	private int conversions;
    private double revenue;
    private double cost;

    private JLabel ctrLabel;
    private JLabel convRateLabel;
    private JLabel cpcLabel;
    private JLabel cpmLabel;
    private JLabel vpcLabel;
    private JLabel roiLabel;

    public AdvertiserQueryInfoPanel(int agent, String advertiser, Query query, TACAASimulationPanel simulationPanel) {
		this.agent = agent;
		this.advertiser = advertiser;
        this.query = query;

        initialize();

		simulationPanel.addViewListener(new DataUpdateListener());
	}

	private void initialize() {
		setLayout(new GridLayout(3,4));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);

        ctrLabel = new JLabel("---");
        add(new JLabel("CTR:"));
        add(ctrLabel);

        cpcLabel = new JLabel("---");
        add(new JLabel("CPC:"));
        add(cpcLabel);

        convRateLabel = new JLabel("---");
        add(new JLabel("Conv. Rate:"));
        add(convRateLabel);

        vpcLabel = new JLabel("---");
        add(new JLabel("VPC:"));
        add(vpcLabel);

        cpmLabel = new JLabel("---");
        add(new JLabel("CPM:"));
        add(cpmLabel);

        roiLabel = new JLabel("---");
        add(new JLabel("ROI:"));
        add(roiLabel);        

		setBorder(BorderFactory.createTitledBorder("Rate Metrics"));
	}

	public int getAgent() {
		return agent;
	}

	public String getAdvertiser() {
		return advertiser;
	}

    private class DataUpdateListener implements ViewListener {

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
            if (agent == AdvertiserQueryInfoPanel.this.agent) {
				switch (type) {
				case TACAAConstants.DU_SALES_REPORT:
					handleSalesReport((SalesReport)value);
					break;
				case TACAAConstants.DU_QUERY_REPORT:
					handleQueryReport((QueryReport)value);
					break;
				}
			}
		}

        private void handleQueryReport(QueryReport queryReport) {
            addImpressions(queryReport.getImpressions(query));
            addClicks(queryReport.getClicks(query));
            addCost(queryReport.getCost(query));

        }

        private void handleSalesReport(SalesReport salesReport) {
            addConversions(salesReport.getConversions(query));
            addRevenue(salesReport.getRevenue(query));
        }

        public void dataUpdated(int type, Transportable value) {
		}

		public void participant(int agent, int role, String name, int participantID) {
		}
	}

    protected void addRevenue(double revenue) {
		this.revenue += revenue;

		updateCTR();
        updateVPC();
        updateROI();
	}

    protected void addCost(double cost) {
		this.cost += cost;

		updateCPC();
        updateVPC();
        updateCPM();
        updateROI();
	}

    protected void addImpressions(int impressions) {
		this.impressions += impressions;

		updateCTR();
        updateCPM();
	}

	protected void addClicks(int clicks) {
		this.clicks += clicks;

		updateCTR();
		updateConvRate();
        updateCPC();
        updateVPC();
	}

	protected void addConversions(int conversions) {
		this.conversions += conversions;

		updateConvRate();
	}

	protected void updateCTR() {
		if (impressions > 0) {
			ctrLabel.setText(String.format("%.2f%%",(100.0 * ((double) clicks) / ((double) impressions))));
		} else {
			ctrLabel.setText("---");
		}
	}

	protected void updateConvRate() {
		if (clicks > 0) {
			convRateLabel.setText(String.format("%.2f%%",(100.0 * ((double) conversions)/ ((double) clicks))));
		} else {
			convRateLabel.setText("---");
		}
	}

    protected void updateCPC() {
		if (clicks > 0) {
			cpcLabel.setText(String.format("%.2f",cost/((double) clicks)));
		} else {
			cpcLabel.setText("---");
		}
	}

	protected void updateCPM() {
		if (impressions > 0) {
			cpmLabel.setText(String.format("%.2f",cost/ (impressions/1000.0)));
		} else {
			cpmLabel.setText("---");
		}
	}

    protected void updateROI() {
		if (cost > 0.0) {
			roiLabel.setText(String.format("%.2f%%",(100.0 * (revenue-cost)/cost)));
		} else {
			roiLabel.setText("---");
		}
	}

	protected void updateVPC() {
		if (clicks > 0) {
			vpcLabel.setText(String.format("%.2f%%",(100.0 * (revenue - cost)/ ((double) clicks))));
		} else {
			vpcLabel.setText("---");
		}
	}
}
