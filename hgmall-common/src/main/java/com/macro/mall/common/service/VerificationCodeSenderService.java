package com.macro.mall.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 验证码发送服务
 * 负责生成、存储和发送验证码（邮箱/短信）
 * 将验证码发送逻辑封装到公共模块，提高代码复用性
 */
@Service
public class VerificationCodeSenderService {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private EmailService emailService;

    /**
     * 邮箱格式正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /**
     * 手机号格式正则表达式（11位数字，1开头）
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 发送验证码
     * 支持手机号和邮箱
     * 
     * @param contact 联系方式（邮箱或手机号）
     * @return 发送结果消息
     */
    public String sendCode(String contact) {
        // 生成并存储验证码
        String code = verificationCodeService.generateAndStoreCode(contact);
        
        // 判断是邮箱还是手机号
        boolean isEmail = EMAIL_PATTERN.matcher(contact).matches();
        
        if (isEmail) {
            // 发送邮件验证码
            if (emailService.isAvailable()) {
                boolean sent = emailService.sendVerificationCode(contact, code);
                if (sent) {
                    return "验证码已发送到您的邮箱，请查收";
                } else {
                    // 邮件发送失败，但验证码已生成（开发环境可以查看日志）
                    System.err.println("邮件发送失败，验证码: " + code);
                    throw new RuntimeException("邮件发送失败，请检查邮件配置");
                }
            } else {
                // 邮件服务未配置，仅打印到控制台（开发环境）
                System.out.println("邮件服务未配置，验证码已生成（仅用于开发测试）");
                System.out.println("发送到: " + contact + ", 验证码: " + code);
                return "验证码已生成（开发模式，请查看控制台）";
            }
        } else {
            // 手机号验证码（TODO: 实现短信发送）
            System.out.println("短信服务未实现，验证码已生成（仅用于开发测试）");
            System.out.println("发送到: " + contact + ", 验证码: " + code);
            return "验证码已生成（开发模式，请查看控制台）";
        }
    }

    /**
     * 判断是否为邮箱格式
     * 
     * @param contact 联系方式
     * @return 是否为邮箱
     */
    public boolean isEmail(String contact) {
        return EMAIL_PATTERN.matcher(contact).matches();
    }

    /**
     * 判断是否为手机号格式
     * 
     * @param contact 联系方式
     * @return 是否为手机号
     */
    public boolean isPhone(String contact) {
        return PHONE_PATTERN.matcher(contact).matches();
    }
}

