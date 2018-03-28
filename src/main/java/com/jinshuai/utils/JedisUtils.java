package com.jinshuai.utils;

import jdk.nashorn.internal.objects.NativeUint8Array;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: JS
 * @date: 2018/3/27
 * @description:
 *  对Jedis简单的封装
 */
public class JedisUtils {

    /**
     * JedisUtils实例
     * */
    private static volatile JedisUtils JEDISUTILS;

    /**
     * 获取JedisUtils单例
     * */
    public static JedisUtils getSingleInstance() {
        if (JEDISUTILS == null) {
            synchronized (JedisUtils.class) {
                JEDISUTILS = new JedisUtils();
            }
        }
        return JEDISUTILS;
    }

    /**
     * 保存若干个jedisPool
     * key 为IP+port
     * */
    private static Map<String,JedisPool> maps = new ConcurrentHashMap<String, JedisPool>();

    private JedisPool getJedisPool(final String ip, final int port) {
        JedisPool jedisPool = null;
        if (maps.get(ip) == null) {
            redis.clients.jedis.JedisPoolConfig jedisPoolConfig = new redis.clients.jedis.JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(JedisPoolConfig.MAX_ACTIVE);
            jedisPoolConfig.setMaxIdle(JedisPoolConfig.MAX_IDLE);
            jedisPoolConfig.setMaxWaitMillis(JedisPoolConfig.MAX_WAIT);
            jedisPool = new JedisPool(jedisPoolConfig, ip, port, JedisPoolConfig.TIMEOUT);
        } else {
            jedisPool = maps.get(ip);
        }
            return jedisPool;
    }

    /**
     * 从jedisPool中获取jedis
     * */
    public Jedis getJedis(String ip, int port) {
        return getJedisPool(ip,port).getResource();
    }

}

class JedisPoolConfig {

    /**
     * 可用连接实例的最大数目，默认值为8；
     * 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
     */
    public static int MAX_ACTIVE = 5000;

    /**
     * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
     */
    public static int MAX_IDLE = 8;

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