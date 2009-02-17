package edu.umich.eecs.tac.logviewer.util;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;

/**
 * Created by IntelliJ IDEA.
 * User: leecallender
 * Date: Feb 17, 2009
 * Time: 11:33:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class VisualizerUtils {
  private VisualizerUtils(){}

  public static String formatToString(final Ad ad){
    Product product = ad.getProduct();
    if(product == null)
      return new String("GENERIC");

    return String.format("(%s,%s)", product.getManufacturer(), product.getComponent());
  }

  public static String formatToString(final Query query){
    return String.format("(%s,%s)", query.getManufacturer(), query.getComponent());
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

}
