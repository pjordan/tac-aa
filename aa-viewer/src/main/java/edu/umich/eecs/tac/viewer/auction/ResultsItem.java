/*
 * ResultsItem.java
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

import edu.umich.eecs.tac.props.Ad;

/**
 * @author Patrick R. Jordan
 */
public class ResultsItem implements Comparable<ResultsItem> {
    private final String advertiser;
    private final Ad ad;
    private final double position;

    public ResultsItem(String advertiser, Ad ad, double position) {
        this.advertiser = advertiser;
        this.ad = ad;
        this.position = position;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    public Ad getAd() {
        return ad;
    }

    public double getPosition() {
        return position;
    }

    public int compareTo(ResultsItem o) {
        return Double.compare(getPosition(), o.getPosition());
    }
}
