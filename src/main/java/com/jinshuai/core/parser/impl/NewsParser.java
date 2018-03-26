package com.jinshuai.core.parser.impl;

import com.jinshuai.core.parser.Parser;
import com.jinshuai.entity.Page;
import com.jinshuai.entity.UrlSeed;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  针对新闻类的网页，解析相应内容。
 */
public class NewsParser implements Parser {

    // TODO: 优化解析过程
    public Page parse(Page page) {
        // 获取DOM树
        Document document = page.getDocument();
        Map<String, String> items = new HashMap<String, String>(3);
        // 标题
        Element titleElement = document.selectFirst("div.sub_articleTitle");
        items.put("title",titleElement.getElementsByTag("h2").text());
        // 时间
        Element dateElement = document.selectFirst("div.sub_articleAuthor");
        items.put("date",dateElement.getElementsByTag("strong").eachText().get(0));
        // 正文
        Element textElement = document.selectFirst("div.sub_articleInfo");
        Iterator textIterator = textElement.getElementsByTag("span").iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (textIterator.hasNext()) {
            Element element3 = (Element) textIterator.next();
            stringBuilder.append(element3.text());
        }
        items.put("text",stringBuilder.toString());
        page.setItems(items);
        // 种子
        Set<String> urlSeeds = new HashSet<String>();
        Iterator seedIterator = document.getElementsByTag("a").iterator();
        while (seedIterator.hasNext()) {
            Element element3 = (Element) seedIterator.next();
            String href = element3.attr("href").toString();
            if (href.contains("/") || href.contains("#")) continue;
            urlSeeds.add("http://xww.hebut.edu.cn/gdyw/" + href);
        }
        urlSeeds.remove("index.html");
        urlSeeds.remove("");
        urlSeeds.remove("javascript:void(0);");
        page.setUrlSeeds(urlSeeds);
        return page;
    }

    /**
     * test
     * */
    public static void main(String[] args) {
        Page page = new Page(new UrlSeed("http://xww.hebut.edu.cn/gdyw/66900.htm",5),
                "<!doctype html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\" />\n" +
                "<meta name=\"format-detection\" content=\"telephone=no\" />\n" +
                "<title>学校举行新入职教师岗前培训开班仪式-工大要闻-河北工业大学新闻网</title>\n" +
                "<link rel=\"stylesheet\" href=\"../css/style.css\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/subcon.css\">\n" +
                "\n" +
                "<!--[if lt IE 9]>\n" +
                "<script src=\"../js/html5.js\"  type=\"text/javascript\"></script>\n" +
                "<![endif]-->\n" +
                "<!-- <meta http-equiv=\"X-UA-Compatible\" content=\"IE=8\" >\n" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" >\n" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE8\" > -->\n" +
                "<!-- 以上代码为强制转为IE8浏览器 -->\n" +
                "<!-- <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\"> -->\n" +
                "<!-- 以上代码为强制转为IE最高浏览器 -->\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\n" +
                "<!--header开始--> \n" +
                "\n" +
                "<header class=\"wraq_header\">\n" +
                "    <div class=\"header\">\n" +
                "        <div class=\"logo\"><a href=\"../index.htm\"><img src=\"../images/logo_01.png\"></a></div>\n" +
                "        <div class=\"topWrap\"> \n" +
                "        \t<div class=\"top\">\n" +
                "            <ul  class=\"links01 f14px\" id=\"topLinks\">\n" +
                "                <li class=\"cur\">\n" +
                "                      <div class=\"ser pa\" id=\"ser\">\n" +
                "                            <form name=\"dataForm\" class=\"search\" action=\"/cms/search/searchResults.jsp\" target=\"_blank\" method=\"post\" accept-charset=\"utf-8\" onsubmit=\"document.charset='utf-8';\">\n" +
                "                                <input name=\"siteID\" value=\"35\" type =\"hidden\"> \n" +
                "                                <input class=\"notxt\" value=\"站内搜索\" name=\"query\" type=\"text\" id=\"keywords\" onFocus=\"if(value==defaultValue){value='';}\" onBlur=\"if(!value){value=defaultValue;}\"\n" +
                "                                 onclick=\"if(this.value==''){this.value='';this.form.keywords.style.color='#444'}\">\n" +
                "                                <input class=\"notxt1\" name=\"Submit\" type=\"submit\" value=\"\"/>\n" +
                "                            </form>\n" +
                "                        </div>\n" +
                "                    </li>\n" +
                "                <li class=\"weibo\"><a href=\"#\"></a>\n" +
                "                  <div class=\"subTop\">\n" +
                "                    <img src=\"../images/sub_weibo.jpg\">\n" +
                "                  </div>\n" +
                "                </li>\n" +
                "                <li class=\"weixin\"><a href=\"#\"></a>\n" +
                "                  <div class=\"subTop\">\n" +
                "                    <img src=\"../images/sub_weixin.png\">\n" +
                "                  </div>\n" +
                "                </li>\n" +
                "                <li class=\"home\"><a href=\"http://www.hebut.edu.cn/\" target=\"_blank\">工大首页</a></li>\n" +
                "              </ul>\n" +
                "          </div>\n" +
                "        </div>\n" +
                "        <div class=\"wraq_nav\">\n" +
                "          <div class=\"nav\" >\n" +
                "            <ul id=\"nav\">\n" +
                "                <li><a href=\"../index.htm\" target=\"_blank\">首页</a></li>\n" +
                "                 <li><a href=\"index.htm\" target=\"_blank\">工大要闻</a></li>\n" +
                "                <li><a href=\"../zhxw/index.htm\" target=\"_blank\">综合新闻</a></li>\n" +
                "                <li><a href=\"../mtgd/index.htm\" target=\"_blank\">媒体工大</a></li>\n" +
                "                               <li><a href=\"http://hbgydxb.cuepa.cn/\" target=\"_blank\">工大校报</a></li>\n" +
                "                <li><a href=\"../spkj/index.htm\" target=\"_blank\">视频空间</a></li>\n" +
                "                <li><a href=\"../gdzz/index.htm\" target=\"_blank\">工大之子</a></li>\n" +
                "                <li><a href=\"../tygd/index.htm\" target=\"_blank\">图说工大</a></li>\n" +
                "                <li><a href=\"../gdgs/index.htm\" target=\"_blank\">工大历史与文化</a></li>\n" +
                "                             </ul>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <!-- 移动端主导航 -->\n" +
                "        <section class=\"snav pa\">\n" +
                "           <section id=\"mbtn\" class=\"mbtn pa\"><img  src=\"../images/btn01.png\" /> </section>\n" +
                "           <div class=\"navm pa\">\n" +
                "            <div class=\"nlinks\" id=\"subTopClick\">\n" +
                "                 <span><a href=\"http://www.hebut.edu.cn/\">工大首页</a></span>    \n" +
                "                 <span class=\"subWeixin\"><a href=\"javascript:void(0);\"></a>\n" +
                "                    <div class=\"subTop\">\n" +
                "                      <img src=\"../images/sub_weixin.png\">\n" +
                "                    </div>\n" +
                "                 </span>\n" +
                "                 <span class=\"subWeibo\"><a href=\"javascript:void(0);\"></a>\n" +
                "                    <div class=\"subTop\">\n" +
                "                      <img src=\"../images/sub_weibo.jpg\">\n" +
                "                    </div>\n" +
                "                 </span>\n" +
                "             </div>\n" +
                "           \t<div class=\"ser nser\" id=\"ser\">\n" +
                "                <form name=\"dataForm\" class=\"search\" action=\"/cms/search/searchResults.jsp\" target=\"_blank\" method=\"post\" accept-charset=\"utf-8\" onsubmit=\"document.charset='utf-8';\">\n" +
                "                      <input name=\"siteID\" value=\"35\" type =\"hidden\">\n" +
                "                      <input class=\"notxt\" value=\"站内搜索\" name=\"query\" type=\"text\" id=\"keywords\" onFocus=\"if(value==defaultValue){value='';}\" onBlur=\"if(!value){value=defaultValue;}\"\n" +
                "                            onclick=\"if(this.value==''){this.value='';this.form.keywords.style.color='#444'}\">\n" +
                "                      <input class=\"notxt1\" name=\"Submit\" type=\"submit\" value=\"\" />\n" +
                "               </form>\n" +
                "            </div>\n" +
                "            <ul>\n" +
                "                <li class=\"sub_has\"><a href=\"../index.htm\">首页</a></li>\n" +
                "                                 <li><a href=\"index.htm\">工大要闻</a></li>\n" +
                "                               <li><a href=\"../zhxw/index.htm\">综合新闻</a></li>\n" +
                "                               <li><a href=\"../mtgd/index.htm\">媒体工大</a></li>\n" +
                "                 <li><a href=\"http://hbgydxb.cuepa.cn/\">工大校报</a></li>\n" +
                "\n" +
                "                               <li><a href=\"../spkj/index.htm\">视频空间</a></li>\n" +
                "                               <li><a href=\"../gdzz/index.htm\">工大之子</a></li>\n" +
                "                               <li><a href=\"../tygd/index.htm\">图说工大</a></li>\n" +
                "                               <li><a href=\"../gdgs/index.htm\">工大历史与文化</a></li>\n" +
                "                              \n" +
                "            </ul>\n" +
                "        </div>                                                                          \n" +
                "        </section>\n" +
                "        <!-- <span class=\"old-load\"><a href=\"#\">怀旧版</a></span> -->\n" +
                "    </div> \n" +
                "    \n" +
                "</header><!--header结束 -->\n" +
                "\n" +
                "<!--content开始-->\n" +
                "<article class=\"subPage\">\n" +
                "  <div class=\"sub_top\">\n" +
                "    <div class=\"bread\"><span>当前位置：<a href=\"../index.htm\">首页</a> > <a  href=\"index.htm\" >工大要闻</a> > \n" +
                "    <a>内容</a></span></div>\n" +
                "  </div>\n" +
                "  <div class=\"subPage_con\">\n" +
                "    <section class=\"sub_left\">\n" +
                "      <div class=\"sub_article\">\n" +
                "        <div class=\"sub_articleTitle\">\n" +
                "          <h2>学校举行新入职教师岗前培训开班仪式</h2>\n" +
                "          <div class=\"sub_articleAuthor\">\n" +
                "            <span>日期：<strong>2018-03-21</strong></span>\n" +
                "            <span>信息来源：<strong>宣传部</strong></span>            <!--<span>点击量：<strong><script src=\"http://www.hebut.edu.cn/gplog/log/articleVisitLog.jsp?parentID=66900\"></script>次</strong></span>-->\n" +
                "          </div>\n" +
                "           <!-- <script src=\"http://www.hebut.edu.cn/gplog/log/writeLog.jsp?siteID=35&articleID=66900\"></script>-->\n" +
                "        </div>\n" +
                "        <div class=\"sub_articleInfo\">\n" +
                "\n" +
                "                &nbsp;\n" +
                "<p class=\"MsoNormal\" style=\"text-indent:28.0pt;mso-char-indent-count:2.0;&#10;line-height:150%\"><span lang=\"EN-US\" style=\"font-size:14.0pt;line-height:150%;&#10;font-family:宋体\">3</span><span style=\"font-size:14.0pt;line-height:150%;&#10;font-family:宋体\">月<span lang=\"EN-US\">21</span>日上午，<span lang=\"EN-US\">2018</span>年春季学期河北工业大学新入职教师岗前培训开班仪式在红桥校区第七教学楼<span lang=\"EN-US\">EMBA</span>会议室举行。河北工业大学副校长刘兵出席开班仪式作培训首讲，并带领<span lang=\"EN-US\">64</span>名新入职教师宣誓。仪式由人事处副处长、教师发展中心副主任张金主持。<br />\n" +
                "</span></p>\n" +
                "<p class=\"MsoNormal\" style=\"text-indent: 28pt; line-height: 150%; text-align: center;\"><img src=\"../images/content/2018-03/20180321220629252568.jpg\" width=\"600\" height=\"480\" alt=\"\" /><br />\n" +
                "<span style=\"font-family: 宋体; font-size: 18.6667px; text-align: start; text-indent: 37.3333px;\">副校长刘兵为新入职教师做首场培训，勉励他们做&ldquo;四有&rdquo;好老师</span></p>\n" +
                "<p class=\"MsoNormal\" style=\"text-indent:28.0pt;mso-char-indent-count:2.0;&#10;line-height:150%\"><span style=\"font-size:14.0pt;line-height:150%;&#10;font-family:宋体\"><span lang=\"EN-US\"><o:p></o:p></span></span></p>\n" +
                "<p class=\"MsoNormal\" style=\"text-indent:28.0pt;mso-char-indent-count:2.0;&#10;line-height:150%\"><span style=\"font-size:14.0pt;line-height:150%;font-family:&#10;宋体\">刘兵校长首先代表学校对新入职教师的到来表示祝贺和欢迎，并介绍了学校的人才引进、&ldquo;双一流&rdquo;学科建设等方面的情况，展望了学校立足京津冀协同发展和雄安新区建设大背景下的发展愿景。</span></p>\n" +
                "<p class=\"MsoNormal\" style=\"text-indent:28.0pt;mso-char-indent-count:2.0;&#10;line-height:150%\"><span style=\"font-size:14.0pt;line-height:150%;font-family:&#10;宋体\">围绕&ldquo;怎样才能成为一名优秀的教师&rdquo;这个问题，刘校长引用习总书记提出的&ldquo;四有&rdquo;好老师标准与新教师共勉。第一，心中要有国家和民族的使命感；第二，要有立德树人的道德情操；第三，要有扎实的教学功底、过硬的教学能力、勤勉的教学态度和科学的教学方法；第四，对待学生，要有仁爱之心。同时，刘校长对即将踏上教书育人之路的新入职教师提出殷切嘱托：一是要明确生涯规划，借助学校提供的平台循序渐进、与学校对标发展；二是要在教学、科研实践中锻炼成长，不忘初心、砥砺前行。</span><span style=\"font-family: 宋体; font-size: 14pt; text-indent: 28pt;\">&nbsp;</span></p>\n" +
                "<p class=\"MsoNormal\" style=\"text-indent: 28pt; line-height: 150%; text-align: center;\"><span style=\"font-size:14.0pt;line-height:150%;font-family:&#10;宋体\"><img src=\"../images/content/2018-03/20180321220816288662.jpg\" width=\"600\" height=\"371\" alt=\"\" /><br />\n" +
                "刘兵校长为64名青年教师入职宣誓领誓<span lang=\"EN-US\"><o:p></o:p></span></span></p>\n" +
                "<p class=\"MsoNormal\" style=\"text-indent:28.0pt;mso-char-indent-count:2.0;&#10;line-height:150%\"><span style=\"font-size:14.0pt;line-height:150%;font-family:&#10;宋体\">随后，<span lang=\"EN-US\">64</span>名新入职教师面对国旗，右手握拳举过头顶，在刘兵校长引领下，庄严宣誓：<span lang=\"EN-US\">&ldquo;</span>我志愿成为一名人民教师，忠诚党的教育事业，遵守教育法律法规，履行教书育人职责，引领学生健康成长，做到有理想信念、有道德情操、有扎实学识、有仁爱之心，为教育发展、国家繁荣和民族振兴努力奋斗！<span lang=\"EN-US\">&rdquo;</span>。<span lang=\"EN-US\"><o:p></o:p></span></span></p> \n" +
                "\n" +
                "     \n" +
                "\n" +
                "        <div class=\"wraqShare\">\n" +
                "          <div class=\"share\"><div class=\"bdsharebuttonbox\"><a href=\"#\" class=\"bds_more\" data-cmd=\"more\"><a href=\"#\" class=\"bds_qzone\" data-cmd=\"qzone\" title=\"分享到QQ空间\"></a><a href=\"#\" class=\"bds_tsina\" data-cmd=\"tsina\" title=\"分享到新浪微博\"></a><a href=\"#\" class=\"bds_tqq\" data-cmd=\"tqq\" title=\"分享到腾讯微博\"></a><a href=\"#\" class=\"bds_renren\" data-cmd=\"renren\" title=\"分享到人人网\"></a><a href=\"#\" class=\"bds_weixin\" data-cmd=\"weixin\" title=\"分享到微信\"></a></a></div>\n" +
                "        </div>\n" +
                "      <script>\n" +
                "      window._bd_share_config={\"common\":{\"bdSnsKey\":{},\"bdText\":\"\",\"bdMini\":\"2\",\"bdMiniList\":false,\"bdPic\":\"\",\"bdStyle\":\"0\",\"bdSize\":\"32\"},\"share\":{}};with(document)0[(getElementsByTagName('head')[0]||body).appendChild(createElement('script')).src='http://bdimg.share.baidu.com/static/api/js/share.js?v=89860593.js?cdnversion='+~(-new Date()/36e5)];\n" +
                "      </script>\n" +
                "        <span class=\"shareWZ\">分享：</span>\n" +
                "        </div>\n" +
                "        </div>\n" +
                "\n" +
                "\n" +
                "      </div>\n" +
                "    </section>\n" +
                "\n" +
                "\n" +
                "    <aside class=\"sub_right\">\n" +
                "      <div class=\"sub_hotNews\">\n" +
                "        <div class=\"subTitle1\">\n" +
                "          <span class=\"subMore\"><a href=\"index.htm\" target=\"_blank\">更多</a></span>\n" +
                "          <h2>热点新闻</h2>\n" +
                "        </div>\n" +
                "        <ul class=\"subHotNewsList\">\n" +
                "                 \n" +
                "          <li class=\"hotNewsImg\">\n" +
                "            <a href=\"67001.htm\" target=\"_blank\"><img src=\"../images/content/2018-03/20180326125851650836.jpg\"></a>\n" +
                "          </li>\n" +
                "                                      <li>\n" +
                "            <a href=\"67028.htm\" target=\"_blank\">            学校举行115周年校庆新闻发布会 \n" +
                "    </a>\n" +
                "          </li>\n" +
                "                                     <li>\n" +
                "            <a href=\"66900.htm\" target=\"_blank\">            学校举行新入职教师岗前培训开班仪式 \n" +
                "    </a>\n" +
                "          </li>\n" +
                "                                     <li>\n" +
                "            <a href=\"66870.htm\" target=\"_blank\">            学校党委理论学习中心组召开扩大会议贯彻落实“两会”精神 \n" +
                "    </a>\n" +
                "          </li>\n" +
                "                          </ul>\n" +
                "      </div>\n" +
                "      <div class=\"sub_gdVideo\">\n" +
                "        <div class=\"subTitle1 subVideoTitle\">\n" +
                "          <span class=\"subMore\"><a href=\"../spkj/index.htm\" target=\"_blank\">更多</a></span>\n" +
                "          <h2>工大视频</h2>\n" +
                "        </div>\n" +
                "        <ul class=\"subVideoList\">\n" +
                "                   <li><a href=\"../spkj/52542.htm\" target=\"_blank\">            “抗日英雄杨十三”专题片 \n" +
                "    </a></li>\n" +
                "                  <li><a href=\"../spkj/52541.htm\" target=\"_blank\">            校庆宣传片 \n" +
                "    </a></li>\n" +
                "                  <li><a href=\"../spkj/52540.htm\" target=\"_blank\">            校史文化育人建设工程 \n" +
                "    </a></li>\n" +
                "                  <li><a href=\"../spkj/52539.htm\" target=\"_blank\">            图说我们的价值观 \n" +
                "    </a></li>\n" +
                "                </ul>\n" +
                "      </div>\n" +
                "    </aside>\n" +
                "  </div>\n" +
                "</article>\n" +
                "<!--content结束-->\n" +
                "<!--footer开始-->\n" +
                "\n" +
                "\n" +
                "<article class=\"fooer_wrap\">\n" +
                "    <div class=\"copy_wrap\">\n" +
                "      <p>Copyright &copy Hebei University of Technology，河北工业大学 <br>    \n" +
                "      地址:天津市北辰区西平道 5340 号,&nbsp;邮编：300401&nbsp;&nbsp;津ICP备05003053号 津教备0020号&nbsp;<!--总访问量：<script src=\"http://www.hebut.edu.cn/gplog/log/channelVisitLog.jsp?parentID=1926\">\n" +
                "</script>  &nbsp;今日访问： <script src=\"/cms/web/channelVisitLog_today.jsp?parentID=1926\"></script> --></p>\n" +
                "    </div>\n" +
                "</article>\n" +
                "\n" +
                "<!--<script src=\"http://www.hebut.edu.cn/gplog/log/writeLog.jsp?siteID=35&channelID=1933\"></script>\n" +
                "\n" +
                "<script src=\"/cms/web/writeLog.jsp?channelID=1933\"></script>-->\n" +
                "<a href=\"#\" class=\"goTop\"><i class=\"fa fa-lg fa-chevron-up\"></i>TOP</a>     \n" +
                "<!--footer结束-->\n" +
                "<script type=\"text/javascript\" src=\"../js/jquery-1.7.1.min.js\"></script>\n" +
                "<!-- 通用JS -->\n" +
                "<script type=\"text/javascript\" src=\"../js/script.js\"></script>\n" +
                "<script src=\"../js/slider.js\" type=\"text/javascript\"></script>\n" +
                "<script type=\"text/javascript\" src=\"../js/tPicnav.js\"></script>\n" +
                "<!-- <script type=\"text/javascript\" src=\"../js/jquery.flexslider-min.js\"></script> -->\n" +
                "<script type=\"text/javascript\">\n" +
                "Nav('#nav');//导航\n" +
                "Nav('#topLinks');\n" +
                "TopList('#subTopClick');\n" +
                "navMin();  //手机端下拉菜单\n" +
                "snavWidth();//导航子菜单的宽度\n" +
                "Menu('.in_mbtn','.navm');//手机版导航\n" +
                "// ImgHeight();\n" +
                "// Mask({parents : '#pic_con'});\n" +
                "// SerMax()\t//搜索框\n" +
                "//cover();\n" +
                "\n" +
                "</script>\n" +
                "</body></html>");
        System.out.println(new NewsParser().parse(page));
    }
}