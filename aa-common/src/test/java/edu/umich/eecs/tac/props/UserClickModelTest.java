package edu.umich.eecs.tac.props;

import java.text.ParseException;
import org.junit.Test;
import se.sics.isl.transport.BinaryTransportReader;
import se.sics.isl.transport.BinaryTransportWriter;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;
import static org.junit.Assert.*;

/**
 *
 * @author Kemal Eren
 */

public class UserClickModelTest {
    int num_advertisors;
    int num_manufacturers;
    int num_components;

    public UserClickModelTest() {
        num_advertisors = 3;
        num_manufacturers = 3;
        num_components = 3;
    }

    public UserClickModel getModel() {
        String[] advertisers = {"advertiser_1", "advertiser_2", "advertiser_3"};
        Query[] queries = new Query[10];

        String manufacturer = "manufacturer_";
        String component = "component_";

        Query f = new Query();
        queries[0]=(f);

        int j = 1;
        for(int i = 1; i <=9; i += 3) {
            // Create f1's
            queries[i] = new Query(manufacturer + Integer.toString(j) , null);
            queries[i+1] = new Query(null, component + Integer.toString(j));
            queries[i+2] = new Query(manufacturer + Integer.toString(j) , component + Integer.toString(j));
            j++;
        }

        UserClickModel defaultClickModel = new UserClickModel(queries, advertisers);
        for (int queryIndex = 0; queryIndex < defaultClickModel.queryCount(); queryIndex++) {
            double continuationProbability = ((double) queryIndex)/(defaultClickModel.queryCount()-1);
            defaultClickModel.setContinuationProbability(queryIndex, continuationProbability);

            for(int advertiserIndex = 0; advertiserIndex < defaultClickModel.advertiserCount(); advertiserIndex++) {
                double effect = (double) advertiserIndex/(defaultClickModel.advertiserCount()-1);
                defaultClickModel.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
            }
        }
        return defaultClickModel;
    }

    /**
     * Test of advertiserCount method, of class UserClickModel.
     */
    @Test
    public void testAdvertiserCount() {
        UserClickModel instance = new UserClickModel();
        int expResult = 0;
        int result = instance.advertiserCount();
        assertEquals(expResult, result);

        instance = getModel();
        assertEquals(instance.advertiserCount(), 3);

    }

    /**
     * Test of advertiser method, of class UserClickModel.
     */
    @Test
    public void testAdvertiser() {
        int index = 0;
        UserClickModel instance = new UserClickModel();
        String expResult = "";
        try{
            String result = instance.advertiser(index);
            fail();
        } catch (IndexOutOfBoundsException expected) {
            ; //Intentional semicolon.
        }


        instance = getModel();
        assertEquals(instance.advertiser(0), "advertiser_1");
        assertEquals(instance.advertiser(num_advertisors-1), "advertiser_3");

        try{
            index = num_advertisors;
            String result = instance.advertiser(index);
            fail();
        } catch (IndexOutOfBoundsException expected) {
            ; //Intentional semicolon.
        }
    }

    /**
     * Test of advertiserIndex method, of class UserClickModel.
     */
    @Test
    public void testAdvertiserIndex() {
        String advertiser = "";
        UserClickModel instance = new UserClickModel();
        int expResult = -1;
        int result = instance.advertiserIndex(advertiser);
        assertEquals(expResult, result);

        instance = getModel();

        result = instance.advertiserIndex(advertiser);
        assertEquals(expResult, result);

        advertiser = "advertiser_1";
        expResult = 0;
        result = instance.advertiserIndex(advertiser);
        assertEquals(expResult, result);

        advertiser = "advertiser_3";
        expResult = 2;
        result = instance.advertiserIndex(advertiser);
        assertEquals(expResult, result);
    }

    /**
     * Test of queryCount method, of class UserClickModel.
     */
    @Test
    public void testQueryCount() {
        UserClickModel instance = new UserClickModel();
        int expResult = 0;
        int result = instance.queryCount();
        assertEquals(expResult, result);

        instance = getModel();
        expResult = 10;
        result = instance.queryCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of query method, of class UserClickModel.
     */
    @Test
    public void testQuery() {
        int index = 0;
        UserClickModel instance = new UserClickModel();
        Query expResult = null;
        Query result;
        try{
            result = instance.query(index);
            fail();
        } catch (IndexOutOfBoundsException expected) {
            ; //Intentional semicolon.
        }
        
        instance = getModel();

        try {
            index = -1;
            result = instance.query(index);
            fail();
        } catch (IndexOutOfBoundsException expected) {
            ; //Intentional semicolon.
        }

        index = 0;
        expResult = new Query();
        result = instance.query(index);
        assertEquals(expResult, result);

        index = 1;
        result = instance.query(index);
        expResult = new Query("manufacturer_1", null);
        assertEquals(expResult, result);

        index = 2;
        result = instance.query(index);
        expResult = new Query(null, "component_1");
        assertEquals(expResult, result);

        index = 3;
        result = instance.query(index);
        expResult = new Query("manufacturer_1", "component_1");
        assertEquals(expResult, result);

        try {
            index = 11;
            result = instance.query(index);
            fail();
        } catch (IndexOutOfBoundsException expected) {
            ; //Intentional semicolon.
        }
    }

    /**
     * Test of queryIndex method, of class UserClickModel.
     */
    @Test
    public void testQueryIndex() {
        Query query = null;
        UserClickModel instance = new UserClickModel();
        int expResult = -1;
        int result = instance.queryIndex(query);
        assertEquals(expResult, result);

        instance = getModel();

        query = new Query();
        expResult = 0;
        result = instance.queryIndex(query);
        assertEquals(expResult, result);

        query = new Query("manufacturer_1", null);
        expResult = 1;
        result = instance.queryIndex(query);
        assertEquals(expResult, result);

        query = new Query(null, "component_1");
        expResult = 2;
        result = instance.queryIndex(query);
        assertEquals(expResult, result);

        query = new Query("manufacturer_1", "component_1");
        expResult = 3;
        result = instance.queryIndex(query);
        assertEquals(expResult, result);

        query = new Query("manufacturer_3", null);
        expResult = 7;
        result = instance.queryIndex(query);
        assertEquals(expResult, result);

        query = new Query(null, "component_2");
        expResult = 5;
        result = instance.queryIndex(query);
        assertEquals(expResult, result);

        query = new Query("manufactuerer_3", "component_2");
        expResult = -1;
        result = instance.queryIndex(query);
        assertEquals(expResult, result);

        query = new Query("manufacturer_3", "component_3");
        expResult = 9;
        result = instance.queryIndex(query);
        assertEquals(expResult, result);
    }

    /**
     * Test of getContinuationProbability method, of class UserClickModel.
     */
    @Test
    public void testGetContinuationProbability() {
        int queryIndex = 0;
        UserClickModel instance = new UserClickModel();
        double expResult = 0.0;
        double result;
        try {
             result = instance.getContinuationProbability(queryIndex);
            fail();
        }
        catch (IndexOutOfBoundsException expected) {
            ; //intentional
        }
        

        instance = getModel();
        queryIndex = 0;
        expResult = 0.00;
        result = instance.getContinuationProbability(queryIndex);
        assertEquals(expResult, result);

        queryIndex = 6;
        expResult = 6.0/9.0;
        result = instance.getContinuationProbability(queryIndex);
        assertEquals(expResult, result);

        queryIndex = 9;
        expResult = 1.0;
        result = instance.getContinuationProbability(queryIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of setContinuationProbability method, of class UserClickModel.
     */
    @Test
    public void testSetContinuationProbability() {
        int queryIndex = 0;
        double probability = 0.0;
        UserClickModel instance = new UserClickModel();
        try {
            instance.setContinuationProbability(queryIndex, probability);
            fail();
        } catch (IndexOutOfBoundsException expected) {
            ; //intentional
        }

        instance = getModel();

        try {
            queryIndex = -1;
            instance.setContinuationProbability(queryIndex, probability);
            fail();
        } catch (IndexOutOfBoundsException expected) {
            ; //intentional
        }

        probability = 1.00;
        queryIndex = 0;
        instance.setContinuationProbability(queryIndex, probability);
        assertEquals(instance.getContinuationProbability(queryIndex), probability);

        probability = 0.33;
        instance.setContinuationProbability(queryIndex, probability);
        assertEquals(instance.getContinuationProbability(queryIndex), probability);

        probability = 0.1;
        queryIndex = 1;
        instance.setContinuationProbability(queryIndex, probability);
        assertEquals(instance.getContinuationProbability(queryIndex), probability);
        
        instance.lock();
        try {
            instance.setContinuationProbability(queryIndex, probability);
            fail();
        } catch (IllegalStateException expected) {
            ; //intentional
        }
    }

    /**
     * Test of getAdvertiserEffect method, of class UserClickModel.
     */
    @Test
    public void testGetAdvertiserEffect() {
        int queryIndex = 0;
        int advertiserIndex = 0;
        UserClickModel instance = new UserClickModel();
        double expResult = 0.0;
        double result;
        try {
            result = instance.getAdvertiserEffect(queryIndex, advertiserIndex);
        }
        catch (IndexOutOfBoundsException expected) {
            ; //intentional
        }
        

        instance = getModel();

        queryIndex = 0;
        advertiserIndex = 0;
        expResult = 0.0;
        result = instance.getAdvertiserEffect(queryIndex, advertiserIndex);
        assertEquals(expResult, result);

        queryIndex = 0;
        advertiserIndex = 1;
        expResult = 1.0/2.0;
        result = instance.getAdvertiserEffect(queryIndex, advertiserIndex);
        assertEquals(expResult, result);

        queryIndex = 0;
        advertiserIndex = 2;
        expResult = 1.0;
        result = instance.getAdvertiserEffect(queryIndex, advertiserIndex);
        assertEquals(expResult, result);

        queryIndex = 9;
        advertiserIndex = 0;
        expResult = 0.0;
        result = instance.getAdvertiserEffect(queryIndex, advertiserIndex);
        assertEquals(expResult, result);

        queryIndex = 9;
        advertiserIndex = 2;
        expResult = 1.0;
        result = instance.getAdvertiserEffect(queryIndex, advertiserIndex);
        assertEquals(expResult, result);

        queryIndex = 3;
        advertiserIndex = 1;
        expResult = 1.0/2.0;
        result = instance.getAdvertiserEffect(queryIndex, advertiserIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of setAdvertiserEffect method, of class UserClickModel.
     */
    @Test
    public void testSetAdvertiserEffect() {
        int queryIndex = 0;
        int advertiserIndex = 0;
        double effect = 0.0;
        UserClickModel instance = new UserClickModel();
        try {
            instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
            fail();
        } catch (IndexOutOfBoundsException expected) {
            ; //intentional
        }

        instance = getModel();
        try {
            queryIndex = -1;
            instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
            fail();
        } catch (IndexOutOfBoundsException expected) {
            ; //intentional
        }


        try {
            queryIndex = 0;
            advertiserIndex = -1;
            instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
            fail();
        } catch (IndexOutOfBoundsException expected) {
            ; //intentional
        }

        queryIndex = 0;
        advertiserIndex = 0;
        effect = 0.33;
        instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
        assertEquals(instance.getAdvertiserEffect(queryIndex, advertiserIndex), effect);

        queryIndex = 0;
        advertiserIndex = 0;
        effect = 0.0;
        instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
        assertEquals(instance.getAdvertiserEffect(queryIndex, advertiserIndex), effect);

        queryIndex = 4;
        advertiserIndex = 0;
        effect = 0.93;
        instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
        assertEquals(instance.getAdvertiserEffect(queryIndex, advertiserIndex), effect);

        queryIndex = 4;
        advertiserIndex = 0;
        effect = 0.0;
        instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
        assertEquals(instance.getAdvertiserEffect(queryIndex, advertiserIndex), effect);
        
        queryIndex = 5;
        advertiserIndex = 1;
        effect = 0.23;
        instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
        assertEquals(instance.getAdvertiserEffect(queryIndex, advertiserIndex), effect);

        queryIndex = 5;
        advertiserIndex = 1;
        effect = 0.0;
        instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
        assertEquals(instance.getAdvertiserEffect(queryIndex, advertiserIndex), effect);

        queryIndex = 9;
        advertiserIndex = 2;
        effect = 0.53;
        instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
        assertEquals(instance.getAdvertiserEffect(queryIndex, advertiserIndex), effect);

        instance.lock();
        try {
            instance.setAdvertiserEffect(queryIndex, advertiserIndex, effect);
            fail();
        } catch (IllegalStateException expected) {
            ; //intentional
        }
    }

    public void testEmptyTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        UserClickModel instance = new UserClickModel();

        byte[] buffer = getBytesForTransportable(writer, instance);
        UserClickModel received = readFromBytes(reader, buffer, "UserClickModel");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(0, received.queryCount());
        assertEquals(0, received.advertiserCount());

        instance.lock();
        received = new UserClickModel();

        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "UserClickModel");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(0, received.queryCount());
        assertEquals(0, received.advertiserCount());
    }
        @Test
    public void testValidTransport() throws ParseException {
        BinaryTransportWriter writer = new BinaryTransportWriter();
        BinaryTransportReader reader = new BinaryTransportReader();
        reader.setContext(new AAInfo().createContext());

        UserClickModel instance = getModel();

        byte[] buffer = getBytesForTransportable(writer, instance);
        UserClickModel received = readFromBytes(reader, buffer, "UserClickModel");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(10, received.queryCount());
        assertEquals(3, received.advertiserCount());

        instance.lock();
        received = new UserClickModel();

        buffer = getBytesForTransportable(writer, instance);
        received = readFromBytes(reader, buffer, "UserClickModel");

        assertNotNull(instance);
        assertNotNull(received);
        assertEquals(10, received.queryCount());
        assertEquals(3, received.advertiserCount());
    }

    @Test(expected = IllegalStateException.class)
    public void testWriteToLocked() {
        UserClickModel instance = getModel();
        instance.lock();

        instance.setContinuationProbability(1, 0.5);
        instance.setAdvertiserEffect(0, 0, 0.5);
    }
}