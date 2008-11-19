package edu.umich.eecs.tac.user;

/**
 * @author Patrick Jordan
 */
public enum QueryState {
    NON_SEARCHING(false,false),
    INFORMATIONAL_SEARCH(true,false),
    FOCUS_LEVEL_ZERO(true,false),
    FOCUS_LEVEL_ONE(true,false),
    FOCUS_LEVEL_TWO(true,false),
    TRANSACTED(false,false);

    private boolean searching;
    private boolean transacting;


    QueryState(boolean searching, boolean transacting) {
        this.searching = searching;
        this.transacting = transacting;
    }

    public boolean isSearching() {
        return searching;
    }

    public boolean isTransacting() {
        return transacting;
    }
}
