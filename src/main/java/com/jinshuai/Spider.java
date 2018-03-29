package com.jinshuai;

import com.jinshuai.core.downloader.Downloader;
import com.jinshuai.core.downloader.impl.HttpClientPoolDownloader;
import com.jinshuai.core.parser.Parser;
import com.jinshuai.core.parser.impl.NewsParser;
import com.jinshuai.core.saver.Saver;
import com.jinshuai.core.saver.impl.TextSaver;
import com.jinshuai.core.scheduler.Scheduler;
import com.jinshuai.core.scheduler.impl.RedisScheduler;
import com.jinshuai.entity.Page;
import com.jinshuai.entity.UrlSeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
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

    private static final int TARGET_TASK_NUMBER = 800;
    private static final Logger LOGGER = LoggerFactory.getLogger(Spider.class);

    public static Spider build() {
        return new Spider();
    }

    private Downloader downloader;
    private Parser parser;
    private Saver saver;
    private Scheduler scheduler;

    /**
     * 线程池配置
     * */
    private ThreadPoolExecutor pool;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 5;
    private static final long KEEP_ALIVE_TIME = 1500L;

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

    public Spider addUrlSeed(UrlSeed urlSeed) {
        if (urlSeed == null) {
            LOGGER.error("未添加初始种子，启动失败");
            return null;
        }
        scheduler.push(urlSeed);
        return this;
    }

    private Spider setThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime) {
        pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        return this;
    }

    public void run() {
        LOGGER.info("spider start");
        setThreadPool(CORE_POOL_SIZE,MAX_POOL_SIZE,KEEP_ALIVE_TIME);
        UrlSeed urlSeed = null;
        while (true) {
            LOGGER.info("当前线程池已完成: " + pool.getCompletedTaskCount() +
                    " 运行中：" + pool.getActiveCount() +
                    " 最大运行: " + pool.getMaximumPoolSize() +
                    " 等待队列: " + pool.getQueue().size());
           urlSeed = scheduler.pop();
           // 种子仓库没有种子并且活跃线程为0(不再解析页面产生新的种子)
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
           } else {
               // 一切正常，分配线程处理任务，
               // 如果运行的线程达到了最大数量则接受的任务会进入等待队列，等待线程执行完成后再从队列里取任务
               LOGGER.info("正在处理:" + urlSeed.getUrl() + "  优先级(默认:5):" + urlSeed.getPriority());
               // 调用线程执行任务
               pool.execute(new SpiderWork(urlSeed));
           }
           // 自定义的任务停止目标//TODO:随便写的--..
           if (pool.getCompletedTaskCount() >= TARGET_TASK_NUMBER && urlSeed ==null && pool.getQueue().size() == 0) {
               pool.shutdown();
               LOGGER.info("达到目标，强制停止爬虫... ...");
               System.exit(-1);
           }
        }
    }

    class SpiderWork implements Runnable{

        private UrlSeed urlSeed;

        SpiderWork(UrlSeed urlSeed) {
            this.urlSeed = urlSeed;
        }

        public void run() {
            LOGGER.info("当前线程池已完成:" + pool.getCompletedTaskCount() +
                    " 运行中：" + pool.getActiveCount() +
                    " 最大运行: " + pool.getMaximumPoolSize() +
                    " 等待队列: " + pool.getQueue().size());
            Page page = downloader.download(urlSeed);
            parser.parse(page);
            // 将新的种子添加到调度器中
            page.getUrlSeeds().forEach(seed -> scheduler.push(seed));
            saver.save(page);
        }
    }

    /**
     * 入口
     * */
    public static void main(String[] args) {
        Spider.build().setDownloader(new HttpClientPoolDownloader())
                .setParser(new NewsParser())
                .setSaver(new TextSaver())
                .setScheduler(new RedisScheduler())
                .addUrlSeed(new UrlSeed("http://xww.hebut.edu.cn/gdyw/index.htm",5))
                .run();
    }

}