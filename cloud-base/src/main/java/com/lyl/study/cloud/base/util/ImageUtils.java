package com.lyl.study.cloud.base.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 图片工具类，图片添加logo，文字等
 *
 * @author guoliang6
 */
public class ImageUtils {
    private final static String PICTRUE_FORMATE_PNG = "jpg";

    /**
     * 获取字符长度，一个汉字作为1个字符，一个英文字母或数字作为0.5个字符
     *
     * @param text 文本
     * @return 字符长度
     */
    private static double getLength(String text) {
        int textLength = text.length();
        int length = 0;
        for (int i = 0; i < textLength; i++) {
            if (String.valueOf(text.charAt(i)).getBytes().length == 1) {
                length++;
            }
        }
        return textLength - (length * 0.5);
    }

    /**
     * 添加文字水印
     *
     * @param targetImg 目标图片路径
     * @param pressText 水印文字
     * @param fontName  字体名称，如：宋体
     * @param fontStyle 字体样式，如：粗体和斜体(Font.BOLD|Font.ITALIC)
     * @param fontSize  字体大小，单位为像素
     * @param color     字体颜色
     * @param x         水印文字距离目标图片左侧的偏移量，如果x<0, 则在正中间
     * @param y         水印文字距离目标图片上侧的偏移量，如果y<0, 则在正中间
     * @param alpha     透明度（0.0--1.0，0.0为完全透明，1.0为完全不透明）
     */
    public static void pressText(String targetImg, String pressText, String fontName, int fontStyle, int fontSize, Color color, int x, int y, float alpha) {
        try {
            File file = new File(targetImg);

            Image image = ImageIO.read(file);
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setColor(color);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

            int width_1 = (int) (fontSize * getLength(pressText));
            int height_1 = fontSize;
            int widthDiff = width - width_1;
            int heightDiff = height - height_1;
            if (x < 0) {
                x = widthDiff / 2;
            } else if (x > widthDiff) {
                x = widthDiff;
            }
            if (y < 0) {
                y = heightDiff / 2;
            } else if (y > heightDiff) {
                y = heightDiff;
            }

            g.drawString(pressText, x, y + height_1);
            g.dispose();
            ImageIO.write(bufferedImage, PICTRUE_FORMATE_PNG, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片缩放
     *
     * @param filePath 图片路径
     * @param height   高度：缩放的高度
     * @param width    宽度：缩放的宽度
     * @param bb       比例不对时是否需要补白
     *                 bb：为true表示按比例压缩，是按照等比例，如：4:1,16:9等；
     *                 比如原图为1279*724的尺寸，按高度900等比例压缩应该是900*509，
     *                 但非要生成900*700的比例，bb参数应该是false，
     *                 意味着非等比例压缩，意味着强制压缩。bb=false也可等同于图片裁剪功能
     * @param output   压缩后文件存储的路径
     */
    public static void resize(String filePath, int height, int width, boolean bb, String output) throws Exception {
        // 缩放比例
        double ratio = 0;
        File f = new File(filePath);
        BufferedImage bi = ImageIO.read(f);

        // 初始化需要缩放的高度，宽度的画板
        BufferedImage itemp = new BufferedImage(width, height, BufferedImage.SCALE_SMOOTH);
        Graphics2D g2d = itemp.createGraphics();
        g2d.drawImage(bi, 0, 0, null);

        // 计算比例
        if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
            ratio = bi.getHeight() > bi.getWidth() ? (new Integer(height)).doubleValue() / bi.getHeight() : (new Integer(width)).doubleValue() / bi.getWidth();

            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
            itemp = op.filter(bi, null);
        }

        // 等比例压缩
        if (bb) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, width, height);
            if (width == itemp.getWidth(null)) {
                g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null), Color.white, null);
            } else {
                g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null), Color.white, null);
            }
            g.dispose();
            itemp = image;
        }
        File file = new File(output);
        ImageIO.write((BufferedImage) itemp, PICTRUE_FORMATE_PNG, file);
    }
}