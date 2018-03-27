package com.jinshuai;

import com.jinshuai.core.downloader.Downloader;
import com.jinshuai.core.parser.Parser;
import com.jinshuai.core.saver.Saver;
import com.jinshuai.core.scheduler.Scheduler;
import com.jinshuai.entity.Page;
import com.jinshuai.entity.UrlSeed;
import com.jinshuai.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: JS
 * @date: 2018/3/27
 * @description:
 *  程序启动入口
 */
public class Spider {

    /**
     * 设置爬虫的各个组件：downloader，parser，saver，scheduler，urlSeed
     * 配置线程池
     * 启动
     * */

    public static final Logger LOGGER = LoggerFactory.getLogger(Spider.class);

    public static Spider build() {
        return new Spider();
    }

    private Downloader downloader;
    private Parser parser;
    private Saver saver;
    private Scheduler scheduler;

    /**
     * 线程池线程数量
     * */
    private int threadPoolSize = 5;
    private ThreadPoolExecutor pool;

    public Spider setDownloader(Downloader downloader) {
        if (downloader == null) {
            LOGGER.error("未设置下载器，启动失败");
            return null;
        }
        this.downloader = downloader;
        return this;
    }

    public Spider setParser(Parser parser) {
        if (parser == null) {
            LOGGER.error("未设置解析器，启动失败");
            return null;
        }
        this.parser = parser;
        return this;
    }

    public Spider setSaver(Saver saver) {
        this.saver = saver;
        return this;
    }

    public Spider setScheduler(Scheduler scheduler) {
        if (scheduler == null) {
            LOGGER.error("未设置调度器，启动失败");
            return null;
        }
        this.scheduler = scheduler;
        return this;
    }

    public Spider addUrlSeed(String url) {
        if (url == null) {
            LOGGER.error("未添加初始种子，启动失败");
            return null;
        }
        scheduler.push(new UrlSeed(url, 5));
        return this;
    }

    public Spider setThreadPool(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        if (threadPoolSize <= 0) {
            this.threadPoolSize = 5;
        }
        pool = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 1500L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()); // 用的阻塞队列
        return this;
    }

    public void run() {
        LOGGER.info("spider start");

        setThreadPool(5);
        UrlSeed urlSeed = null;
        while (true) {
            LOGGER.info("当前线程池" + "已完成:" + pool.getCompletedTaskCount() +
                    "   运行中：" + pool.getActiveCount() +
                    "  最大运行:" + pool.getPoolSize() +
                    " 等待队列:" + pool.getQueue().size());
           urlSeed = scheduler.pop();
           if (urlSeed == null && pool.getActiveCount() == 0) {
               pool.shutdown();
               LOGGER.info("spider end");
               break;
           } else if (urlSeed == null) { // 线程池中的线程还在完成任务，需要等待
               LOGGER.info("调度器中已无种子，等待中... ...");
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
                   LOGGER.error("sleep()异常" + e.getMessage());
               }
           } else { // 一切正常，取种子。
               LOGGER.info("正在处理:" + urlSeed.getUrl() + "  优先级(默认:5):" + urlSeed.getPriority());
               pool.execute(new SpiderWork(urlSeed));
           }

        }
    }

    class SpiderWork implements Runnable{

        private UrlSeed urlSeed;

        SpiderWork(UrlSeed urlSeed) {
            this.urlSeed = urlSeed;
        }

        public void run() {
            LOGGER.info("当前线程池" + "已完成:" + pool.getCompletedTaskCount() + "   运行中：" + pool.getActiveCount() + "  最大运行:" + pool.getPoolSize() + " 等待队列:" + pool.getQueue().size());

            Page page = downloader.download(urlSeed);
            parser.parse(page);
            // 将新的种子添加到调度器中
            page.getUrlSeeds().forEach(seed -> scheduler.push(seed));
            saver.save(page);

        }
    }

}