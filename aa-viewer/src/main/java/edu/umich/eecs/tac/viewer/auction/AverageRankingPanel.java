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

//import com.sun.tools.javac.util.Pair;

/**
 * @author Patrick Jordan
 */
public class AverageRankingPanel extends JPanel {
    private JList resultsList;
    private ResultsPageModel model;

    public AverageRankingPanel(ResultsPageModel model) {
        this.model = model;

        initialize();
    }

    private Query getQuery() {
        return model.getQuery();
    }
    
    private void initialize() {
        setBorder(BorderFactory.createTitledBorder(String.format("(%s,%s) average results", getQuery().getManufacturer(), getQuery().getComponent())));
        setBackground(TACAAViewerConstants.CHART_BACKGROUND);
        
        resultsList = new JList(model);
        resultsList.setCellRenderer(new AdRenderer(getQuery()));
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new GridLayout(1, 1));
        add(new JScrollPane(resultsList));
    }    
}
