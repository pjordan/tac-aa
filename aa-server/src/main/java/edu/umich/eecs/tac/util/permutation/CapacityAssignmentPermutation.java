/*
 * CapacityAssignmentPermutation.java
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


import static edu.umich.eecs.tac.sim.CapacityType.*;
import edu.umich.eecs.tac.sim.CapacityType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Patrick R. Jordan
 */
public final class CapacityAssignmentPermutation {

    private static CapacityType[][] GROUP_CAPACITIES = {{LOW,LOW,MED,MED,MED,MED,HIGH,HIGH},
                                                        {MED,MED,LOW,LOW,HIGH,HIGH,MED,MED},
                                                        {MED,MED,HIGH,HIGH,LOW,LOW,MED,MED},
                                                        {HIGH,HIGH,MED,MED,MED,MED,LOW,LOW}};

    private CapacityAssignmentPermutation() {
    }

    public static CapacityType[] permutation(int group, int groupOffset) {
        CapacityType[] p = new CapacityType[8];

        int[] eightPerm = permutationOfEight(group);

        for(int i = 0; i < 8; i++) {
            p[i] = GROUP_CAPACITIES[groupOffset][eightPerm[i]];
        }

        return p;
    }
    
    private static int[] permutationOfEight(int group) {

        group = group % PermutationOfEightGenerator.TOTAL_PERMUTATIONS;
        if(group < 0) {
            group += PermutationOfEightGenerator.TOTAL_PERMUTATIONS;
        }

        PermutationOfEightGenerator generator = new PermutationOfEightGenerator();

        for(int i = 0; i < group; i++) {
            generator.next();
        }

        return generator.next();
    }

    public static CapacityType[] secretPermutation(int secret, int simulationId, int baseId) {

        int group = digest(secret,(simulationId - baseId)/4);

        int groupOffset = (simulationId - baseId)%4;

        if(groupOffset < 0 ) {
            groupOffset += 4;
        }

        return permutation(group, groupOffset);
    }

    private static int digest(int secret, int value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");

            md.update(((Integer)secret).byteValue());
            md.update(((Integer)value).byteValue());

            return bytesToInt(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static int bytesToInt(byte[] bytes) {
        int d = 0;

        for(int i = 0; i < Math.max(bytes.length,4); i++) {
            d += ((bytes[i] & 0xFF) << (24-i*8));
        }

        return d;
    }
}
