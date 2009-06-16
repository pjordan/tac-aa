/**
 * @author Patrick R. Jordan
 */
package edu.umich.eecs.tac.viewer;

import edu.umich.eecs.tac.props.Product;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class GraphicUtils {
    private GraphicUtils() {
    }

    private static final ImageIcon GENERIC = new ImageIcon(GraphicUtils.class.getResource("/generic_regular.gif"));

    private static final Map<Product, ImageIcon> PRODUCT_ICONS;

    static {
        PRODUCT_ICONS = new HashMap<Product, ImageIcon>();
        for (String manufacturer : new String[]{"lioneer", "pg", "flat"}) {
            for (String component : new String[]{"tv", "dvd", "audio"}) {
                PRODUCT_ICONS.put(new Product(manufacturer, component), new ImageIcon(GraphicUtils.class.getResource(String.format("/%s_%s_regular.gif", manufacturer, component))));
            }
        }
    }

    private static final Map<String, ImageIcon> MANUFACTURER_ICONS;

    static {
        MANUFACTURER_ICONS = new HashMap<String, ImageIcon>();
        for (String name : new String[]{"lioneer", "pg", "flat"}) {
            MANUFACTURER_ICONS.put(name, new ImageIcon(GraphicUtils.class.getResource(String.format("/%s_thumb.gif", name))));
        }
    }

    private static final Map<String, ImageIcon> COMPONENT_ICONS;

    static {
        COMPONENT_ICONS = new HashMap<String, ImageIcon>();
        for (String name : new String[]{"tv", "dvd", "audio"}) {
            COMPONENT_ICONS.put(name, new ImageIcon(GraphicUtils.class.getResource(String.format("/%s_thumb.gif", name))));
        }
    }

    public static ImageIcon genericIcon() {
        return GENERIC;
    }

    public static ImageIcon iconForProduct(Product product) {
        return PRODUCT_ICONS.get(product);
    }

    public static ImageIcon iconForManufacturer(String manufacturer) {
        return MANUFACTURER_ICONS.get(manufacturer);
    }

    public static ImageIcon iconForComponent(String component) {
        return COMPONENT_ICONS.get(component);
    }


}
