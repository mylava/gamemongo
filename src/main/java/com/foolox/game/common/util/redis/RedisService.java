package com.foolox.game.common.util.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 21/08/2018
 */
@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;

    private static final String EMPT_STRING = "";

    /**
     * --------------- ---------------
     * String
     * --------------- ---------------
     */
    /**
     * 存数据
     *
     * @param prefix
     * @param key
     * @param value
     * @return
     */
    public <T> String set(KeyPrefix prefix, String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.setex(prefix.getPrefix() + key, prefix.getExpire(), value);
            return result;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 取数据
     *
     * @param prefix
     * @param key
     * @return
     */
    public String get(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(prefix.getPrefix() + key);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 取数据
     *
     * @param prefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = jedis.get(prefix.getPrefix() + key);
            T t = jsonString2Bean(str, clazz);
            return t;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }


    /**
     * 存数据
     *
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> String set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = bean2JsonString(value);
            if (null == str) {
                return "";
            }
            String result = jedis.setex(prefix.getPrefix() + key, prefix.getExpire(), str);
            return result;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * --------------- ---------------
     * 原子操作
     * --------------- ---------------
     */
    /**
     * 递增
     *
     * @param prefix
     * @param key
     * @return
     */
    public Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long incr = jedis.incr(prefix.getPrefix() + key);
            return incr;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 递减
     *
     * @param prefix
     * @param key
     * @return
     */
    public Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long incr = jedis.decr(prefix.getPrefix() + key);
            return incr;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * --------------- ---------------
     * 通用操作
     * --------------- ---------------
     */
    /**
     * 判断key是否存在
     *
     * @param prefix
     * @param key
     * @return
     */
    public Boolean exists(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(prefix.getPrefix() + key);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 删除指定key
     *
     * @param key
     * @return
     */
    public Long del(KeyPrefix prefix, String key) {
        if (exists(prefix, key)) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                Long del = jedis.del(prefix.getPrefix() + key);
                return del;
            } finally {
                if (null != jedis) {
                    jedis.close();
                }
            }
        }
        return null;
    }

    /**
     * 更新过期时间
     *
     * @param prefix
     * @param key
     * @return
     */
    public void expire(KeyPrefix prefix, String key) {
        if (exists(prefix, key)) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.expire(prefix.getPrefix() + key, prefix.getExpire());
            } finally {
                if (null != jedis) {
                    jedis.close();
                }
            }
        }
    }

    /**
     * 返回指定Key的剩余生存时间
     *
     * @param prefix
     * @param key
     * @return
     */
    public Long ttl(KeyPrefix prefix, String key) {
        if (exists(prefix, key)) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return jedis.ttl(prefix.getPrefix() + key);
            } finally {
                if (null != jedis) {
                    jedis.close();
                }
            }
        }
        return null;
    }

    /**
     * --------------- ---------------
     * hash
     * --------------- ---------------
     */
    /**
     * 存hash
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public <T> Long hset(KeyPrefix prefix, String key, String field, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hset(prefix.getPrefix() + key, field, bean2JsonString(value));
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }
    /**
     * 取Hash
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(KeyPrefix prefix, String key, String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hget(prefix.getPrefix() + key, field);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 删除hash 中对应的多个field
     *
     * @param key
     * @param field
     * @return
     */
    public Long hdel(KeyPrefix prefix, String key, String... field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hdel(prefix.getPrefix() + key, field);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * hash中是否存在field
     *
     * @param key
     * @param field
     * @return
     */
    public Boolean hexists(KeyPrefix prefix, String key, String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hexists(prefix.getPrefix() + key, field);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 获取hash中指定key的所有值
     *
     * @param prefix
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hgetAll(prefix.getPrefix() + key);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 获取hash中指定key的所有值
     *
     * @param prefix
     * @param key
     * @return
     */
    public <T> Map<String, T> hgetAll(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Map<String, String> map = jedis.hgetAll(prefix.getPrefix() + key);
            Map<String, T> result = new HashMap<>();
            for (String s : map.keySet()) {
                result.put(s,jsonString2Bean(map.get(s), clazz));
            }
            return result;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 获取hash中指定key的所有 field
     *
     * @param prefix
     * @param key
     * @return
     */
    public Set<String> hkeys(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hkeys(prefix.getPrefix() + key);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 获取hash中指定key的所有 value
     *
     * @param prefix
     * @param key
     * @return
     */
    public List<String> hvals(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hvals(prefix.getPrefix() + key);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * --------------- ---------------
     * List
     * --------------- ---------------
     */

    /**
     * 将一个或多个值 value 插入到 list 的表头
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，对空list mylist 执行命令 LPUSH mylist a b c ，list的值将是 c b a ，
     * 这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
     *
     * @param prefix
     * @param key
     * @return
     */
    public <T> void lpush(KeyPrefix prefix, String key, List<T> values) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            for (T value : values) {
                String str = bean2JsonString(value);
                if (null == str) {
                    str = EMPT_STRING;
                }
                jedis.lpush(prefix.getPrefix() + key, str);
            }
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public <T> void lpush(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = bean2JsonString(value);
            if (null == str) {
                str = EMPT_STRING;
            }
            jedis.lpush(prefix.getPrefix() + key, str);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 返回list 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示list的第一个元素，以 1 表示list的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示list的最后一个元素， -2 表示list的倒数第二个元素，以此类推。
     *
     * @param prefix
     * @param key
     * @return
     */
    public <T> List<T> lrange(KeyPrefix prefix, String key, Integer start, Integer end, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            List<T> result = new ArrayList<>();
            List<String> lrange = jedis.lrange(prefix.getPrefix() + key, start, end);
            for (String s : lrange) {
                result.add(jsonString2Bean(s, clazz));
            }
            return result;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 移除并返回list 的头元素。
     *
     * @param prefix
     * @param key
     * @return
     */
    public <T> T lpop(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String lpop = jedis.lpop(prefix.getPrefix() + key);
            return jsonString2Bean(lpop, clazz);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * 移除并返回list 的尾元素。
     *
     * @param prefix
     * @param key
     * @return
     */
    public <T> T rpop(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String lpop = jedis.rpop(prefix.getPrefix() + key);
            return jsonString2Bean(lpop, clazz);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    private <T> String bean2JsonString(T value) {
        if (null == value) {
            return null;
        }
        return JSON.toJSONString(value);
    }

    private <T> T jsonString2Bean(String str, Class<T> clazz) {
        if (null == str || str.length() <= 0 || null == clazz) {
            return null;
        }
        if (clazz == String.class) {
            return (T) str;
        }
        return JSON.toJavaObject(JSON.parseObject(str), clazz);
    }


}
