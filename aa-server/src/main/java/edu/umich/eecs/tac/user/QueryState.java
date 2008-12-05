package edu.umich.eecs.tac.user;

import java.util.Random;

/**
 * @author Patrick Jordan
 */
public enum QueryState {
    NON_SEARCHING(false,false,SuccessorTable.NON_SEARCHING){
    	QueryState generateSuccessor(){
    		return null;
    	} 
    },
    INFORMATIONAL_SEARCH(true,false,SuccessorTable.INFORMATIONAL_SEARCH){
    	QueryState generateSuccessor(){
    		return null;
    	} 
    },
    FOCUS_LEVEL_ZERO(true,false,SuccessorTable.FOCUS_LEVEL_ZERO){
    	QueryState generateSuccessor(){
    		return null;
    	} 
    },
    FOCUS_LEVEL_ONE(true,false,SuccessorTable.FOCUS_LEVEL_ONE){
    	QueryState generateSuccessor(){
    		return null;
    	} 
    },
    FOCUS_LEVEL_TWO(true,false,SuccessorTable.FOCUS_LEVEL_TWO){
    	QueryState generateSuccessor(){
    		return null;
    	} 
    },
    TRANSACTED(false,false,null){
    	QueryState generateSuccessor(){
    		return null;
    	} 
    };

    private boolean searching;
    private boolean transacting;
    private SuccessorTable st;
    private static Random random = new Random();

    QueryState(boolean searching, boolean transacting, SuccessorTable st) {
        this.searching = searching;
        this.transacting = transacting;
        this.st = st;
    }

    public boolean isSearching() {
        return searching;
    }

    public boolean isTransacting() {
        return transacting;
    }
    
    abstract QueryState generateSuccessor();
}
