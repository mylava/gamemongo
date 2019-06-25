/**
 * comment:
 *
 * @author: lipengfei
 * @date: 24/06/2019
 */
public class UtilTest {
    public static void main(String[] args) {
        double random = (Math.random() * 9 + 1);
        System.out.println(random);
        int pow = (int) Math.pow(1d, 5);
        System.out.println(pow);
        System.out.println(String.valueOf((int) (random * pow)));
    }
}
