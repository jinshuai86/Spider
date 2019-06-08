package com.jinshuai.core.parser.impl;

import com.jinshuai.core.downloader.impl.HttpClientPoolDownloader;
import com.jinshuai.core.parser.Parser;
import com.jinshuai.entity.Page;
import com.jinshuai.entity.UrlSeed;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  针对hebut新闻类的网页，解析相应内容。
 */
@Slf4j
public class NewsParser implements Parser {

    private static volatile int firstTime = 0;

    // TODO: 待优化解析过程
    public Page parse(Page page) {
        // 获取DOM树
        Document document;
        try {
            document = page.getDocument();
            long priority = timestamp2Priority(document);
            // 种子,并进行预处理
            Set<UrlSeed> urlSeeds = new HashSet<>();
            Iterator seedIterator = document.getElementsByTag("a").iterator();
            while (seedIterator.hasNext()) {
                Element element3 = (Element) seedIterator.next();
                String href = element3.attr("href");
                if (href.contains("http://www.hebut.edu.cn/")|| href.contains("/")  || href.contains("#") || href.contains("index.htm") || href.contains("javascript:void(0);")) continue;
                if ("http://xww.hebut.edu.cn/".equals(page.getUrlSeed().getUrl())) continue;
                urlSeeds.add(new UrlSeed("http://xww.hebut.edu.cn/gdyw/" + href, priority));
            }
            page.setUrlSeeds(urlSeeds);
            if ("http://xww.hebut.edu.cn/".equals(page.getUrlSeed().getUrl())) {
                return page;
            }
            Map<String, String> items = new HashMap<String, String>(3);
            // 标题
            Element titleElement = document.selectFirst("div.sub_articleTitle");
            items.put("title", titleElement.getElementsByTag("h2").text());
            // 时间
            Element dateElement = document.selectFirst("div.sub_articleAuthor");
            items.put("date", dateElement.getElementsByTag("strong").eachText().get(0));
            // 正文
            Element textElement = document.selectFirst("div.sub_articleInfo");
            Iterator textIterator = textElement.getElementsByTag("span").iterator();
            StringBuilder stringBuilder = new StringBuilder();
            while (textIterator.hasNext()) {
                Element element3 = (Element) textIterator.next();
                stringBuilder.append(element3.text());
            }
            items.put("content", stringBuilder.toString());
            page.setItems(items);
        } catch (Exception e) {
            log.error("解析页面[{}]出错",page.getUrlSeed().getUrl(),e);
        } finally {
            return page;
        }
    }

    /**
     * 该Page中的url时间戳参考该Page的时间戳计算优先级
     * */
    private long timestamp2Priority(Document document) {
        String date;
        try {
            date = document.selectFirst("div.sub_articleAuthor").getElementsByTag("strong").eachText().get(0);
        } catch (Exception e) {
            log.error("解析页面异常",e);
            return 5;
        }
        DateTime dateTime = new DateTime(date);
        // 获取时间戳的差值
        long v = DateTimeUtils.currentTimeMillis() - dateTime.getMillis();
        // 换算成天数
        v /= 86400000;
        // 发布时间超过10天设置低的优先级：3，10天：5，小于10天：3
        return v > 10 ? 3 : v == 10 ? 5 : 10;
    }

    private Page getHyperLinkTag(Page page) {
        if (page == null) {
            throw new RuntimeException("page 为空");
        }
        // 获取DOM树
        Document document = page.getDocument();
        // 如果是首页
        if ("http://xww.hebut.edu.cn".equals(page.getUrlSeed().getUrl()) && firstTime == 0) {
            Set<UrlSeed> urlSeeds = new HashSet<UrlSeed>();
            Iterator seedIterator = document.getElementsByTag("a").iterator();
            while (seedIterator.hasNext()) {
                Element element3 = (Element) seedIterator.next();
                String href = element3.attr("href");
                if (href.contains("#") || href.contains("index.html") || href.contains("javascript:void(0);")) continue;
                if (href.startsWith("gdyw") || href.startsWith("zhyw")) {
                    urlSeeds.add(new UrlSeed("http://xww.hebut.edu.cn/" + href,
                            (int) (Math.random() * 10)));
                }
            }
            page.setUrlSeeds(urlSeeds);
            // 已经访问过首页
            firstTime = 1;
        }
        return page;
    }
    /**
     * test
     * */
    public static void main(String[] args) {
        UrlSeed urlSeed = new UrlSeed("http://xww.hebut.edu.cn/gdyw/70772.htm",5);
        Page page = new HttpClientPoolDownloader().download(urlSeed);
//        Page page = new Page(new UrlSeed("http://xww.hebut.edu.cn/gdyw/70772.htm",5), Jsoup.parse("<html></html>","http://xww.hebut.edu.cn/gdyw/index.htm"));
        System.out.println(new NewsParser().parse(page));
    }
}