package lists;

import lists.IntListList;
import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *
 *  @author FIXME
 */

public class ListsTest {
    /** FIXME
     */

    // It might initially seem daunting to try to set up
    // IntListList expected.
    //
    // There is an easy way to get the IntListList that you want in just
    // few lines of code! Make note of the IntListList.list method that
    // takes as input a 2D array.
    @Test
    public void testNalskdmflaksdmf() {
        int[][] regularCase1 = {
                {1, 3, 7},
                {5},
                {4, 6, 9, 10},
                {10, 11}
        };
        IntListList list1 = IntListList.list(regularCase1);
        IntList list2 = IntList.list(new int[] {1, 3, 7, 5, 4, 6, 9, 10, 10, 11});
        assertEquals(list1, Lists.naturalRuns(list2));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
