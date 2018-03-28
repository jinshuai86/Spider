package com.jinshuai.core.parser.impl;

import com.jinshuai.core.parser.Parser;
import com.jinshuai.entity.Page;
import com.jinshuai.entity.UrlSeed;
import com.sun.xml.internal.ws.message.ProblemActionHeader;
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
            LOGGER.error("[解析页面] " + page.getUrlSeed().getUrl() + " 出现异常: " + e.getStackTrace());
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
        Page page = new Page(new UrlSeed("http://xww.hebut.edu.cn/gdyw/index.htm",5),
                "\n" +
                        "<!doctype html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta charset=\"utf-8\">\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\" />\n" +
                        "<meta name=\"format-detection\" content=\"telephone=no\" />\n" +
                        "<title>河北工业大学新闻网 工大要闻</title>\n" +
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
                        "</header>\n" +
                        "<!--header结束 -->\n" +
                        "\n" +
                        "<!--content开始-->\n" +
                        "<article class=\"subPage\">\n" +
                        "  <div class=\"sub_top\">\n" +
                        "    <div class=\"bread\"><span>当前位置：<a href=\"../index.htm\">首页</a> > <a  href=\"index.htm\">工大要闻</a></span></div>\n" +
                        "  </div>\n" +
                        "  <div class=\"subPage_con\">\n" +
                        "    <section class=\"sub_left\">\n" +
                        "\n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "            \n" +
                        "      <div class=\"gdImpNews\">\n" +
                        "        <div>\n" +
                        "          <a href=\"67001.htm\" target=\"_blank\" ><img src=\"../images/content/2018-03/20180326125851650836.jpg\"></a>\n" +
                        "          <p><span class=\"sub_ImpTime\"><strong>24</strong><i>2018.03</i></span>\n" +
                        "            <a href=\"67001.htm\" target=\"_blank\" >            我校韩旭教授主持承担的国家重点研发计划“智能机器人”重点专项启动 \n" +
                        "    </a></p>\n" +
                        "        </div>\n" +
                        "      </div>\n" +
                        "\n" +
                        "                        \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                              \n" +
                        "                  \n" +
                        "                  \n" +
                        "                              \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                              \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  \n" +
                        "                              \n" +
                        "                              \n" +
                        "                  \n" +
                        "                  \n" +
                        "                              \n" +
                        "                  \n" +
                        "                  \n" +
                        "                  <div class=\"subTitle\">\n" +
                        "        <h2>工大要闻</h2>\n" +
                        "      </div>\n" +
                        "      <ul class=\"gdImpNewsList\">\n" +
                        "\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>27</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"67112.htm\" target=\"_blank\">            “区域协调发展战略与京津冀协同发展新征程研讨会暨河北经济蓝皮书发... \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>26</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"67115.htm\" target=\"_blank\">            “学府花堤”尽展百年河工人文气韵 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>24</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"67001.htm\" target=\"_blank\">            我校韩旭教授主持承担的国家重点研发计划“智能机器人”重点专项启动 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>23</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"67028.htm\" target=\"_blank\">            学校举行115周年校庆新闻发布会 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>21</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66900.htm\" target=\"_blank\">            学校举行新入职教师岗前培训开班仪式 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>21</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66870.htm\" target=\"_blank\">            学校党委理论学习中心组召开扩大会议贯彻落实“两会”精神 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>21</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66865.htm\" target=\"_blank\">            【115周年校庆系列】我校成功举办“三一九学生节”系列活动 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>21</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66901.htm\" target=\"_blank\">            【115周年校庆系列】校友大讲堂|徐华：在工业强国的时代背景下实... \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>17</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66812.htm\" target=\"_blank\">            【115周年校庆系列】贴心暖意，做女教职工的知心人 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>17</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66807.htm\" target=\"_blank\">            【115周年校庆系列】校友大讲堂|李少远：自己的格局有多大世界就... \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>16</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66787.htm\" target=\"_blank\">            学“习”之行  践“习”之路 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>14</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66777.htm\" target=\"_blank\">            我校扶贫脱贫驻村工作队顺利完成交接工作 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>12</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66754.htm\" target=\"_blank\">            第二届城乡规划专业京津冀高校“X+1”联合毕业设计开幕式顺利召开 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>12</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66742.htm\" target=\"_blank\">            我校获批河北省“引智工作站”和“引才引智示范基地” \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>09</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66727.htm\" target=\"_blank\">            学校召开2018年度本科教育教学工作会议 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>07</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66668.htm\" target=\"_blank\">            我校承办河北省2018年知识产权维权培训班 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>06</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66652.htm\" target=\"_blank\">            喜报：我校国际技术转移中心被科技部认定为“国家国际科技合作基地” \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>04</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66618.htm\" target=\"_blank\">            【聚焦两会“河工”声音】殷福星委员：构建河北高等教育资源聚集区 ... \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>04</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66619.htm\" target=\"_blank\">            【聚焦两会“河工”声音】千人计划殷福星：可借助雄安新区构建高端人... \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "               <li>\n" +
                        "          <span class=\"gdImpNewsTime\"><strong>03</strong><i>2018.03</i></span>\n" +
                        "          <div class=\"gdImpNewsCon\">\n" +
                        "            <h2><a href=\"66615.htm\" target=\"_blank\">            我校两个基层团支部荣获全国高校“活力团支部”称号 \n" +
                        "    </a></h2>\n" +
                        "            <p></p>\n" +
                        "          </div>\n" +
                        "        </li>\n" +
                        "       \n" +
                        "      </ul>\n" +
                        "\n" +
                        "    \n" +
                        " <div class=\"pages\">\n" +
                        "      页数：<span>1/67</span>\n" +
                        "      总数：<span>1325</span>\n" +
                        "\n" +
                        "                                                  <a href=\"index.htm\" class=\"start\"><img src=\"../images/pages01.jpg\" /></a>\n" +
                        "            <a  class=\"prev\"><img src=\"../images/pages02.jpg\" /></a>\n" +
                        "                              <a class=\"on_pages\">1</a>\n" +
                        "                         <a href=\"index1.htm\">2</a>\n" +
                        "                        <a href=\"index2.htm\">3</a>\n" +
                        "                        <a href=\"index3.htm\">4</a>\n" +
                        "                        <a href=\"index4.htm\">5</a>\n" +
                        "            \n" +
                        "                 <a href=\"index1.htm\" class=\"next\"><img src=\"../images/pages04.jpg\" /></a>\n" +
                        "        \n" +
                        "        <a href=\"index66.htm\" class=\"end\"><img src=\"../images/pages05.jpg\" /></a>\n" +
                        "\n" +
                        "     \n" +
                        "      </div>\n" +
                        "      <div class=\"pages_min f14px\">\n" +
                        "      <a href=\"index.htm\" class=\"prev_min\">上一页</a>\n" +
                        "      <a href=\"index1.htm\" class=\"next_min\">下一页</a>\n" +
                        "      </div>\n" +
                        "    </section>\n" +
                        "    \n" +
                        "\n" +
                        "    <aside class=\"sub_right\">\n" +
                        "      <div class=\"sub_hotNews\">\n" +
                        "        <div class=\"subTitle1\">\n" +
                        "          <span class=\"subMore\"><a href=\"index.htm\" target=\"_blank\">更多</a></span>\n" +
                        "          <h2>热点新闻</h2>\n" +
                        "        </div>\n" +
                        "        <ul class=\"subHotNewsList\">\n" +
                        "                           <li>\n" +
                        "            <a href=\"67112.htm\" target=\"_blank\">            “区域协调发展战略与京津冀协同发展新征程研讨会暨河北经济蓝皮书发布会”顺... \n" +
                        "    </a>\n" +
                        "          </li>\n" +
                        "                                     <li>\n" +
                        "            <a href=\"67115.htm\" target=\"_blank\">            “学府花堤”尽展百年河工人文气韵 \n" +
                        "    </a>\n" +
                        "          </li>\n" +
                        "                           \n" +
                        "          <li class=\"hotNewsImg\">\n" +
                        "            <a href=\"67001.htm\" target=\"_blank\"><img src=\"../images/content/2018-03/20180326125851650836.jpg\"></a>\n" +
                        "          </li>\n" +
                        "                                      <li>\n" +
                        "            <a href=\"67028.htm\" target=\"_blank\">            学校举行115周年校庆新闻发布会 \n" +
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
                        "<script src=\"/cms/web/writeLog.jsp?channelID=1933\"></script>--><a href=\"#\" class=\"goTop\"><i class=\"fa fa-lg fa-chevron-up\"></i>TOP</a>     \n" +
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