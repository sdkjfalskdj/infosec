import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
//另一种实现方法：创建64位字符串，
public class Des {
    public static float total = 0;
    //num1、num2为用来改变位数用的十六进制数，如0010(含一个1)、0011(含两个1)、0111(含三个1)
    public static byte[] num1 = {0x2, 0x3, 0x7, 0xF, 0x1F, 0x3F, 0x7F, (byte) 0xFF};
    public static byte[] num2 = {(byte) 0xFF, 0x7F, 0x3F, 0x1F, 0xF, 0x7, 0x3, 0x1};
    //array1、array2为最初的数组模板，全0或全1
    public static byte[] array1 = {0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
    public static byte[] array2 = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
    public static byte[] des_key = new byte[8];  //密钥
    public static byte[] des_input = new byte[8]; //明文

    public static String byteArrayToHex(byte[] bs) {
        StringBuilder res = new StringBuilder();

        for (byte b : bs) res.append(String.format("%02X", (b & 0xff)));

        return res.toString();
    }

    public static int count(byte[] data1, byte[] data2) {//计算明文加密前后位数改变的个数
        int sum = 0;
        for (int i = 0; i < data1.length; i++) {
            //System.out.println("位数：" + Integer.bitCount(new Integer(data1[i] & 0xff) ^ new Integer(data2[i] & 0xff)));
            sum += Integer.bitCount(new Integer(data1[i] & 0xff) ^ new Integer(data2[i] & 0xff));
        }
        return sum;
    }

    public static byte[] encrypt(byte[] des_key, byte[] des_input) {//用des加密算法对明文进行加密
        //DES加密算法
        Cipher des = null;

        //加密后的输出
        byte[] des_output = null;

        //创建DES密钥
        SecretKey secretKey = new SecretKeySpec(des_key, "DES");

        //创建DES密码算法对象，指定电码本模式和无填充方式
        try {
            des = Cipher.getInstance("DES/ECB/NoPadding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        //初始化DES算法
        try {
            des.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        //加密
        try {
            des_output = des.doFinal(des_input);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return des_output;
    }

    public static void compute_plain_change(byte[] array, byte[] des_reference) {//实现改变明文的位数
        byte addition;//填充8位用的数据,改变的位数在1-32内，填充0xff；改变的位数在33-64内，填充0x0;

        //改变的位数在33-64内其实是利用了改变1-32位时的相同做法，只是将数组模板换为全1，填充数据换为全0，改变位数所用数组换为num2

        des_input = array;//改变的位数在1-32内，一开始全为0；改变的位数在33-64内，一开始全为1
        byte[] temp;
        int c = 1;//计算改变多少位
        byte[] num;//改变的位数在1-32内，为num1；改变的位数在33-64内，为num2

        if (array == array1) {
            addition = (byte) 0xFF;//改变的位数在1-32内，填充0xff
            num = num1;//改变的位数在1-32内，为num1
        } else {
            addition = 0x0;//改变的位数在33-64内，填充0x0;
            num = num2;//改变的位数在33-64内，为num2
        }

        for (int j = 0; j < 4; j++) {//j控制改变的位数始终在1-32内，因为改变的位数在33-64内其实是利用了改变1-32位时的逆方法
            for (int i = 0; i < 8; i++) {//i控制num的下标从0到7
                temp = des_input;//保存原des_input数组
                for (int k = j; k < 8; k++) {//k控制num[i]数组存放在des_input的位置，达到多次统计的目的
                    des_input[k] = num[i];
                    total += count(des_reference, encrypt(des_key, des_input));//计算多次统计改变位数的总和
                    des_input = temp;//避免des_input数组因为前面的操作而改变
                }
                if (array == array1)
                    System.out.println("输入明文改变" + c + "位，输出密文改变位数的平均值为：" + total / (8 - j));//计算平均值
                //由于逆方法按改变64位到改变33位的顺序进行，所以只能倒序（64~33）输出结果
                else System.out.println("输入明文改变" + (65 - c) + "位，输出密文改变位数的平均值为：" + total / (8 - j));
                total = 0;
                c++;
            }
            des_input[j] = addition;//改变的位数在1-32内，填充0xff；改变的位数在33-64内，填充0x0;
        }
    }

    public static void compute_key_change(byte[] array, byte[] des_reference) {//实现改变密钥的位数，和改变明文时所用方法一样
        byte addition;//填充8位用的数据
        des_key = array;
        byte[] temp;
        int c = 1;
        byte[] num;

        if (array == array1) {
            addition = (byte) 0xFF;
            num = num1;
        } else {
            addition = 0x0;
            num = num2;
        }

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 8; i++) {//控制num的下标从0到7
                temp = des_key;
                for (int k = j; k < 8; k++) {
                    des_key[k] = num[i];
                    total += count(des_reference, encrypt(des_key, des_input));
                    des_key = temp;
                }
                if (array == array1)
                    System.out.println("输入密钥改变" + c + "位，输出密文改变位数的平均值为：" + total / (8 - j));
                else System.out.println("输入密钥改变" + (65 - c) + "位，输出密文改变位数的平均值为：" + total / (8 - j));
                total = 0;
                c++;
            }
            des_key[j] = addition;
        }
    }

    public static void main(String[] args) {
        int number;
        System.out.println("默认明文为：0000000000000000");
        System.out.println("默认密钥为：0000000000000000");
        for (int i = 0; i < 8; i++) {
            des_key[i] = 0x0;
            des_input[i] = 0x0;
        }
        byte[] des_reference = encrypt(des_key, des_input);//进行参照的加密后的明文
        System.out.println("输出密文为：" + byteArrayToHex(des_reference));
            System.out.println();
            System.out.println("请输入你想改变的数据：1.明文 2.密钥");
            Scanner sc = new Scanner(System.in);
            number = sc.nextInt();
            switch (number) {
                case 1:
                    compute_plain_change(array1, des_reference);//改变的位数在1-32内
                    System.out.println();
                    compute_plain_change(array2, des_reference);//改变的位数在33-64内
                    break;
                case 2:
                    compute_key_change(array1, des_reference);//改变的位数在1-32内
                    System.out.println();
                    compute_key_change(array2, des_reference);//改变的位数在33-64内
                    break;
            }
    }
}

