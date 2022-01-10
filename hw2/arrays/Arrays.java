package arrays;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author
 */
public class  Arrays {

    /* C1. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        int[] result = new int[A.length+B.length];
        int index = 0;
        for (int i:A){
            result[index] = i;
            index++;
        }
        for (int i:B){
            result[index] = i;
            index++;
        }
        return result;
    }

    /* C2. */
    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. If the start + len is out of bounds for our array, you
     *  can return null.
     *  Example: if A is [0, 1, 2, 3] and start is 1 and len is 2, the
     *  result should be [0, 3]. */
    static int[] remove(int[] A, int start, int len) {
        if (start+len>A.length-1){
            return null;
        }
        else{
            int[] result = new int[A.length-len];
            int index = 0;
            for(int i = start; i<start+len; i++){
                result[index] = A[i];
                index++;
            }
            return result;
        }
    }

    /* C3. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {0,1,2,0,0,1,2,3}
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        /* *Replace this body with the solution. */
        int counter = 1;
        int[] B= new int[A.length];
        B[0] = 0;
        for (int i = 1; i<A.length; i++){
            if (A[i]>A[i-1]){
                B[i]= B[i-1]+1;
            }
            else{
                B[i] = 0;
                counter++;
            }
        }
        int[][] result = new int[counter][];
        int index = 0;
        for (int i = 0; i<A.length-1; i++){
            if (B[i+1]==0){
                result[index] = new int[B[i]+1];
                index++;
            }
            else if (i == B.length-2){
                result[index] = new int[B[i+1]+1];
            }
        }

        index = -1;
        for (int i = 0; i<A.length; i++){
            if (B[i]==0){
                index++;
            }
            result[index][B[i]] = A[i];
        }
        return result;

    }
}