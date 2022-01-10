import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** A set of String values.
 *  @author Marcus Cheung
 */
class ECHashStringSet implements StringSet {

    private LinkedList<String>[] buckets;

    private int num = 5;

    private double LF = 0.2;

    private double size;

    public ECHashStringSet(){
        buckets = (LinkedList<String>[]) new LinkedList[num];
//        for (int i = 0; i< buckets.length; i++){
//            buckets[i] = new LinkedList();
//        }
        size = 0;
    }

    private void resize(){
        if ((size + 1)/buckets.length>LF){
            LinkedList<String>[] temp = (LinkedList<String>[]) new LinkedList[buckets.length*2];
//            for (int i = 0; i< temp.length; i++){
//                temp[i] = new LinkedList();
//            }
            for (LinkedList<String> L:buckets){
                if (L!=null){
                    for (int i = 0; i < L.size(); i++){
                        String s = L.get(i);
                        int index = wBucket(s, temp.length);
                        if (temp[index] == null){
                            temp[index] = new LinkedList();
                        }
                        temp[index].add(s);
                    }
                }

            }
            buckets = temp;
        }
    }

    @Override
    public void put(String s) {
        resize();
        int index = wBucket(s, buckets.length);
        if (buckets[index]==null){
            buckets[index]=new LinkedList();
        }
        buckets[index].add(s);
        size++;
    }

    private int wBucket(String s, int length){
        if (s.hashCode()<0){
            return (s.hashCode() & 0x7fffffff) % length;
        }
        return s.hashCode() % length;
    }

    @Override
    public boolean contains(String s) {
        return buckets[wBucket(s, buckets.length)]!=null && buckets[wBucket(s, buckets.length)].contains(s);
    }

    @Override
    public List<String> asList() {
        List<String> L = new ArrayList<>();
        for (LinkedList<String> l:buckets){
            if (l!=null){
                for (int i = 0; i<l.size(); i++){
                    L.add(l.get(i));
                }
            }
        }
        return L;
    }
}
