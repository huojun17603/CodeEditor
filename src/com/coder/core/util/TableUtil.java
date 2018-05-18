package com.coder.core.util;

public class TableUtil {

    public static String getClassName(String tableName) {
        String arr[] = tableName.trim().split("_");
        String result = "";
        for(String a : arr){
            result += a.substring(0,1).toUpperCase() + a.substring(1);
        }
        return result;
    }

    public static String upperCase(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

    public static String lowerCase(String str) {
        return str.substring(0,1).toLowerCase() + str.substring(1);
    }
}
