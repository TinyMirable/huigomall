package com.macro.mall.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 * 负责生成验证码并存储到Redis中
 */
@Service
public class VerificationCodeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 验证码有效期（分钟）
     */
    private static final int CODE_EXPIRE_MINUTES = 10;

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    /**
     * 验证码Redis Key前缀
     */
    private static final String CODE_KEY_PREFIX = "verification:code:";

    /**
     * 验证码发送频率限制Redis Key前缀
     */
    private static final String RATE_LIMIT_KEY_PREFIX = "verification:rate_limit:";

    /**
     * 同一联系方式两次发送验证码的最小间隔（秒）
     */
    private static final int SEND_INTERVAL_SECONDS = 60;

    /**
     * 同一联系方式每小时最多发送次数
     */
    private static final int MAX_SENDS_PER_HOUR = 20;

    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 检查是否可以发送验证码（频率限制）
     * 
     * @param key 验证码的key，通常是手机号或邮箱
     * @throws RuntimeException 如果频率超限则抛出异常
     */
    private void checkRateLimit(String key) {
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + key;
        
        // 检查是否在冷却期内（距离上次发送不足60秒）
        String lastSendKey = rateLimitKey + ":last_send";
        Object lastSendTime = redisTemplate.opsForValue().get(lastSendKey);
        if (lastSendTime != null) {
            long lastSend = Long.parseLong(lastSendTime.toString());
            long currentTime = System.currentTimeMillis();
            long elapsed = (currentTime - lastSend) / 1000; // 转换为秒
            
            if (elapsed < SEND_INTERVAL_SECONDS) {
                long remaining = SEND_INTERVAL_SECONDS - elapsed;
                throw new RuntimeException("验证码发送过于频繁，请等待" + remaining + "秒后重试");
            }
        }
        
        // 检查每小时发送次数限制
        String hourCountKey = rateLimitKey + ":hour_count";
        Object hourCount = redisTemplate.opsForValue().get(hourCountKey);
        int count = hourCount == null ? 0 : Integer.parseInt(hourCount.toString());
        
        if (count >= MAX_SENDS_PER_HOUR) {
            throw new RuntimeException("发送次数已达上限，请1小时后再试");
        }
    }

    /**
     * 更新发送频率限制记录
     * 
     * @param key 验证码的key
     */
    private void updateRateLimit(String key) {
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + key;
        
        // 更新最后发送时间（60秒过期）
        String lastSendKey = rateLimitKey + ":last_send";
        redisTemplate.opsForValue().set(lastSendKey, String.valueOf(System.currentTimeMillis()), 
            SEND_INTERVAL_SECONDS, TimeUnit.SECONDS);
        
        // 更新每小时发送次数（1小时过期）
        String hourCountKey = rateLimitKey + ":hour_count";
        Long count = redisTemplate.opsForValue().increment(hourCountKey);
        if (count != null && count == 1) {
            // 第一次发送，设置过期时间
            redisTemplate.expire(hourCountKey, 1, TimeUnit.HOURS);
        }
    }

    /**
     * 生成并存储验证码（带频率限制）
     * 
     * @param key 验证码的key，通常是手机号或邮箱
     * @return 生成的验证码
     * @throws RuntimeException 如果频率超限则抛出异常
     */
    public String generateAndStoreCode(String key) {
        // 检查频率限制
        checkRateLimit(key);
        
        // 生成验证码
        String code = generateCode();
        String redisKey = CODE_KEY_PREFIX + key;
        
        // 存储验证码到Redis，设置10分钟过期时间
        redisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        // 更新频率限制记录
        updateRateLimit(key);
        
        return code;
    }

    /**
     * 验证验证码
     * 
     * @param key 验证码的key，通常是手机号或邮箱
     * @param code 用户输入的验证码
     * @return 验证是否成功
     */
    public boolean verifyCode(String key, String code) {
        String redisKey = CODE_KEY_PREFIX + key;
        Object storedCode = redisTemplate.opsForValue().get(redisKey);
        
        if (storedCode == null) {
            return false;
        }
        
        return storedCode.toString().equals(code);
    }

    /**
     * 删除验证码（验证成功后删除）
     * 
     * @param key 验证码的key
     */
    public void deleteCode(String key) {
        String redisKey = CODE_KEY_PREFIX + key;
        redisTemplate.delete(redisKey);
    }

    /**
     * 清除发送频率限制（验证成功后调用，允许立即发送下一个验证码）
     * 同时减少发送次数计数，确保验证成功的验证码不计入发送次数限制
     * 
     * @param key 验证码的key
     */
    public void clearRateLimit(String key) {
        String rateLimitKey = RATE_LIMIT_KEY_PREFIX + key;
        String lastSendKey = rateLimitKey + ":last_send";
        // 删除最后发送时间的限制，允许立即发送下一个验证码
        redisTemplate.delete(lastSendKey);
        
        // 减少发送次数计数（验证成功的验证码不计入发送次数限制）
        String hourCountKey = rateLimitKey + ":hour_count";
        Object hourCount = redisTemplate.opsForValue().get(hourCountKey);
        if (hourCount != null) {
            try {
                int count = Integer.parseInt(hourCount.toString());
                if (count > 0) {
                    // 减少计数，但不能小于0
                    Long newCount = redisTemplate.opsForValue().decrement(hourCountKey);
                    if (newCount != null && newCount < 0) {
                        // 如果减到负数，重置为0
                        redisTemplate.opsForValue().set(hourCountKey, 0, 1, TimeUnit.HOURS);
                    }
                }
            } catch (NumberFormatException e) {
                // 如果解析失败，忽略错误
            }
        }
    }

    /**
     * 检查验证码是否存在
     * 
     * @param key 验证码的key
     * @return 是否存在
     */
    public boolean hasCode(String key) {
        String redisKey = CODE_KEY_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }
}

