import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class FinalOneTimePad {

    public static String key;
    public static byte[] keyBytes;//将口令从字符串转换为字节
    public static String File;
    public static String enFile;

    public static void main(String[] args) {

        final JFrame jf = new JFrame("一次一密算法");
        jf.setSize(400, 400);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();

        //提示标签
        JLabel label = new JLabel();
        label.setText("密钥：");
        label.setFont(new Font(null, Font.PLAIN, 12));  // 设置字体，null 表示使用默认字体
        panel.add(label);

        // 输入密钥
        JTextArea keyTextArea = new JTextArea(1, 15);
        keyTextArea.setLineWrap(true);
        panel.add(keyTextArea);


        JButton ChooseEncBtn = new JButton("选择加密文件");
        ChooseEncBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showFileOpenDialog(jf);
                    showFileSaveDialog(jf);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(ChooseEncBtn);

        JButton EncBtn = new JButton("加密");
        EncBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    key = keyTextArea.getText();
                    keyBytes = key.getBytes();
                    enc(File, enFile);
                    JOptionPane.showMessageDialog(
                            jf,
                            "加密成功",
                            "提示",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(EncBtn);

        //提示标签
        JLabel label1 = new JLabel();
        label1.setText("密钥：");
        label1.setFont(new Font(null, Font.PLAIN, 12));  // 设置字体，null 表示使用默认字体
        panel.add(label1);

        // 输入密钥
        JTextArea keyTextArea1 = new JTextArea(1, 15);
        keyTextArea1.setLineWrap(true);
        panel.add(keyTextArea1);


        JButton ChooseDecBtn = new JButton("选择解密文件");
        ChooseDecBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showFileOpenDialog(jf);
                    showFileSaveDialog(jf);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(ChooseDecBtn);

        JButton DecBtn = new JButton("解密");
        DecBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    key = keyTextArea1.getText();
                    keyBytes = key.getBytes();
                    dec(File, enFile);
                    JOptionPane.showMessageDialog(
                            jf,
                            "解密成功",
                            "提示",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(DecBtn);

        jf.setContentPane(panel);
        jf.setVisible(true);
    }

    /*
     * 打开文件
     */
    private static void showFileOpenDialog(Component parent) throws IOException {
        // 创建一个默认的文件选取器
        JFileChooser fileChooser = new JFileChooser();

        //设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(new File("."));

        fileChooser.setSelectedFile(new File(""));

        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            java.io.File file = fileChooser.getSelectedFile();

            // 如果允许选择多个文件, 则通过下面方法获取选择的所有文件
            //   File[] files = fileChooser.getSelectedFiles();
            File = file.getAbsolutePath();
        }
    }

    /*
     * 选择文件保存路径
     */
    private static void showFileSaveDialog(Component parent) throws IOException {
        // 创建一个默认的文件选取器
        JFileChooser fileChooser = new JFileChooser();
        //设置默认显示的文件夹为当前文件夹
        fileChooser.setCurrentDirectory(new File("."));
        // 设置打开文件选择框后默认输入的文件名
        fileChooser.setSelectedFile(new File("文件.zip"));

        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showSaveDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"保存", 则获取选择的保存路径
            java.io.File file = fileChooser.getSelectedFile();
            enFile = file.getAbsolutePath();
        }
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