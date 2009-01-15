package edu.umich.eecs.tac.props;

/**
 * This enumeration defined the possible query types:
 * <ul>
 *   <li><code>FOCUS_LEVEL_ZERO</code> - specifies neither the manufacturer nor the component</li>
 *   <li><code>FOCUS_LEVEL_ONE</code> - specifies either the manufacturer or the component, but not both</li>
 *   <li><code>FOCUS_LEVEL_TWO</code> - specifies both the manufacturer or the component</li>
 * </ul>
 *
 * @author Patrick Jordan
 */
public enum QueryType {
    FOCUS_LEVEL_ZERO,
    FOCUS_LEVEL_ONE,
    FOCUS_LEVEL_TWO;

    /**
     * Returns the query type of the given query.
     *
     * @param query the query whose type is to be determined.
     *
     * @return the query type of the given query.
     */
    public static QueryType value(Query query) {
        int components = 0;
        if(query.getManufacturer()!=null)
            components++;
        if(query.getComponent()!=null)
            components++;

        return values()[components];
    }

}
