package edu.umich.eecs.tac.user;


import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public class DefaultUserQueryManagerTest {
    private RetailCatalog catalog;
    private Product product;
    private Random random;
    private String manufacturer;
    private String component;

    @Before
    public void setUp() {
        manufacturer = "ACME";
        component = "Widget";

        product = new Product(manufacturer,component);

        catalog = new RetailCatalog();
        catalog.addProduct(product);
        catalog.setSalesProfit(product, 1.0);

        random = new Random(100);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new DefaultUserQueryManager(catalog));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorRetailCatalogNull() {
        new DefaultUserQueryManager(null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorRandomNull() {
        new DefaultUserQueryManager(catalog,null);
    }

    @Test
    public void testQueryBehavior() {
        DefaultUserQueryManager manager = new DefaultUserQueryManager(catalog, random);
        manager.nextTimeUnit(0);
        
        User nsUser = new User();
        nsUser.setProduct(product);
        nsUser.setState(QueryState.NON_SEARCHING);
        
        assertEquals(manager.generateQuery(nsUser), null);

        User isUser = new User();
        isUser.setProduct(product);
        isUser.setState(QueryState.INFORMATIONAL_SEARCH);

        Query isQuery = manager.generateQuery(isUser);
        assertTrue(new Query().equals(isQuery) || new Query(manufacturer,null).equals(isQuery) || new Query(null,component).equals(isQuery) || new Query(manufacturer,component).equals(isQuery));

        User f0User = new User();
        f0User.setProduct(product);
        f0User.setState(QueryState.FOCUS_LEVEL_ZERO);

        assertEquals(manager.generateQuery(f0User), new Query());

        User f1User = new User();
        f1User.setProduct(product);
        f1User.setState(QueryState.FOCUS_LEVEL_ONE);

        Query f1Query = manager.generateQuery(f1User);
        assertTrue(new Query(manufacturer,null).equals(f1Query) || new Query(null,component).equals(f1Query));

        User f2User = new User();
        f2User.setProduct(product);
        f2User.setState(QueryState.FOCUS_LEVEL_TWO);

        assertEquals(manager.generateQuery(f2User), new Query(manufacturer, component));

        User tUser = new User();
        tUser.setProduct(product);
        tUser.setState(QueryState.TRANSACTED);

        assertEquals(manager.generateQuery(tUser), null);
    }
}
