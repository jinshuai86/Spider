package com.jinshuai.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: JS
 * @date: 2018/3/27
 * @description:
 *  对Jedis简单的封装
 */
@Slf4j
public class JedisUtils {

    /**
     * JedisUtils实例
     * */
    private static volatile JedisUtils jedisUtils;

    /**
     * 获取JedisUtils单例
     * */
    public static JedisUtils getSingleInstance() {
        if (jedisUtils == null) {
            synchronized (JedisUtils.class) {
                jedisUtils = new JedisUtils();
            }
        }
        return jedisUtils;
    }

    private JedisPool jedisPool;

    JedisUtils() {
        init();
    }

    private void init() {
        configJedisPool();
    }

    /**
     * 获取套接字、密码
     * */
    private static final String IP = PropertiesUtils.getInstance().get("ip");
    private static final int PORT = Integer.valueOf(PropertiesUtils.getInstance().get("port"));
    private static final String PASSWORD = PropertiesUtils.getInstance().get("password");

    /**
     * 可用连接实例的最大数目，默认值为8；
     * 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
     */
    private static int MAX_ACTIVE = 2048;

    /**
     * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
     */
    private static int MAX_IDLE = 200;

    /**
     * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
     * */
    private static int MAX_WAIT = 10000;

    /**
     * 超时时间
     * */
    private static int TIMEOUT = 10000;

    /**
     * 保存若干个jedisPool
     * key 为IP+port
     * */
    private static Map<String,JedisPool> maps = new ConcurrentHashMap<>();

    private void configJedisPool() {
        if (maps.get(IP) == null) {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(MAX_ACTIVE);
            jedisPoolConfig.setMaxIdle(MAX_IDLE);
            jedisPoolConfig.setMaxWaitMillis(MAX_WAIT);
            jedisPoolConfig.setTestOnReturn(true);
            // 未设置密码
            if (PASSWORD == null || PASSWORD.length() == 0) {
                log.info("配置文件中未设置Redis密码，请确保Redis服务器不需要密码验证!!!");
                jedisPool = new JedisPool(jedisPoolConfig, IP, PORT, TIMEOUT);
            } else {
                jedisPool = new JedisPool(jedisPoolConfig, IP, PORT, TIMEOUT, PASSWORD);
            }
            maps.put(IP,jedisPool);
        } else {
            jedisPool = maps.get(IP);
        }
    }

    /**
     * 从jedisPool中获取jedis
     * */
    public Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            log.error("连接Redis失败,检查IP、端口、密码", e);
        }
        return jedis;
    }

}