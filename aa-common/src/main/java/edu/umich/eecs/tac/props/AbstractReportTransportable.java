package edu.umich.eecs.tac.props;

import se.sics.isl.transport.TransportReader;
import se.sics.isl.transport.TransportWriter;

import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrick Jordan
 */
public abstract class AbstractReportTransportable<T extends ReportEntry> extends AbstractTransportable {
    private List<T> reportEntries;


    public AbstractReportTransportable() {
        this.reportEntries = new ArrayList<T>();
    }

    public final int size() {
        return reportEntries.size();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("(");
        builder.append(this.getClass().getSimpleName());

        for (T reportEntry : reportEntries) {
            builder.append(' ').append(reportEntry);
        }
        builder.append(')');

        return builder.toString();
    }

    protected final int findReportEntry(Query query) {
        for (int i = 0; i < reportEntries.size(); i++) {
            if (reportEntries.get(i).getQuery().equals(query))
                return i;
        }
        return -1;
    }

    protected final void readWithLock(TransportReader reader) throws ParseException {
        while (reader.nextNode(entryClass().getSimpleName(), false)) {
            reportEntries.add((T) reader.readTransportable());
        }
    }

    protected final void writeWithLock(TransportWriter writer) {
        for (T reportEntry : reportEntries) {
            writer.write(reportEntry);
        }
    }

    public final boolean containsQuery(Query query) {
        return findReportEntry(query)>-1;
    }

    protected final T getEntry(int index) {
        return reportEntries.get(index);
    }

    protected final void removeEntry(int index) {
        lockCheck();

        reportEntries.remove(index);
    }

    protected final int addEntry(T entry) {
        lockCheck();

        if(reportEntries.add(entry))
            return size()-1;
        else
            return -1;
    }

    protected final int addQuery(Query query) {
        
        if(query==null) {
            throw new NullPointerException("Query cannot be null");
        }
        
        if(reportEntries.add(createEntry(query)))
            return size()-1;
        else
            return -1;
    }

    protected abstract T createEntry(Query query);

    protected abstract Class entryClass();
}
