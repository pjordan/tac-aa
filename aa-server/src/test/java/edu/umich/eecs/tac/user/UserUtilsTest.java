package edu.umich.eecs.tac.user;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;

import static edu.umich.eecs.tac.user.UserUtils.*;
import edu.umich.eecs.tac.props.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Patrick Jordan
 */
public class UserUtilsTest {
    private static Product product;
    private static User user;
    private static Query query;
    private static AdvertiserInfo advertiserInfo;
    private static AdLink genericAdLink;
    private static AdLink focusedAdLink;
    private static AdLink focusedWrongAdLink;
    private static String advertiser;
    private static String manufacturer;
    private static String component;
    private static UserClickModel userClickModel;

    @BeforeClass
    public static void setup() {
        manufacturer = "man";
        component = "com";
        product = new Product(manufacturer, component);
        user = new User(QueryState.NON_SEARCHING, product);
        query = new Query();
        advertiserInfo = new AdvertiserInfo();
        advertiserInfo.setDistributionCapacity(2);
        advertiserInfo.setFocusEffects(QueryType.FOCUS_LEVEL_ZERO, 0.5);
        advertiserInfo.setDecayRate(0.5);
        advertiserInfo.setComponentBonus(2.0);
        advertiserInfo.setComponentSpecialty(component);
        advertiserInfo.setTargetEffect(0.5);

        advertiser = "alice";
        genericAdLink = new AdLink(null, advertiser);
        focusedAdLink = new AdLink(product, advertiser);
        focusedWrongAdLink = new AdLink(new Product(manufacturer, "not" + component), advertiser);

        userClickModel = new UserClickModel(new Query[]{query}, new String[]{advertiser});
        userClickModel.setAdvertiserEffect(0, 0, 0.8);

    }

    @Test(expected=IllegalAccessException.class)
    public void testConstructor() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor constructor = UserUtils.class.getDeclaredConstructor();
        constructor.newInstance();
    }

    @Test
    public void testModifyOdds() {
        assertEquals(modifyOdds(0.5, 2.0), 2.0 / 3.0);
    }

    @Test
    public void testModifySalesProfitForManufacturerSpecialty() {
        assertEquals(modifySalesProfitForManufacturerSpecialty(user, manufacturer, 0.5, 1.0), 1.5);
        assertEquals(modifySalesProfitForManufacturerSpecialty(user, "not" + manufacturer, 0.5, 1.0), 1.0);
    }

    @Test
    public void testModifyOddsForComponentSpecialty() {
        assertEquals(modifyOddsForComponentSpecialty(user, component, 1.0, 0.5), 2.0 / 3.0);
        assertEquals(modifyOddsForComponentSpecialty(user, "not" + component, 1.0, 0.5), 0.5);
    }

    @Test
    public void testCalculateConversionProbability() {
        assertEquals(calculateConversionProbability(user, query, advertiserInfo, 1.0), 0.75);
    }

    @Test
    public void testCalculateClickProbability() {
        assertEquals(calculateClickProbability(user, genericAdLink, advertiserInfo, 0.8), 0.8);
        assertEquals(calculateClickProbability(user, focusedAdLink, advertiserInfo, 0.8), 0.8571428571428572);
        assertEquals(calculateClickProbability(user, focusedWrongAdLink, advertiserInfo, 0.8), 0.761904761904762);
    }

    @Test
    public void testFindAdvertiserEffect() {

        assertEquals(findAdvertiserEffect(query, genericAdLink, userClickModel), 0.8);
        assertEquals(findAdvertiserEffect(query, new AdLink(product, "bob"), userClickModel), 0.0);
        assertEquals(findAdvertiserEffect(new Query("a", "b"), genericAdLink, userClickModel), 0.0);
    }
}
