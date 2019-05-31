import java.util.Random;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 15/05/2019
 */
public class Test {
    public static void main(String[] args) {
//        System.out.println(new Random().nextInt(100000000));

        for (int i=0;i<10000;i++) {
//            int a = new Random().nextInt(100000000);
            int a = (int)((Math.random()*9+1)*10000000);
            int length = 15;
            int b = new Random().nextInt(length*2);

            int c = new Random().nextInt(3);

            System.out.println(c + "======" + c%3);

            /*if (b==30) {
                System.out.println(b);
            }*/
        }

        double pow = Math.pow(10d, 5d);
        System.out.println(pow);

    }
}
