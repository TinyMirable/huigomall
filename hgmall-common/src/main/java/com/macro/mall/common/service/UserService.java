package com.macro.mall.common.service;

import com.macro.mall.common.domain.UserSummaryVO;
import com.macro.mall.mapper.MerchantMapper;
import com.macro.mall.mapper.OrderMapper;
import com.macro.mall.mapper.RoleMapper;
import com.macro.mall.mapper.ShopMapper;
import com.macro.mall.mapper.UsrMapper;
import com.macro.mall.model.Merchant;
import com.macro.mall.model.MerchantExample;
import com.macro.mall.model.OrderExample;
import com.macro.mall.model.Role;
import com.macro.mall.model.ShopExample;
import com.macro.mall.model.Usr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务
 */
@Service
public class UserService {

    @Autowired
    private UsrMapper usrMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private VerificationCodeSenderService verificationCodeSenderService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    /**
     * Redis Key 前缀
     */
    private static final String USER_SUMMARY_KEY_PREFIX = "user:summary:";

    /**
     * 缓存TTL（秒）
     */
    private static final long USER_SUMMARY_TTL_MIN = 30;
    private static final long USER_SUMMARY_TTL_MAX = 60;

    /**
     * 获取用户概要信息
     * 
     * @param userId 用户ID
     * @return 用户概要信息
     */
    public UserSummaryVO getUserSummary(Long userId) {
        // 构建缓存Key
        String cacheKey = USER_SUMMARY_KEY_PREFIX + userId;

        // 尝试从缓存获取
        UserSummaryVO cached = (UserSummaryVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 查询用户信息
        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return null;
        }

        // 查询角色信息
        Role role = roleMapper.selectByPrimaryKey(user.getRoleId());
        String roleCode = role != null ? role.getRoleCode() : "USER";

        // 查询商家信息
        MerchantExample merchantExample = new MerchantExample();
        merchantExample.createCriteria().andUserIdEqualTo(userId);
        java.util.List<Merchant> merchants = merchantMapper.selectByExample(merchantExample);
        
        String merchantStatus = "NONE";
        Integer shopCount = 0;
        Long merchantId = null;
        
        if (merchants != null && !merchants.isEmpty()) {
            Merchant merchant = merchants.get(0);
            merchantId = merchant.getMerchantId();
            
            // 根据商家状态确定merchantStatus
            if (merchant.getStatus() == null || merchant.getStatus() == 0) {
                merchantStatus = "REJECTED";
            } else if (merchant.getStatus() == 1) {
                merchantStatus = "APPROVED";
            } else {
                merchantStatus = "PENDING";
            }

            // 统计店铺数量
            ShopExample shopExample = new ShopExample();
            shopExample.createCriteria().andMerchantIdEqualTo(merchantId);
            shopCount = (int) shopMapper.countByExample(shopExample);
        }

        // 统计订单数量
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andUserIdEqualTo(userId);
        Integer orderCount = (int) orderMapper.countByExample(orderExample);

        // 手机号脱敏
        String maskedPhoneNumber = maskPhoneNumber(user.getPhoneNumber());

        // 构建返回结果
        UserSummaryVO summary = new UserSummaryVO();
        summary.setUserId(user.getUserId());
        summary.setUserName(user.getUserName());
        summary.setEmail(user.getEmail());
        summary.setPhoneNumber(maskedPhoneNumber);
        summary.setRole(roleCode);
        summary.setMerchantStatus(merchantStatus);
        summary.setMerchantId(merchantId);
        summary.setShopCount(shopCount);
        summary.setOrderCount(orderCount);

        // 存入缓存（随机TTL，30-60秒）
        long ttl = USER_SUMMARY_TTL_MIN + new Random().nextInt((int) (USER_SUMMARY_TTL_MAX - USER_SUMMARY_TTL_MIN + 1));
        redisTemplate.opsForValue().set(cacheKey, summary, ttl, TimeUnit.SECONDS);

        return summary;
    }

    /**
     * 手机号脱敏
     * 将手机号中间4位替换为*，例如：138****1234
     * 
     * @param phoneNumber 原始手机号
     * @return 脱敏后的手机号
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            return phoneNumber;
        }
        // 保留前3位和后4位，中间4位替换为*
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
    }

    /**
     * 修改用户名
     * 
     * @param userId 用户ID
     * @param newUsername 新用户名
     * @return 更新后的用户名
     * @throws RuntimeException 修改失败时抛出异常
     */
    public String updateUsername(Long userId, String newUsername) {
        // 验证用户是否存在
        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 检查新用户名是否与当前用户名相同
        if (newUsername.equals(user.getUserName())) {
            throw new RuntimeException("新用户名与当前用户名相同");
        }

        // 检查新用户名是否已被其他用户使用
        Usr existingUser = usrMapper.selectByUserName(newUsername);
        if (existingUser != null && !existingUser.getUserId().equals(userId)) {
            throw new RuntimeException("用户名已被使用");
        }

        // 更新用户名
        user.setUserName(newUsername);
        user.setUpdateTime(LocalDateTime.now());
        usrMapper.updateByPrimaryKeySelective(user);

        // 清除用户概要信息缓存
        String cacheKey = USER_SUMMARY_KEY_PREFIX + userId;
        redisTemplate.delete(cacheKey);

        // 返回更新后的用户名
        return newUsername;
    }

    /**
     * 发送邮箱验证码（用于修改邮箱）
     * 
     * @param email 新邮箱地址
     * @throws RuntimeException 发送失败时抛出异常
     */
    public void sendEmailCodeForUpdate(String email) {
        // 检查邮箱是否已被其他用户使用
        Usr existingUser = usrMapper.selectByEmail(email);
        if (existingUser != null) {
            throw new RuntimeException("该邮箱已被使用");
        }

        // 发送验证码到新邮箱
        verificationCodeSenderService.sendCode(email);
    }

    /**
     * 修改邮箱
     * 
     * @param userId 用户ID
     * @param newEmail 新邮箱
     * @param code 验证码（管理员不需要）
     * @param isAdmin 是否为管理员
     * @return 更新后的邮箱
     * @throws RuntimeException 修改失败时抛出异常
     */
    @Transactional
    public String updateEmail(Long userId, String newEmail, String code, boolean isAdmin) {
        // 验证用户是否存在
        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 检查新邮箱是否与当前邮箱相同
        if (user.getEmail() != null && newEmail.equals(user.getEmail())) {
            throw new RuntimeException("新邮箱与当前邮箱相同");
        }

        // 检查新邮箱是否已被其他用户使用
        Usr existingUser = usrMapper.selectByEmail(newEmail);
        if (existingUser != null && !existingUser.getUserId().equals(userId)) {
            throw new RuntimeException("该邮箱已被使用");
        }

        // 如果不是管理员，需要验证验证码
        if (!isAdmin) {
            if (code == null || code.trim().isEmpty()) {
                throw new RuntimeException("验证码不能为空");
            }
            // 验证新邮箱的验证码
            if (!verificationCodeService.verifyCode(newEmail, code)) {
                throw new RuntimeException("验证码错误或已过期");
            }
            // 验证成功后删除验证码
            verificationCodeService.deleteCode(newEmail);
        }

        // 更新邮箱
        user.setEmail(newEmail);
        user.setUpdateTime(LocalDateTime.now());
        usrMapper.updateByPrimaryKeySelective(user);

        // 清除用户概要信息缓存
        String cacheKey = USER_SUMMARY_KEY_PREFIX + userId;
        redisTemplate.delete(cacheKey);

        // 返回更新后的邮箱
        return newEmail;
    }

    /**
     * 修改手机号
     * 
     * @param userId 用户ID
     * @param newPhoneNumber 新手机号
     * @param code 验证码（管理员不需要）
     * @param isAdmin 是否为管理员
     * @return 更新后的手机号
     * @throws RuntimeException 修改失败时抛出异常
     */
    @Transactional
    public String updatePhoneNumber(Long userId, String newPhoneNumber, String code, boolean isAdmin) {
        // 验证用户是否存在
        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 检查新手机号是否与当前手机号相同
        if (newPhoneNumber.equals(user.getPhoneNumber())) {
            throw new RuntimeException("新手机号与当前手机号相同");
        }

        // 检查新手机号是否已被其他用户使用
        Usr existingUser = usrMapper.selectByPhoneNumber(newPhoneNumber);
        if (existingUser != null && !existingUser.getUserId().equals(userId)) {
            throw new RuntimeException("该手机号已被使用");
        }

        // 如果不是管理员，需要验证邮箱验证码
        if (!isAdmin) {
            // 检查用户是否有邮箱
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                throw new RuntimeException("修改手机号需要先绑定邮箱，请先修改邮箱");
            }

            if (code == null || code.trim().isEmpty()) {
                throw new RuntimeException("验证码不能为空");
            }
            // 验证邮箱验证码（使用用户当前邮箱）
            if (!verificationCodeService.verifyCode(user.getEmail(), code)) {
                throw new RuntimeException("验证码错误或已过期");
            }
            // 验证成功后删除验证码
            verificationCodeService.deleteCode(user.getEmail());
        }

        // 更新手机号
        user.setPhoneNumber(newPhoneNumber);
        user.setUpdateTime(LocalDateTime.now());
        usrMapper.updateByPrimaryKeySelective(user);

        // 清除用户概要信息缓存
        String cacheKey = USER_SUMMARY_KEY_PREFIX + userId;
        redisTemplate.delete(cacheKey);

        // 返回更新后的手机号
        return newPhoneNumber;
    }
}

