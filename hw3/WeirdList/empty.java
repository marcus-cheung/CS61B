public class empty extends WeirdList{

    public empty(int head, WeirdList tail){
        super(head, tail);
    }

    @Override
    public String toString(){
        return "";
    }

    @Override
    public int length(){
        return 0;
    }

    @Override
    public empty map(IntUnaryFunction func){
        return this;
    }
}