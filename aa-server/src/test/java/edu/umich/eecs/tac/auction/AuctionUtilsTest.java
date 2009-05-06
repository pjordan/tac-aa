package edu.umich.eecs.tac.auction;

import org.junit.Test;
import static org.junit.Assert.*;
import static edu.umich.eecs.tac.auction.AuctionUtils.*;

/**
 * @author Patrick Jordan, Lee Callender
 */
public class AuctionUtilsTest {
	@Test
	public void testHardSort() {
		double[] scores = new double[] { 0.5, 1.0, Double.NaN, 0.25 };
		int[] indices = new int[] { 0, 1, 2, 3 };

		hardSort(scores, indices);

		assertEquals(indices[0], 1, 0);
		assertEquals(indices[1], 0, 0);
		assertEquals(indices[2], 3, 0);
		assertEquals(indices[3], 2, 0);
	}

	@Test
	public void testGeneralizedSecondPrice() {
		double[] scores = new double[] { 0.5, 1.0, Double.NaN, 0.25 };
		double[] bids = new double[] { 1.0, 1.0, 1.0, 1.0 };
		int[] indices = new int[] { 1, 0, 3, 2 };
		double[] cpc = new double[4];
		boolean[] promoted = new boolean[4];

		generalizedSecondPrice(indices, scores, bids, cpc, promoted, 1, 0.05,
				4, 0.01);

		assertEquals(cpc[0], 0.25 / 0.5, 0.0);
		assertEquals(cpc[1], 0.5, 0.0);
		assertTrue(Double.isNaN(cpc[2]));
		assertEquals(cpc[3], 0.01 / 0.25, 0.0);
	}

    @Test
	public void testAdditionalGeneralizedSecondPrice() {
		double[] scores = new double[] { 0.5, 1.0, Double.NaN, 0.25 };
		double[] bids = new double[] { 4.0, 3.0, 2.0, 1.0 };
		int[] indices = new int[] { 1, 0, 3, 2 };
		double[] cpc = new double[4];
		boolean[] promoted = new boolean[4];

		generalizedSecondPrice(indices, scores, bids, cpc, promoted, 1, 0.05,
				4, 0.01);

		assertEquals(cpc[0], 0.5, 0.0);
		assertEquals(cpc[1], 2.0, 0.0);
		assertTrue(Double.isNaN(cpc[2]));
		assertEquals(cpc[3], 0.01 / 0.25, 0.0);
	}

	@Test
	public void testCalculateSecondPriceWithReservee() {
		assertEquals(calculateSecondPriceWithReserve(1.0, 0.5, 1.0, 0.1), 0.5,
				0.0);
		assertEquals(calculateSecondPriceWithReserve(1.0, 0.5, 0.0, 0.1), 0.1,
				0.0);
		assertEquals(
				calculateSecondPriceWithReserve(1.0, 0.5, Double.NaN, 0.1),
				0.1, 0.0);
	}
}
