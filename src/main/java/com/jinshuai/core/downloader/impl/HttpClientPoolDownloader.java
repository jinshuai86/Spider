package com.jinshuai.core.downloader.impl;

import com.jinshuai.core.downloader.Downloader;
import com.jinshuai.entity.UrlSeed;
import com.jinshuai.utils.HttpUtils;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  通过Http连接池下载
 */
public class HttpClientPoolDownloader implements Downloader {

    public String download(UrlSeed urlSeed) {
        return HttpUtils.getSingleInstance().getContent(urlSeed.getUrl());
    }

}