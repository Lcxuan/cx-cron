package com.cxcron.redis;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 登录失败次数限流。
 */
@Slf4j
@Component
public class LoginFailureRedis {

    private static final String USER_KEY_PREFIX = "auth:login:fail:user:";

    private static final String IP_KEY_PREFIX = "auth:login:fail:ip:";

    private static final int MAX_USER_FAILURES = 5;

    private static final int MAX_IP_FAILURES = 20;

    private static final Duration FAILURE_WINDOW = Duration.ofMinutes(15);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 判断用户名或客户端 IP 是否已被临时锁定。
     *
     * @param username 登录用户名
     * @param clientIp 客户端 IP
     * @return 是否锁定
     */
    public boolean isLocked(String username, String clientIp) {
        try {
            return getCount(USER_KEY_PREFIX + username) >= MAX_USER_FAILURES
                    || (StrUtil.isNotBlank(clientIp)
                    && getCount(IP_KEY_PREFIX + clientIp) >= MAX_IP_FAILURES);
        } catch (Exception ex) {
            log.warn("登录限流检查失败，已放行本次登录", ex);
            return false;
        }
    }

    /**
     * 记录一次登录失败。
     *
     * @param username 登录用户名
     * @param clientIp 客户端 IP
     */
    public void recordFailure(String username, String clientIp) {
        try {
            increment(USER_KEY_PREFIX + username);
            if (StrUtil.isNotBlank(clientIp)) {
                increment(IP_KEY_PREFIX + clientIp);
            }
        } catch (Exception ex) {
            log.warn("记录登录失败次数失败", ex);
        }
    }

    /**
     * 登录成功后清空失败计数。
     *
     * @param username 登录用户名
     * @param clientIp 客户端 IP
     */
    public void clear(String username, String clientIp) {
        try {
            stringRedisTemplate.delete(USER_KEY_PREFIX + username);
            if (StrUtil.isNotBlank(clientIp)) {
                stringRedisTemplate.delete(IP_KEY_PREFIX + clientIp);
            }
        } catch (Exception ex) {
            log.warn("清除登录失败次数失败", ex);
        }
    }

    /**
     * 读取指定 Key 的失败次数，Key 不存在或值非法时返回 0。
     *
     * @param key Redis Key
     * @return 失败次数
     */
    private long getCount(String key) {
        return NumberUtil.parseLong(stringRedisTemplate.opsForValue().get(key), 0L);
    }

    /**
     * 将指定 Key 的失败次数加 1。
     *
     * <p>
     * 先通过 {@code setIfAbsent} 保证 Key 存在且带有过期时间，再执行自增；
     * 后续自增不会刷新过期时间，因此计数窗口从第一次失败开始计算。
     * </p>
     *
     * @param key Redis Key
     */
    private void increment(String key) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, "0", FAILURE_WINDOW.toSeconds(), TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().increment(key);
    }
}
