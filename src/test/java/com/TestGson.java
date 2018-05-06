package com;

import com.google.gson.Gson;
import com.jinshuai.entity.UrlSeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: JS
 * @date: 2018/3/27
 * @description:
 */
public class TestGson {
    static Logger logger = LoggerFactory.getLogger(TestGson.class);

    public static void main(String[] args) {
        Gson gson = new Gson();
        UrlSeed urlSeed = new UrlSeed("http://www.baidu.com",9);
        System.out.println(gson.toJson(urlSeed));
        System.out.println(5 < 5l);
        try {
            int i = 4/0;
        } catch (Exception e) {
            logger.error("{}",e);
        }

    }

}
