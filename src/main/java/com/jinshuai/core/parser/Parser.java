package com.jinshuai.core.parser;

import com.jinshuai.entity.Page;

/**
 * @author JS
 * @date 2018/03/26
 * @description
 *  解析响应体为Page
 * */
public interface Parser {

    /**
     * @param page 想要解析的Page
     * @return Page实例
     * @description 将响应体解析并封装成Page
     * */
    Page parse(Page page);

}