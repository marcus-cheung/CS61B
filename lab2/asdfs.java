public class asdfs {
    public static void main(String... args){
        IntList A = IntList.list(1, 2, 3, 4, 5, 6, 7, 8);
        IntList p, n;
        p=A;
        while (p!=null) {
            n = p;
            p = p.tail.tail;
            n.tail = n;
        }
    }
}
