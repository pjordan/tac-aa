package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.AdLink;
import edu.umich.eecs.tac.props.AdvertiserInfo;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.UserClickModel;

/**
 * @author Patrick Jordan
 */
public class UserUtils {
    private UserUtils() {
    }

    public static double modifyOdds(double probability, double effect) {
        return probability * effect / (effect * probability + (1.0 - probability));
    }

    public static double modifySalesProfitForManufacturerSpecialty(User user, String manufacturerSpecialty, double MSB, double salesProfit) {
        if (manufacturerSpecialty.equals(user.getProduct().getManufacturer()))
            salesProfit *= (1.0+MSB);

        return salesProfit;
    }

    public static double modifyOddsForComponentSpecialty(User user, String componentSpecialty, double effect, double probability) {
        if (user.getProduct().getComponent().equals(componentSpecialty)) {
            probability = modifyOdds(probability, 1.0 + effect);
        }

        return probability;
    }

    public static double calculateConversionProbability(User user, Query query, AdvertiserInfo advertiserInfo, double sales) {
        double criticalSales = advertiserInfo.getDistributionCapacity();

        double probability = advertiserInfo.getFocusEffects(query.getType()) * Math.pow(advertiserInfo.getDecayRate(), Math.max(0.0, sales - criticalSales));

        probability = modifyOddsForComponentSpecialty(user, advertiserInfo.getComponentSpecialty(), advertiserInfo.getComponentBonus(), probability);

        return probability;
    }

    public static double calculateClickProbability(User user, AdLink ad, double targetEffect, double promotionEffect, double advertiserEffect) {

        double probability = advertiserEffect;

        if (!ad.isGeneric()) {
            if (user.getProduct().equals(ad.getProduct())) {
                probability = modifyOdds(probability, (1.0 + promotionEffect) * (1.0 + targetEffect) );
            } else {
                probability = modifyOdds(probability, (1.0 + promotionEffect) / (1.0 + targetEffect) );
            }
        } else {
            probability = modifyOdds(probability, 1.0 + promotionEffect );
        }

        return probability;
    }

    public static double findAdvertiserEffect(Query query, AdLink ad, UserClickModel userClickModel) {
        int advertiserIndex = userClickModel.advertiserIndex(ad.getAdvertiser());
        int queryIndex = userClickModel.queryIndex(query);


        return advertiserIndex >= 0 && queryIndex >= 0 ? userClickModel.getAdvertiserEffect(queryIndex, advertiserIndex) : 0.0;
    }
}
