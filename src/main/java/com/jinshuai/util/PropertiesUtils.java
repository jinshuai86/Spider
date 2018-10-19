package com.jinshuai.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * @author: JS
 * @date: 2018/5/4
 * @description:
 *  读取配置文件工具类
 */
public class PropertiesUtils {

    private Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

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
        if (key == null) return null;
        Properties properties = new Properties();
        InputStream inputStream = PropertiesUtils.class.getResourceAsStream("/application.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("加载文件失败",e);
        }
        return properties.getProperty(key);
    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = PropertiesUtils.class.getResourceAsStream("/application.properties");
        properties.load(inputStream);
        System.out.println(properties.getProperty("password"));
    }

}