package com.jinshuai.core.parser.impl;

import com.jinshuai.core.parser.Parser;
import com.jinshuai.entity.Page;
import com.jinshuai.entity.UrlSeed;
import com.sun.xml.internal.ws.message.ProblemActionHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  针对新闻类的网页，解析相应内容。
 */
public class NewsParser implements Parser {

    private static Logger LOGGER = LoggerFactory.getLogger(NewsParser.class);

    private static volatile int firstTime = 0;

    // TODO: 待优化解析过程
    public Page parse(Page page) {
        // 获取DOM树
        Document document = page.getDocument();
        try {
            // 种子,并进行预处理
            Set<UrlSeed> urlSeeds = new HashSet<UrlSeed>();
            Iterator seedIterator = document.getElementsByTag("a").iterator();
            while (seedIterator.hasNext()) {
                Element element3 = (Element) seedIterator.next();
                String href = element3.attr("href").toString();
                if (href.contains("http://www.hebut.edu.cn/")|| href.contains("/")  || href.contains("#") || href.contains("index.htm") || href.contains("javascript:void(0);")) continue;
                if ("http://xww.hebut.edu.cn/".equals(page.getUrlSeed().getUrl())) continue;
                urlSeeds.add(new UrlSeed("http://xww.hebut.edu.cn/gdyw/" + href, (int) (Math.random() * 10)));
            }
            page.setUrlSeeds(urlSeeds);
            if ("http://xww.hebut.edu.cn/".equals(page.getUrlSeed().getUrl())) {return page;}
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
            items.put("text", stringBuilder.toString());
            page.setItems(items);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("[解析页面] " + page.getUrlSeed().getUrl() + " 出现异常: " + e.getMessage());
        } finally {
            return page;
        }
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
                String href = element3.attr("href").toString();
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
        Page page = new Page(new UrlSeed("http://xww.hebut.edu.cn/gdyw/index.htm",5), Jsoup.parse("<html></html>","http://xww.hebut.edu.cn/gdyw/index.htm"));
        System.out.println(new NewsParser().parse(page));
    }
}