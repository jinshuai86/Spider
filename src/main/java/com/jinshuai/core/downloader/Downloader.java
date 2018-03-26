package com.jinshuai.core.downloader;

import com.jinshuai.entity.UrlSeed;

/**
 * 下载器接口，可以针对此接口构造多种下载器实现
 * */
public interface Downloader {

    /***
     * @param urlSeed  待使用种子
     * @return 响应体内容
     */
    String download(UrlSeed urlSeed);

}
