package com.macro.mall.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务
 * 用于发送验证码邮件
 */
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:}")
    private String fromEmail;

    /**
     * 发送验证码邮件
     * 
     * @param to 收件人邮箱
     * @param code 验证码
     * @return 是否发送成功
     */
    public boolean sendVerificationCode(String to, String code) {
        if (mailSender == null) {
            // 如果邮件服务未配置，返回false
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("HuiGoMall 验证码");
            message.setText("您的验证码是: " + code + "\n验证码有效期为10分钟，请勿泄露给他人。");
            
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("发送邮件失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 检查邮件服务是否可用
     * 
     * @return 是否可用
     */
    public boolean isAvailable() {
        return mailSender != null && fromEmail != null && !fromEmail.isEmpty();
    }

    /**
     * 发送订单邮件
     * 
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    public boolean sendOrderEmail(String to, String subject, String content) {
        if (mailSender == null) {
            // 开发模式，打印到控制台
            System.out.println("=== 订单邮件通知（开发模式）===");
            System.out.println("收件人: " + to);
            System.out.println("主题: " + subject);
            System.out.println("内容:\n" + content);
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("发送订单邮件失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

