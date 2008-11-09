package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractReportEntry implements ReportEntry {
    private Query query;

    public Query getQuery() {
        return query;
    }

    protected void setQuery(Query query) {
        this.query = query;
    }

    public String getTransportName() {
        return this.getClass().getSimpleName();
    }

    public void read(TransportReader reader) throws ParseException {
        readEntry(reader);

        if (reader.nextNode("Query", false)) {
            this.query = (Query) reader.readTransportable();
        }
    }

    public void write(TransportWriter writer) {
        writeEntry(writer);

        if (query != null) {
            writer.write(query);
        }
    }

    protected abstract void readEntry(TransportReader reader) throws ParseException;

    protected abstract void writeEntry(TransportWriter writer);
}
