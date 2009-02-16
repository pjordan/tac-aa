package edu.umich.eecs.tac.logviewer.gui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 1, 2009
 * Time: 2:08:36 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Adapted from TAC-SCM
 */
public class DayChanger {
    protected final int SLIDER_DELAY = 150;

    JPanel mainPane, buttonPane;
    JSlider daySlider;
    JButton nextDayButton, prevDayButton, lastDayButton, firstDayButton;
    JLabel dayLabel;

    ActionListener actionListeners = null;

    PositiveBoundedRangeModel dayModel;

    public DayChanger(PositiveBoundedRangeModel dm) {
	    dayModel = dm;

	    mainPane = new JPanel();
	    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
	    mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder(),
			    " Day Changer "));

	    buttonPane = new JPanel();
	    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));

	    daySlider = new JSlider();
	    daySlider.setMinimum(0);
	    daySlider.setMaximum(dayModel.getLast());
	    daySlider.setValue(dayModel.getCurrent());
	    daySlider.addChangeListener(new ChangeListener() {
		  int value = -1;

		  Timer timer = new Timer(SLIDER_DELAY, new ActionListener() {
			  public void actionPerformed(ActionEvent evt) {
			    dayModel.setCurrent(value);

			    // Stop timer if slider was released
			    if (!daySlider.getValueIsAdjusting())
				    timer.stop();
			  }
		  });

		public void stateChanged(ChangeEvent ce) {
		    value = daySlider.getValue();

		    // Start timer for updating if it isn't running
		    if(!timer.isRunning())
			timer.start();
		}
	    });

	dayLabel = new JLabel(dayModel.getCurrent() + " / " +
			      dayModel.getLast());

	dayModel.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent ce) {
		    daySlider.setMaximum(dayModel.getLast());
		    daySlider.setValue(dayModel.getCurrent());
		    dayLabel.setText(dayModel.getCurrent() +
				     " / " +
				     dayModel.getLast());
		}
	    });


	nextDayButton = new JButton(">");
	prevDayButton = new JButton("<");
	lastDayButton = new JButton(">|");
	firstDayButton = new JButton("|<");

	nextDayButton.setAlignmentX(Component.LEFT_ALIGNMENT);
	prevDayButton.setAlignmentX(Component.LEFT_ALIGNMENT);
	lastDayButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
	firstDayButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

	nextDayButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    dayModel.changeCurrent(1);
		}
	    });

	prevDayButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    dayModel.changeCurrent(-1);
		}
	    });

	firstDayButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    dayModel.setCurrent(0);
		}
	    });

	lastDayButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    dayModel.setCurrent(dayModel.getLast());
		}
	    });

	buttonPane.add(firstDayButton);
	buttonPane.add(prevDayButton);
	buttonPane.add(Box.createHorizontalGlue());
	buttonPane.add(dayLabel);
	buttonPane.add(Box.createHorizontalGlue());
	buttonPane.add(nextDayButton);
	buttonPane.add(lastDayButton);

	mainPane.add(buttonPane);
	mainPane.add(daySlider);
    }

  public JPanel getMainPane() {
	    return mainPane;
  }
} // DayChanger
