package com.jinshuai.core.downloader.impl;

import com.jinshuai.core.downloader.Downloader;
import com.jinshuai.entity.Page;
import com.jinshuai.entity.UrlSeed;
import com.jinshuai.util.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  通过Http连接池下载
 */
@Slf4j
public class HttpClientPoolDownloader implements Downloader {

    public Page download(UrlSeed urlSeed) {
        Page page = null;
        try {
            String html = HttpUtils.getSingleInstance().getContent(urlSeed.getUrl());
            Document document = Jsoup.parse(html, urlSeed.getUrl());
            page = new Page(urlSeed, document);
        } catch (Exception e) {
            log.error("下载器下载的相应文本获取DOM树失败", e);
        }
        return page;
    }

}