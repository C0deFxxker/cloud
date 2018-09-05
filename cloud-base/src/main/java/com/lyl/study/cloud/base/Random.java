package com.lyl.study.cloud.base;

/**
 * @author liyilin
 */
public class Random extends java.util.Random {
    /**
     * 随机一个字符，可能是大小字母、小写字母
     * @return 一个字符
     */
    public char nextAlphaCharacter() {
        int rand = this.nextInt(52);

        if (rand < 26) {
            // 小写字母
            return (char)(97 + rand);
        } else {
            // 大写字母
            return (char)(65 + rand - 26);
        }
    }

    /**
     * 随机一个字符，可能是大小字母、小写字母、数字
     * @return 一个字符
     */
    public char nextAlphaNumCharacter() {
        int rand = this.nextInt(62);

        if (rand < 26) {
            // 小写字母
            return (char)(97 + rand);
        } else if (rand < 52){
            // 大写字母
            return (char)(65 + rand - 26);
        } else {
            // 数字
            return (char)(48 + rand - 52);
        }
    }

    /**
     * 随机一个字符串，包含大写或小写字母
     * @param length 字符串长度
     * @return 包含大写或小写字母的字符串
     */
    public String nextAlphaString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(this.nextAlphaCharacter());
        }
        return stringBuilder.toString();
    }

    /**
     * 随机一个字符串，包含大写或小写字母或数字
     * @param length 字符串长度
     * @return 包含大写或小写字母的字符串
     */
    public String nextAlphaNumString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(this.nextAlphaNumCharacter());
        }
        return stringBuilder.toString();
    }
}
