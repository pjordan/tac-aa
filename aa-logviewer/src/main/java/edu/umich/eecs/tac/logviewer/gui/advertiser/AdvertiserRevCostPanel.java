/*
 * AdvertiserRevCostPanel.java
 *
 * COPYRIGHT  2008
 * THE REGENTS OF THE UNIVERSITY OF MICHIGAN
 * ALL RIGHTS RESERVED
 *
 * PERMISSION IS GRANTED TO USE, COPY, CREATE DERIVATIVE WORKS AND REDISTRIBUTE THIS
 * SOFTWARE AND SUCH DERIVATIVE WORKS FOR NONCOMMERCIAL EDUCATION AND RESEARCH
 * PURPOSES, SO LONG AS NO FEE IS CHARGED, AND SO LONG AS THE COPYRIGHT NOTICE
 * ABOVE, THIS GRANT OF PERMISSION, AND THE DISCLAIMER BELOW APPEAR IN ALL COPIES
 * MADE; AND SO LONG AS THE NAME OF THE UNIVERSITY OF MICHIGAN IS NOT USED IN ANY
 * ADVERTISING OR PUBLICITY PERTAINING TO THE USE OR DISTRIBUTION OF THIS SOFTWARE
 * WITHOUT SPECIFIC, WRITTEN PRIOR AUTHORIZATION.
 *
 * THIS SOFTWARE IS PROVIDED AS IS, WITHOUT REPRESENTATION FROM THE UNIVERSITY OF
 * MICHIGAN AS TO ITS FITNESS FOR ANY PURPOSE, AND WITHOUT WARRANTY BY THE
 * UNIVERSITY OF MICHIGAN OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT
 * LIMITATION THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. THE REGENTS OF THE UNIVERSITY OF MICHIGAN SHALL NOT BE LIABLE FOR ANY
 * DAMAGES, INCLUDING SPECIAL, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, WITH
 * RESPECT TO ANY CLAIM ARISING OUT OF OR IN CONNECTION WITH THE USE OF THE SOFTWARE,
 * EVEN IF IT HAS BEEN OR IS HEREAFTER ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package edu.umich.eecs.tac.logviewer.gui.advertiser;

import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.logviewer.TACAAVisualizerConstants;
import edu.umich.eecs.tac.logviewer.gui.PositiveBoundedRangeModel;
import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;

import java.util.Set;
import java.util.HashSet;
import java.awt.*;


/**
 * @author Lee Callender
 */
public class AdvertiserRevCostPanel extends JPanel {
    private XYSeriesCollection seriescollection;
    private XYSeries revSeries;
    private XYSeries costSeries;
    private int currentDay;
    private Set<Query> queries;
    private boolean showBorder;
    private Advertiser advertiser;

    public AdvertiserRevCostPanel(Advertiser advertiser, PositiveBoundedRangeModel dm, GameInfo gameInfo,  boolean showBorder) {
        this.advertiser = advertiser;
        setBackground(TACAAVisualizerConstants.CHART_BACKGROUND);
        revSeries = new XYSeries("Revenue");
        costSeries = new XYSeries("Cost");
        seriescollection = new XYSeriesCollection();

        this.showBorder = showBorder;
        initialize();
    }

    private void initialize() {
        setLayout(new GridLayout(1, 1));
        if (showBorder) {
            setBorder(BorderFactory.createTitledBorder("Revenue and Cost"));
        }
        queries = new HashSet<Query>();
        seriescollection.addSeries(revSeries);
        seriescollection.addSeries(costSeries);

        /*JFreeChart chart = createDifferenceChart(showBorder ? null : advertiser, seriescollection);
        ChartPanel chartpanel = new ChartPanel(chart, false);
        chartpanel.setMouseZoomable(true, false);
        add(chartpanel);*/
    }

}
