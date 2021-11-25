import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class OneTimePad {//E:\图片.jpg
    public static String key;
    public static byte[] keyBytes;//将口令从字符串转换为字节

    public static void main(String[] args) throws IOException {//E:\视频.mp4 E:\视频加密.mp4 E:\视频解密.mp4
        int number;
        do {
            System.out.println("1.加密 2.解密 else.退出");
            System.out.print("请输入功能选项：");
            Scanner sc = new Scanner(System.in);
            number = sc.nextInt();
            switch (number) {
                case 1:
                    System.out.println("请输入密钥：");
                    key = sc.next();
                    keyBytes = key.getBytes();
                    System.out.println("请输入需要加密的文件地址(如E:/info/图片.jpg)：");
                    String file = sc.next();
                    System.out.println("请输入加密文件的保存地址(如E:/info/加密图片.jpg)：");
                    String enfile = sc.next();
                    enc(file, enfile);break;
                case 2:
                    System.out.println("请输入密钥：");
                    key = sc.next();
                    keyBytes = key.getBytes();
                    System.out.println("请输入需要解密的文件地址(如E:/info/加密图片.jpg)：");
                    String enfile1 = sc.next();
                    System.out.println("请输入解密文件的保存地址(如E:/info/解密图片.jpg)：");
                    String defile = sc.next();
                    dec(enfile1, defile);break;
                default:  System.exit(0);
            }
        }while (true);
    }

    private static void enc(String file, String encFile) throws IOException {
        InputStream is = new FileInputStream(file);//源文件创建输入流,字符串转换为字节
        int length;
        byte[] buffer = new byte[1024];
        byte[] buffer2 = new byte[1024];
        FileOutputStream enfile = new FileOutputStream(encFile);//加密文件的输出流
        while ((length = is.read(buffer)) != -1) {//从输入流中读取1024个字节，并将其存储在缓冲区数组buffer中
            for (int i = 0; i < length; i++) {
                buffer2[i] = (byte) (buffer[i] ^ keyBytes[i % key.length()]);//将字节流的每一位和密钥的对应位进行异或
            }                                                               //明文长度大于等于密文的长度
            enfile.write(buffer2, 0, length);//写入加密后的字节
            enfile.flush();
        }
        enfile.close();
    }

    private static void dec(String encFile, String decFile) throws IOException {//解密过程类似
        InputStream is = new FileInputStream(encFile);
        int length;
        byte[] buffer = new byte[1024];
        byte[] buffer2 = new byte[1024];
        FileOutputStream defile = new FileOutputStream(decFile);
        while ((length = is.read(buffer)) != -1) {
            for (int i = 0; i < length; i++) {
                buffer2[i] = (byte) (buffer[i] ^ keyBytes[i % key.length()]);
            }
            defile.write(buffer2, 0, length);
            defile.flush();
        }
        defile.close();
    }
}