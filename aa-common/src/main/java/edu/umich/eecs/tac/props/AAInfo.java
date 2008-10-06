package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Context;
import se.sics.isl.transport.ContextFactory;
import se.sics.tasim.props.*;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Oct 6, 2008
 * Time: 12:32:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class AAInfo implements ContextFactory{
    public static final String CONTEXT_NAME = "aacontext";

    /** Cache of the last created context (since contexts should be constants) */
    private static Context lastContext;

        // Prevent instances of this class
    public AAInfo() {
    }

    public Context createContext() {
      return createContext(null);
    }

    public Context createContext(Context parentContext) {
      Context context = lastContext;
      if (context != null && context.getParent() == parentContext) {
        return context;
      }

      context = new Context(CONTEXT_NAME, parentContext);
      // New in version 0.9.7
      context.addTransportable(new Ping());
      // New in version 0.9.6
      //context.addTransportable(new ActiveOrders());

      context.addTransportable(new Alert());
     /* context.addTransportable(new BankStatus());
      context.addTransportable(new BOMBundle());
      context.addTransportable(new ComponentCatalog());
      context.addTransportable(new DeliveryNotice());
      context.addTransportable(new DeliverySchedule());
      context.addTransportable(new FactoryStatus());
      context.addTransportable(new InventoryStatus());
      context.addTransportable(new MarketReport());
      context.addTransportable(new OfferBundle());
      context.addTransportable(new OrderBundle());
      context.addTransportable(new PriceReport());
      context.addTransportable(new ProductionSchedule());
      context.addTransportable(new RFQBundle()); */
      context.addTransportable(new SimulationStatus());
      context.addTransportable(new StartInfo());
      context.addTransportable(new ServerConfig());
      // Cache the last context
      lastContext = context;
      return context;
    }

  } // AAInfo

