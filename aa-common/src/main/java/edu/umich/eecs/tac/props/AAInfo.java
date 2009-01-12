package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Context;
import se.sics.isl.transport.ContextFactory;
import se.sics.tasim.props.*;

/**
 * @author Lee Callender
 */
public class AAInfo implements ContextFactory {
    public static final String CONTEXT_NAME = "aacontext";

    /**
     * Cache of the last created context (since contexts should be constants)
     */
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
        context.addTransportable(new BankStatus());
        context.addTransportable(new SimulationStatus());
        context.addTransportable(new StartInfo());
        context.addTransportable(new ServerConfig());
        context.addTransportable(new Query());
        context.addTransportable(new Product());
        context.addTransportable(new Ad());
        context.addTransportable(new AdLink());
        context.addTransportable(new SalesReport());
        context.addTransportable(new SalesReport.SalesReportEntry());
        context.addTransportable(new QueryReport());
        context.addTransportable(new QueryReport.QueryReportEntry());
        context.addTransportable(new QueryReport.DisplayReportEntry());
        context.addTransportable(new QueryReport.DisplayReport());
        context.addTransportable(new RetailCatalog());
        context.addTransportable(new RetailCatalog.RetailCatalogEntry());
        context.addTransportable(new BidBundle());
        context.addTransportable(new BidBundle.BidEntry());
        context.addTransportable(new Ranking());
        context.addTransportable(new Ranking.Slot());
        context.addTransportable(new Pricing());
        context.addTransportable(new UserClickModel());
        context.addTransportable(new Auction());
        context.addTransportable(new AdvertiserInfo());
        context.addTransportable(new ManufacturerComponentComposable());

        // Cache the last context
        lastContext = context;
        return context;
    }

} // AAInfo

