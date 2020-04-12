package com.gmall.passport.test;

import com.alibaba.fastjson.JSON;
import com.gmall.util.HttpclientUtil;
import jdk.nashorn.internal.parser.Token;

import java.util.HashMap;
import java.util.Map;

public class TestOauth2 {

    static String getCode() {

        //client_id :806370490
        String s1 = "https://api.weibo.com/oauth2/authorize?client_id=806370490&response_type=code&redirect_uri=http://192.168.1.6:8085/vlogin";

        //获得授权码3796166e20e7a5c057c024be860fe8ba
        String code = "获得授权码3796166e20e7a5c057c024be860fe8ba";
        return null;
    }


    static String getToken() {
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "806370490");
        map.put("client_secret", "44918749c51bbb5cab0641ddc6122748");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://192.168.1.6:8085/vlogin");
        map.put("code","ca3d66c7952a9fca0b0e4b804eabd98a");
        String token = HttpclientUtil.doPost("https://api.weibo.com/oauth2/access_token", map);
        return token;
    }

    static String getUserInfo() {
        String userInfo = HttpclientUtil.doGet("https://api.weibo.com/2/users/show.json?access_token=2.00chhG_G0en8Zs8bbeedd6f12XIklB&uid=1");

        Map map = JSON.parseObject(userInfo, Map.class);

        return null;
    }


    public static void main(String[] args) {
//        String token = getToken();
//        System.out.println(token);
//        //{"access_token":"2.00chhG_G0en8Zs8bbeedd6f12XIklB","remind_in":"157679999","expires_in":157679999,"uid":"5779145340","isRealName":"true"}

        getUserInfo();

    }

}
