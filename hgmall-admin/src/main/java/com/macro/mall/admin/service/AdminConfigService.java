package com.macro.mall.admin.service;

import com.macro.mall.admin.domain.SystemConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 管理员系统配置服务
 */
@Service
public class AdminConfigService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String MERCHANT_AUDIT_KEY = "config:merchant:audit:enabled";
    private static final String SHOP_AUDIT_KEY = "config:shop:audit:enabled";
    private static final Boolean DEFAULT_AUDIT_ENABLED = false; // 默认不审核

    /**
     * 获取系统配置
     */
    public SystemConfigVO getSystemConfig() {
        SystemConfigVO config = new SystemConfigVO();

        // 获取商家审核开关
        Object merchantAudit = redisTemplate.opsForValue().get(MERCHANT_AUDIT_KEY);
        config.setMerchantAuditEnabled(merchantAudit != null ? (Boolean) merchantAudit : DEFAULT_AUDIT_ENABLED);

        // 获取店铺审核开关
        Object shopAudit = redisTemplate.opsForValue().get(SHOP_AUDIT_KEY);
        config.setShopAuditEnabled(shopAudit != null ? (Boolean) shopAudit : DEFAULT_AUDIT_ENABLED);

        return config;
    }

    /**
     * 更新商家审核开关
     */
    public void updateMerchantAuditEnabled(Boolean enabled) {
        if (enabled == null) {
            enabled = DEFAULT_AUDIT_ENABLED;
        }
        redisTemplate.opsForValue().set(MERCHANT_AUDIT_KEY, enabled);
    }

    /**
     * 更新店铺审核开关
     */
    public void updateShopAuditEnabled(Boolean enabled) {
        if (enabled == null) {
            enabled = DEFAULT_AUDIT_ENABLED;
        }
        redisTemplate.opsForValue().set(SHOP_AUDIT_KEY, enabled);
    }

    /**
     * 检查商家审核是否开启
     */
    public boolean isMerchantAuditEnabled() {
        Object value = redisTemplate.opsForValue().get(MERCHANT_AUDIT_KEY);
        return value != null ? (Boolean) value : DEFAULT_AUDIT_ENABLED;
    }

    /**
     * 检查店铺审核是否开启
     */
    public boolean isShopAuditEnabled() {
        Object value = redisTemplate.opsForValue().get(SHOP_AUDIT_KEY);
        return value != null ? (Boolean) value : DEFAULT_AUDIT_ENABLED;
    }
}

