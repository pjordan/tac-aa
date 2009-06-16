package edu.umich.eecs.tac.viewer.role.publisher;

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.viewer.TACAAViewerConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Guha Balakrishnan
 */
public class LegendPanel extends JPanel {
    private SeriesTabPanel seriesTabPanel;
    private Color[] legendColors;

    public LegendPanel(SeriesTabPanel seriesTabPanel, Color[] legendColors) {
        super(new GridLayout(1, 0));
        this.seriesTabPanel = seriesTabPanel;
        this.legendColors = legendColors;
        initialize();
    }

    private void initialize() {

        int count = seriesTabPanel.getAgentCount();

        JTable table = new JTable(1, 2 * (seriesTabPanel.getAgentCount() - 2));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setBorder(BorderFactory.createEmptyBorder());

        for (int i = 0; i < table.getColumnCount(); i = i + 2) {
            table.getColumnModel().getColumn(i).setCellRenderer(
                    new LegendColorRenderer(legendColors[i / 2]));
            table.getColumnModel().getColumn(i).setPreferredWidth(1);

        }
        int advertiser = 0;
        for (int index = 0; index < count; index++) {
            if (seriesTabPanel.getRole(index) == TACAAConstants.ADVERTISER) {
                table.getColumnModel().getColumn(advertiser * 2 + 1).setCellRenderer(
                        new LegendTextRenderer(seriesTabPanel.getAgentName(index)));
                advertiser++;
            }
        }

        table.setGridColor(TACAAViewerConstants.CHART_BACKGROUND);

        add(table);

    }

    private class LegendColorRenderer extends DefaultTableCellRenderer {
        Color bkgndColor;

        public LegendColorRenderer(Color bkgndColor) {
            super();
            this.bkgndColor = bkgndColor;
        }

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {

            JLabel cell = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            cell.setBackground(bkgndColor);
            return cell;
        }
    }

    private class LegendTextRenderer extends DefaultTableCellRenderer {
        String agent;

        public LegendTextRenderer(String agent) {
            super();
            this.agent = agent;
        }

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel cell = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            cell.setText(agent);
            return cell;
        }
    }
}
