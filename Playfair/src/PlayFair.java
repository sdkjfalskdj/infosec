import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlayFair {

    /**
     * 处理明文，重复字母中间插入该字母的下一位字母，长度非偶数在末尾添加末尾字母的下一位字母
     *
     * @param P 原始明文
     * @return 处理后的明文
     */
    public static String DealPlain(String P) {
        P = P.toUpperCase();// 将明文转换成大写
        P = P.replaceAll("[^A-Z]", "");//去除所有非字母的字符
        StringBuilder sb = new StringBuilder(P);//一个可变的字符序列
        for (int i = 1; i < sb.length(); i = i + 2) {
            //一对明文字母如果是重复的则在这对明文字母之间插入一个填充字符
            if (sb.charAt(i) == sb.charAt(i - 1)) {
                if (sb.charAt(i) == 'Z')
                    sb.insert(i, 'A');//主要是序列是可变的，调用方法就可以插入
                else sb.insert(i, (char) (sb.charAt(i) + 1));
            }
        }
        //如果经过处理后的明文长度非偶数，则在后面加上字母x
        if (sb.length() % 2 != 0) {
            if (sb.charAt(sb.length() - 1) == 'Z')
                sb.append('A');
            else sb.append((char) (sb.charAt(sb.length() - 1) + 1));
        }
        String p = sb.toString();
        System.out.println("处理后的明文：" + p);
        return p;
    }

    /**
     * 处理密钥，将J转换成I，除去重复字母
     *
     * @param K 密钥
     * @return 修改后的密钥List<Character>
     */
    public static List<Character> DealKey(String K) {//密钥除去重复字母
        K = K.toUpperCase();// 将密钥转换成大写
        K = K.replaceAll("[^A-Z]", "");//去除所有非字母的字符
        K = K.replaceAll("J", "I");//将J转换成I
        List<Character> list = new ArrayList<>();//List标明Character后，ArrayList不用再强调，否则会冗余
        char[] ch = K.toCharArray();
        for (char c : ch) {
            //除去重复出现的字母
            if (!list.contains(c)) //利用一个flag数组标记字母太麻烦，不如利用新的容器，而且保持了原顺序
                list.add(c);
        }
        System.out.println("处理后的密钥：" + list);
        return list;
    }

    /**
     * 将密钥的字母逐个加入5×5的矩阵内，剩下的空间按照a-z的顺序添加
     * 未加入的英文字母，其中将I和J视作同一字母
     *
     * @param K 密钥
     * @return 5*5字母矩阵
     */
    public static char[][] Matrix(String K) {/*为密钥构建字母矩阵*/
        List<Character> key = DealKey(K);
        //添加在K中出现的字母
        List<Character> list = new ArrayList<>(key);//构造一个包含指定 collection 的元素的列表，这些元素是按照该 collection 的迭代器返回它们的顺序排列的
        //添加按字母表顺序排序的剩余的字母
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            if (ch != 'J' && !list.contains(ch)) {
                list.add(ch);
            }
        }
        char[][] Matrix = new char[5][5];//二维数组作为矩阵
        int count = 0;
        for (int i = 0; i < 5; i++) {//把字母放进矩阵
            for (int j = 0; j < 5; j++) {
                Matrix[i][j] = list.get(count++);//ArrayList的get方法，contain方法和add方法
            }
        }
        System.out.println("根据密钥'" + K + "'构建的矩阵：");
        ShowMatrix(Matrix);
        return Matrix;
    }

    /**
     * 打印矩阵
     *
     * @param Matrix 矩阵
     */
    public static void ShowMatrix(char[][] Matrix) {
        for (char[] chars : Matrix) {
            for (char aChar : chars) {
                System.out.print(aChar + " ");//空格输出
            }
            System.out.println();//换行
        }
    }

    /**
     * 用playfair算法将明文按对加密
     *
     * @param Matrix 矩阵
     * @param ch1    字母1
     * @param ch2    字母2
     * @return 密文对
     */

    public static String EncryptPerTwoCharacter(char[][] Matrix, char ch1, char ch2) {

        int r1 = 0, c1 = 0, r2 = 0, c2 = 0;
        for (int i = 0; i < Matrix.length; i++) {//获取明文对在矩阵中的位置，行列下标
            for (int j = 0; j < Matrix[i].length; j++) {
                if (ch1 == Matrix[i][j]) {
                    r1 = i;
                    c1 = j;
                }
                if (ch2 == Matrix[i][j]) {
                    r2 = i;
                    c2 = j;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        //进行规制匹配，得到密文对
        if (r1 == r2) {
            //明文字母对的两个字母在同一行，则截取右边的字母
            sb.append(Matrix[r1][(c1 + 1) % 5]);
            sb.append(Matrix[r1][(c2 + 1) % 5]);
        } else if (c1 == c2) {
            //明文字母对的两个字母在同一列，则截取下方的字母
            sb.append(Matrix[(r1 + 1) % 5][c1]);
            sb.append(Matrix[(r2 + 1) % 5][c1]);
        } else {
            //明文字母斜对
            //明文对中的每一个字母将由与其同行，且与另一个字母同列的字母代替
            sb.append(Matrix[r1][c2]);
            sb.append(Matrix[r2][c1]);
        }
        sb.append(' ');/*---------------用于检验*/
        return sb.toString();//StringBuilder完了之后要转为String输出
    }

    /**
     * 对明文进行加密
     *
     * @param P 明文
     * @param K 密钥
     * @return 密文
     */
    public static String Encrypt(String P, String K) {
        char[] ch = DealPlain(P).toCharArray();
        char[][] Matrix = Matrix(K);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ch.length; i = i + 2) {
            sb.append(EncryptPerTwoCharacter(Matrix, ch[i], ch[i + 1]));
        }
        return sb.toString();
    }

    /**
     * 根据playfair算法对密文对进行解密
     *
     * @param Matrix 字母矩阵
     * @param ch1    字母1
     * @param ch2    字母2
     * @return 明文对
     */
    public static String DecryptPerTwoCharacter(char[][] Matrix, char ch1, char ch2) {
        //获取密文对在矩阵中的位置
        int r1 = 0, c1 = 0, r2 = 0, c2 = 0;
        for (int i = 0; i < Matrix.length; i++) {
            for (int j = 0; j < Matrix[i].length; j++) {
                if (ch1 == Matrix[i][j]) {
                    r1 = i;
                    c1 = j;
                }
                if (ch2 == Matrix[i][j]) {
                    r2 = i;
                    c2 = j;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        //进行规制匹配，得到明文对
        if (r1 == r2) {
            //密文字母对的两个字母在同一行，则截取左边的字母
            sb.append(Matrix[r1][(c1 - 1 + 5) % 5]);
            sb.append(Matrix[r1][(c2 - 1 + 5) % 5]);
        } else if (c1 == c2) {
            //密文字母对的两个字母在同一列，则截取上方的字母
            sb.append(Matrix[(r1 - 1 + 5) % 5][c1]);
            sb.append(Matrix[(r2 - 1 + 5) % 5][c1]);
        } else {
            //密文字母所形成的矩形对角线上的两个字母，任意选择两种方向
            sb.append(Matrix[r1][c2]);
            sb.append(Matrix[r2][c1]);
        }
        sb.append(' ');
        return sb.toString();
    }

    /**
     * 对密文进行解密
     *
     * @param C 密文
     * @param K 密钥
     * @return 明文
     */
    public static String Decrypt(String C, String K) {
        C = C.toUpperCase();
        C = C.replaceAll("[^A-Z]", "");//去除所有非字母的字符
        char[] ch = C.toCharArray();
        char[][] Matrix = Matrix(K);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ch.length; i = i + 2) {
            sb.append(DecryptPerTwoCharacter(Matrix, ch[i], ch[i + 1]));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("--------加密--------");
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入需要加密的明文:");
        String P = sc.nextLine();

        System.out.println("请输入密钥:");
        String K = sc.nextLine();

        String C;// 密文
        if (K.length() <= 25) {//除了字母j
            C = Encrypt(P, K);
            System.out.println("加密后的密文：" + C);
            System.out.println();
            System.out.println("--------解密--------");
            P = Decrypt(C, K);
            System.out.println("解密后的明文：" + P);
        } else {
            System.out.println("密钥长度不符合条件！");
        }
    }
}