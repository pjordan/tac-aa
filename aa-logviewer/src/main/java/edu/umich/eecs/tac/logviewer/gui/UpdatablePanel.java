package edu.umich.eecs.tac.logviewer.gui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Mar 2, 2009
 * Time: 2:02:12 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class UpdatablePanel {
  JPanel mainPane;
  PositiveBoundedRangeModel dayModel;

  public UpdatablePanel(PositiveBoundedRangeModel dm){
    this.dayModel = dm;
    this.mainPane = new JPanel();

    if(dayModel != null) {
      dayModel.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent ce) {
			    updateMePlz();
		    }
		  });
    }
  }

  protected abstract void updateMePlz();

  public final Component getMainPane() {
    return mainPane;
  }
}
