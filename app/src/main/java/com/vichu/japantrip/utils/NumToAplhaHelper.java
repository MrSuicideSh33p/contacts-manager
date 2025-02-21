package com.vichu.japantrip.utils;

public class NumToAplhaHelper {

    public static String numToLetterByAsciiCode(int i) {
        if (i > 0 && i <= 26) {
            return String.valueOf((char) ('A' + i - 1));
        } else {
            throw new IllegalArgumentException("Invalid integer range passed.");
        }
    }
}
