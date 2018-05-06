package com.jinshuai.core.downloader.impl;

import com.jinshuai.core.downloader.Downloader;
import com.jinshuai.entity.Page;
import com.jinshuai.entity.UrlSeed;
import com.jinshuai.utils.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  通过Http连接池下载
 */
public class HttpClientPoolDownloader implements Downloader {

    private final Logger LOGGER = LoggerFactory.getLogger(HttpClientPoolDownloader.class);

    public Page download(UrlSeed urlSeed) {
        Page page = null;
        try {
            String html = HttpUtils.getSingleInstance().getContent(urlSeed.getUrl());
            Document document = Jsoup.parse(html, urlSeed.getUrl());
            page = new Page(urlSeed, document);
        } catch (Exception e) {
            LOGGER.error("下载器下载的相应文本获取DOM树失败",e);
        }
        return page;
    }

}