public class sumFunc implements IntUnaryFunction{
    int sum;
    public sumFunc(){
        sum = 0;
    }
    @Override
    public int apply(int head){
        sum+=head;
        return sum;
    }
}
