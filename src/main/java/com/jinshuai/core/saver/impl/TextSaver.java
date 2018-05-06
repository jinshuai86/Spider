package com.jinshuai.core.saver.impl;

import com.jinshuai.core.saver.Saver;
import com.jinshuai.entity.Page;
import com.jinshuai.entity.UrlSeed;
import org.jsoup.Jsoup;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author: JS
 * @date: 2018/3/27
 * @description:
 *  存储在文本文件中
 */
public class TextSaver implements Saver {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextSaver.class);

    public void save(Page page) {
        if (page == null) return;
        String parentDir = "F:/HEBUTNews/";
        File file = new File(parentDir + (new Date().getTime()) + ".txt");
        boolean isExist = file.getParentFile().exists();
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        FileWriter fw = null;
        try {
            file.createNewFile();
            fw = new FileWriter(file);
            if (page.getItems() == null) {
                fw.flush();
                return;
            }
            fw.append("[标题] " + page.getItems().get("title") + "\n");
            fw.append("[日期] " + page.getItems().get("date") + "\n");
            fw.append("[正文] " + page.getItems().get("text")+ "\n");
            fw.append("[链接] " + page.getUrlSeed().getUrl());
            fw.flush();
        } catch (IOException e) {
            LOGGER.error("存储路径无效",e);
        } finally {
            if (fw != null) try {
                fw.close();
            } catch (IOException e) {
                LOGGER.error("流关闭失败");
            }
        }
    }

    public static void main(String[] args) {
        new TextSaver().save(new Page(new UrlSeed("",5), Jsoup.parse("HTML","")).setItems(null));
    }

}