package edu.umich.eecs.tac.props;

/**
 * @author Patrick Jordan
 */
public enum QueryType {
    FOCUS_LEVEL_ZERO,
    FOCUS_LEVEL_ONE,
    FOCUS_LEVEL_TWO;

    public static QueryType value(Query query) {
        int components = 0;
        if(query.getManufacturer()!=null)
            components++;
        if(query.getComponent()!=null)
            components++;

        return values()[components];
    }

}
