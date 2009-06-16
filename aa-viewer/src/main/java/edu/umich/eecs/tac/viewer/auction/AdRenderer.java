/*
 * AdRenderer.java
 * 
 * Copyright (C) 2006-2009 Patrick R. Jordan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.umich.eecs.tac.viewer.auction;

import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;

/**
 * @author Patrick R. Jordan
 */
public class AdRenderer extends DefaultListCellRenderer {
    private static final ImageIcon GENERIC = new ImageIcon(AdRenderer.class.getResource("/generic_regular.gif"));
    private static final Map<Product, ImageIcon> icons;

    static {
        icons = new HashMap<Product, ImageIcon>();
        for (String manufacturer : new String[]{"lioneer", "pg", "flat"}) {
            for (String component : new String[]{"tv", "dvd", "audio"}) {
                icons.put(new Product(manufacturer, component), new ImageIcon(AdRenderer.class.getResource(String.format("/%s_%s_regular.gif", manufacturer, component))));
            }
        }
    }

    private Query query;
    private String adCopy;
    private Map<String, String> textCache;

    public AdRenderer(Query query) {


        this.query = query;
        textCache = new HashMap<String, String>();

        switch (query.getType()) {
            case FOCUS_LEVEL_ZERO:
                adCopy = "products";
                break;
            case FOCUS_LEVEL_ONE:
                adCopy = String.format("<b>%s</b> products", query.getManufacturer(),
                        query.getComponent(),
                        query.getManufacturer());
                break;
            case FOCUS_LEVEL_TWO:
                adCopy = String.format("<b>%s %s</b> units", query.getManufacturer(), query.getComponent());
                break;
        }
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        ResultsItem item = (ResultsItem) value;

        ImageIcon icon = icons.get(item.getAd().getProduct());

        if (icon == null) {
            icon = GENERIC;
        }

        label.setIcon(icon);


        String text = textCache.get(item.getAdvertiser());
        if (text == null) {
            text = String.format("<html><font color='green'>Sale on %s</font><br><font color='blue'>From <b>%s</b>'s website</font></html>", adCopy, item.getAdvertiser());
            textCache.put(item.getAdvertiser(), text);
        }

        label.setText(text);


        return label;
    }
}
