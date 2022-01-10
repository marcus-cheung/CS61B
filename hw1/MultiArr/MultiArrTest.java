import static org.junit.Assert.*;
import org.junit.Test;

public class MultiArrTest {

    @Test
    public void testMaxValue() {
        int mat[][] = { { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 } };
        assertEquals(MultiArr.maxValue(mat), 12);
    }

    @Test
    public void testAllRowSums() {
        int mat[][] = { { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 } };
        int ans[] = {10, 26, 42};
        assertEquals(MultiArr.allRowSums(mat)[0], ans[0]);
        assertEquals(MultiArr.allRowSums(mat)[1], ans[1]);
        assertEquals(MultiArr.allRowSums(mat)[2], ans[2]);

    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(MultiArrTest.class));
    }
}
