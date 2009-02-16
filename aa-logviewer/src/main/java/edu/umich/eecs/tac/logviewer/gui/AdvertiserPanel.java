package edu.umich.eecs.tac.logviewer.gui;

import edu.umich.eecs.tac.logviewer.info.Advertiser;
import edu.umich.eecs.tac.logviewer.info.GameInfo;
import edu.umich.eecs.tac.logviewer.monitor.ParserMonitor;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.BevelBorder;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 2, 2009
 * Time: 2:33:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdvertiserPanel {

    JPanel mainPane;
    JPanel diagramPane;
    JLabel name, manufacturer, component, capacity;
    PositiveRangeDiagram accountDiagram;
    Advertiser advertiser;

    PositiveBoundedRangeModel dayModel;
    AdvertiserWindow advertiserWindow;
    GameInfo gameInfo;
    ParserMonitor[] monitors;

    public AdvertiserPanel(final GameInfo gameInfo,
			     final Advertiser advertiser,
			     final PositiveBoundedRangeModel dayModel,
			     final ParserMonitor[] monitors) {
      this.dayModel = dayModel;
      this.advertiser = advertiser;
      this.gameInfo = gameInfo;
      this.monitors = monitors;
      mainPane = new JPanel();
      mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
      mainPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
      //mainPane.setMinimumSize(new Dimension(150, 100));
      //mainPane.setMaximumSize(new Dimension(150, 100));

      mainPane.addMouseListener(new MouseInputAdapter() {
	      public void mouseClicked(MouseEvent me) {
		      openAgentWindow();
	      }
	    });

      name = new JLabel(advertiser.getName());
      name.setForeground(advertiser.getColor());

      manufacturer = new JLabel("Manufacturer: "+advertiser.getManufacturerSpecialty());
      component = new JLabel("Component: "+advertiser.getComponentSpecialty());
      capacity = new JLabel("Capacity: "+advertiser.getDistributionCapacity());

      mainPane.add(name);  
      mainPane.add(manufacturer);
      mainPane.add(component);
      mainPane.add(capacity);
    }

    protected void openAgentWindow() {
      if(advertiserWindow == null) {
	      advertiserWindow = new AdvertiserWindow(gameInfo, advertiser, dayModel, monitors);
	      advertiserWindow.setLocationRelativeTo(mainPane);
	      advertiserWindow.setVisible(true);
	    } else if (advertiserWindow.isVisible())
	      advertiserWindow.toFront();
	    else
	      advertiserWindow.setVisible(true);

      int state = advertiserWindow.getExtendedState();
	    if ((state & AdvertiserWindow.ICONIFIED) != 0) {
	      advertiserWindow.setExtendedState(state & ~AdvertiserWindow.ICONIFIED);
	    }
    }

    public Component getMainPane() {
      return mainPane;
    }

} // AdvertiserPanel

