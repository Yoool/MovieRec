package com.work.movierec.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ValidatorUtil {
    //ѧ��У��
    private static final Pattern stuno_pattern = Pattern.compile("\\d{1,20}");

    public static boolean isStuno(String src) {
        return StringUtils.isEmpty(src) ? false : stuno_pattern.matcher(src).matches();
    }
}
