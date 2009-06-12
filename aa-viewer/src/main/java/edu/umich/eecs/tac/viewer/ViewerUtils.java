/**
 * Created by IntelliJ IDEA.
 * User: pjordan
 * Date: Jun 12, 2009
 * Time: 12:48:02 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.umich.eecs.tac.viewer;

import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.RetailCatalog;
import edu.umich.eecs.tac.props.Product;

import java.util.Set;

public class ViewerUtils {

    private ViewerUtils() {
    }

    public static void buildQuerySpace(Set<Query> queries, RetailCatalog retailCatalog) {
        queries.clear();

        for (Product product : retailCatalog) {
            Query f0 = new Query();
            Query f1Manufacturer = new Query(product.getManufacturer(), null);
            Query f1Component = new Query(null, product.getComponent());
            Query f2 = new Query(product.getManufacturer(), product.getComponent());
            
            queries.add(f0);
            queries.add(f1Manufacturer);
            queries.add(f1Component);
            queries.add(f2);
        }
    }
}
