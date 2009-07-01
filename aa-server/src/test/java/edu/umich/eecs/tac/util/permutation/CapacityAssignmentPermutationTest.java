/*
 * CapacityAssignmentPermutationTest.java
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
package edu.umich.eecs.tac.util.permutation;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import edu.umich.eecs.tac.sim.CapacityType;
import static edu.umich.eecs.tac.sim.CapacityType.LOW;
import static edu.umich.eecs.tac.sim.CapacityType.MED;
import static edu.umich.eecs.tac.sim.CapacityType.HIGH;

import java.util.Arrays;

/**
 * @author Patrick R. Jordan
 */
public class CapacityAssignmentPermutationTest {

    @Test
    public void testFirstPermutation() {
        CapacityType[] types0 = CapacityAssignmentPermutation.permutation(0,0);
        CapacityType[] types1 = CapacityAssignmentPermutation.permutation(0,1);
        CapacityType[] types2 = CapacityAssignmentPermutation.permutation(0,2);
        CapacityType[] types3 = CapacityAssignmentPermutation.permutation(0,3);

        assertArrayEquals(types0, new CapacityType[]{LOW,LOW,MED,MED,MED,MED,HIGH,HIGH});
        assertArrayEquals(types1, new CapacityType[]{MED,MED,LOW,LOW,HIGH,HIGH,MED,MED});
        assertArrayEquals(types2, new CapacityType[]{MED,MED,HIGH,HIGH,LOW,LOW,MED,MED});
        assertArrayEquals(types3, new CapacityType[]{HIGH,HIGH,MED,MED,MED,MED,LOW,LOW});
    }

    @Test
    public void testGroupPermutation() {
        CapacityType[] types0 = CapacityAssignmentPermutation.permutation(1,0);
        CapacityType[] types1 = CapacityAssignmentPermutation.permutation(1,1);
        CapacityType[] types2 = CapacityAssignmentPermutation.permutation(1,2);
        CapacityType[] types3 = CapacityAssignmentPermutation.permutation(1,3);

        int[][] counts = new int[8][3];

        for(int i = 0; i < 8; i++) {
            counts[i][types0[i].ordinal()]++;
            counts[i][types1[i].ordinal()]++;
            counts[i][types2[i].ordinal()]++;
            counts[i][types3[i].ordinal()]++;
        }

        for(int i = 0; i < 8; i++) {
            assertEquals(counts[i][0],1,0);
            assertEquals(counts[i][1],2,0);
            assertEquals(counts[i][2],1,0);
        }        
    }

    @Test
    public void testDistinct() {
        CapacityType[] types0 = CapacityAssignmentPermutation.permutation(0,0);
        CapacityType[] types1 = CapacityAssignmentPermutation.permutation(100,0);

        assertFalse(Arrays.equals(types0,types1));        
    }

    @Test
    public void testSecretPermutation() {
        CapacityType[] types0 = CapacityAssignmentPermutation.secretPermutation(1234123412,1,1);
        CapacityType[] types1 = CapacityAssignmentPermutation.secretPermutation(1234123412,2,1);
        CapacityType[] types2 = CapacityAssignmentPermutation.secretPermutation(1234123412,3,1);
        CapacityType[] types3 = CapacityAssignmentPermutation.secretPermutation(1234123412,4,1);

        int[][] counts = new int[8][3];

        for(int i = 0; i < 8; i++) {
            counts[i][types0[i].ordinal()]++;
            counts[i][types1[i].ordinal()]++;
            counts[i][types2[i].ordinal()]++;
            counts[i][types3[i].ordinal()]++;
        }

        for(int i = 0; i < 8; i++) {
            assertEquals(counts[i][0],1,0);
            assertEquals(counts[i][1],2,0);
            assertEquals(counts[i][2],1,0);
        }
    }
}
