package com.jinshuai.core.scheduler.impl;

import com.google.gson.Gson;
import com.jinshuai.core.scheduler.Scheduler;
import com.jinshuai.entity.UrlSeed;
import com.jinshuai.util.JedisUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @author: JS
 * @date: 2018/3/26
 * @description:
 *  将种子存放到Redis
 */
@Slf4j
public class RedisScheduler implements Scheduler {

    /**
     * 存放UrlSeed.url hash && 进行种子判重
     * */
    private final static String PREFIX_SET = "Spider.set";

    /**
     * 根据种子的优先级先简单创建不同的几个队列
     * */
    private final static String PREFIX_QUEUE_HIGH = "Spider.queue.high";
    private final static String PREFIX_QUEUE_LOW = "Spider.queue.low";
    private final static String PREFIX_QUEUE_DEFAULT = "Spider.queue.default";

    /**
     * @param urlSeed 种子
     * @desciption:
     *  配置 jedisPool
     *  添加种子的URL到Set，种子序列话后的JSON文本到List
     *  添加种子之前需要判断种子是否已经存在。
     * */
    public void push(UrlSeed urlSeed) {
        try (Jedis jedis = JedisUtils.getSingleInstance().getJedis()) {
            // 种子不存在
            if (!jedis.sismember(PREFIX_SET, urlSeed.getUrlHash())) {
                // 添加种子Url对应的hash到判重Set
                jedis.sadd(PREFIX_SET, urlSeed.getUrlHash());
                // 添加种子序列化后的JSON文本到List
                Gson gson = new Gson();
                String urlSeedToJson = gson.toJson(urlSeed);
                long urlSeedPriority = urlSeed.getPriority();
                if (urlSeedPriority > 5) {
                    jedis.lpush(PREFIX_QUEUE_HIGH, urlSeedToJson);
                } else if (urlSeedPriority == 5) {
                    jedis.lpush(PREFIX_QUEUE_DEFAULT, urlSeedToJson);
                } else {
                    jedis.lpush(PREFIX_QUEUE_LOW, urlSeedToJson);
                }
            }
        } catch (Exception e) {
            log.error("JedisPushUrl[{}]出错",urlSeed.toString(),e);
        }
    }

    /**
     * @return 从列表中获取的种子JSON反序列化为UrlSeed
     * @description:
     *  优先从高优先级别的列表里取种子
     * */
    public UrlSeed pop() {
        Jedis jedis = JedisUtils.getSingleInstance().getJedis();
        Gson gson = new Gson();
        String urlSeedToJson = null;
        UrlSeed urlSeed = null;
        try {
            if ((urlSeedToJson = jedis.lpop(PREFIX_QUEUE_HIGH)) != null) {
                urlSeed = gson.fromJson(urlSeedToJson,UrlSeed.class);
            } else if ((urlSeedToJson = jedis.lpop(PREFIX_QUEUE_DEFAULT)) != null) {
                urlSeed = gson.fromJson(urlSeedToJson,UrlSeed.class);
            } else if ((urlSeedToJson = jedis.lpop(PREFIX_QUEUE_LOW)) != null) {
                urlSeed = gson.fromJson(urlSeedToJson,UrlSeed.class);
            }
            return urlSeed;
        } catch (Exception e) {
            log.error("JedisPopUrl [{}]出错", urlSeedToJson, e);
        } finally {
            if (jedis != null && jedis.isConnected())
                jedis.disconnect();
        }
        return gson.fromJson(urlSeedToJson,UrlSeed.class);
    }

    /**
     * test connection
     * */
    public static void main(String[] args) {
        Jedis jedis = JedisUtils.getSingleInstance().getJedis();
        System.out.println(jedis.ping());
        UrlSeed urlSeed = new RedisScheduler().pop();
        System.out.println(urlSeed);
        jedis.lpush(PREFIX_QUEUE_LOW, "dasdasdasdsa");
    }

}