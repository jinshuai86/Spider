package com.jinshuai.entity;

import com.jinshuai.util.hash.MurmurHash;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  每个Url需要设置优先级，不需要对低于某个优先级的Url进行解析。
 */
@ToString
@EqualsAndHashCode
public class UrlSeed {

    /**
     * 种子对应的Url
     * */
    private String url;

    /**
     * url hash
     * */
    private String urlHash;

    /**
     * 种子优先级
     * 硬编码为5,通过时间戳设置优先级
     * */
    private long priority = 5;

    public UrlSeed(String url, long priority) {
        this.url = url;
        this.priority = priority;
        this.urlHash = String.valueOf(MurmurHash.hash64(url));
    }

    public String getUrl() {
        return url;
    }

    public UrlSeed setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUrlHash() {
        return urlHash;
    }

    public long getPriority() {
        return priority;
    }

    public UrlSeed setPriority(long priority) {
        this.priority = priority;
        return this;
    }

}