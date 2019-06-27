package com.jinshuai.core.scheduler;

import com.jinshuai.entity.UrlSeed;

/**
 * @author JS
 * @date 2018/03/26
 * @description：
 *  种子调度器: 提供种子，存放种子。
 * */
public interface Scheduler {

    /**
     * 存放种子
     * */
    void push(UrlSeed urlSeed);
    /**
     * 提供种子
     * */
    UrlSeed pop();

}