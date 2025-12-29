# API 代码生成指南

本项目使用 OpenAPI Generator 从后端 OpenAPI 规范自动生成前端 API 客户端代码。

## 前置条件

1. 安装依赖：
   ```bash
   npm install
   ```

2. **确保后端服务正在运行**（如果使用在线生成）：
   - Portal 服务：`http://localhost:8080`
   - Admin 服务：`http://localhost:8081`

   ⚠️ **如果后端服务未运行，请使用"方法三：使用本地文件"**

## 生成 API 代码

### 方法一：使用 npm 脚本（推荐）

#### 生成 Portal API（用户端）
```bash
npm run generate:api
```

#### 生成 Admin API（管理端）
```bash
npm run generate:api:admin


### 方法二：使用 OpenAPI Generator CLI 直接命令

#### 生成 Portal API
```bash
npx @openapitools/openapi-generator-cli generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-axios \
  -o src/api/generated \
  --additional-properties=supportsES6=true,withInterfaces=true,typescriptThreePlus=true
```

#### 生成 Admin API
```bash
npx @openapitools/openapi-generator-cli generate \
  -i http://localhost:8081/v3/api-docs \
  -g typescript-axios \
  -o src/api/generated-admin \
  --additional-properties=supportsES6=true,withInterfaces=true,typescriptThreePlus=true
```

### 方法三：使用本地 OpenAPI JSON 文件

如果后端服务未运行，可以先下载 OpenAPI JSON 文件：

1. 访问 `http://localhost:8080/v3/api-docs` 并保存为 `openapi-portal.json`
2. 访问 `http://localhost:8081/v3/api-docs` 并保存为 `openapi-admin.json`

然后使用文件生成：

```bash
npx @openapitools/openapi-generator-cli generate \
  -i openapi-portal.json \
  -g typescript-axios \
  -o src/api/generated \
  --additional-properties=supportsES6=true,withInterfaces=true,typescriptThreePlus=true
```

## 生成的文件结构

生成后的文件将位于：
- Portal API: `src/api/generated/`
- Admin API: `src/api/generated-admin/`

主要文件：
- `api/` - API 接口类
- `models/` - 数据模型类
- `index.ts` - 导出文件
- `base.ts` - 基础配置

## 使用生成的 API

### 示例：使用 Portal API

```typescript
import { AuthControllerApi, LoginRequest } from '@/api/generated';

// 创建 API 实例
const authApi = new AuthControllerApi();

// 配置基础路径（可选，如果与默认不同）
authApi.basePath = 'http://localhost:8080';

// 调用登录接口
const loginRequest: LoginRequest = {
  username: 'user123',
  password: 'password123'
};

try {
  const response = await authApi.login(loginRequest);
  console.log('登录成功:', response.data);
} catch (error) {
  console.error('登录失败:', error);
}
```

### 示例：使用 Admin API

```typescript
import { AdminAuthControllerApi, LoginRequest } from '@/api/generated-admin';

const adminAuthApi = new AdminAuthControllerApi();
adminAuthApi.basePath = 'http://localhost:8081';

const loginRequest: LoginRequest = {
  username: 'admin',
  password: 'admin123'
};

try {
  const response = await adminAuthApi.login(loginRequest);
  console.log('管理员登录成功:', response.data);
} catch (error) {
  console.error('登录失败:', error);
}
```

## 配置 Axios 拦截器（可选）

如果需要添加认证 token 或统一错误处理，可以配置 Axios：

```typescript
import axios from 'axios';
import { Configuration } from '@/api/generated';

// 配置默认的 axios 实例
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
});

// 添加请求拦截器（添加 token）
axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 添加响应拦截器（统一错误处理）
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 处理未授权错误
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 使用自定义的 axios 实例
const configuration = new Configuration({
  basePath: 'http://localhost:8080',
});
```

## 更新 API 代码

当后端 API 发生变化时，重新运行生成命令即可更新前端 API 代码。

## 注意事项

1. 生成前确保后端服务正在运行
2. 生成的文件会被覆盖，不要手动修改生成的文件
3. 如果需要自定义，可以在生成后创建包装类
4. 建议将生成的文件添加到 `.gitignore` 中，或者提交到版本控制（根据团队约定）

