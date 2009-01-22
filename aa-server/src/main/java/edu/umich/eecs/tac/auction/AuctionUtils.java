package edu.umich.eecs.tac.auction;

/**
 * @author Patrick Jordan
 */
public class AuctionUtils {
	private AuctionUtils() {
	}

	public static void hardSort(double[] scores, int[] indices) {
		for (int i = 0; i < indices.length - 1; i++) {
			for (int j = i + 1; j < indices.length; j++) {
				if (scores[indices[i]] < scores[indices[j]]
						|| Double.isNaN(scores[indices[i]])) {
					int sw = indices[i];
					indices[i] = indices[j];
					indices[j] = sw;
				}
			}
		}
	}

	public static void generalizedSecondPrice(int[] indices, double[] weights,
			double[] bids, double[] cpc, boolean[] promoted, int promotedSlots,
			double promotedReserve, int regularSlots, double regularReserve) {
		int positions = Math.min(indices.length, regularSlots);

		int promotedCount = 0;

		for (int i = 0; i < positions; i++) {
			double weight = weights[indices[i]];
			double bid = bids[indices[i]];
			double secondWeight;
			double secondBid;

			if (i < indices.length - 1) {
				secondWeight = weights[indices[i + 1]];
				secondBid = bids[indices[i + 1]];
			} else {
				secondWeight = Double.NaN;
				secondBid = Double.NaN;
			}

			// Check if the ad can be a promoted slot
			if (promotedCount < promotedSlots
					&& weight * bid >= promotedReserve) {
				cpc[indices[i]] = calculateSecondPriceWithReserve(weight,
						secondWeight, secondBid, promotedReserve);
				promoted[indices[i]] = true;
				promotedCount++;

				// Check if the ad can be in a normal slot
			} else if (weight * bid >= regularReserve) {
				cpc[indices[i]] = calculateSecondPriceWithReserve(weight,
						secondWeight, secondBid, regularReserve);
				promoted[indices[i]] = false;

				// Reject the ad
			} else {
				cpc[indices[i]] = Double.NaN;
				promoted[indices[i]] = false;
			}
		}

		for (int i = positions; i < indices.length; i++) {
			cpc[indices[i]] = Double.NaN;
		}
	}

	public static double calculateSecondPriceWithReserve(double weight,
			double secondWeight, double secondBid, double reserve) {
		double price;

		if (reserve <= secondWeight * secondBid) {
			price = secondWeight / weight * secondBid;
		} else {
			price = reserve / weight;
		}

		return price;
	}
}
