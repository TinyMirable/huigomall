# 邮件服务配置说明

## 当前状态

### ✅ 服务启动配置
- **Portal 服务**：端口 `8080`
- **Admin 服务**：端口 `8081`
- 两个服务可以同时启动，不会冲突

### ⚠️ 邮件服务状态

**当前邮件服务未完全配置，验证码发送功能处于开发模式。**

#### 开发模式（当前）
- 验证码会生成并存储到 Redis
- 验证码会打印到控制台（仅用于开发测试）
- **不会实际发送邮件**

#### 生产模式（需要配置）
- 需要配置 SMTP 邮件服务器
- 验证码会通过邮件实际发送给用户

## 邮件服务配置步骤

### 1. 选择邮件服务提供商

推荐使用以下服务：

#### QQ 邮箱（推荐，免费）
- SMTP 服务器：`smtp.qq.com`
- 端口：`587` (TLS) 或 `465` (SSL)
- 需要开启 SMTP 服务并获取授权码

#### 163 邮箱（免费）
- SMTP 服务器：`smtp.163.com`
- 端口：`587` (TLS) 或 `465` (SSL)
- 需要开启 SMTP 服务并获取授权码

#### Gmail（需要科学上网）
- SMTP 服务器：`smtp.gmail.com`
- 端口：`587` (TLS) 或 `465` (SSL)
- 需要开启"允许不够安全的应用访问"

#### 企业邮箱
- 根据企业邮箱提供商的要求配置

### 2. 获取邮箱授权码

#### QQ 邮箱获取授权码步骤：
1. 登录 QQ 邮箱
2. 点击"设置" → "账户"
3. 找到"POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务"
4. 开启"POP3/SMTP服务"或"IMAP/SMTP服务"
5. 点击"生成授权码"
6. 按照提示发送短信获取授权码
7. **保存授权码**（这就是 `spring.mail.password` 的值）

#### 163 邮箱获取授权码步骤：
1. 登录 163 邮箱
2. 点击"设置" → "POP3/SMTP/IMAP"
3. 开启"POP3/SMTP服务"或"IMAP/SMTP服务"
4. 点击"生成授权码"
5. 按照提示获取授权码
6. **保存授权码**

### 3. 配置 application.properties

在 `hgmall-portal/mall/src/main/resources/application.properties` 中添加以下配置：

```properties
# 邮件服务配置
spring.mail.host=smtp.qq.com
spring.mail.port=587
spring.mail.username=your-email@qq.com
spring.mail.password=your-authorization-code
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=your-email@qq.com
```

#### 配置说明：

- `spring.mail.host`: SMTP 服务器地址
- `spring.mail.port`: SMTP 端口（587 用于 TLS，465 用于 SSL）
- `spring.mail.username`: 发送邮件的邮箱地址
- `spring.mail.password`: **邮箱授权码**（不是邮箱登录密码）
- `spring.mail.properties.mail.smtp.auth`: 启用 SMTP 认证
- `spring.mail.properties.mail.smtp.starttls.enable`: 启用 TLS
- `spring.mail.properties.mail.smtp.starttls.required`: 要求 TLS
- `spring.mail.from`: 发件人邮箱（通常与 username 相同）

### 4. 不同邮箱服务商的配置示例

#### QQ 邮箱配置：
```properties
spring.mail.host=smtp.qq.com
spring.mail.port=587
spring.mail.username=123456789@qq.com
spring.mail.password=abcdefghijklmnop  # 授权码，不是QQ密码
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=123456789@qq.com
```

#### 163 邮箱配置：
```properties
spring.mail.host=smtp.163.com
spring.mail.port=587
spring.mail.username=your-email@163.com
spring.mail.password=your-authorization-code
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=your-email@163.com
```

#### Gmail 配置：
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password  # 需要使用应用专用密码
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=your-email@gmail.com
```

### 5. 使用 SSL 端口（465）的配置

如果使用 465 端口（SSL），配置略有不同：

```properties
spring.mail.host=smtp.qq.com
spring.mail.port=465
spring.mail.username=your-email@qq.com
spring.mail.password=your-authorization-code
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.ssl.required=true
spring.mail.from=your-email@qq.com
```

### 6. 验证配置

配置完成后：

1. 重启 Portal 服务
2. 调用验证码发送接口：
   ```bash
   POST http://localhost:8080/api/auth/send-code
   Content-Type: application/json
   
   {
     "contact": "test@example.com"
   }
   ```
3. 检查邮箱是否收到验证码邮件
4. 如果未收到，检查控制台错误日志

### 7. 常见问题

#### 问题1：邮件发送失败，提示"Authentication failed"
- **原因**：密码错误或未使用授权码
- **解决**：确保使用的是**授权码**，不是邮箱登录密码

#### 问题2：邮件发送失败，提示"Connection timeout"
- **原因**：网络问题或 SMTP 服务器地址/端口错误
- **解决**：检查网络连接和 SMTP 配置

#### 问题3：邮件发送失败，提示"Could not convert socket to TLS"
- **原因**：TLS 配置不正确
- **解决**：确保 `spring.mail.properties.mail.smtp.starttls.enable=true`

#### 问题4：Gmail 发送失败
- **原因**：Gmail 需要应用专用密码
- **解决**：在 Google 账户中生成应用专用密码，而不是使用普通密码

## 开发环境建议

在开发环境中，如果不想配置邮件服务，可以：
1. 不配置邮件相关属性
2. 验证码会打印到控制台
3. 从控制台查看验证码进行测试

## 生产环境要求

生产环境**必须**配置邮件服务，确保：
1. 邮件服务稳定可靠
2. 验证码能够及时送达
3. 监控邮件发送状态
4. 处理邮件发送失败的情况

