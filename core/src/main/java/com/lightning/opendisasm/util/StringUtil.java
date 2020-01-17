package com.lightning.opendisasm.util;

import java.util.Arrays;

public class StringUtil {
    public static String getSpaces(int numSpaces) {
        char[] charArray = new char[numSpaces];
        Arrays.fill(charArray, ' ');
        return new String(charArray);
    }
}
