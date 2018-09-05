package com.lyl.study.cloud.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class ImageVerification {
    private static final Logger logger = LoggerFactory.getLogger(ImageVerification.class);

    /**
     * 图片的宽度
     */
    private int width = 160;
    /**
     * 图片的高度
     */
    private int height = 40;
    /**
     * 验证码干扰线数
     */
    private int lineCount = 150;

    public ImageVerification() {
    }

    /**
     * @param width  图片宽
     * @param height 图片高
     */
    public ImageVerification(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * @param width     图片宽
     * @param height    图片高
     * @param lineCount 干扰线条数
     */
    public ImageVerification(int width, int height, int lineCount) {
        this.width = width;
        this.height = height;
        this.lineCount = lineCount;
    }

    public String createCode(int length) {
        return new Random().nextAlphaNumString(length);
    }

    public void write(OutputStream sos, String code) throws IOException {
        int x = 0, fontHeight = 0, codeY = 0;
        int red = 0, green = 0, blue = 0;
        int codeCount = code.length();

        // 每个字符的宽度(左右各空出一个字符)
        x = width / (codeCount + 2);
        // 字体的高度
        fontHeight = height - 2;
        codeY = height - 4;

        // 图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        // 生成随机数
        java.util.Random random = new java.util.Random();
        // 将图像填充为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 创建字体,可以修改为其它的
        Font font = new Font("DejaVu Sans", Font.PLAIN, fontHeight);
        // Font font = new Font("Times New Roman", Font.ROMAN_BASELINE,
        // fontHeight);
        g.setFont(font);

        for (int i = 0; i < lineCount; i++) {
            // 设置随机开始和结束坐标
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width / 8);
            int ye = ys + random.nextInt(height / 8);

            // 产生随机的颜色值，让输出的每个干扰线的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawLine(xs, ys, xe, ye);
        }

        // 随机产生codeCount个字符的验证码。
        for (int i = 0; i < codeCount; i++) {
            String str = code.substring(i, i + 1);
            // 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawString(str, (i + 1) * x, codeY);
        }

        try {
            // 将图片直接输出到前端
            ImageIO.write(buffImg, "JPEG", sos);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            sos.close();
        }
    }
}
