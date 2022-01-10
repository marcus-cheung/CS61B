package arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class ArraysTest {
    @Test
    public void testcatenate() {
        int[] A = new int[]{1,2,3};
        int[] B = new int[]{4,5};
        int[] expected = new int[]{1,2,3,4,5};
        assertArrayEquals(expected, Arrays.catenate(A,B));
    }

    @Test
    public void testremove() {
        int[] A = new int[]{0,1,2,3};
        int[] expected = new int[]{1,2};
        assertArrayEquals(expected, Arrays.remove(A, 1, 2));
        assertArrayEquals(null, Arrays.remove(A,1,10));

    }

    @Test
    public void testNR() {
        int[] A = new int[]{1, 3, 7, 5, 4, 6, 9, 10};
        int[][] expected = new int[][]{{1, 3, 7}, {5}, {4, 6, 9, 10}};
        assertArrayEquals(expected, Arrays.naturalRuns(A));

    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
