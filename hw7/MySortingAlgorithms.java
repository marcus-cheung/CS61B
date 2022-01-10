import antlr.collections.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;

/**
 * Note that every sorting algorithm takes in an argument k. The sorting 
 * algorithm should sort the array from index 0 to k. This argument could
 * be useful for some of your sorts.
 *
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Counting Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i = 0; i<k; i++) {
                for (int j = i; j>0; j--){
                    if (array[j]<array[j-1]){
                        int temp = array[j];
                        array[j] = array[j-1];
                        array[j-1] = temp;
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i = 0; i<k-1; i++) {
                int min = array[i];
                int index = i;
                for (int j = i; j<k; j++){
                    if (array[j]<min){
                        min = array[j];
                        index = j;
                    }
                }
                int temp = array[i];
                array[i] = min;
                array[index] = temp;
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            int[] sorted = new int[k];
            for(int i = 0; i<k; i++){
                sorted[i] = array[i];
            }
            sorted = mergesort(sorted);
            for (int i = 0; i<k; i++){
                array[i] = sorted[i];
            }
        }

        public int[] mergesort(int[] lst){
            if (lst.length==0 || lst.length==1){
                return lst;
            }
            int[] a = new int[lst.length/2];
            int[] b = new int[lst.length-lst.length/2];
            for (int i = 0; i<lst.length/2; i++){
                a[i] = lst[i];
            }
            for (int i = 0; i<lst.length-lst.length/2; i++){
                b[i] = lst[i+lst.length/2];
            }
            a = mergesort(a);
            b = mergesort(b);
            return merge(a,b);
        }

        public int[] merge(int[] a, int[] b) {
            int[] merged = new int[a.length+b.length];
            int index = 0;
            int ai = 0;
            int bi = 0;
            while (ai < a.length && bi < b.length){
                if (a[ai]>=b[bi]) {
                    merged[index] = b[bi];
                    bi++;
                } else {
                    merged[index] = a[ai];
                    ai++;
                }
                index++;
            }
            while(ai<a.length) {
                merged[index] = a[ai];
                index++;
                ai++;
            }
            while(bi<b.length) {
                merged[index] = b[bi];
                index++;
                bi++;
            }
            return merged;
        }

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Counting Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class CountingSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Counting Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            ArrayList<Integer>[] buckets = new ArrayList[4];
            for (int i = 0; i<4; i++){
                buckets[i] = new ArrayList<Integer>();
            }
            int mask = 3;
            for (int i = 0; i<=32; i+=2){
                for (int index = 0; index<k; index++){
                    buckets[((a[index]&mask)>>>i)%4].add(a[index]);
                }
                mask <<=2;
                int index = 0;
                for (ArrayList<Integer> bucket:buckets){
                    while (bucket.size()>0) {
                        a[index] = bucket.remove(0);
                        index++;
                    }
                }
            }
        }


        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
