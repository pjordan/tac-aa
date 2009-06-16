/*
 * ResultsPageModel.java
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

import edu.umich.eecs.tac.TACAAConstants;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryReport;
import edu.umich.eecs.tac.viewer.TACAASimulationPanel;
import edu.umich.eecs.tac.viewer.ViewListener;
import se.sics.isl.transport.Transportable;

import javax.swing.*;
import java.util.*;

/**
 * @author Patrick R. Jordan
 */
public class ResultsPageModel extends AbstractListModel implements ViewListener {
    private Query query;
    private List<ResultsItem> results;
    private Map<Integer,ResultsItem> items;
    private Map<Integer,String> names;

    public ResultsPageModel(Query query, TACAASimulationPanel simulationPanel) {
        this.query = query;
        this.results = new ArrayList<ResultsItem>();
        this.names = new HashMap<Integer, String>();
        this.items = new HashMap<Integer, ResultsItem>();

        simulationPanel.addViewListener(this);
    }

    public int getSize() {
        return results.size();
    }

    public Object getElementAt(int index) {
        return results.get(index);
    }

    public Query getQuery() {
        return query;
    }

    public void dataUpdated(int agent, int type, int value) {
    }

    public void dataUpdated(int agent, int type, long value) {
    }

    public void dataUpdated(int agent, int type, float value) {
    }

    public void dataUpdated(int agent, int type, double value) {
    }

    public void dataUpdated(int agent, int type, String value) {
    }

    public void dataUpdated(final int agent, int type, Transportable value) {
        if (type == TACAAConstants.DU_QUERY_REPORT &&
                value.getClass().equals(QueryReport.class)) {

            final QueryReport queryReport = (QueryReport) value;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    ResultsItem item = items.get(agent);

                    if (item != null) {
                        results.remove(item);
                    }

                    Ad ad = queryReport.getAd(query);

                    double position = queryReport.getPosition(query);

                    if (ad != null && !Double.isNaN(position)) {

                        String advertiser = names.get(agent);

                        if (advertiser != null) {

                            item = new ResultsItem(advertiser, ad, position);

                            results.add(item);

                            items.put(agent, item);

                        }

                    }


                    Collections.sort(results);
                    fireContentsChanged(this, 0, getSize());
                }
            });

        }
    }

    public void dataUpdated(int type, Transportable value) {
    }

    public void participant(int agent, int role, String name, int participantID) {
        if (role == TACAAConstants.ADVERTISER) {
            names.put(agent, name);
        }
    }    
}

