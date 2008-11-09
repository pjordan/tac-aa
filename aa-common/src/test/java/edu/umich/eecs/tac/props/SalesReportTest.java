package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;
import static edu.umich.eecs.tac.props.TransportableTestUtils.*;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.TransportWriter;
import se.sics.isl.transport.Transportable;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class SalesReportTest {

    @Test
    public void testEmptySalesReportEntry() {
        SalesReport.SalesReportEntry entry = new SalesReport.SalesReportEntry();
        assertNotNull(entry);
    }

    @Test
    public void testEmptySalesReport() {
        SalesReport report = new SalesReport();
        assertNotNull(report);
        assertFalse(report.containsQuery(new Query()));
    }

    @Test
    public void testBasicSalesReportEntry() {
        SalesReport.SalesReportEntry entry = new SalesReport.SalesReportEntry();
        assertNotNull(entry);

        assertNull(entry.getQuery());
        entry.setQuery(new Query());
        assertNotNull(entry.getQuery());

        assertEquals(entry.getConversions(), 0);
        entry.setConversions(1);
        assertEquals(entry.getConversions(), 1);

        assertEquals(entry.getRevenue(), 0.0);
        entry.setRevenue(1.5);
        assertEquals(entry.getRevenue(), 1.5);


        assertEquals(entry.getTransportName(), "SalesReportEntry");
        assertEquals(entry.toString(), "((Query (null,null)) conv: 1 rev: 1.500000)");

    }

    @Test
    public void testValidTransportOfSalesReportEntry() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());


        SalesReport.SalesReportEntry entry = new SalesReport.SalesReportEntry();
        entry.setQuery(new Query());
        entry.setConversions(1);
        entry.setRevenue(1.5);

        byte[] buffer = getBytesForTransportable(writer, entry);
        SalesReport.SalesReportEntry received = readFromBytes(reader, buffer, "SalesReportEntry");


        assertNotNull(entry);
        assertNotNull(received);

        assertEquals(received.getQuery(), new Query());
        assertEquals(received.getConversions(), 1);
        assertEquals(received.getRevenue(), 1.5);
        assertEquals(entry.getTransportName(), "SalesReportEntry");


    }

    @Test
    public void testValidTransportOfNullQuerySalesReportEntry() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());


        SalesReport.SalesReportEntry entry = new SalesReport.SalesReportEntry();
        entry.setConversions(1);
        entry.setRevenue(1.5);

        byte[] buffer = getBytesForTransportable(writer, entry);
        SalesReport.SalesReportEntry received = readFromBytes(reader, buffer, "SalesReportEntry");


        assertNotNull(entry);
        assertNotNull(received);

        assertNull(received.getQuery());
        assertEquals(received.getConversions(), 1);
        assertEquals(received.getRevenue(), 1.5);


        assertEquals(received.getTransportName(), "SalesReportEntry");
        assertEquals(received.toString(), "(null conv: 1 rev: 1.500000)");
    }

    @Test
    public void testValidTransportOfSalesReport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());


        SalesReport report = new SalesReport();
        report.setConversions(new Query(), 2);
        report.lock();
        
        byte[] buffer = getBytesForTransportable(writer, report);
        SalesReport received = readFromBytes(reader, buffer, "SalesReport");


        assertNotNull(report);
        assertNotNull(received);
        assertEquals(report.size(),1);
        assertEquals(received.size(),1);
        assertEquals(received.getConversions(new Query()),2);

        assertEquals(report.getTransportName(), "SalesReport");

        buffer = getBytesForTransportable(writer, new SalesReport());
        received = readFromBytes(reader, buffer, "SalesReport");
        assertFalse(received.isLocked());

    }

    @Test(expected = NullPointerException.class)
    public void testAddQueryToSalesReport() {
        SalesReport report = new SalesReport();
        assertEquals(report.size(), 0);

        report.addQuery(new Query());
        assertEquals(report.size(), 1);
        assertTrue(report.containsQuery(new Query()));


        report.addQuery(null);
    }

    @Test(expected = NullPointerException.class)
    public void testSetConversions() {
        SalesReport report = new SalesReport();
        assertEquals(report.size(), 0);

        report.setConversions(new Query(), 1);
        assertEquals(report.size(), 1);
        assertTrue(report.containsQuery(new Query()));
        assertEquals(report.getConversions(new Query()), 1);

        report.setConversions(new Query(), 3);
        assertEquals(report.size(), 1);
        assertEquals(report.getConversions(new Query()), 3);

        report.setConversions(null, 2);
    }

    @Test(expected = NullPointerException.class)
    public void testSetRevenue() {
        SalesReport report = new SalesReport();
        assertEquals(report.size(), 0);

        report.setRevenue(new Query(), 1.0);
        assertEquals(report.size(), 1);
        assertTrue(report.containsQuery(new Query()));
        assertEquals(report.getRevenue(new Query()), 1.0);


        report.setRevenue(new Query(), 3.0);
        assertEquals(report.size(), 1);
        assertEquals(report.getRevenue(new Query()), 3.0);

        report.setRevenue(null, 2);
    }

    @Test(expected = NullPointerException.class)
    public void testSetConversionsAndRevenue() {
        SalesReport report = new SalesReport();
        assertEquals(report.size(), 0);

        report.setConversionsAndRevenue(new Query(), 1, 2.0);
        assertEquals(report.size(), 1);
        assertTrue(report.containsQuery(new Query()));
        assertEquals(report.getConversions(new Query()), 1);
        assertEquals(report.getRevenue(new Query()), 2.0);

        report.setConversionsAndRevenue(new Query(), 3, 4.0);
        assertEquals(report.size(), 1);
        assertEquals(report.getConversions(new Query()), 3);
        assertEquals(report.getRevenue(new Query()), 4.0);

        report.setConversionsAndRevenue(null, 2, 3.0);
    }

    @Test
    public void testSalesReportGettersForEmptyQueries() {
        SalesReport report = new SalesReport();
        assertEquals(report.size(), 0);

        assertEquals(report.getConversions(null), 0);
        assertEquals(report.getRevenue(null), 0.0);
    }

    @Test
    public void testSalesReportToString() {
        SalesReport report = new SalesReport();
        report.addQuery(new Query());
        assertEquals(report.toString(),"(SalesReport ((Query (null,null)) conv: 0 rev: 0.000000))");
    }

    @Test
    public void testSalesReportAdders() {
        SalesReport report = new SalesReport();

        Query query = new Query();

        assertEquals(report.getConversions(query), 0);
        assertEquals(report.getRevenue(query), 0.0);
        report.addConversions(query, 2);
        report.addRevenue(query, 3.0);
        assertEquals(report.getConversions(query), 2);
        assertEquals(report.getRevenue(query), 3.0);
        report.addConversions(query, 2);
        report.addRevenue(query, 3.0);
        assertEquals(report.getConversions(query), 4);
        assertEquals(report.getRevenue(query), 6.0);

        report = new SalesReport();
        report.addRevenue(query, 3.0);
        assertEquals(report.getRevenue(query), 3.0);
    }
}
