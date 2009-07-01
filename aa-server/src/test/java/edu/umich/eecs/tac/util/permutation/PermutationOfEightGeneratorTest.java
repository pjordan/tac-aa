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

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.Arrays;

/**
 * @author Patrick R. Jordan
 */
public class PermutationOfEightGeneratorTest {
    private PermutationOfEightGenerator generator;

    @Before
    public void setup() {
        generator = new PermutationOfEightGenerator();
    }

    @Test
    public void testConstructor() {
        assertNotNull(generator);
    }

    @Test
    public void testSize() {
        int count = 0;


        while(generator.hasNext()) {
            count++;
            generator.next();
        }

        assertEquals(count, 40320, 0);
    }

    @Test
    public void testDistinct() {
        int[] p1 = generator.next();
        assertEquals(p1.length,8,0);
        int[] p2 = generator.next();
        assertEquals(p2.length,8,0);

        assertFalse(Arrays.equals(p1,p2));
        Arrays.sort(p1);
        Arrays.sort(p2);
        assertTrue(Arrays.equals(p1,p2));
        assertTrue(Arrays.equals(p1,new int[] {0,1,2,3,4,5,6,7}));
        assertNotSame(p1,p2);
    }
}
