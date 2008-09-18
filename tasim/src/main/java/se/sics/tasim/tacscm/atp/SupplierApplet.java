/**
 * TAC Supply Chain Management Simulator
 * http://www.sics.se/tac/    tac-dev@sics.se
 *
 * Copyright (c) 2001-2005 SICS AB. All rights reserved.
 *
 * SICS grants you the right to use, modify, and redistribute this
 * software for noncommercial purposes, on the conditions that you:
 * (1) retain the original headers, including the copyright notice and
 * this text, (2) clearly document the difference between any derived
 * software and the original, and (3) acknowledge your use of this
 * software in pertaining publications and reports.  SICS provides
 * this software "as is", without any warranty of any kind.  IN NO
 * EVENT SHALL SICS BE LIABLE FOR ANY DIRECT, SPECIAL OR INDIRECT,
 * PUNITIVE, INCIDENTAL OR CONSEQUENTIAL LOSSES OR DAMAGES ARISING OUT
 * OF THE USE OF THE SOFTWARE.
 *
 * -----------------------------------------------------------------
 *
 * SupplierApplet
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Tue Feb 22 14:30:40 2005
 * Updated : $Date: 2008-03-07 09:59:58 -0600 (Fri, 07 Mar 2008) $
 *           $Revision: 3824 $
 */
package se.sics.tasim.tacscm.atp;

import java.awt.BorderLayout;
import javax.swing.JApplet;

/**
 */
public class SupplierApplet extends JApplet
{
  
  private SupplierViewer sup = new SupplierViewer();
  
  public SupplierApplet()
  {
    getContentPane().add(sup, BorderLayout.CENTER);
    getContentPane().add(sup.getButtonPanel(), BorderLayout.SOUTH);
  }
  
  public void start()
  {
    sup.start();
  }
  
  public void stop()
  {
    sup.stop();
  }
  
} // SupplierApplet
