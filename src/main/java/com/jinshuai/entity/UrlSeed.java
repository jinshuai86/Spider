package com.jinshuai.entity;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  每个Url需要设置优先级，不需要对低于某个优先级的Url进行解析。
 */
public class UrlSeed {

    /**
     * 该种子对应的Url
     * */
    private String url;
    /**
     * 该种子的优先级
     * 硬编码为5。TODO: 待为每一个种子设置合理的优先级
     * 因为种子可能太多，用int可能会溢出。
     * */
    private long priority = 5;

    public UrlSeed(String url, int priority) {
        this.url = url;
        this.priority = priority;
    }

    public String getUrl() {
        return url;
    }

    public UrlSeed setUrl(String url) {
        this.url = url;
        return this;
    }

    public long getPriority() {
        return priority;
    }

    public UrlSeed setPriority(long priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public String toString() {
        return "UrlSeed{" +
                "url='" + url + '\'' +
                ", priority=" + priority +
                '}';
    }
}