/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yami.shop.common.config.Constant;
import com.yami.shop.common.constants.CacheNames;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 *
 * @author yami
 */
@Slf4j
public class RedisUtil {

    @SuppressWarnings("unchecked")
    private static final RedisTemplate<String, Object> REDIS_TEMPLATE = SpringContextUtils.getBean("redisTemplate", RedisTemplate.class);

    public static final StringRedisTemplate STRING_REDIS_TEMPLATE = SpringContextUtils.getBean("stringRedisTemplate", StringRedisTemplate.class);
    //=============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public static Boolean expire(String key, long time) {
        try {
            if (time > 0) {
                REDIS_TEMPLATE.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("指定Redis缓存失效时间错误：", e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回-1代表为永久有效 失效时间为0，说明该主键未设置失效时间（失效时间默认为-1）
     */
    public static Long getExpire(String key) {
        return REDIS_TEMPLATE.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false 不存在
     */
    public static Boolean hasKey(String key) {
        try {
            return REDIS_TEMPLATE.hasKey(key);
        } catch (Exception e) {
            log.error("判断Redis中指定的key是否存在错误:", e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public static void del(String...key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                REDIS_TEMPLATE.delete(key[0]);
            } else {
                REDIS_TEMPLATE.delete(Arrays.asList(key));
            }
        }
    }

    /**
     * 删除缓存
     *
     * @param keys key列表
     */
    public static void del(List<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        REDIS_TEMPLATE.delete(keys);
    }

    //============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return key == null ? null : (T) REDIS_TEMPLATE.opsForValue().get(key);
    }

    /**
     * 批量缓存获取
     * @param keyName 键名
     * @param ids
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<Long, T> mapByIds(String keyName, List<Long> ids, Class<T> tClass) {
        ids = CollUtil.distinct(ids);
        List<String> keys = new ArrayList<>(ids.size());
        for (Long id : ids) {
            keys.add(keyName + CacheNames.UNION + id);
        }
        Map<Long, T> map = new HashMap<>(Constant.INITIAL_CAPACITY);
        List<Object> objects = REDIS_TEMPLATE.opsForValue().multiGet(keys);
        if (CollUtil.isEmpty(objects)) {
            return map;
        }
        for (Object o : objects) {
            Long id = ids.remove(0);
            if (o != null) {
                T t = BeanUtil.map(o, tClass);
                map.put(id, t);
            }
        }
        return map;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                REDIS_TEMPLATE.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                REDIS_TEMPLATE.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置Redis缓存错误:", e);
            return false;
        }
    }

    /**
     * 递增 此时value值必须为int类型 否则报错
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public static Long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return STRING_REDIS_TEMPLATE.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public static Long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须小于0");
        }
        return STRING_REDIS_TEMPLATE.opsForValue().increment(key, -delta);
    }

    public static void setLongValue(String key, Long value, long time) {
        try {
            if (time > 0) {
                STRING_REDIS_TEMPLATE.opsForValue().set(key, String.valueOf(value), time, TimeUnit.SECONDS);
            } else {
                STRING_REDIS_TEMPLATE.opsForValue().set(key, String.valueOf(value));
            }
        } catch (Exception e) {
            log.error("设置Redis缓存错误:", e);
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Long getLongValue(String key) {
        if (key == null) {
            return null;
        }
        String result = STRING_REDIS_TEMPLATE.opsForValue().get(key);
        if (result == null) {
            return null;
        }
        return Long.valueOf(result);
    }

    /**
     * 比较和删除标记，原子性
     *
     * @return 是否成功
     */
    public static boolean cad(String key, String value) {

        if (key.contains(StrUtil.SPACE) || value.contains(StrUtil.SPACE)) {
            throw new YamiShopBindException("yami.network.busy");
        }

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        //通过lure脚本原子验证令牌和删除令牌
        Long result = STRING_REDIS_TEMPLATE.execute(new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                value);

        return !Objects.equals(result, 0L);
    }

    /**
     * 批量删除缓存
     *
     * @param keys
     */
    public static void deleteBatch(List<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        for (String key : keys) {
            if (key.contains(StrUtil.SPACE)) {
                throw new YamiShopBindException("yami.network.busy");
            }
        }
        REDIS_TEMPLATE.delete(keys);
    }

    public static Set<String> keys(String pattern) {
        throw new RuntimeException("请不要使用keys");
    }

    /**
     * list缓存放入并设置时间
     *
     * @param key  键
     * @param list 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static void setRightPushAll(String key, List list, long time) {
        if (key.contains(StrUtil.SPACE)) {
            throw new YamiShopBindException(ResponseEnum.EXCEPTION);
        }
        try {
            if (time > 0) {
                REDIS_TEMPLATE.opsForList().rightPushAll(key, list);
                expire(key, time);
            } else {
                REDIS_TEMPLATE.opsForList().rightPushAll(key, list);
            }
        } catch (Exception e) {
            log.error("Redis opsForList error: {}", e.getMessage());
        }
    }

    /**
     * 获取list缓存的size
     *
     * @param key 键
     * @return true成功 false 失败
     */
    public static Long getListSize(String key) {
        if (key.contains(StrUtil.SPACE)) {
            throw new YamiShopBindException(ResponseEnum.EXCEPTION);
        }
        try {
            return REDIS_TEMPLATE.opsForList().size(key);
        } catch (Exception e) {
            log.error("Redis opsForList error: {}", e.getMessage());
            return 0L;
        }
    }

    /**
     * 根据key 获取list指定范围的内容
     *
     * @param key   键 不能为null
     * @param start
     * @param end
     * @return 时间(秒) 返回-1代表为永久有效 失效时间为0，说明该主键未设置失效时间（失效时间默认为-1）
     */
    public static List getListRange(String key, Long start, Long end) {
        if (key.contains(StrUtil.SPACE)) {
            throw new YamiShopBindException(ResponseEnum.EXCEPTION);
        }
        return REDIS_TEMPLATE.opsForList().range(key, start, end);
    }


    /**
     * 获取模糊匹配的的所有key
     * @return 所有field
     */
    public static List<String> hScan(StringRedisTemplate redisTemplate,String cachePrefix) {
        if(Objects.nonNull(redisTemplate)){
            Cursor<String> cursor = null;
            List<String> list = new ArrayList<>();
            try {
                ScanOptions options = ScanOptions.scanOptions()
                        .count(1000)
                        .match(cachePrefix + "*")
                        .build();
                RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
                cursor = redisTemplate.executeWithStickyConnection(redisConnection ->
                        new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize));
                if(Objects.isNull(cursor)){
                    return list;
                }
                while (cursor.hasNext()) {
                    list.add(cursor.next());
                }
                return list;
            } finally {
                if(Objects.nonNull(cursor)) {
                    cursor.close();
                }
            }
        }
        return new ArrayList<>();
    }
}
