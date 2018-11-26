package com.jinshuai.core.scheduler.impl;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.jinshuai.core.scheduler.Scheduler;
import com.jinshuai.entity.UrlSeed;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.PriorityQueue;

/**
 * @author: JS
 * @date: 2018/10/19
 * @description: 优先级队列结合布隆过滤器进行种子调度
 */
@Slf4j
public class PriorityQueueScheduler implements Scheduler {

    /**
     * 存储种子的优先队列，采用大根堆实现
     * */
    private final PriorityQueue<UrlSeed> urlQueue = new PriorityQueue<>(
            (o1,o2) -> -Long.compare(o1.getPriority(),o2.getPriority())
        );

    /**
     * 布隆过滤器判断种子是否重复
     * 预定要完成的任务数量是800
     * 允许0.01的错误率
     * */
    private final BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), 800,0.01);

    @Override
    public void push(UrlSeed urlSeed) {
        String url = urlSeed.getUrl();
        // 判断url是否已经在种子队列中
        if (bloomFilter.mightContain(url)) {
            log.warn("url:[{}]已存在", urlSeed.getUrl());
            return;
        } else {
            urlQueue.add(urlSeed);
            bloomFilter.put(url);
        }
    }

    @Override
    public UrlSeed pop() {
        if (urlQueue.size() == 0) {
            return null;
        } else {
            return urlQueue.poll();
        }
    }

    /**
     * test
     * */
    public static void main(String[] args) {
        UrlSeed urlSeed1 = new UrlSeed("123",5);
        UrlSeed urlSeed2 = new UrlSeed("1234",6);
        UrlSeed urlSeed3 = new UrlSeed("1234",4);
        PriorityQueueScheduler priorityQueueScheduler = new PriorityQueueScheduler();
        priorityQueueScheduler.push(urlSeed1);
        priorityQueueScheduler.push(urlSeed2);
        priorityQueueScheduler.push(urlSeed3);
    }

}