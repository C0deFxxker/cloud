package com.lyl.study.cloud.base.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;


public class QrCodeUtils {
    private static final String CHARSET = "UTF-8";
    private static final String FORMAT_NAME = "JPG";
    /**
     * 二维码尺寸
     */
    private static final int QRCODE_SIZE = 200;
    /**
     * LOGO宽度
     */
    private static final int WIDTH = 60;
    /**
     * LOGO高度
     */
    private static final int HEIGHT = 60;

    /**
     * 根据内容生成二维码
     *
     * @param content 二维码内容
     * @return 返回二维码图片
     * @throws IOException BufferedImage
     */
    private static BufferedImage createImage(String content) throws WriterException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    /**
     * user: Rex
     * date: 2016年12月29日  上午12:30:09
     *
     * @param source       二维码图片
     * @param logoImgPath  Logo
     * @param needCompress 是否压缩Logo
     * @throws IOException void
     */
    private static void insertImage(BufferedImage source, String logoImgPath, boolean needCompress) throws IOException {
        File file = new File(logoImgPath);
        if (!file.exists()) {
            return;
        }

        Image src = ImageIO.read(new File(logoImgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        // 压缩LOGO
        if (needCompress) {
            if (width > WIDTH) {
                width = WIDTH;
            }

            if (height > HEIGHT) {
                height = HEIGHT;
            }

            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            // 绘制缩小后的图
            g.drawImage(image, 0, 0, null);
            g.dispose();
            src = image;
        }

        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * user: Rex
     * date: 2016年12月29日  上午12:32:32
     *
     * @param content  二维码内容
     * @param destPath 二维码输出路径
     * @throws Exception void
     */
    public static void encode(String content, String destPath) throws Exception {
        BufferedImage image = createImage(content);
        File file = new File(destPath);
        FileCopyUtils.copy("".getBytes(), file);
        ImageIO.write(image, FORMAT_NAME, file);
    }

    /**
     * user: Rex
     * date: 2016年12月29日  上午12:36:58
     *
     * @param content 二维码内容
     * @param output  输出流
     * @throws Exception void
     */
    public static void encode(String content, OutputStream output) throws Exception {
        BufferedImage image = createImage(content);
        ImageIO.write(image, FORMAT_NAME, output);
    }

    /**
     * 解码
     *
     * @param file 二维码
     * @return 返回解析得到的二维码内容
     * @throws Exception String
     */
    public static String decode(File file) throws Exception {
        return decode(new FileInputStream(file));
    }

    /**
     * 解码
     *
     * @param path 二维码存储位置
     * @return 返回解析得到的二维码内容
     * @throws Exception String
     */
    public static String decode(String path) throws Exception {
        return decode(new File(path));
    }

    /**
     * 解码
     *
     * @param inputStream 二维码存储位置
     * @return 返回解析得到的二维码内容
     * @throws Exception String
     */
    public static String decode(InputStream inputStream) throws Exception {
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) {
            return null;
        }
        HybridBinarizer hybridBinarizer = new HybridBinarizer(new BufferedImageLuminanceSource(image));
        BinaryBitmap bitmap = new BinaryBitmap(hybridBinarizer);

        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

//    public static void main(String[] args) throws Exception {
//        BufferedImage image = QrCodeUtils.createImage("https://www.baidu.com/");
//        File file = new File("/Users/liyilin/dev/workspace/java/meicloud/scrm-project/scrm-parent/data.jpg");
//        ImageIO.write(image, "jpg", file);
//        String content = QrCodeUtils.decode(file);
//        System.out.println("解码内容：" + content);
//    }
}