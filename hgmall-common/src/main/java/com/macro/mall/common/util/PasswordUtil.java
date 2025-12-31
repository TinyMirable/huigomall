package com.macro.mall.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码工具类
 * 提供密码加密和验证功能
 */
public class PasswordUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码
     * 
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 主方法：用于生成加密密码
     * 使用方法：java PasswordUtil <原始密码>
     * 例如：java PasswordUtil admin123
     * 
     * @param args 命令行参数，第一个参数为要加密的原始密码
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("使用方法: java PasswordUtil <原始密码>");
            System.out.println("示例: java PasswordUtil admin123");
            System.out.println("\n或者直接运行，将使用默认密码 'admin123'");
            System.out.println("==========================================");
            String defaultPassword = "user123";
            String encoded = encode(defaultPassword);
            System.out.println("原始密码: " + defaultPassword);
            System.out.println("加密后的密码: " + encoded);
            return;
        }

        String rawPassword = args[0];
        String encodedPassword = encode(rawPassword);
        
        System.out.println("==========================================");
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后的密码: " + encodedPassword);
        System.out.println("==========================================");
    }
}









