package edu.umich.eecs.tac.viewer.role.publisher;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Guha Balakrishnan
 * Date: Jun 3, 2009
 * Time: 4:40:22 PM
 * To change this template use File | Settings | File Templates.
 */


class RankingRenderer extends DefaultTableCellRenderer
{
   Color bkgndColor, fgndColor;

   public RankingRenderer(Color bkgnd, Color foregnd) {
      super();
      bkgndColor = bkgnd;
      fgndColor = foregnd;
   }

   public Component getTableCellRendererComponent
	    (JTable table, Object value, boolean isSelected,
	     boolean hasFocus, int row, int column)
   {
      if(value.getClass().equals(Double.class)){
          value = round((Double)value, 3);
      }
      JLabel cell = (JLabel) super.getTableCellRendererComponent(
                               table, value, isSelected, hasFocus, row, column);
      cell.setBackground( bkgndColor );
      cell.setForeground( fgndColor );
      cell.setFont(new Font("serif", Font.BOLD, 12));
      cell.setHorizontalAlignment((int)JLabel.CENTER_ALIGNMENT);
      return cell;
   }
   public static double round(double Rval, int Rpl) {
      double p = (double)Math.pow(10,Rpl);
      Rval = Rval * p;
      double tmp = Math.round(Rval);
      return (double)tmp/p;
   }
}
