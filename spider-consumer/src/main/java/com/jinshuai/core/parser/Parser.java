package com.jinshuai.core.parser;

import com.jinshuai.entity.Page;

/**
 * @author JS
 * @date 2018/03/26
 * @description
 *  解析Page
 * */
public interface Parser {

    /**
     * @param page 要解析的Page
     * @return 解析后的Page(Map、Set)
     * @description 解析Page中的Document的内容到Map中，URL到Set中
     * */
    Page parse(Page page);

}