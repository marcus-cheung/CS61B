public class methods {
    public static int max(int[] a){
        int s = a[0];
        for (int i=0; i<a.length; i++){
            if (a[i]>s){
                s = a[i];
            }
        }
        return s;
    }

    public static boolean threeSum(int[] a){
        for (int i = 0; i<a.length-2; i++){
            for (int j = i+1; j<a.length-1; j++){
                for (int h = j+1; h<a.length; h++){
                    if(a[i]+a[j]+a[h]==0){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean threeSumDistinct(int[] a){
        for (int i = 0; i<a.length-2; i++){
            for (int j = i+1; j<a.length-1; j++){
                for (int h = j+1; h<a.length; h++){
                    if(a[i]+a[j]+a[h]==0 && a[i]!=a[j] && a[i]!=a[h] && a[j]!=a[h]){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}