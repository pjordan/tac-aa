package edu.umich.eecs.tac.viewer.auction;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;

import javax.swing.*;
import java.awt.*;

/**
 * @author Patrick Jordan
 */
public class AverageRankingPanel extends JPanel {
    private final ResultsPageModel model;

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

        JList resultsList = new JList(model);
        resultsList.setCellRenderer(new AdRenderer(getQuery()));
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new GridLayout(1, 1));
        add(new JScrollPane(resultsList));
    }    
}
