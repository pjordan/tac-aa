package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Context;
import se.sics.isl.transport.ContextFactory;
import se.sics.tasim.props.*;

/**
 * {@link AAInfo} is a context factory that provides the allowable transports
 * for the TAC/AA simulation.
 *
 * @author Lee Callender, Patrick Jordan
 */
public class AAInfo implements ContextFactory {
    /**
     * Basic context name.
     */
    private static final String CONTEXT_NAME = "aacontext";

    /**
     * Cache of the last created context (since contexts should be constants).
     */
    private static Context lastContext;

    /**
     * Creates a new AA context factory.
     */
    public AAInfo() {
    }

    /**
     * Adds the allowable transports to the context.
     *
     * @return the base context with new transports added.
     */
    public final Context createContext() {
        return createContext(null);
    }

    /**
     * Creates the allowable transports in a {@link Context}.
     *
     * @param parentContext the parent context
     * @return the context with new transports added.
     */
    public final Context createContext(final Context parentContext) {
        Context context = lastContext;
        if (context != null && context.getParent() == parentContext) {
            return context;
        }

        context = new Context(CONTEXT_NAME, parentContext);
        context.addTransportable(new Ping());

        context.addTransportable(new Alert());
        context.addTransportable(new BankStatus());
        context.addTransportable(new AdminContent());
        context.addTransportable(new SimulationStatus());
        context.addTransportable(new StartInfo());
        context.addTransportable(new SlotInfo());
        context.addTransportable(new ReserveInfo());
        context.addTransportable(new PublisherInfo());
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
        context.addTransportable(new UserPopulationState());
        context.addTransportable(new UserPopulationState.UserPopulationEntry());

        // Cache the last context
        lastContext = context;
        return context;
    }
}
