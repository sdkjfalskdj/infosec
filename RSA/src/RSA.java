import java.util.Scanner;

public class RSA {//限制在long范围,简单的RSA算法实现
    public static long p;
    public static long q;
    public static long n;
    public static long fn;
    public static long e=3533;
    public static long d;
    public static long x;
    public static long y;
    public static long m;
    public static long c;

    public static boolean isPrime(long k) {//判断一个数是否为素数
        for (int i = 2; i <= Math.sqrt(k); i++) {
            if (k % i == 0) return false;
        }
        return true;
    }

    public static long gcd(long a, long b) {//求两个数的最大公约数
        if (b == 0)
            return a;
        else return gcd(b, a % b);
    }

    public static void find_e() {//找一个互素的数e,即找出加密密钥
        while (true) {
            e = (long) (Math.random() * (fn - 2) + 2);//小数取整应该不会进位，保证e最小取值为2
            if (gcd(e, fn) == 1) return;
        }
    }

    public static void invMod(long a, long b) {//通过扩展欧几里得算法求私钥
        if (b == 0) {
            x = 1;
            y = 0;
            return;
        }
        invMod(b, a % b);
        long k = x;
        x = y;
        y = k - a / b * y;
    }//最终所求逆元存储在x里面

    private static long expMod(long x, long y, long n) {//整数 x，y，n；x>0，y>=0，n>1；x^y(mod n)
        if (y == 0) {
            return 1;
        } else {
            if (y % 2 == 0) {
                return expMod((x * x) % n, y / 2, n); //用到了模运算的性质3
            } else {
                return x * expMod((x * x) % n, y / 2, n) % n;  //用到了乘法模运算的交换律
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入素数p：");
        p = sc.nextLong();
        while (!isPrime(p)) {
            System.out.println("p不是素数,请重新输入：");
            p = sc.nextLong();
        }
        System.out.println("请输入素数q：");
        q = sc.nextLong();
        if (!isPrime(q)) {
            System.out.println("q不是素数，请重新输入：");
            q = sc.nextLong();
        }
        fn = (p - 1) * (q - 1);
        find_e();
        System.out.println("随机生成的加密密钥：" + e);

        invMod(e, fn);
        if (x < 0) x += fn;//避免密钥是负数，加一次“循环”
        d = x;//得到解密密钥
        System.out.println("求出的解密密钥为：" + d);

        System.out.println("请输入明文m：");
        m = sc.nextLong();

        n = p * q;
        c = expMod(m, e, n);
        System.out.println("密文：" + c);

        System.out.println("明文：" + expMod(c, d, n));
    }
}
