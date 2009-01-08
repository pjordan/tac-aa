package edu.umich.eecs.tac.auction;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Patrick Jordan
 */
public class BudgetManagerImplTest {

    @Test
    public void testConstructor() {
        BudgetManagerImpl budgetManager = new BudgetManagerImpl();

        assertNotNull(budgetManager);
    }

    @Test
    public void testAddAdvertiser() {
        BudgetManagerImpl budgetManager = new BudgetManagerImpl();

        String advertiser = "Alice";

        assertEquals(budgetManager.size(),0);
        budgetManager.addAdvertiser(advertiser);
        assertEquals(budgetManager.size(),1);
        budgetManager.addAdvertiser(advertiser);
        assertEquals(budgetManager.size(),1);


        for(int i = 0; i < 8; i++) {
            budgetManager.addAdvertiser(""+i);
            assertEquals(budgetManager.size(),i+2);
        }
    }

    @Test
    public void testAddCost() {
        BudgetManagerImpl budgetManager = new BudgetManagerImpl();

        String advertiser = "Alice";
        String query = "q";
        double cost = 1.0;

        //budgetManager.addCost(advertiser,query,cost);

    }
}
