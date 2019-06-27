package com;

import com.google.gson.Gson;
import com.jinshuai.entity.UrlSeed;
import org.ansj.splitWord.analysis.*;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: JS
 * @date: 2018/3/27
 * @description:
 */
public class TestGson {

   @Test
   public void testSegment() {
       String[] arr = {"碳酸铵","硫酸铁", "醋酸钠", "碳酸钙", "氢氧化钠", "硫酸亚铁", "高锰酸钾"};
       for (String str : arr) {
           System.out.println(BaseAnalysis.parse(str));
           System.out.println(ToAnalysis.parse(str));
           System.out.println(DicAnalysis.parse(str));
           System.out.println(IndexAnalysis.parse(str));
           System.out.println(NlpAnalysis.parse(str));
       }

   }

}