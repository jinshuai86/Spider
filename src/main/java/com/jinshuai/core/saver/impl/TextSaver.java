package com.jinshuai.core.saver.impl;

import com.jinshuai.core.saver.Saver;
import com.jinshuai.entity.Page;
import com.jinshuai.util.PropertiesUtils;
import com.jinshuai.util.hash.PageUtils;
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
 *  存储到txt
 */
public class TextSaver implements Saver {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextSaver.class);

    private String parentDir;

    private PageUtils pageUtil = PageUtils.getInstance();

    private PropertiesUtils propertiesUtil = PropertiesUtils.getInstance();

    public TextSaver() {
        init();
    }

    /**
     * 初始化文件要存的目录
     * */
    private void init() {
        parentDir = PropertiesUtils.getInstance().get("dir");
        File file = new File(parentDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        LOGGER.info("解析后的文件存放位置：[{}]",parentDir);
    }

    public void save(Page page) {
        if (page == null) {
            return;
        }
        // 文本相似度检测
        String similarCheck = propertiesUtil.get("similarCheck");
        if (similarCheck != null && !similarCheck.trim().equals("") &&similarCheck.equalsIgnoreCase("true")) {
            String title = page.getItems().get("title");
            String content = page.getItems().get("content");
            if(pageUtil.exist(title, content)) {
                LOGGER.info("标题为 [{}] 的相似文章已经存在", title);
            }
        }
        File file = new File(String.format("%s%s.txt",parentDir,new Date().getTime()));
        try (FileWriter fw = new FileWriter(file)) {
            if (page.getItems() == null) {
                fw.flush();
                return;
            }
            fw.append(String.format("[标题] %s\n",page.getItems().get("title")));
            fw.append(String.format("[日期] %s\n", page.getItems().get("date")));
            fw.append(String.format("[正文] %s\n",page.getItems().get("content")));
            fw.append(String.format("[链接] %s\n",page.getUrlSeed().getUrl()));
            fw.flush();
        } catch (IOException e) {
            LOGGER.error("存储路径无效",e);
        }
    }

    public static void main(String[] args) throws IOException {
//        String parentDir = "E:/HEBUTNews/";
//        File file = new File(parentDir+ (new Date().getTime()) + ".txt");
//        //file.createNewFile();
//        if (!file.getParentFile().exists()) {
//            //file.getParentFile().mkdirs();
//
//        }
//        FileWriter fileWriter = new FileWriter(file);
//        fileWriter.append("fasdfs");
//        fileWriter.flush();
        Saver saver = new TextSaver();

//        new TextSaver().save(new Page(new UrlSeed("",5), Jsoup.parse("HTML","")).setItems(null));
    }

}