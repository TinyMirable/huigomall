package com.macro.mall.portal.service;

import com.macro.mall.common.service.VerificationCodeService;
import com.macro.mall.mapper.UsrMapper;
import com.macro.mall.model.Usr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 敏感操作验证服务
 * 用于验证邮箱验证码，确保敏感操作的安全性
 */
@Service
public class SensitiveOperationService {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private UsrMapper usrMapper;

    /**
     * 验证邮箱验证码（用于敏感操作）
     * 
     * @param userId 用户ID
     * @param code 验证码
     * @throws RuntimeException 验证失败时抛出异常
     */
    public void verifyEmailCode(Long userId, String code) {
        // 查询用户信息
        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户邮箱
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RuntimeException("用户未绑定邮箱，无法进行敏感操作");
        }

        // 验证验证码
        if (!verificationCodeService.verifyCode(user.getEmail(), code)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 验证成功后删除验证码
        verificationCodeService.deleteCode(user.getEmail());
        
        // 清除发送频率限制，允许立即发送下一个验证码
        verificationCodeService.clearRateLimit(user.getEmail());
    }
}


