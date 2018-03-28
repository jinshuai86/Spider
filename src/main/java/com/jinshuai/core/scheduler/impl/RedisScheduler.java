package com.jinshuai.core.scheduler.impl;

import com.google.gson.Gson;
import com.jinshuai.Spider;
import com.jinshuai.core.scheduler.Scheduler;
import com.jinshuai.entity.UrlSeed;
import com.jinshuai.utils.HttpUtils;
import com.jinshuai.utils.JedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.net.URL;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  将种子存放到Redis
 */
public class RedisScheduler implements Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisScheduler.class);

    /**
     * 套接字 TODO:待写在配置文件中
     * */
    private static final String IP = "127.0.0.1";
    private static final int PORT = 6379;

    private static Jedis jedis = JedisUtils.getSingleInstance().getJedis(IP,PORT);
    /**
     * 存放UrlSeed.url
     * */
    private final static String PREFIX_SET = "ScriptSpider.set";

    /**
     * 根据种子的优先级先简单创建不同的几个队列
     * */
    private final static String PREFIX_QUEUE_HIGH = "ScriptSpider.queue.high";
    private final static String PREFIX_QUEUE_LOW = "ScriptSpider.queue.low";
    private final static String PREFIX_QUEUE_DEFAULT = "ScriptSpider.queue.default";

    /**
     * @param urlSeed 种子
     * @desciption:
     *  配置 jedisPool
     *  添加种子的URL到Set，种子序列话后的JSON文本到List
     *  添加种子之前需要判断种子是否已经存在。
     * */
    public void push(UrlSeed urlSeed) {
        try {
            String url = urlSeed.getUrl();
            // 此种子已经存在
            if (jedis.sismember(PREFIX_SET, url)) {
                LOGGER.info(url + "已存在");
                return;
            }
            // 添加种子的Url到Set
            jedis.sadd(PREFIX_SET, url);
            // 添加种子序列化后的JSON文本到List
            Gson gson = new Gson();
            String urlSeedToJson = gson.toJson(urlSeed);
            long urlSeedPriority = urlSeed.getPriority();
            if (urlSeedPriority > 5) {
                jedis.lpush(PREFIX_QUEUE_HIGH, urlSeedToJson);
                return;
            }
            if (urlSeedPriority == 5) {
                jedis.lpush(PREFIX_QUEUE_DEFAULT, urlSeedToJson);
                return;
            }
            if (urlSeedPriority < 5) {
                jedis.lpush(PREFIX_QUEUE_LOW, urlSeedToJson);
                return;
            }
        } catch (Exception e) {
            LOGGER.error("[JedisPushUrl] "  + urlSeed.toString() + " 出现异常：" + e.getMessage());
        } finally {
            if (jedis != null) jedis.disconnect();
        }
    }

    /**
     * @return 从列表中获取的种子JSON反序列化为UrlSeed
     * @description:
     *  优先从高优先级别的列表里取种子
     * */
    public UrlSeed pop() {
        Gson gson = new Gson();
        String urlSeedToJson = null;
        UrlSeed urlSeed = null;
        try {
            if ((urlSeedToJson = jedis.lpop(PREFIX_QUEUE_HIGH)) != null) {
                urlSeed = gson.fromJson(urlSeedToJson,UrlSeed.class);
                jedis.srem("ScriptSpider.set",urlSeed.getUrl());
                return urlSeed;
            } else if ((urlSeedToJson = jedis.lpop(PREFIX_QUEUE_DEFAULT)) != null) {
                urlSeed = gson.fromJson(urlSeedToJson,UrlSeed.class);
                jedis.srem("ScriptSpider.set",urlSeed.getUrl());
                return urlSeed;
            } else if ((urlSeedToJson = jedis.lpop(PREFIX_QUEUE_LOW)) != null) {
                urlSeed = gson.fromJson(urlSeedToJson,UrlSeed.class);
                jedis.srem("ScriptSpider.set",urlSeed.getUrl());
                return urlSeed;
            }
        } catch (Exception e) {
            LOGGER.error("[JedisPop] " + urlSeedToJson + " 出现异常" + e.getStackTrace());
        } finally {
            if (jedis != null) jedis.disconnect();
        }
        return gson.fromJson(urlSeedToJson,UrlSeed.class);
    }

    /**
     * test connection
     * */
    public static void main(String[] args) {
        Jedis jedis = RedisScheduler.jedis;
        System.out.println(jedis.ping());
        UrlSeed urlSeed = new RedisScheduler().pop();
        System.out.println(urlSeed);
        jedis.lpush(PREFIX_QUEUE_LOW, "dasdasdasdsa");
    }

}