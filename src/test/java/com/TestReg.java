package com;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: JS
 * @date: 2018/3/23
 * @description:
 */
public class TestReg {

    public static void main(String[] args) {
        String src = "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Cache-Control\" content=\"no-siteapp\"/>\n" +
                "<meta name=\"shenma-site-verification\" content=\"5a59773ab8077d4a62bf469ab966a63b_1497598848\"/>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n" +
                "<meta name=\"referrer\" content=\"always\">\n" +
                "<link href=\"https://csdnimg.cn//public/favicon.ico\" rel=\"shortcut icon\" />\n" +
                "<link href=\"https://blog.csdn.net/lfdfhl/article/details/8225546\"  rel=\"canonical\" />\n" +
                "<link href=\"https://blog.csdn.net/lfdfhl/rss/list\" id=\"RSSLink\" title=\"RSS\" type=\"application/rss+xml\" rel=\"alternate\" />\n" +
                "<script src=\"https://csdnimg.cn/public/common/libs/jquery/jquery-1.9.1.min.js\" type=\"text/javascript\"></script>\n" +
                "<script src=\"https://dup.baidustatic.com/js/ds.js\" type=\"text/javascript\"></script>";
        Pattern pattern = Pattern.compile("<head>([\\s\\S]*?)<meta([\\s\\S]*?)charset\\s*=(\")?(.*?)\"");
        Matcher matcher = pattern.matcher(src.toLowerCase());
        if (matcher.find()) {
            System.out.println(matcher.group(4));
        }

    }
}
