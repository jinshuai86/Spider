package com.jinshuai.entity;

import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.Set;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  每一个UrlSeed对应的页面抽象为一个Page
 */
public class Page {

    /**
     * Page对应的UrlSeed
     * */
    private UrlSeed urlSeed;

    /**
     * Page对应的jsoup文档
     * */
    private Document document;

    /**
     * Page包含的url
     * */
    private Set<UrlSeed> urlSeeds;

    /**
     * Page所包含的有用信息
     * */
    private Map<String,String> items;

    public Page(UrlSeed urlSeed, Document document) {
        this.urlSeed = urlSeed;
        this.document = document;
    }

    public UrlSeed getUrlSeed() {
        return urlSeed;
    }

    public Page setUrlSeed(UrlSeed urlSeed) {
        this.urlSeed = urlSeed;
        return this;
    }

    public Document getDocument() {
        return document;
    }

    public Page setDocument(Document document) {
        this.document = document;
        return this;
    }

    public Set<UrlSeed> getUrlSeeds() {
        return urlSeeds;
    }

    public Page setUrlSeeds(Set<UrlSeed> urlSeeds) {
        this.urlSeeds = urlSeeds;
        return this;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public Page setItems(Map<String, String> items) {
        this.items = items;
        return this;
    }

    @Override
    public String toString() {
        return "Page{" +
                "urlSeed=" + urlSeed +
                ", urlSeeds=" + urlSeeds +
                ", items=" + items +
                '}';
    }
}