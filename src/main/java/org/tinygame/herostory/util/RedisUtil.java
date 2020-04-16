package org.tinygame.herostory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RedisUtil {

    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);
    /**
     * Redis 连接池
     */
    private static JedisPool _jedisPool = null;

    /**
     * 默认私有化
     */
    private RedisUtil(){}

    public static void init() {
        try {
            _jedisPool = new JedisPool("127.0.0.1",6379);
        }catch (Exception ex) {
            LOGGER.error(ex.getMessage(),ex);
        }
    }

    /**
     * 获取Redis 实例
     * @return
     */
    public static Jedis getRedis(){
        if(_jedisPool == null) {
            throw new RuntimeException("_jedisPool 尚未初始化");
        }
        Jedis redis = _jedisPool.getResource();

        return redis;
    }

}
