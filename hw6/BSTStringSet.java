import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 * @author Marcus Cheung
 */
public class BSTStringSet implements StringSet, Iterable<String> {
    /** Creates a new empty set. */
    public BSTStringSet() {
        _root = null;
    }

    @Override
    public void put(String s) {
        if (_root==null){
            _root = new Node(s);
        }
        else{
            Node p = _root;
            while (p!=null){
                int comp = s.compareTo(p.s);
                Node temp = p;
                if (comp==0){
                    break;
                }
                else if (comp<0){
                    p = p.left;
                    if(p==null){
                        temp.left = new Node(s);
                    }
                }
                else{
                    p = p.right;
                    if(p==null){
                        temp.right = new Node(s);
                    }
                }

            }
        }

    }
    @Override
    public boolean contains(String s) {
        if (_root == null){
            return false;
        }
        Node p = _root;
        while (p!=null){
            int comp = s.compareTo(p.s);
            if (comp==0){
                return true;
            }
            else if (comp<0){
                p = p.left;
            }
            else{
                p = p.right;
            }
        }
        return false;
    }
    @Override
    public List<String> asList() {
        List<String> l = new ArrayList();
        if (_root == null){
            return l;
        }
        BSTIterator i = new BSTIterator(_root);
        while(i.hasNext()){
            l.add(i.next());
        }
        return l;
    }


    /** Represents a single Node of the tree. */
    private static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        Node(String sp) {
            s = sp;
        }
    }

    /** An iterator over BSTs. */
    private static class BSTIterator implements Iterator<String> {
        /** Stack of nodes to be delivered.  The values to be delivered
         *  are (a) the label of the top of the stack, then (b)
         *  the labels of the right child of the top of the stack inorder,
         *  then (c) the nodes in the rest of the stack (i.e., the result
         *  of recursively applying this rule to the result of popping
         *  the stack. */
        private Stack<Node> _toDo = new Stack<>();

        /** A new iterator over the labels in NODE. */
        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Add the relevant subtrees of the tree rooted at NODE. */
        private void addTree(Node node) {
            while (node != null) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new BSTIterator(_root);
    }

    // FIXME: UNCOMMENT THE NEXT LINE FOR PART B
    // @Override
    public Iterator<String> iterator(String low, String high) {
        return null;  // FIXME: PART B (OPTIONAL)
    }


    /** Root node of the tree. */
    private Node _root;
}
