package com.jinshuai.core.scheduler.impl;

import com.jinshuai.core.scheduler.Scheduler;
import com.jinshuai.entity.UrlSeed;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  将种子存放到Redis
 */
public class RedisScheduler implements Scheduler {

    public void push(UrlSeed urlSeed) {

    }

    public UrlSeed pop() {
        return null;
    }
}
