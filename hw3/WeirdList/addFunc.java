public class addFunc implements IntUnaryFunction{
    int n;
    public addFunc(int a){
        n = a;
    }
    @Override
    public int apply(int head){
        return head+n;
    }
}