package com.work.movierec.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "web2020";

    // 第一次md5，用户密码加密为FormPassword
    public static String inputPassToFormPass(String inputPass) {
        String str = "" + salt.substring(0, 3) + inputPass + salt.substring(3);
        System.out.println(str);
        return md5(str);
    }

    // 第二次md5，FormPassword加密为数据库存的密码DBPassword
    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" + salt.substring(0, 3) + formPass + salt.substring(3);
        return md5(str);
    }

    // 两次md5
    public static String inputPassToDbPass(String inputPass, String salt) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, salt);
        return dbPass;
    }
}
