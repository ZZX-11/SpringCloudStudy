package com.example.train.business.service;

public class Test {
    public static void main(String[] args) {
        String str = "00000";
        int start = 3;
        int end = 5;

//        String result = getSubstring(str, start, end);
//        System.out.println("指定位置之间的字符是: " + result);

        String s = replaceInRange(str, start, end);
        System.out.println(s);
    }

    public static String getSubstring(String str, int start, int end) {
        if (start >= 0  && start <= end) {
            return str.substring(start-1, end-1);
        } else {
            return "位置输入无效";
        }
    }
    public static String replaceInRange(String str, int start, int end) {
//        StringBuilder replacedString = new StringBuilder(str);
        String str1="";
        int length = str.length();
        String str2 = str.substring(0, start-1);
        String str3 = str.substring(end-1, length);

        str1 = getSubstring(str, start, end).replaceAll(".","1");
        return str2+str1+str3;
    }
}
