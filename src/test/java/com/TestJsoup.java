package com;

import junit.framework.TestCase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author: JS
 * @date: 2018/3/23
 * @description:
 */
public class TestJsoup extends TestCase{

    public void testJsoup() {
        Document document = Jsoup.parse("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta http-equiv=\"content-type\" content=\"text/html; charset=gb2312\" />\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=10,IE=9,IE=8\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0\" />\n" +
                "    <title>计算机辅助创新设计公共服务平台</title>\n" +
                "    <meta name=\"robots\" content=\"noindex,follow\">\n" +
                "    <link href=\"../../css/stylenew.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
                "    <link href=\"../../css/lightbox.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
                "    <script src=\"../bootstrap/js/jquery.js\" type=\"text/javascript\"></script>\n" +
                "    <script src=\"../../js/NewsIndex.js\" type=\"text/javascript\"> </script>\n" +
                "    <script src=\"js/main.js\" type=\"text/javascript\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "/* <![CDATA[ */\n" +
                "var ie6w = {\"url\":\"http:\\/\\/localhost:8000\\/wp-content\\/plugins\\/shockingly-big-ie6-warning\",\"test\":\"false\",\"jstest\":\"false\",\"t1\":\"WARNING\",\"t2\":\"You are using Internet Explorer version 6.0 or lower. Due to security issues and lack of support for Web Standards it is highly recommended that you upgrade to a modern browser.\",\"firefox\":\"true\",\"opera\":\"true\",\"chrome\":\"true\",\"safari\":\"true\",\"ie\":\"true\",\"firefoxu\":\"http:\\/\\/www.getfirefox.net\\/\",\"operau\":\"http:\\/\\/www.opera.com\\/\",\"chromeu\":\"http:\\/\\/www.google.com\\/chrome\\/\",\"safariu\":\"http:\\/\\/www.apple.com\\/safari\\/\",\"ieu\":\"http:\\/\\/www.microsoft.com\\/windows\\/ie\\/\"};\n" +
                "/* ]]> */\n" +
                "    </script>\n" +
                "    <script src=\"../js/ie6w_top.js\" type=\"text/javascript\"></script>\n" +
                "    <!-- Begin - HITS-IE6 PNGFix -->\n" +
                "    <!-- IE6 has not been detected as the users browser version by the server -->\n" +
                "    <!--  End  - HITS-IE6 PNGFix -->\n" +
                "    <!--[if lt IE 9]><script src=\"javascript:void(0)/wp-content/themes/CAI/js/html5.js\"></script><![endif]-->\n" +
                "</head>\n" +
                "<body class=\"home blog\" onload=\"loadnum()\">\n" +
                "    <div class=\"headerwrap\">\n" +
                "        <header class=\"header\">\n" +
                "\t<div class=\"navbar\">\n" +
                "    <h2 class=\"logo\">\n" +
                "\t\t<img width=\"60px;\" height=\"20px;\" src=\"../../img/logo.png\">\n" +
                "        <span class=\"label-important\">计算机辅助创新设计公共服务平台<br/>\n" +
                "        \n" +
                "        </span>\n" +
                "        \n" +
                "        </h2>\n" +
                "        \n" +
                "              \n" +
                "        \n" +
                "        \n" +
                "\t\t<!--\n" +
                "\t\t<ul class=\"nav\">\n" +
                "\t\t\t<div class=\"menu\"></div>\n" +
                "\t\t</ul>\n" +
                "\t\t-->\n" +
                "\t\t<div class=\"menu pull-right\">\n" +
                "\t\t\t<form method=\"get\" class=\"dropdown search-form\" action=\"javascript:void(0)/\">\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<ul class=\"dropdown-menu search-suggest\"></ul>\n" +
                "\t\t\t</form>\n" +
                "\t\t\t<!--\n" +
                "\t\t\t<div class=\"btn-group pull-left\">\n" +
                "\t\t\t\t<button class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#feed\">订阅</button>\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t-->\n" +
                "\t\t</div>\n" +
                "\n" +
                "\t\t<a  style=\"float:right; font-size:medium\" href=\"#\" onclick=\"downloadApp();\" >TRIZ创新辅助APP(下载量:<span id=\"downloadcount\"></span>)</a>\n" +
                "        \n" +
                "\t</div>\n" +
                "\t\n" +
                "\n" +
                "\t\n" +
                "\n" +
                "\t<!--\n" +
                "\t<div class=\"speedbar\">\n" +
                "\t\t\t\t\n" +
                "\t\t\t</div>\n" +
                "\t-->\n" +
                "\n" +
                "\n" +
                "</header>\n" +
                "    </div>\n" +
                "    <section class=\"container\">\n" +
                "\t\n" +
                "<div id=\"idcontentwrap\" style=\"float:left;width:100%;\">\n" +
                "\t<div class=\"content\">\n" +
                "        <div class=\"slideshow_container slideshow_container_style-light\" style=\"height: 326px; width: 880px;\" data-session-id=\"0\" data-style-name=\"style-light\" data-style-version=\"2.2.21\">\n" +
                "\n" +
                "\t\t\t\n" +
                "\t\n" +
                "\t<div class=\"slideshow_content\" style=\"width: 880px; height: 326px;\">\n" +
                "\n" +
                "\t\t<div style=\"width: 880px; height: 326px; z-index: 0; display: block; top: 0px; left: 0px;\" class=\"slideshow_view slideshow_currentView\">\n" +
                "\t\t\t<div style=\"margin-left: 0px; margin-right: 0px; width: 880px; height: 326px;\" class=\"slideshow_slide slideshow_slide_image\">\n" +
                "\t\t\t\t\t\t\t\t\t \n" +
                "                                    <img style=\"margin-top: -106px; margin-left: 0px; width: 880px; height: 538px;\" src=\"../../img/chapter_innovation_bob.jpg\"  height=\"538\" width=\"880\">\n" +
                "\t\t\t\t\t\t\t\t<div style=\"display: block; position: absolute; top: 326px;\" class=\"slideshow_description_box slideshow_transparent\">\n" +
                "                                </div>\n" +
                "\t\t\t</div>\n" +
                "\n" +
                "\t\t\t<div style=\"clear: both;\"></div></div><div style=\"top: 326px; width: 880px; height: 326px; left: 0px; z-index: 0; display: block;\" class=\"slideshow_view\">\n" +
                "\t\t\t<div style=\"margin-left: 0px; margin-right: 0px; width: 880px; height: 326px;\" class=\"slideshow_slide slideshow_slide_image\">\n" +
                "\t\t\t\t\t\t\t\t\t<img style=\"margin-top: 0px; margin-left: 0px; width: 880px; height: 326px;\" src=\" \"   height=\"326\" width=\"880\">\n" +
                "\t\t\t\t\t\t\t\t<div style=\"display: block; position: absolute; top: 326px;\" class=\"slideshow_description_box slideshow_transparent\"></div>\n" +
                "\t\t\t</div>\n" +
                "\n" +
                "\t\t\t<div style=\"clear: both;\"></div></div><div style=\"top: 326px; width: 880px; height: 326px; left: 0px; z-index: 0; display: block;\" class=\"slideshow_view\">\n" +
                "\t\t\t<div style=\"margin-left: 0px; margin-right: 0px; width: 880px; height: 326px;\" class=\"slideshow_slide slideshow_slide_image\">\n" +
                "\t\t\t\t\t\t\t\t\t<img style=\"margin-top: 0px; margin-left: 0px; height: 326px; width: 880px;\"  height=\"326\" width=\"880\">\n" +
                "\t\t\t\t\t\t\t\t<div style=\"display: block; position: absolute; top: 326px;\" class=\"slideshow_description_box slideshow_transparent\">\n" +
                "\t\t\t\t\t<div class=\"slideshow_title\"></div>\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t</div>\n" +
                "\n" +
                "\t\t\t<div style=\"clear: both;\"></div></div>\n" +
                "\t</div>\n" +
                "\n" +
                "\t<div class=\"slideshow_controlPanel slideshow_transparent\" style=\"display: none;\"><ul><li class=\"slideshow_togglePlay\" data-play-text=\"Play\" data-pause-text=\"Pause\"></li></ul></div>\n" +
                "\n" +
                "\t<div title=\"Previous\" tabindex=\"0\" class=\"slideshow_button slideshow_previous slideshow_transparent\" role=\"button\" data-previous-text=\"Previous\" style=\"display: block;\"><span class=\"assistive-text hide-text\">Previous</span></div>\n" +
                "\t<div title=\"Next\" tabindex=\"0\" class=\"slideshow_button slideshow_next slideshow_transparent\" role=\"button\" data-next-text=\"Next\" style=\"display: block;\"><span class=\"assistive-text hide-text\">Next</span></div>\n" +
                "\n" +
                "\t<div class=\"slideshow_pagination\" style=\"display: block; opacity: 1;\" data-go-to-text=\"Go to slide\"><div class=\"slideshow_pagination_center\"><ul><li tabindex=\"0\" class=\"slideshow_transparent slideshow_currentView\" data-view-id=\"0\" role=\"button\" title=\"Go to slide 1\"><span class=\"assistive-text hide-text\">Go to slide 1</span></li><li tabindex=\"0\" class=\"slideshow_transparent\" data-view-id=\"1\" role=\"button\" title=\"Go to slide 2\"><span class=\"assistive-text hide-text\">Go to slide 2</span></li><li tabindex=\"0\" class=\"slideshow_transparent\" data-view-id=\"2\" role=\"button\" title=\"Go to slide 3\"><span class=\"assistive-text hide-text\">Go to slide 3</span></li></ul></div></div>\n" +
                "\n" +
                "\t<!-- WordPress Slideshow Version 2.2.21 -->\n" +
                "\n" +
                "\t</div>\t</div>\n" +
                "</div>\n" +
                "\n" +
                "<aside class=\"sidebar\">\t\n" +
                "<div class=\"widget d_textbanner\"><a class=\"style01\" href=\"./view/thinking/thought.html\" target=\"_blank\"><strong></strong><h2>创新理论基础 - 培养专业的创新思维</h2><p>目标：训练设计者思维、扩展视野、打破思维局限</p></a></div><div class=\"widget d_textbanner\"><a class=\"style02\" href=\"./view/user/login.html\" target=\"_blank\"><strong></strong><h2>创新工具 - 加速创新过程</h2><p>包括标准解、冲突、进化三大经典及前期的问题分析工具</p></a></div><div class=\"widget d_textbanner\"><a class=\"style05\" href=\"./view/search/index.html\" target=\"_blank\"><strong></strong><h2>知识检索 专利推荐</h2><p>快速检索到相关领域的内容 加速创新进程</p></a></div></aside>\n" +
                "\n" +
                "\n" +
                "<div id=\"idcontentzhuanjia\" class=\"content-wrap\">\n" +
                "\n" +
                "\n" +
                "\t<div class=\"content-fullwidth\">\n" +
                "\t<h2 class=\"title\">创新动态</h2>\n" +
                "    <div id=\"cxdt\">\n" +
                "</div>\n" +
                "\t</div>\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "\n" +
                "<div id=\"idcontentchuangyichanpin\" class=\"content-wrap\">\n" +
                "\t<div class=\"content-fullwidth\">\n" +
                "\t<h2 class=\"title\">创意展示 - 新产品</h2>\n" +
                "<div id=\"cyzsxcp\">\n" +
                "</div>\n" +
                "\t</div>\n" +
                "</div>\n" +
                "\n" +
                "<div id=\"idcontentchuangyichuangyi\" class=\"content-wrap\">\n" +
                "\t<div class=\"content-fullwidth\">\n" +
                "\t<h2 class=\"title\">创意展示 - 新创意</h2>\n" +
                "    <div id=\"cyzsxcy\">\n" +
                "</div>\n" +
                "\t</div>\n" +
                "</div>\n" +
                "\n" +
                "<div id=\"idcontentzhishigongxiang\" class=\"content-wrap\">\n" +
                "\n" +
                "\t<div class=\"content-fullwidth\">\n" +
                "\t<h2 class=\"title\">知识共享</h2>\n" +
                "     <div id=\"zsgx\">\n" +
                "</div>\n" +
                "\t</div>\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "<div id=\"idcontentfangan\" class=\"content-wrap\">\n" +
                "\t<div class=\"content-fullwidth\">\n" +
                "\t<h2 class=\"title\">方案征集</h2>\n" +
                "<div class=\"gridstyle4cols\">\n" +
                "<div id=\"fazj\">\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "</div>\n" +
                "\t<!--\n" +
                "\t\n" +
                "\t-->\n" +
                "\t</div>\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</section>\n" +
                "    <!--------------------------------------------------友情链接--------------------------------------------------------------------------------->\n" +
                "    <div class=\"dilink\">\n" +
                "        <h3>\n" +
                "            友情链接 <a href=\"\"></a>\n" +
                "        </h3>\n" +
                "        <a target=\"_blank\" href=\"http://www.hebstd.gov.cn/\">\n" +
                "            <img width=\"150\" height=\"60\" src=\"./img/connect3.png\" alt=\"\" /></a> <a target=\"_blank\"\n" +
                "                href=\"http://www.trizconsulting.com/\">\n" +
                "                <img width=\"150\" height=\"60\" src=\"./img/connect5.jpg\" alt=\"\" /></a> <a target=\"_blank\"\n" +
                "                    href=\"http://www.triz.com.cn\">\n" +
                "                    <img width=\"150\" height=\"60\" src=\"./img/connect8.jpg\" alt=\"\" /></a>\n" +
                "        <a target=\"_blank\" href=\"http://www.triz-journal.com/\">\n" +
                "            <img width=\"150\" height=\"60\" src=\"./img/connect6.jpg\" alt=\"\" /></a> <a target=\"_blank\"\n" +
                "                href=\"http://www.most.gov.cn/\">\n" +
                "                <img width=\"150\" height=\"60\" src=\"./img/connect1.jpg\" alt=\"\" /></a> <a target=\"_blank\"\n" +
                "                    href=\"http://www.innovationmethod.org.cn/\">\n" +
                "                    <img width=\"150\" height=\"60\" src=\"./img/connect2.jpg\" alt=\"\" /></a>\n" +
                "        <div>\n" +
                "            <select onchange=\"javascript:window.open(this.options[this.selectedIndex].value)\"\n" +
                "                id=\"select2\" name=\"areaselect\">\n" +
                "                <option selected=\"\">国家级</option>\n" +
                "                <option value=\"http://www.most.gov.cn/\">国家科技部</option>\n" +
                "                <option value=\"http://www.mii.gov.cn/mii/index.html\">国家信息产业部</option>\n" +
                "                <option value=\"http://www.moe.edu.cn/\">国家教育部</option>\n" +
                "                <option value=\"http://www.dost.moe.edu.cn/\">教育部科技司</option>\n" +
                "                <option value=\"http://www.cutech.edu.cn/\">教育部科技发展</option>\n" +
                "                <option value=\"http://www.chinainfo.gov.cn/\">中国科学情报网</option>\n" +
                "                <option value=\"http://www.cnipr.com/\">中国知识产权网</option>\n" +
                "                <option value=\"http://www.casted.org.cn/cn/\">中国科技战略研究网</option>\n" +
                "            </select>\n" +
                "            <select onchange=\"javascript:window.open(this.options[this.selectedIndex].value)\"\n" +
                "                id=\"select3\" name=\"areaselect\">\n" +
                "                <option selected=\"\">各省有关部门</option>\n" +
                "                <option value=\"http://www.triz.gov.cn\">黑龙江TRIZ专题网站</option>\n" +
                "                <option value=\"http://www.chiantriz.net.cn\">四川省技术创新方法网</option>\n" +
                "                <option value=\"http://www.jstriz.cn/\">TRIZ江苏创新方法网</option>\n" +
                "                <option value=\"http://www.cxff100.com/\">天津创新方法网</option>\n" +
                "                <option value=\"http://www.sntriz.cn/\">陕西创新方法网</option>\n" +
                "                <option value=\"http://www.xjtriz.gov.cn/\">新疆创新方法网</option>\n" +
                "                <option value=\"http://www.gdim.org.cn\">广东创新方法网</option>\n" +
                "                <option value=\"http://www.hntriz.cn/\">河南省创新方法网</option>\n" +
                "                <option value=\"http://www.yntriz.rog.cn/\">云南创新方法网</option>\n" +
                "                <option value=\"http://www.jltriz.com:8080/\">吉林省创新方法网</option>\n" +
                "                <option value=\"http://www.cxff.org\">重庆市创新方法网</option>\n" +
                "            </select>\n" +
                "            <select onchange=\"javascript:window.open(this.options[this.selectedIndex].value)\"\n" +
                "                id=\"select1\" name=\"areaselect\">\n" +
                "                <option selected=\"\">本省内</option>\n" +
                "                <option value=\"http://www.hebstd.gov.cn/\">河北省科技厅</option>\n" +
                "                <option value=\"http://qbs.heinfo.gov.cn/index.do?templet=index\">河北省科学情报研究院</option>\n" +
                "                <option value=\"http://www.triz.com.cn\">河北工业大学triz研究中心</option>\n" +
                "                <option value=\"http://www.ii.gov.cn/\">河北省工信厅</option>\n" +
                "                <option value=\"http://www.hebsts.org.cn/\">河北省科技统计网</option>\n" +
                "                <option value=\"http://www.heinfo.gov.cn/\">河北科技信息网</option>\n" +
                "                <option value=\"http://www.hbdrc.gov.cn/\">河北省发改委员会</option>\n" +
                "                <option value=\"http://www.hebcz.gov.cn/\">河北省财政厅</option>\n" +
                "                <option value=\"http://www.hebipo.gov.cn/\">河北省知识产权信息网</option>\n" +
                "                <option value=\"http://www.hbkp.gov.cn/template/site00_index.jsp\">河北科普网</option>\n" +
                "                <option value=\"http://www.he.lss.gov.cn/\">省人力资源和社会保障厅</option>\n" +
                "            </select>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <!--------------------------------------------------友情链接--------------------------------------------------------------------------------->\n" +
                "    <div class=\"footerwrap\">\n" +
                "        <footer class=\"footer\">\n" +
                "    <div class=\"footer-inner\">\n" +
                "        <div class=\"copyright pull-left\"> \n" +
                "版权所有：河北工业大学CAI研究实验室 天津市北辰区西平道 5340 号, 邮编:300401\n" +
                "        </div>\n" +
                "        <div class=\"trackcode pull-right\"><a href=\"javascript:void(0)\">・</a>\n" +
                "                    </div>\n" +
                "    </div>\n" +
                "</footer>\n" +
                "    </div>\n" +
                "    <div class=\"modal hide fade\" id=\"feed\" tabindex=\"-1\" style=\"width: 400px; margin-left: -200px;\">\n" +
                "        <div class=\"modal-header\">\n" +
                "            <button type=\"button\" class=\"close\" data-dismiss=\"modal\">\n" +
                "                ×</button><h3>\n" +
                "                    订阅计算机辅助创新设计公共服务平台</h3>\n" +
                "        </div>\n" +
                "        <div class=\"modal-body\">\n" +
                "            <p>\n" +
                "                <strong>订阅地址</strong><br>\n" +
                "                <input class=\"input-block-level\" readonly=\"readonly\" type=\"text\"></p>\n" +
                "            <p>\n" +
                "                <strong>订阅到</strong><br>\n" +
                "                <a class=\"btn btn-mini btn-success\" target=\"_blank\" href=\"http://mail.qq.com/cgi-bin/feed?u=http://\">\n" +
                "                    QQ邮箱</a> <a class=\"btn btn-mini btn-success\" target=\"_blank\" href=\"http://www.xianguo.com/subscribe.php?url=http://\">\n" +
                "                        鲜果</a> <a class=\"btn btn-mini btn-success\" target=\"_blank\" href=\"http://mail.qq.com/cgi-bin/feed?u=http://\">\n" +
                "                            抓虾</a></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div class=\"rollto\">\n" +
                "        <button class=\"btn btn-inverse\" data-type=\"totop\" title=\"回顶部\">\n" +
                "            <i class=\"icon-eject icon-white\"></i>\n" +
                "        </button>\n" +
                "    </div>\n" +
                "   \n" +
                "</body>\n" +
                "<script type=\"text/javascript\">\n" +
                "  function loadnum(){\n" +
                "        //显示，修改下载量\n" +
                "        $.ajax({\n" +
                "            url: \"http://etriz.hebut.edu.cn/down/count?countMethod=0\",\n" +
                "            type: 'get',\n" +
                "            crossDomain: true,\n" +
                "            dataType: 'jsonp',\n" +
                "            success: function (data, status) {\n" +
                "                data = $.parseJSON(data);\n" +
                "                $(\"#downloadcount\").text(data[\"\\\"result\\\"\"]);\n" +
                "            }\n" +
                "        });\n" +
                "    }\n" +
                "    function downloadApp() {\n" +
                "        //导出管理创新项目立项评审意见汇总表\n" +
                "\n" +
                "        //显示，修改下载量\n" +
                "        $.ajax({\n" +
                "            url: \"http://etriz.hebut.edu.cn/down/count?countMethod=1\",\n" +
                "            type: 'get',\n" +
                "            crossDomain: true,\n" +
                "            dataType: 'jsonp',\n" +
                "            success: function (data, status) {\n" +
                "                data = $.parseJSON(data);\n" +
                "                $(\"#downloadcount\").text(data[\"\\\"result\\\"\"]);\n" +
                "            }\n" +
                "        });\n" +
                "        //下载文件\n" +
                "        if (typeof (downloadApp.iframe) == \"undefined\") {\n" +
                "            var iframe = document.createElement(\"iframe\");\n" +
                "            downloadApp.iframe = iframe;\n" +
                "            downloadApp.iframe.style.display = \"none\";\n" +
                "            document.body.appendChild(downloadApp.iframe);\n" +
                "        }\n" +
                "        downloadApp.iframe.src = \"/view/User/etrizappdownload.ashx\";\n" +
                "    }\n" +
                "</script>\n" +
                "</html>");
        Document document1 = null;
        try {
            document1 = Jsoup.connect("https://baike.baidu.com/item/%E6%95%B0%E6%8D%AE%E5%BA%93%E5%BC%95%E6%93%8E").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(document1.getElementsByTag("div"));

    }

}
