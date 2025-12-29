package com.macro.mall.admin.service;

import com.macro.mall.admin.domain.MerchantListVO;
import com.macro.mall.admin.domain.MerchantVO;
import com.macro.mall.admin.domain.ShopSimpleVO;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员商家管理服务
 */
@Service
public class AdminMerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private UsrMapper usrMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    /**
     * 获取商家列表（分页、筛选）
     */
    public MerchantListVO getMerchantList(Integer status, String keyword, Integer page, Integer size) {
        // 分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        int offset = (page - 1) * size;

        // 构建查询条件
        MerchantExample example = new MerchantExample();
        MerchantExample.Criteria criteria = example.createCriteria();

        if (status != null) {
            criteria.andStatusEqualTo(status);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordPattern = "%" + keyword.trim() + "%";
            example.or().andMerchantNameLike(keywordPattern);
            example.or().andOwnerLike(keywordPattern);
        }

        // 查询总数
        long total = merchantMapper.countByExample(example);

        // 查询列表
        example.setOrderByClause("create_time DESC");
        List<Merchant> merchants = merchantMapper.selectByExample(example);

        // 限制分页（MyBatis Example不支持分页，需要手动处理）
        List<Merchant> pagedMerchants = merchants.stream()
                .skip(offset)
                .limit(size)
                .collect(Collectors.toList());

        // 转换为VO
        List<MerchantVO> merchantVOs = new ArrayList<>();
        for (Merchant merchant : pagedMerchants) {
            merchantVOs.add(convertToMerchantVO(merchant, true));
        }

        MerchantListVO result = new MerchantListVO();
        result.setMerchants(merchantVOs);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);

        return result;
    }

    /**
     * 获取商家详情
     */
    public MerchantVO getMerchantDetail(Long merchantId) {
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        return convertToMerchantVO(merchant, false);
    }

    /**
     * 审核商家申请
     */
    @Transactional
    public void auditMerchant(Long merchantId, String action, String reason, Long adminUserId) {
        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }

        if ("approve".equals(action)) {
            // 同意：设置状态为正常
            merchant.setStatus(1);
            merchant.setUpdateTime(LocalDateTime.now());
            merchantMapper.updateByPrimaryKeySelective(merchant);

            // 记录审计日志
            recordAuditLog(adminUserId, merchantId, "merchant", "audit",
                    String.format("管理员审核商家[%s]申请，审核通过。原因：%s", merchant.getMerchantName(), reason != null ? reason : "无"));
        } else if ("reject".equals(action)) {
            // 拒绝：设置状态为禁用
            merchant.setStatus(0);
            merchant.setUpdateTime(LocalDateTime.now());
            merchantMapper.updateByPrimaryKeySelective(merchant);

            // 记录审计日志
            recordAuditLog(adminUserId, merchantId, "merchant", "audit",
                    String.format("管理员审核商家[%s]申请，审核拒绝。原因：%s", merchant.getMerchantName(), reason != null ? reason : "无"));
        } else {
            throw new RuntimeException("无效的审核操作");
        }
    }

    /**
     * 管理商家状态
     */
    @Transactional
    public void updateMerchantStatus(Long merchantId, Integer status, String reason, Long adminUserId) {
        if (status != 0 && status != 1) {
            throw new RuntimeException("无效的商家状态");
        }

        Merchant merchant = merchantMapper.selectByPrimaryKey(merchantId);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }

        merchant.setStatus(status);
        merchant.setUpdateTime(LocalDateTime.now());
        merchantMapper.updateByPrimaryKeySelective(merchant);

        // 记录审计日志
        String statusText = status == 1 ? "启用" : "禁用";
        recordAuditLog(adminUserId, merchantId, "merchant", "update_status",
                String.format("管理员%s商家[%s]。原因：%s", statusText, merchant.getMerchantName(), reason != null ? reason : "无"));
    }

    /**
     * 转换为MerchantVO
     */
    private MerchantVO convertToMerchantVO(Merchant merchant, boolean simple) {
        MerchantVO vo = new MerchantVO();
        vo.setMerchantId(merchant.getMerchantId());
        vo.setMerchantName(merchant.getMerchantName());
        vo.setOwner(merchant.getOwner());
        vo.setUserId(merchant.getUserId());
        vo.setStatus(merchant.getStatus());
        vo.setStatusText(merchant.getStatus() != null && merchant.getStatus() == 1 ? "正常" : "禁用");
        vo.setCreateTime(merchant.getCreateTime());
        vo.setUpdateTime(merchant.getUpdateTime());

        // 查询用户信息
        // 管理员可以看到完整信息，不进行脱敏
        Usr user = usrMapper.selectByPrimaryKey(merchant.getUserId());
        if (user != null) {
            vo.setUserName(user.getUserName());
            vo.setUserEmail(user.getEmail());
            vo.setUserPhone(user.getPhoneNumber());
        }

        // 查询店铺信息
        if (!simple) {
            ShopExample shopExample = new ShopExample();
            shopExample.createCriteria().andMerchantIdEqualTo(merchant.getMerchantId());
            List<Shop> shops = shopMapper.selectByExample(shopExample);
            vo.setShopCount(shops.size());

            List<ShopSimpleVO> shopVOs = new ArrayList<>();
            for (Shop shop : shops) {
                ShopSimpleVO shopVO = new ShopSimpleVO();
                shopVO.setShopId(shop.getShopId());
                shopVO.setName(shop.getName());
                shopVO.setStatus(shop.getStatus());
                shopVO.setStatusText(getShopStatusText(shop.getStatus()));
                shopVO.setCreateTime(shop.getCreateTime());
                shopVOs.add(shopVO);
            }
            vo.setShops(shopVOs);
        } else {
            // 简单模式只查询店铺数量
            ShopExample shopExample = new ShopExample();
            shopExample.createCriteria().andMerchantIdEqualTo(merchant.getMerchantId());
            long shopCount = shopMapper.countByExample(shopExample);
            vo.setShopCount((int) shopCount);
        }

        return vo;
    }

    /**
     * 获取店铺状态文本
     */
    private String getShopStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "正常营业";
            case 2:
                return "平台冻结";
            case 3:
                return "已关闭";
            default:
                return "未知";
        }
    }


    /**
     * 记录审计日志
     */
    private void recordAuditLog(Long adminUserId, Long targetId, String targetType,
                                String operationType, String operationDesc) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminUsrId(adminUserId);
        auditLog.setTargetId(targetId);
        auditLog.setTargetType(targetType);
        auditLog.setOperationType(operationType);
        auditLog.setOperationDesc(operationDesc);
        auditLog.setCreateTime(LocalDateTime.now());
        auditLogMapper.insertSelective(auditLog);
    }
}

