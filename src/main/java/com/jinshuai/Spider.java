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
import com.jinshuai.util.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;


/**
 * @author: JS
 * @date: 2018/3/27
 * @description: 程序启动入口
 */
@Slf4j
public class Spider {
    
    /**
     * 设置爬虫组件：scheduler、downloader、parser、saver、
     */
    private Scheduler scheduler;
    private Downloader downloader;
    private Parser parser;
    private Saver saver;

    /**
     * 初始目标任务量
     * */
    private long targetTaskNumbers = Long.MAX_VALUE;

    /**
     * 线程池参数配置
     */
    private ThreadPoolExecutor pool;
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 5;
    private static final long KEEP_ALIVE_TIME = 1500L;
    private static final int MAX_QUEUE_SIZE = 100;

    /**
     * 最多只有MAX_QUEUE_SIZE + MAX_POOL_SIZE个任务并发执行 -> 控制任务的提交速率
     */
    private Semaphore semaphore = new Semaphore(MAX_QUEUE_SIZE + MAX_POOL_SIZE);

    private Spider setScheduler(Scheduler scheduler) {
        if (scheduler == null) {
            log.error("未设置调度器，启动失败");
            System.exit(-1);
        }
        this.scheduler = scheduler;
        return this;
    }

    private Spider setDownloader(Downloader downloader) {
        if (downloader == null) {
            log.error("未设置下载器，启动失败");
            System.exit(-1);
        }
        this.downloader = downloader;
        return this;
    }

    private Spider setParser(Parser parser) {
        if (parser == null) {
            log.error("未设置解析器，启动失败");
            System.exit(-1);
        }
        this.parser = parser;
        return this;
    }

    private Spider setSaver(Saver saver) {
        if (saver == null) {
            log.error("未设置保存器，启动失败");
            System.exit(-1);
        }
        this.saver = saver;
        return this;
    }

    private Spider setThreadPool() {
        pool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(MAX_QUEUE_SIZE));

        return this;
    }

    private Spider addUrlSeed(UrlSeed urlSeed) {
        if (urlSeed == null) {
            log.error("未添加初始种子，启动失败");
            System.exit(-1);
        }
        scheduler.push(urlSeed);
        return this;
    }

    private Spider setTargetTaskNumbers() {
        String configTargetNum = PropertiesUtils.getInstance().get("targetNum");
        if (configTargetNum != null && !configTargetNum.trim().equals("")) {
            try {
                targetTaskNumbers = Long.valueOf(configTargetNum);
                if (targetTaskNumbers <= 0) {
                    log.error("无效的目标任务数量:[{}]", targetTaskNumbers);
                }
            } catch (Exception e) {
                log.error("无效的目标任务数量:[{}]", configTargetNum, e);
            }
        }
        return targetTaskNumbers > 0 ? this : null;
    }

    private void run() {
        log.info("爬虫启动......");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            pool.shutdown(); // clean resource
        }));
        UrlSeed urlSeed = null;
        while (true) {
            try {
                // 种子仓库没有种子并且活跃线程为0(不再解析页面产生新的种子)
                if ((urlSeed = scheduler.pop()) == null && pool.getActiveCount() == 0) {
                    pool.shutdown();
                    log.info("解析完毕，正在停止......");
                    break;
                } else if (urlSeed == null) {
                    log.info("种子仓库已无种子，等待中......");
                    Thread.sleep(1000);
                } else {
                    // 一切正常，分配线程处理任务，
                    log.info("准备解析URL:[{}]，优先级(默认5):[{}]", urlSeed.getUrl(), urlSeed.getPriority());
                    // 获取许可
                    semaphore.acquire();
                    // 调用线程执行任务
                    pool.execute(new SpiderWork(urlSeed));
                }
                // 自定义的任务停止目标: 当完成的任务数量达到目标值
                if (pool.getCompletedTaskCount() >= targetTaskNumbers && urlSeed == null && pool.getQueue().size() == 0) {
                    pool.shutdown();
                    log.info("达到目标，正在停止......");
                }
            } catch (InterruptedException ie) {
                log.error("当前线程被中断", ie);
            } catch (RejectedExecutionException ree) {
                log.error("拒绝此次提交的任务[{}]", urlSeed, ree);
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
                log.info("已完成任务数量:[{}]，运行中线程数量：[{}]，最大线程运行数量: [{}]，工作队列任务数量：[{}]",
                        pool.getCompletedTaskCount(), pool.getActiveCount(), pool.getMaximumPoolSize(), pool.getQueue().size());
                Page page = downloader.download(urlSeed);
                parser.parse(page);
                // 将新的种子添加到调度器中
                page.getUrlSeeds().forEach(seed -> scheduler.push(seed));
                saver.save(page);
            } finally {
                semaphore.release();
            }
        }
    }

    private static Spider build() {
        return new Spider()
                .setDownloader(new HttpClientPoolDownloader())
                .setParser(new NewsParser())
                .setSaver(new TextSaver())
//                .setScheduler(new RedisScheduler())
                .setScheduler(new PriorityQueueScheduler())
                .setThreadPool()
                .setTargetTaskNumbers();
    }

    /**
     * Test
     *
     * 线程池提交任务流程：
     * 判断当前活跃的线程数量和corePoolSize的大小关系，如果没达到corePoolSize就会开新的线程执行任务，如果达到了
     * 判断和工作队列的大小关系，如果工作队列还没有满，将任务放到工作队列中，如果满了
     * 判断和maximumPoolSize的大小关系，如果没达到maximumPoolSize，就会新开线程执行任务，如果达到了
     * 回调注册的拒绝策略
     *
     */
    public static void main(String[] args) {
        Spider.build()
                .addUrlSeed(new UrlSeed("http://xww.hebut.edu.cn/gdyw/index.htm"))
                .run();
    }

}