package edu.umich.eecs.tac.props;

import se.sics.isl.transport.Transportable;

import java.io.Serializable;

/**
 *
 * @author Patrick Jordan
 */
public interface ReportEntry extends Serializable, Transportable {
    public Query getQuery();
}
