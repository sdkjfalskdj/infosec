import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class FinalRSA {//限制在long范围,简单的RSA算法实现
    public static long p;
    public static long q;
    public static long n;
    public static long fn;
    public static long e;
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
        final JFrame jf = new JFrame("RSA加解密");
        jf.setSize(500, 450);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();

        //提示标签
        JLabel label = new JLabel();
        label.setText("素数p：");
        label.setFont(new Font(null, Font.PLAIN, 12));  // 设置字体，null 表示使用默认字体
        panel.add(label);

        // 输入素数p
        JTextArea PTextArea = new JTextArea(1, 15);
        PTextArea.setLineWrap(true);
        panel.add(PTextArea);

        //提示标签
        JLabel label1 = new JLabel();
        label1.setText("素数q：");
        label1.setFont(new Font(null, Font.PLAIN, 12));  // 设置字体，null 表示使用默认字体
        panel.add(label1);

        // 输入素数q
        JTextArea QTextArea = new JTextArea(1, 15);
        QTextArea.setLineWrap(true);
        panel.add(QTextArea);

        JLabel label2 = new JLabel();
        label2.setText("明文m：");
        label2.setFont(new Font(null, Font.PLAIN, 12));  // 设置字体，null 表示使用默认字体
        panel.add(label2);

        JTextArea MTextArea = new JTextArea(1, 28);
        MTextArea.setLineWrap(true);
        panel.add(MTextArea);

        JTextArea ContentTextArea = new JTextArea(20, 35);
        ContentTextArea.setEnabled(false);  //不能点击
        ContentTextArea.setDisabledTextColor(Color.BLACK);//颜色设置

        JButton Btn2 = new JButton("执行");
        Btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                p = Long.parseLong(PTextArea.getText());
                q = Long.parseLong(QTextArea.getText());
                if (!isPrime(p)) {
                    JOptionPane.showMessageDialog(
                            jf,
                            "p不是素数!",
                            "提示",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
                if (!isPrime(q)) {
                    JOptionPane.showMessageDialog(
                            jf,
                            "q不是素数!",
                            "提示",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
                fn = (p - 1) * (q - 1);
                find_e();
                ContentTextArea.append("随机生成的加密密钥e：" + e + '\n');
                invMod(e, fn);
                if (x < 0) x += fn;//避免密钥是负数，加一次“循环”
                d = x;//得到解密密钥
                ContentTextArea.append("求出的解密密钥d：" + d + '\n');
                m = Long.parseLong(MTextArea.getText());
                n = p * q;
                c = expMod(m, e, n);

                ContentTextArea.append("密文c：" + c + '\n');

                long z = expMod(c, d, n);
                ContentTextArea.append("明文m：" + z + '\n' + '\n');
            }
        });
        panel.add(Btn2);
        //panel.add(ContentTextArea);
        JScrollPane scrollPane = new JScrollPane(ContentTextArea);
        panel.add(scrollPane);

        jf.setContentPane(panel);
        jf.setVisible(true);
    }
}
