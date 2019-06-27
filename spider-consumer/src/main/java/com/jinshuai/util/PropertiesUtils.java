package com.jinshuai.util;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: JS
 * @date: 2018/5/4
 * @description:
 *  读取配置文件工具类
 */
@Slf4j
public class PropertiesUtils {

    private Map<String, String> cache = new ConcurrentHashMap<>();

    private static volatile PropertiesUtils propertiesUtils;

    public static PropertiesUtils getInstance() {
        if (propertiesUtils == null) {
            synchronized (PropertiesUtils.class) {
                if (propertiesUtils == null) {
                    propertiesUtils = new PropertiesUtils();
                }
            }
        }
        return propertiesUtils;
    }

    public String get(String key) {
        if (key == null)
            return null;
        if (cache.get(key) != null) {
            return cache.get(key);
        }
        Properties properties = new Properties();
        InputStream inputStream = PropertiesUtils.class.getResourceAsStream("/application.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("加载配置文件[application.properties]失败",e);
        }
        String value = properties.getProperty(key);
        cache.put(key,value);
        return value;
    }

    public static void main(String[] args) throws IOException {
//        Properties properties = new Properties();
//        InputStream inputStream = PropertiesUtils.class.getResourceAsStream("/application.properties");
//        properties.load(inputStream);
//        System.out.println(properties.getProperty("ip"));
//        System.out.println(properties.getProperty("ip"));
        System.out.println(PropertiesUtils.getInstance().get("ip"));
    }

}