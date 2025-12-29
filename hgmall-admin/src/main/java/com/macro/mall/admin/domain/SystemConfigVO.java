package com.macro.mall.admin.domain;

/**
 * 系统配置VO
 */
public class SystemConfigVO {
    private Boolean merchantAuditEnabled;
    private Boolean shopAuditEnabled;

    public Boolean getMerchantAuditEnabled() {
        return merchantAuditEnabled;
    }

    public void setMerchantAuditEnabled(Boolean merchantAuditEnabled) {
        this.merchantAuditEnabled = merchantAuditEnabled;
    }

    public Boolean getShopAuditEnabled() {
        return shopAuditEnabled;
    }

    public void setShopAuditEnabled(Boolean shopAuditEnabled) {
        this.shopAuditEnabled = shopAuditEnabled;
    }
}



