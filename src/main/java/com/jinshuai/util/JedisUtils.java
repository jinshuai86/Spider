package com.jinshuai.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: JS
 * @date: 2018/3/27
 * @description:
 *  对Jedis简单的封装
 */
public class JedisUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisUtils.class);

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

    /**
     * 获取套接字、密码
     * */
    private static final String IP = PropertiesUtils.getInstance().get("ip");
    private static final int PORT = Integer.valueOf(PropertiesUtils.getInstance().get("port"));
    private static final String PASSWORD = PropertiesUtils.getInstance().get("password");


    /**
     * 保存若干个jedisPool
     * key 为IP+port
     * */
    private static Map<String,JedisPool> maps = new ConcurrentHashMap<String, JedisPool>();

    private JedisPool getJedisPool() {
        JedisPool jedisPool;
        if (maps.get(IP) == null) {
            redis.clients.jedis.JedisPoolConfig jedisPoolConfig = new redis.clients.jedis.JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(JedisPoolConfig.MAX_ACTIVE);
            jedisPoolConfig.setMaxIdle(JedisPoolConfig.MAX_IDLE);
            jedisPoolConfig.setMaxWaitMillis(JedisPoolConfig.MAX_WAIT);
            jedisPoolConfig.setTestOnReturn(true);
            // 未设置密码
            if (PASSWORD == null || PASSWORD.length() == 0) {
                LOGGER.info("配置文件中未设置Redis密码，请确保Redis服务器不需要密码验证!!!");
                jedisPool = new JedisPool(jedisPoolConfig, IP, PORT, JedisPoolConfig.TIMEOUT);
            } else {
                jedisPool = new JedisPool(jedisPoolConfig, IP, PORT, JedisPoolConfig.TIMEOUT, PASSWORD);
            }
            maps.put(IP,jedisPool);
        } else {
            jedisPool = maps.get(IP);
        }
            return jedisPool;
    }

    /**
     * 从jedisPool中获取jedis
     * */
    public Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = getJedisPool().getResource();
        } catch (Exception e) {
            LOGGER.error("连接Redis失败,检查IP、端口、密码",e);
            throw e;
        }
        return jedis;
    }

}

class JedisPoolConfig {

    /**
     * 可用连接实例的最大数目，默认值为8；
     * 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
     */
    public static int MAX_ACTIVE = 2048;

    /**
     * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
     */
    public static int MAX_IDLE = 200;

    /**
     * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
     * */
    public static int MAX_WAIT = 10000;

    /**
     * 超时时间
     * */
    public static int TIMEOUT = 10000;

    /**
     * 重试次数
     * */
    public static int RETRY_NUM = 5;
}