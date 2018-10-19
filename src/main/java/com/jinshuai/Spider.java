package com.jinshuai;

import com.jinshuai.core.downloader.Downloader;
import com.jinshuai.core.downloader.impl.HttpClientPoolDownloader;
import com.jinshuai.core.parser.Parser;
import com.jinshuai.core.parser.impl.NewsParser;
import com.jinshuai.core.saver.Saver;
import com.jinshuai.core.saver.impl.TextSaver;
import com.jinshuai.core.scheduler.Scheduler;
import com.jinshuai.core.scheduler.impl.PriorityQueueScheduler;
import com.jinshuai.core.scheduler.impl.RedisScheduler;
import com.jinshuai.entity.Page;
import com.jinshuai.entity.UrlSeed;
import com.jinshuai.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;


/**
 * @author: JS
 * @date: 2018/3/27
 * @description: 程序启动入口
 */
public class Spider {

    /**
     * 设置爬虫的各个组件：downloader，parser，saver，scheduler，urlSeed
     * 配置线程池
     * 启动
     */

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
     * 线程池参数配置
     */
    private ThreadPoolExecutor pool;
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 5;
    private static final long KEEP_ALIVE_TIME = 1500L;

    /**
     * 任务队列size
     */
    private static final int MAX_QUEUE_SIZE = 100;

    /**
     * 最多只有MAX_QUEUE_SIZE + MAX_POOL_SIZE个任务并发执行 -> 控制任务的提交速率
     */
    private Semaphore semaphore = new Semaphore(MAX_QUEUE_SIZE + MAX_POOL_SIZE);

    private Spider setDownloader(Downloader downloader) {
        if (downloader == null) {
            LOGGER.error("未设置下载器，启动失败");
            return null;
        }
        this.downloader = downloader;
        return this;
    }

    private Spider setParser(Parser parser) {
        if (parser == null) {
            LOGGER.error("未设置解析器，启动失败");
            return null;
        }
        this.parser = parser;
        return this;
    }

    private Spider setSaver(Saver saver) {
        if (saver == null) {
            LOGGER.error("未设置保存器，启动失败");
            return null;
        }
        this.saver = saver;
        return this;
    }

    private Spider setScheduler(Scheduler scheduler) {
        if (scheduler == null) {
            LOGGER.error("未设置调度器，启动失败");
            return null;
        }
        this.scheduler = scheduler;
        return this;
    }

    private Spider addUrlSeed(UrlSeed urlSeed) {
        if (urlSeed == null) {
            LOGGER.error("未添加初始种子，启动失败");
            return null;
        }
        scheduler.push(urlSeed);
        return this;
    }

    private Spider setThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime) {
        pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(MAX_QUEUE_SIZE));
        return this;
    }

    private void run() {
        LOGGER.info("爬虫启动......");
        setThreadPool(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME);
        UrlSeed urlSeed = null;
        while (true) {
            try {
                LOGGER.info("已完成任务数量:[{}]，运行中线程数量：[{}]，最大线程运行数量: [{}]，工作队列任务数量：[{}]",
                        pool.getCompletedTaskCount(), pool.getActiveCount(), pool.getMaximumPoolSize(), pool.getQueue().size());
                urlSeed = scheduler.pop();
                // 种子仓库没有种子并且活跃线程为0(不再解析页面产生新的种子)
                if (urlSeed == null && pool.getActiveCount() == 0) {
                    pool.shutdown();
                    LOGGER.info("解析完毕，正在停止......");
                    break;
                } else if (urlSeed == null) { // 线程池中的线程还在完成任务，需要等待
                    LOGGER.info("种子仓库已无种子，等待中......");
                    Thread.sleep(1000);
                } else {
                    // 一切正常，分配线程处理任务，
                    // 如果运行的线程达到了最大数量则接受的任务会进入等待队列，等待线程执行完成后再从队列里取任务
                    LOGGER.info("正在处理:[{}]，优先级(默认5):[{}]", urlSeed.getUrl(), urlSeed.getPriority());
                    // 获取许可
                    semaphore.acquire();
                    // 调用线程执行任务
                    pool.execute(new SpiderWork(urlSeed));
                }
                // 自定义的任务停止目标: 当完成的任务数量达到800 TODO:随便写的--..
                if (pool.getCompletedTaskCount() >= TARGET_TASK_NUMBER && urlSeed == null && pool.getQueue().size() == 0) {
                    pool.shutdown();
                    LOGGER.info("达到目标，正在停止......");
                }
            } catch (InterruptedException e) {
                LOGGER.error("sleep期间中断异常", e);
            } catch (RejectedExecutionException ree) {
                LOGGER.error("未获取到信号量的许可证", ree);
            } finally {
                // 需要释放许可
                semaphore.release();
            }
        }
    }

    private class SpiderWork implements Runnable {

        private UrlSeed urlSeed;

        SpiderWork(UrlSeed urlSeed) {
            this.urlSeed = urlSeed;
        }

        public void run() {
            try {
                LOGGER.info("已完成任务数量:[{}]，运行中线程数量：[{}]，最大线程运行数量: [{}]，工作队列任务数量：[{}]",
                        pool.getCompletedTaskCount(), pool.getActiveCount(), pool.getMaximumPoolSize(), pool.getQueue().size());
                Page page = downloader.download(urlSeed);
                parser.parse(page);
                // 将新的种子添加到调度器中
                page.getUrlSeeds().forEach(seed -> scheduler.push(seed));
                saver.save(page);
            } finally {
                // 完成任务，释放许可
                semaphore.release();
            }
        }
    }

    /**
     * 入口
     */
    public static void main(String[] args) {
        Spider.build()
                .setDownloader(new HttpClientPoolDownloader())
                .setParser(new NewsParser())
                .setSaver(new TextSaver())
//                .setScheduler(new RedisScheduler())
                .setScheduler(new PriorityQueueScheduler())
                .addUrlSeed(new UrlSeed("http://xww.hebut.edu.cn/gdyw/index.htm", 5))
                .run();
    }

}