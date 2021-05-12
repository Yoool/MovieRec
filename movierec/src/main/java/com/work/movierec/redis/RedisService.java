//package com.work.seckill.redis;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import com.alibaba.fastjson.JSON;
//
//@Service
//public class RedisService {
//
//    @Autowired
//    JedisPool jedisPool;    //连接池
//
//    /**
//     * 获取单个对象
//     */
//    public <T> T get(KeyPrefix prefix, String key, Class<T> cls) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisPool.getResource();
//            String realKey = prefix.getPrefix() + key;
//            String str = jedis.get(realKey);
////            String str = jedis.get(key);//temp
//            T t = stringToBean(str, cls);
//            return t;
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * 设置对象
//     */
//    public <T> boolean set(KeyPrefix prefix, String key, T value) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisPool.getResource();
//            String str = beanToString(value);
//            if (str == null || str.length() <= 0) {
//                return false;
//            }
//
//            String realKey = prefix.getPrefix() + key;
//            int seconds = prefix.expireSeconds();
//            if (seconds <= 0) {
//                jedis.set(realKey, str);
//            } else {
//                jedis.setex(realKey, seconds, str);
//            }
//            return true;
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * 判断key是否存在
//     */
//    public <T> boolean exists(KeyPrefix prefix, String key) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisPool.getResource();
//            String realKey = prefix.getPrefix() + key;
//            return jedis.exists(realKey);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * 增加值
//     */
//    public <T> Long incr(KeyPrefix prefix, String key) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisPool.getResource();
//            String realKey = prefix.getPrefix() + key;
//            return jedis.incr(realKey);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * 减少值
//     */
//    public <T> Long decr(KeyPrefix prefix, String key) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisPool.getResource();
//            String realKey = prefix.getPrefix() + key;
//            return jedis.decr(realKey);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * 删除
//     * */
//    public boolean delete(KeyPrefix prefix, String key) {
//        Jedis jedis = null;
//        try {
//            jedis =  jedisPool.getResource();
//            //生成真正的key
//            String realKey  = prefix.getPrefix() + key;
//            long ret =  jedis.del(realKey);
//            return ret > 0;
//        }finally {
//            returnToPool(jedis);
//        }
//    }
//
//    //归还到连接池
//    private void returnToPool(Jedis jedis) {
//        if (jedis != null) jedis.close();
//    }
//
//    public static  <T> String beanToString(T value) {
//        if (value == null) return null;
//
//        Class<?> cls = value.getClass();
//        if (cls == int.class || cls == Integer.class) {
//            return "" + value;
//        } else if (cls == String.class) {
//            return (String) value;
//        } else if (cls == long.class || cls == Long.class) {
//            return "" + value;
//        } else
//            return JSON.toJSONString(value);
//    }
//
//    @SuppressWarnings("unchecked")
//    public static  <T> T stringToBean(String str, Class<T> cls) {
//        if (str == null || str.length() <= 0 || cls == null) return null;
//
//        if (cls == int.class || cls == Integer.class) {
//            return (T) Integer.valueOf(str);
//        } else if (cls == String.class) {
//            return (T) str;
//        } else if (cls == long.class || cls == Long.class) {
//            return (T) Long.valueOf(str);
//        } else
//            return JSON.toJavaObject(JSON.parseObject(str), cls);
//    }
//
//
//}
