/*
 * PermutationOfEightGeneratorTest.java
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

/**
 * Generator for permutations of ordered set (0,1,2,3,4,5,6,7).
 *
 * @author Patrick R. Jordan
 */
public class PermutationOfEightGenerator {
    /**
     * Total number of distinct permutations (8!).
     */
    public static final int TOTAL_PERMUTATIONS = 40320;
    /**
     * The current permutation.
     */
    private int[] current;
    /**
     * Number of remaining distrint permutations.
     */
    private int remaining;

    public PermutationOfEightGenerator() {
        current = new int[8];

        for (int i = 0; i < 8; i++) {
            current[i] = i;
        }

        remaining = TOTAL_PERMUTATIONS;
    }

    /**
     * Returns <code>true</code> if there are additional permutations.
     * @return <code>true</code> if there are additional permutations.
     */
    public boolean hasNext() {
        return remaining > 0;
    }

    /**
     * Returns the next permutation of the ordered set.
     *
     * <p>Rosen's next permutation function. Kenneth H. Rosen, Discrete Mathematics and Its Applications, 2nd edition
     * (NY: McGraw-Hill, 1991), pp. 282-284.</p>
     *        
     * @return the next permutation of the ordered set.
     */
    public int[] next() {

        if (remaining < TOTAL_PERMUTATIONS) {
            int swap;

            // Find largest index j with current[j] < current[j+1]

            int j = current.length - 2;
            while (current[j] > current[j + 1]) {
                j--;
            }

            // Find index k such that current[k] is smallest integer
            // greater than current[j] to the right of current[j]

            int k = current.length - 1;
            while (current[j] > current[k]) {
                k--;
            }

            // Interchange current[j] and current[k]

            swap = current[k];
            current[k] = current[j];
            current[j] = swap;

            // Put tail end of permutation after jth position in increasing order

            int r = current.length - 1;
            int s = j + 1;

            while (r > s) {
                swap = current[s];
                current[s] = current[r];
                current[r] = swap;
                r--;
                s++;
            }
        }

        remaining--;
        
        return current.clone();
    }
}
