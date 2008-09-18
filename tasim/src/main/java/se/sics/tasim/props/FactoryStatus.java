/**
 * TAC Supply Chain Management Simulator
 * http://www.sics.se/tac/    tac-dev@sics.se
 *
 * Copyright (c) 2001-2003 SICS AB. All rights reserved.
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
 * FactoryStatus
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Fri Feb 21 11:07:49 2003
 * Updated : $Date: 2008-04-04 21:07:49 -0500 (Fri, 04 Apr 2008) $
 *           $Revision: 3982 $
 */
package se.sics.tasim.props;

import java.text.ParseException;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

/**
 * <code>FactoryStatus</code> is an {@link InventoryStatus} extended with
 * information about factory utilization.
 * 
 * <p>
 * <b>Warning:</b> serialized objects of this class might not be compatible
 * with future versions. Only use serialization of this class for temporary
 * storage or RMI using the same version of the class.
 */
public class FactoryStatus extends InventoryStatus
{
  
  private static final long serialVersionUID = -6621544466562255259L;
  
  private float utilization;
  
  public FactoryStatus()
  {}
  
  public FactoryStatus(InventoryStatus is)
  {
    super(is);
  }
  
  public float getUtilization()
  {
    return utilization;
  }
  
  public void setUtilization(float utilization)
  {
    if (isLocked())
    {
      throw new IllegalStateException("locked");
    }
    this.utilization = utilization;
  }
  
  /*****************************************************************************
   * Transportable (externalization support)
   ****************************************************************************/
  
  /**
   * Returns the transport name used for externalization.
   */
  public String getTransportName()
  {
    return "factoryStatus";
  }
  
  public void read(TransportReader reader) throws ParseException
  {
    // This method will check if locked
    setUtilization(reader.getAttributeAsFloat("utilization", utilization));
    super.read(reader);
  }
  
  public void write(TransportWriter writer)
  {
    if (utilization != 0)
    {
      // Only need to send the utilization if not zero (optimization)
      writer.attr("utilization", utilization);
    }
    super.write(writer);
  }
  
} // FactoryStatus
