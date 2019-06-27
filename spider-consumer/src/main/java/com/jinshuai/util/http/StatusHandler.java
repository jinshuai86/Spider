package com.jinshuai.util.http;

import org.apache.http.HttpResponse;

/**
 * @author: JS
 * @date: 2019/4/12
 * @description: 状态码处理策略
 */
public interface StatusHandler {

    void process(String URL, HttpResponse response);

}