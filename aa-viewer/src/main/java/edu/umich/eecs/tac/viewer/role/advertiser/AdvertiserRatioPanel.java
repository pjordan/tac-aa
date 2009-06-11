package edu.umich.eecs.tac.viewer.role.advertiser;

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.viewer.ViewListener;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;

import javax.swing.*;
import java.awt.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.DialShape;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.Range;
import se.sics.isl.transport.Transportable;

/**
 * @author Patrick Jordan
 */
public class AdvertiserRatioPanel extends JPanel {
	private int agent;
	private String advertiser;

	private DefaultValueDataset ctrValue;
	private DefaultValueDataset convValue;

	private int impressions;
	private int clicks;
	private int conversions;
    private Boolean advertiserBorder;

	public AdvertiserRatioPanel(int agent, String advertiser,
			TACAASimulationPanel simulationPanel, Boolean advertiserBorder) {
		this.agent = agent;
		this.advertiser = advertiser;
        this.advertiserBorder = advertiserBorder;
		initialize();

		simulationPanel.addViewListener(new DataUpdateListener());
	}

	private void initialize() {
		setLayout(new GridLayout(2, 1));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);

		add(new ChartPanel(createCTRChart()));
		add(new ChartPanel(createConvChart()));

        if(advertiserBorder)
		  setBorder(BorderFactory.createTitledBorder(advertiser));
        else
          setBorder(BorderFactory.createTitledBorder("Click-Through and Conversion Rates"));
	}

	private JFreeChart createCTRChart() {
		return createChart("CTR", ctrValue = new DefaultValueDataset(0.0));
	}

	private JFreeChart createConvChart() {
		return createChart("Conv Rate", convValue = new DefaultValueDataset(0.0));
	}

	private JFreeChart createChart(String s, ValueDataset dataset) {
		MeterPlot meterplot = new MeterPlot(dataset);
		meterplot.setDialShape(DialShape.CHORD);
		meterplot.setRange(new Range(0.0D, 100D));
		meterplot.addInterval(new MeterInterval("", new Range(0, 100.0D),
				Color.lightGray, new BasicStroke(2.0F),
				new Color(0, 255, 0, 64)));
		meterplot.setNeedlePaint(Color.darkGray);
		meterplot.setDialBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
		meterplot.setDialOutlinePaint(Color.gray);
		meterplot.setMeterAngle(260);
		meterplot.setTickLabelsVisible(true);
		meterplot.setTickLabelFont(new Font("Dialog", 1, 10));
		meterplot.setTickLabelPaint(Color.darkGray);
		meterplot.setTickSize(5D);
		meterplot.setTickPaint(Color.lightGray);
		meterplot.setValuePaint(Color.black);
		meterplot.setValueFont(new Font("Dialog", 1, 14));
		meterplot.setUnits("%");
        JFreeChart chart = new JFreeChart(s, JFreeChart.DEFAULT_TITLE_FONT, meterplot, false);
        chart.setBackgroundPaint(TACAAViewerConstants.CHART_BACKGROUND);
        return chart;
	}

	public int getAgent() {
		return agent;
	}

	public String getAdvertiser() {
		return advertiser;
	}

	protected void addImpressions(int impressions) {
		this.impressions += impressions;

		updateCTR();
	}

	protected void addClicks(int clicks) {
		this.clicks += clicks;

		updateCTR();
		updateConvRate();
	}

	protected void addConversions(int conversions) {
		this.conversions += conversions;

		updateConvRate();
	}

	protected void updateCTR() {
		if (impressions > 0) {
			ctrValue.setValue(100.0 * ((double) clicks)
					/ ((double) impressions));
		} else {
			ctrValue.setValue(0.0D);
		}
	}

	protected void updateConvRate() {
		if (clicks > 0) {
			convValue.setValue(100.0 * ((double) conversions)
					/ ((double) clicks));
		} else {
			convValue.setValue(0.0D);
		}
	}

	private class DataUpdateListener implements ViewListener {

		public void dataUpdated(int agent, int type, int value) {
			if (agent == AdvertiserRatioPanel.this.agent) {
				switch (type) {
				case TACAAConstants.DU_IMPRESSIONS:
					addImpressions(value);
					break;
				case TACAAConstants.DU_CLICKS:
					addClicks(value);
					break;
				case TACAAConstants.DU_CONVERSIONS:
					addConversions(value);
					break;
				}
			}
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

		public void participant(int agent, int role, String name, int participantID) {
		}
	}
}
