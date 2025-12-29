package com.macro.mall.common.service;

import com.macro.mall.common.domain.*;
import com.macro.mall.common.util.JwtUtil;
import com.macro.mall.common.util.PasswordUtil;
import com.macro.mall.mapper.MerchantMapper;
import com.macro.mall.mapper.RoleMapper;
import com.macro.mall.mapper.UsrMapper;
import com.macro.mall.model.Merchant;
import com.macro.mall.model.MerchantExample;
import com.macro.mall.model.Role;
import com.macro.mall.model.RoleExample;
import com.macro.mall.model.Usr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 认证服务
 * 提供管理员和用户的登录、密码管理功能
 */
@Service
public class AuthService {

    @Autowired
    private UsrMapper usrMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private VerificationCodeSenderService verificationCodeSenderService;

    /**
     * 管理员角色编码
     */
    private static final String ADMIN_ROLE_CODE = "ADMIN";

    /**
     * 普通用户角色编码
     */
    private static final String USER_ROLE_CODE = "USER";

    /**
     * 商家角色编码
     */
    private static final String MERCHANT_ROLE_CODE = "MERCHANT";

    /**
     * 邮箱格式正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /**
     * 手机号格式正则表达式（11位数字，1开头）
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 根据标识符（用户名/邮箱/手机号）查找用户
     * 
     * @param identifier 标识符（用户名、邮箱或手机号）
     * @return 用户信息，如果不存在返回null
     */
    private Usr findUsrByIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }
        
        // 判断是邮箱
        if (EMAIL_PATTERN.matcher(identifier).matches()) {
            return usrMapper.selectByEmail(identifier);
        }
        
        // 判断是手机号
        if (PHONE_PATTERN.matcher(identifier).matches()) {
            return usrMapper.selectByPhoneNumber(identifier);
        }
        
        // 默认按用户名查询
        return usrMapper.selectByUserName(identifier);
    }

    /**
     * 构建登录结果
     * 
     * @param user 用户信息
     * @param role 角色信息
     * @return 登录结果
     */
    private LoginResult buildLoginResult(Usr user, Role role) {
        LoginResult result = new LoginResult();
        result.setUserId(user.getUserId());
        result.setUsername(user.getUserName());
        result.setEmail(user.getEmail());
        result.setRoleId(user.getRoleId());
        result.setRoleCode(role.getRoleCode());
        
        // 生成 JWT Token
        String token = JwtUtil.generateToken(
            user.getUserId(),
            user.getUserName(),
            role.getRoleCode()
        );
        result.setToken(token);
        
        return result;
    }

    /**
     * 验证用户和密码，并返回用户和角色信息
     * 
     * @param identifier 标识符（用户名、邮箱或手机号）
     * @param password 密码
     * @return 用户和角色的数组 [Usr, Role]
     * @throws RuntimeException 验证失败时抛出异常
     */
    private Object[] validateUsrAndPassword(String identifier, String password) {
        // 查询用户
        Usr user = findUsrByIdentifier(identifier);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 查询角色信息
        Role role = roleMapper.selectByPrimaryKey(user.getRoleId());
        if (role == null) {
            throw new RuntimeException("用户角色不存在");
        }

        return new Object[]{user, role};
    }

    /**
     * 验证用户是否为管理员角色
     * 
     * @param role 角色信息
     * @param errorMessage 如果不是管理员时的错误消息
     * @throws RuntimeException 如果不是管理员角色时抛出异常
     */
    private void validateAdminRole(Role role, String errorMessage) {
        if (!ADMIN_ROLE_CODE.equals(role.getRoleCode())) {
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * 验证用户是否为普通用户角色（非管理员）
     * 
     * @param role 角色信息
     * @throws RuntimeException 如果是管理员角色时抛出异常
     */
    private void validateUsrRole(Role role) {
        if (ADMIN_ROLE_CODE.equals(role.getRoleCode())) {
            throw new RuntimeException("管理员请使用管理员登录接口");
        }
    }

    /**
     * 管理员密码登录
     * 支持用户名/邮箱/手机号+密码登录
     * 
     * @param identifier 标识符（用户名、邮箱或手机号）
     * @param password 密码
     * @return 登录结果
     * @throws RuntimeException 登录失败时抛出异常
     */
    public LoginResult adminLoginByPassword(String identifier, String password) {
        // 验证用户和密码
        Object[] userAndRole = validateUsrAndPassword(identifier, password);
        Usr user = (Usr) userAndRole[0];
        Role role = (Role) userAndRole[1];

        // 验证是否为管理员角色（必须验证，防止普通用户登录管理员系统）
        validateAdminRole(role, "该用户不是管理员");

        // 构建登录结果
        return buildLoginResult(user, role);
    }

    /**
     * 管理员验证码登录
     * 支持邮箱/手机号+验证码登录
     * 
     * @param contact 联系方式（邮箱或手机号）
     * @param code 验证码
     * @return 登录结果
     * @throws RuntimeException 登录失败时抛出异常
     */
    public LoginResult adminLoginByCode(String contact, String code) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(contact, code)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 判断是邮箱还是手机号，并查询用户
        Usr user;
        if (EMAIL_PATTERN.matcher(contact).matches()) {
            user = usrMapper.selectByEmail(contact);
        } else if (PHONE_PATTERN.matcher(contact).matches()) {
            user = usrMapper.selectByPhoneNumber(contact);
        } else {
            throw new RuntimeException("请输入正确的邮箱或手机号");
        }

        if (user == null) {
            // 如果邮箱/手机号为空，返回用户名或密码错误消息（按需求）
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 查询角色信息
        Role role = roleMapper.selectByPrimaryKey(user.getRoleId());
        if (role == null) {
            throw new RuntimeException("用户角色不存在");
        }

        // 验证是否为管理员角色（必须验证，防止普通用户登录管理员系统）
        validateAdminRole(role, "该用户不是管理员");

        // 删除验证码（验证成功后）
        verificationCodeService.deleteCode(contact);

        // 构建登录结果
        return buildLoginResult(user, role);
    }

    /**
     * 用户密码登录
     * 支持用户名/邮箱/手机号+密码登录
     * 
     * @param identifier 标识符（用户名、邮箱或手机号）
     * @param password 密码
     * @return 登录结果
     * @throws RuntimeException 登录失败时抛出异常
     */
    public LoginResult userLoginByPassword(String identifier, String password) {
        // 验证用户和密码
        Object[] userAndRole = validateUsrAndPassword(identifier, password);
        Usr user = (Usr) userAndRole[0];
        Role role = (Role) userAndRole[1];

        // 验证不是管理员角色（普通用户登录）
        validateUsrRole(role);

        // 构建登录结果
        return buildLoginResult(user, role);
    }

    /**
     * 根据联系方式（邮箱或手机号）查找用户
     * 
     * @param contact 联系方式（邮箱或手机号）
     * @return 用户信息，如果不存在返回null
     */
    private Usr findUsrByContact(String contact) {
        if (EMAIL_PATTERN.matcher(contact).matches()) {
            return usrMapper.selectByEmail(contact);
        } else if (PHONE_PATTERN.matcher(contact).matches()) {
            return usrMapper.selectByPhoneNumber(contact);
        }
        return null;
    }

    /**
     * 发送用户登录验证码
     * 发送前验证用户是否存在
     * 
     * @param contact 联系方式（邮箱或手机号）
     * @return 发送结果消息
     * @throws RuntimeException 用户不存在或其他错误时抛出异常
     */
    public String sendUserLoginCode(String contact) {
        // 验证用户是否存在
        Usr user = findUsrByContact(contact);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 发送验证码
        return verificationCodeSenderService.sendCode(contact);
    }

    /**
     * 发送管理员登录验证码
     * 发送前验证用户是否存在且为管理员
     * 
     * @param contact 联系方式（邮箱或手机号）
     * @return 发送结果消息
     * @throws RuntimeException 用户不存在或不是管理员时抛出异常
     */
    public String sendAdminLoginCode(String contact) {
        // 验证用户是否存在
        Usr user = findUsrByContact(contact);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 查询角色信息
        Role role = roleMapper.selectByPrimaryKey(user.getRoleId());
        if (role == null) {
            throw new RuntimeException("用户角色不存在");
        }

        // 验证是否为管理员角色
        validateAdminRole(role, "该用户不是管理员");

        // 发送验证码
        return verificationCodeSenderService.sendCode(contact);
    }

    /**
     * 发送通用验证码（用于用户操作，如忘记密码、修改信息等）
     * 发送前验证用户是否存在
     * 
     * @param contact 联系方式（邮箱或手机号）
     * @return 发送结果消息
     * @throws RuntimeException 用户不存在时抛出异常
     */
    public String sendUserCode(String contact) {
        // 验证用户是否存在
        Usr user = findUsrByContact(contact);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 发送验证码
        return verificationCodeSenderService.sendCode(contact);
    }

    /**
     * 发送管理员通用验证码（用于管理员操作）
     * 发送前验证用户是否存在且为管理员
     * 
     * @param contact 联系方式（邮箱或手机号）
     * @return 发送结果消息
     * @throws RuntimeException 用户不存在或不是管理员时抛出异常
     */
    public String sendAdminCode(String contact) {
        // 验证用户是否存在
        Usr user = findUsrByContact(contact);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 查询角色信息
        Role role = roleMapper.selectByPrimaryKey(user.getRoleId());
        if (role == null) {
            throw new RuntimeException("用户角色不存在");
        }

        // 验证是否为管理员角色
        validateAdminRole(role, "该用户不是管理员");

        // 发送验证码
        return verificationCodeSenderService.sendCode(contact);
    }

    /**
     * 发送注册验证码
     * 发送前验证用户不存在（仅用于注册）
     * 
     * @param contact 联系方式（邮箱或手机号）
     * @return 发送结果消息
     * @throws RuntimeException 用户已存在时抛出异常
     */
    public String sendRegisterCode(String contact) {
        // 验证用户是否不存在
        Usr user = findUsrByContact(contact);
        if (user != null) {
            throw new RuntimeException("该邮箱或手机号已被注册");
        }

        // 发送验证码
        return verificationCodeSenderService.sendCode(contact);
    }

    /**
     * 用户验证码登录
     * 支持邮箱/手机号+验证码登录
     * 
     * @param contact 联系方式（邮箱或手机号）
     * @param code 验证码
     * @return 登录结果
     * @throws RuntimeException 登录失败时抛出异常
     */
    public LoginResult userCodeLogin(String contact, String code) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(contact, code)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 判断是邮箱还是手机号，并查询用户
        Usr user;
        if (EMAIL_PATTERN.matcher(contact).matches()) {
            user = usrMapper.selectByEmail(contact);
        } else if (PHONE_PATTERN.matcher(contact).matches()) {
            user = usrMapper.selectByPhoneNumber(contact);
        } else {
            throw new RuntimeException("请输入正确的邮箱或手机号");
        }

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 查询角色信息
        Role role = roleMapper.selectByPrimaryKey(user.getRoleId());
        if (role == null) {
            throw new RuntimeException("用户角色不存在");
        }

        // 验证不是管理员角色
        validateUsrRole(role);

        // 删除验证码（验证成功后）
        verificationCodeService.deleteCode(contact);

        // 构建登录结果
        return buildLoginResult(user, role);
    }

    /**
     * 忘记密码（重置密码）
     * 
     * @param email 邮箱
     * @param code 验证码
     * @param newPassword 新密码
     * @throws RuntimeException 操作失败时抛出异常
     */
    public void forgotPassword(String email, String code, String newPassword) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(email, code)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 查询用户
        Usr user = usrMapper.selectByEmail(email);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 加密新密码
        String encodedPassword = PasswordUtil.encode(newPassword);

        // 更新密码
        user.setPassword(encodedPassword);
        user.setUpdateTime(LocalDateTime.now());
        usrMapper.updateByPrimaryKeySelective(user);

        // 删除验证码
        verificationCodeService.deleteCode(email);
    }

    /**
     * 修改密码（需要验证码）
     * 
     * @param email 邮箱
     * @param code 验证码
     * @param newPassword 新密码
     * @throws RuntimeException 操作失败时抛出异常
     */
    public void changePassword(String email, String code, String newPassword) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(email, code)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 查询用户
        Usr user = usrMapper.selectByEmail(email);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 加密新密码
        String encodedPassword = PasswordUtil.encode(newPassword);

        // 更新密码
        user.setPassword(encodedPassword);
        user.setUpdateTime(LocalDateTime.now());
        usrMapper.updateByPrimaryKeySelective(user);

        // 删除验证码
        verificationCodeService.deleteCode(email);
    }

    /**
     * 用户注册
     * 
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @param code 验证码
     * @return 登录结果
     * @throws RuntimeException 注册失败时抛出异常
     */
    public LoginResult register(String username, String password, String email, String code) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(email, code)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 检查用户名是否已存在
        Usr existingUsr = usrMapper.selectByUserName(username);
        if (existingUsr != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        Usr existingEmail = usrMapper.selectByEmail(email);
        if (existingEmail != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 查询普通用户角色
        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andRoleCodeEqualTo(USER_ROLE_CODE);
        List<Role> roles = roleMapper.selectByExample(roleExample);
        if (roles == null || roles.isEmpty()) {
            throw new RuntimeException("用户角色不存在，请联系管理员");
        }
        Role userRole = roles.get(0);

        // 加密密码
        String encodedPassword = PasswordUtil.encode(password);

        // 创建用户
        Usr user = new Usr();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRoleId(userRole.getRoleId());
        user.setStatus(1); // 默认状态为正常
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        usrMapper.insertSelective(user);

        // 删除验证码（注册成功后）
        verificationCodeService.deleteCode(email);

        // 构建登录结果
        LoginResult result = new LoginResult();
        result.setUserId(user.getUserId());
        result.setUsername(user.getUserName());
        result.setEmail(user.getEmail());
        result.setRoleId(user.getRoleId());
        result.setRoleCode(userRole.getRoleCode());
        
        // 生成 JWT Token
        String token = JwtUtil.generateToken(
            user.getUserId(),
            user.getUserName(),
            userRole.getRoleCode()
        );
        result.setToken(token);
        
        return result;
    }

    /**
     * 用户注册为商家
     * 
     * @param userId 用户ID
     * @param merchantName 商家名称
     * @param owner 负责人姓名（可选）
     * @throws RuntimeException 注册失败时抛出异常
     */
    public void registerAsMerchant(Long userId, String merchantName, String owner) {
        // 验证用户是否存在
        Usr user = usrMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }

        // 查询用户当前角色
        Role userRole = roleMapper.selectByPrimaryKey(user.getRoleId());
        if (userRole == null) {
            throw new RuntimeException("用户角色不存在");
        }

        // 检查用户是否为管理员，管理员不能注册为商家
        if (ADMIN_ROLE_CODE.equals(userRole.getRoleCode())) {
            throw new RuntimeException("管理员不能注册为商家");
        }

        // 检查用户是否已经是商家
        MerchantExample merchantExample = new MerchantExample();
        merchantExample.createCriteria().andUserIdEqualTo(userId);
        List<Merchant> existingMerchants = merchantMapper.selectByExample(merchantExample);
        if (existingMerchants != null && !existingMerchants.isEmpty()) {
            throw new RuntimeException("您已经是商家，无需重复注册");
        }

        // 检查商家名称是否已存在
        MerchantExample nameExample = new MerchantExample();
        nameExample.createCriteria().andMerchantNameEqualTo(merchantName);
        List<Merchant> merchantsWithSameName = merchantMapper.selectByExample(nameExample);
        if (merchantsWithSameName != null && !merchantsWithSameName.isEmpty()) {
            throw new RuntimeException("商家名称已被使用，请更换其他名称");
        }

        // 查询商家角色
        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andRoleCodeEqualTo(MERCHANT_ROLE_CODE);
        List<Role> merchantRoles = roleMapper.selectByExample(roleExample);
        if (merchantRoles == null || merchantRoles.isEmpty()) {
            throw new RuntimeException("商家角色不存在，请联系管理员");
        }
        Role merchantRole = merchantRoles.get(0);

        // 验证商家角色ID是否为3
        if (merchantRole.getRoleId() == null || !merchantRole.getRoleId().equals(3L)) {
            throw new RuntimeException("商家角色配置错误，角色ID应为3，请联系管理员");
        }

        // 创建商家记录
        Merchant merchant = new Merchant();
        merchant.setMerchantName(merchantName);
        merchant.setOwner(owner);
        merchant.setUserId(userId);
        merchant.setStatus(1); // 状态：1-正常（已通过审核）
        merchant.setCreateTime(LocalDateTime.now());
        merchant.setUpdateTime(LocalDateTime.now());
        merchantMapper.insertSelective(merchant);

        // 更新用户角色为商家角色（role_id = 3）
        user.setRoleId(merchantRole.getRoleId());
        user.setUpdateTime(LocalDateTime.now());
        usrMapper.updateByPrimaryKeySelective(user);
    }
}

