import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** HW #7, Sorting ranges.
 *  @author Marcus Cheung
  */
public class Intervals {
    /** Assuming that INTERVALS contains two-element arrays of integers,
     *  <x,y> with x <= y, representing intervals of ints, this returns the
     *  total length covered by the union of the intervals. */
    public static int coveredLength(List<int[]> intervals) {
        if (intervals.size()==0){
            return 0;
        }
        class intervalComp implements Comparator<int[]>{
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0]-o2[0];
            }
        }
        Collections.sort(intervals, new intervalComp());
        int sum = intervals.get(0)[1] - intervals.get(0)[0];
        for (int i = 1; i<intervals.size(); i++){
            int[] p = intervals.get(i);
            int[] last = intervals.get(i-1);
            if (p[1]<last[1]) {
                p[1] = last[1];
            } else if(p[0] < last[1]){
                sum += p[1] - last[1];
            } else {
                sum += p[1] - p[0];
            }
        }
        return sum;
    }

    /** Test intervals. */
    static final int[][] INTERVALS = {
        {19, 30},  {8, 15}, {3, 10}, {6, 12}, {4, 5},
    };
    /** Covered length of INTERVALS. */
    static final int CORRECT = 23;

    /** Performs a basic functionality test on the coveredLength method. */
    @Test
    public void basicTest() {
        assertEquals(CORRECT, coveredLength(Arrays.asList(INTERVALS)));
    }

    /** Runs provided JUnit test. ARGS is ignored. */
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(Intervals.class));
    }

}
