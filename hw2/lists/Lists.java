package lists;

/* NOTE: The file Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2, Problem #1. */

import image.In;

/** List problem.
 *  @author
 */

class Lists {

    /* B. */
    /** Return the list of lists formed by breaking up L into "natural runs":
     *  that is, maximal strictly ascending sublists, in the same order as
     *  the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     *  then result is the four-item list
     *            ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     *  Destructive: creates no new IntList items, and may modify the
     *  original list pointed to by L. */

    static IntListList naturalRuns(IntList L) {
        if (L==null){
            return null;
        }
        IntList m = L;
        while(m.tail!=null&&m.tail.head>m.head) {
            m = m.tail;
        }
        IntListList q = naturalRuns(m.tail);
        m.tail = null;
        return new IntListList(L, q);
    }

//    IntList current = new IntList(L.head, null);
//        IntListList result = new IntListList(current, null);
//        return helper(L, result, current);

    static IntListList helper(IntList L, IntListList result, IntList current){
        if (L.head>current.head){
            current.tail = new IntList(L.head, null);
            current = current.tail;
        }
        else{
            IntList n = new IntList(L.head, null);
            result.tail = new IntListList(n, null);
            result = result.tail;
            current = result.head;
        }
        if(L.tail==null){
            current.tail = null;
            result.tail = null;
            return result;
        }
        else{
            return helper(L.tail, result, current);
        }
    }

}
