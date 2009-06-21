package edu.umich.eecs.tac.viewer.role.publisher;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Guha Balakrishnan
 */
class RankingRenderer extends DefaultTableCellRenderer {
    private Color bkgndColor;
    private Color fgndColor;
    private Font cellFont;

    public RankingRenderer(Color bkgnd, Color foregnd) {
        super();
        bkgndColor = bkgnd;
        fgndColor = foregnd;
        cellFont = new Font("serif", Font.BOLD, 12);
    }

    public Component getTableCellRendererComponent
            (JTable table, Object value, boolean isSelected,
             boolean hasFocus, int row, int column) {

        fgndColor  = ((RankingPanel.MyTableModel)table.getModel()).getRowFgndColor(row);
        bkgndColor = ((RankingPanel.MyTableModel)table.getModel()).getRowBkgndColor(row);


        if (value.getClass().equals(Double.class)) {
            value = round((Double) value, 3);
        }

        if(value.getClass() == Boolean.class){
            boolean targeted = (Boolean) value;
            JCheckBox checkBox = new JCheckBox();
            if (targeted) {
               checkBox.setSelected(true);
            }
            checkBox.setForeground(fgndColor);
            checkBox.setBackground(bkgndColor);
            checkBox.setHorizontalAlignment((int) JCheckBox.CENTER_ALIGNMENT);
            return checkBox;
        }

  
        JLabel cell = (JLabel) super.getTableCellRendererComponent(
                       table, value, isSelected, hasFocus, row, column);

        cell.setForeground(fgndColor);
        cell.setBackground(bkgndColor);
        cell.setFont(cellFont);
        cell.setHorizontalAlignment((int) JLabel.CENTER_ALIGNMENT);

        return cell;
    }

    public static double round(double Rval, int Rpl) {
        double p = Math.pow(10, Rpl);
        Rval = Rval * p;
        double tmp = Math.round(Rval);
        return tmp / p;
    }
}
