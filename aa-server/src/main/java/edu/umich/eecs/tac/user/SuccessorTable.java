package edu.umich.eecs.tac.user;

import java.util.ArrayList;
import java.util.Random;

import edu.umich.eecs.tac.props.Product;

public enum SuccessorTable {
	NON_SEARCHING, INFORMATIONAL_SEARCH, FOCUS_LEVEL_ZERO, 
	FOCUS_LEVEL_ONE, FOCUS_LEVEL_TWO, BURST;
	
	private double [] table;

	public double [] getTable() {
		return table;
	}

	public void setTable(double [] table) {
		int i;
		for(i = 0; i < table.length; i++){
			if(i == 0)
				this.table[0] = table[0];
			else
				this.table[i] = table[i] + this.table[i-1];
		}
	}
	
}
