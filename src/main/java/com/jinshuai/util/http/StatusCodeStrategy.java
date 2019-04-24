package com.jinshuai.util.http;

/**
 * @author: JS
 * @date: 2019/4/12
 * @description: 状态码处理策略
 */
public abstract class StatusCodeStrategy {

    protected abstract void process(String URL);

}