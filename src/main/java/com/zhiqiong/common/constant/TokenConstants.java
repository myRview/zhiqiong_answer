package com.zhiqiong.common.constant;

public class TokenConstants {

    public static final String USER_KEY = "user_key";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_INFO = "user_info";
    public static final String USER_TOKEN = "user_token";

    public static String concatTokenStr(String s) {
        return USER_TOKEN.concat(":").concat(s);
    }
}
