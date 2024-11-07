package io.github.cmmplb.activiti.utils;

import java.security.MessageDigest;

/**
 * @author penglibo
 * @date 2021-08-30 15:45:15
 * @since jdk 17
 * MD5加密工具类
 */

public class MD5Util {

    // 加密算法
    public static final String MD5 = "MD5";

    /**
     * Md5加密
     * @param str 待加密字符串
     * @return md5加密串
     */
    public static String encode(String str) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(MD5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        // 32位加密
        return hexValue.toString();
    }
}
