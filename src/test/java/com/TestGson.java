package com;

import com.google.gson.Gson;
import com.jinshuai.entity.UrlSeed;

/**
 * @author: JS
 * @date: 2018/3/27
 * @description:
 */
public class TestGson {

    public static void main(String[] args) {
        Gson gson = new Gson();
        UrlSeed urlSeed = new UrlSeed("http://www.baidu.com",9);
        System.out.println(gson.toJson(urlSeed));
        System.out.println(5 < 5l);
    }

}
