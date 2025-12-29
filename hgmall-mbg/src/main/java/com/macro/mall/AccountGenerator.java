package com.macro.mall;

import com.macro.mall.mapper.RoleMapper;
import com.macro.mall.mapper.UsrMapper;
import com.macro.mall.model.Role;
import com.macro.mall.model.RoleExample;
import com.macro.mall.model.Usr;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

/**
 * 账号生成器
 * 用于生成测试账号：1个管理员、2个商家、2个用户
 * 
 * 使用方法：
 * 1. 确保数据库已经初始化（执行了 init.sql）
 * 2. 确保 generator.properties 中的数据库配置正确
 * 3. 运行此类的主方法
 */
public class AccountGenerator {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        System.out.println("================== 开始生成账号 ==================");
        
        SqlSession sqlSession = null;
        try {
            // 1. 读取数据库配置
            Properties props = new Properties();
            InputStream propsStream = AccountGenerator.class.getClassLoader()
                    .getResourceAsStream("generator.properties");
            if (propsStream == null) {
                System.err.println("错误：无法找到配置文件 generator.properties");
                return;
            }
            props.load(propsStream);
            propsStream.close();

            String driver = props.getProperty("jdbc.driverClass");
            String url = props.getProperty("jdbc.connectionURL");
            String username = props.getProperty("jdbc.userId");
            String password = props.getProperty("jdbc.password");

            // 2. 创建 MyBatis SqlSessionFactory
            String resource = "mybatis-config.xml";
            InputStream inputStream = AccountGenerator.class.getClassLoader()
                    .getResourceAsStream(resource);
            
            SqlSessionFactory sqlSessionFactory;
            if (inputStream != null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
                inputStream.close();
            } else {
                // 如果没有配置文件，使用代码方式配置
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                configuration.setEnvironment(new org.apache.ibatis.mapping.Environment(
                    "development",
                    new org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory(),
                    new org.apache.ibatis.datasource.pooled.PooledDataSource(driver, url, username, password)
                ));
                
                // 手动添加Mapper接口
                configuration.addMapper(RoleMapper.class);
                configuration.addMapper(UsrMapper.class);
                
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            }

            // 3. 创建 SqlSession
            sqlSession = sqlSessionFactory.openSession();

            // 4. 获取 Mapper
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
            UsrMapper usrMapper = sqlSession.getMapper(UsrMapper.class);

            // 5. 生成账号
            generateAccounts(roleMapper, usrMapper);

            // 6. 提交事务
            sqlSession.commit();
            
            System.out.println("\n================== 账号生成完成 ==================");
            
        } catch (Exception e) {
            System.err.println("异常：" + e.getMessage());
            e.printStackTrace();
            if (sqlSession != null) {
                sqlSession.rollback();
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    /**
     * 生成账号
     */
    private static void generateAccounts(RoleMapper roleMapper, UsrMapper usrMapper) {
        LocalDateTime now = LocalDateTime.now();

        // 1. 查询角色
        Role adminRole = getRoleByCode(roleMapper, "ADMIN");
        Role merchantRole = getRoleByCode(roleMapper, "MERCHANT");
        Role userRole = getRoleByCode(roleMapper, "USER");

        if (adminRole == null || merchantRole == null || userRole == null) {
            System.err.println("错误：角色不存在，请先执行 init.sql 初始化角色");
            return;
        }

        // 2. 生成管理员账号
        System.out.println("\n--- 生成管理员账号 ---");
        createUser(usrMapper, "admin", null, null, "admin123", adminRole.getRoleId(), 1, now, "管理员");

        // 3. 生成商家账号
        System.out.println("\n--- 生成商家账号 ---");
        createUser(usrMapper, "merchant1", null, null, "merchant123", merchantRole.getRoleId(), 1, now, "商家1");
        createUser(usrMapper, "merchant2", null, null, "merchant123", merchantRole.getRoleId(), 1, now, "商家2");

        // 4. 生成用户账号
        System.out.println("\n--- 生成用户账号 ---");
        createUser(usrMapper, "user1", null, null, "user123", userRole.getRoleId(), 1, now, "用户1");
        createUser(usrMapper, "user2", null, null, "user123", userRole.getRoleId(), 1, now, "用户2");

        // 5. 查询并显示生成的账号
        System.out.println("\n--- 生成的账号列表 ---");
        listUsers(usrMapper);
    }

    /**
     * 根据角色编码查询角色
     */
    private static Role getRoleByCode(RoleMapper roleMapper, String roleCode) {
        RoleExample example = new RoleExample();
        example.createCriteria().andRoleCodeEqualTo(roleCode);
        List<Role> roles = roleMapper.selectByExample(example);
        return roles.isEmpty() ? null : roles.get(0);
    }

    /**
     * 创建用户
     */
    private static void createUser(UsrMapper usrMapper, String userName, String email, 
                                   String phoneNumber, String rawPassword, Long roleId, 
                                   Integer status, LocalDateTime now, String displayName) {
        try {
            // 检查用户是否已存在（使用Example查询）
            com.macro.mall.model.UsrExample userExample = new com.macro.mall.model.UsrExample();
            userExample.createCriteria().andUserNameEqualTo(userName);
            List<Usr> existingUsers = usrMapper.selectByExample(userExample);
            if (!existingUsers.isEmpty()) {
                System.out.println("  跳过：" + displayName + " (用户名: " + userName + ") 已存在");
                return;
            }

            // 加密密码
            String encodedPassword = passwordEncoder.encode(rawPassword);

            // 创建用户对象
            Usr user = new Usr();
            user.setUserName(userName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(encodedPassword);
            user.setRoleId(roleId);
            user.setStatus(status);
            user.setCreateTime(now);
            user.setUpdateTime(now);

            // 插入用户
            int result = usrMapper.insertSelective(user);
            if (result > 0) {
                System.out.println("  成功：" + displayName + " (用户名: " + userName + ", 密码: " + rawPassword + ")");
            } else {
                System.out.println("  失败：" + displayName + " (用户名: " + userName + ")");
            }
        } catch (Exception e) {
            System.err.println("  创建用户失败：" + displayName + " (用户名: " + userName + "), 错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 列出所有用户
     */
    private static void listUsers(UsrMapper usrMapper) {
        try {
            List<Usr> users = usrMapper.selectByExample(null);
            System.out.println(String.format("%-5s %-15s %-25s %-15s %-10s", 
                "ID", "用户名", "邮箱", "手机号", "角色ID"));
            System.out.println("------------------------------------------------------------");
            for (Usr user : users) {
                System.out.println(String.format("%-5s %-15s %-25s %-15s %-10s",
                    user.getUserId(),
                    user.getUserName() != null ? user.getUserName() : "",
                    user.getEmail() != null ? user.getEmail() : "",
                    user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                    user.getRoleId()
                ));
            }
        } catch (Exception e) {
            System.err.println("查询用户列表失败：" + e.getMessage());
        }
    }
}

