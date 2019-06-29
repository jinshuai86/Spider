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
import com.jinshuai.util.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;


/**
 * @author: JS
 * @date: 2018/06/27
 * @description: 消费端
 */
@Slf4j
public class Consumer {
    
    /**
     * 设置爬虫组件：scheduler、downloader、parser、saver、
     */
    private Scheduler scheduler;
    private Downloader downloader;
    private Parser parser;
    private Saver saver;

    /**
     * 线程池参数配置
     */
    private ScheduledThreadPoolExecutor pool;
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    private static final String CHARSET = RemotingHelper.DEFAULT_CHARSET;

    private Consumer setScheduler(Scheduler scheduler) {
        if (scheduler == null) {
            log.error("未设置调度器，启动失败");
            System.exit(-1);
        }
        this.scheduler = scheduler;
        return this;
    }

    private Consumer setDownloader(Downloader downloader) {
        if (downloader == null) {
            log.error("未设置下载器，启动失败");
            System.exit(-1);
        }
        this.downloader = downloader;
        return this;
    }

    private Consumer setParser(Parser parser) {
        if (parser == null) {
            log.error("未设置解析器，启动失败");
            System.exit(-1);
        }
        this.parser = parser;
        return this;
    }

    private Consumer setSaver(Saver saver) {
        if (saver == null) {
            log.error("未设置保存器，启动失败");
            System.exit(-1);
        }
        this.saver = saver;
        return this;
    }

    private Consumer setThreadPool() {
        pool =  new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);
        return this;
    }

    private void run() {
        log.info("消费者启动......");
        startConsumer();
        while (true) {
            UrlSeed urlSeed = scheduler.pop();
            try {
                if (urlSeed == null) {
//                    log.info("队列暂无消息，等待中......");
                    TimeUnit.SECONDS.sleep(1);
                } else {
                    log.info("准备解析URL:[{}]，优先级(默认5):[{}]", urlSeed.getUrl(), urlSeed.getPriority());
                    pool.scheduleAtFixedRate(new ConsumerWork(urlSeed),10, 20, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                log.error("当前线程被中断", e);
            } catch (RejectedExecutionException e) {
                log.error("拒绝此次提交的任务[{}]", urlSeed, e);
            }
        }
    }

    private class ConsumerWork implements Runnable {

        private UrlSeed urlSeed;

        ConsumerWork(UrlSeed urlSeed) {
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
            }
        }
    }
    
    private void startConsumer() {
        PropertiesUtils properties = PropertiesUtils.getInstance();
        String ip = properties.get("mq-ip");
        String port = properties.get("mq-port");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("Consumer-Group");
        try {
            consumer.setNamesrvAddr(ip + ":" + port);
            consumer.subscribe("Forbidden-Topic", "*");
            consumer.subscribe("Redirect-Topic", "*");
            consumer.subscribe("ServerWrong-Topic", "*");
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                for (MessageExt msg : msgs) {
                    try {
//                        log.info("consume success [{}]", msg.toString());
                        // 其它状态码对应的url优先级是0
                        scheduler.push(new UrlSeed(new String(msg.getBody(), CHARSET), 0)); // TODO
                    } catch (UnsupportedEncodingException e) {
                        log.error("unsupported encoding[{}]", CHARSET, e);
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; });
            consumer.start();
        } catch (MQClientException e) {
            log.error("failed to start consumer", e);
            System.exit(-1);
        }
        log.info("Consumer Started.");
    }

    private static Consumer build() {
        return new Consumer()
                .setDownloader(new HttpClientPoolDownloader())
                .setParser(new NewsParser())
                .setSaver(new TextSaver())
                .setScheduler(new RedisScheduler())
                .setThreadPool();
    }

    /**
     * Test
     * */
    public static void main(String[] args) {
        Consumer.build()
                .run();
    }

}