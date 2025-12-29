# Docker 一键部署指南

本项目提供了完整的 Docker 一键部署方案，包括数据库、缓存、后端服务和前端应用。

## 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- 至少 4GB 可用内存
- 至少 10GB 可用磁盘空间

## 快速开始

### 1. 一键启动所有服务

```bash
# 启动所有服务（包括初始化账号）
docker-compose --profile init up -d

# 或者分步启动
# 1. 启动基础服务（数据库、Redis）
docker-compose up -d postgres redis

# 2. 等待数据库初始化完成后，生成账号
docker-compose --profile init up account-generator

# 3. 启动后端服务
docker-compose up -d admin-backend portal-backend

# 4. 等待后端服务启动后，生成前端 API 代码并启动前端
docker-compose up -d frontend

# 5. 启动 Nginx 反向代理
docker-compose up -d nginx
```

### 2. 查看服务状态

```bash
docker-compose ps
```

### 3. 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f admin-backend
docker-compose logs -f portal-backend
docker-compose logs -f frontend
```

### 4. 停止服务

```bash
docker-compose down

# 停止并删除数据卷（会删除数据库数据）
docker-compose down -v
```

## 服务说明

### 服务列表

| 服务 | 端口 | 说明 |
|------|------|------|
| PostgreSQL | 5432 | 数据库 |
| Redis | 6379 | 缓存 |
| Admin Backend | 8081 | 管理员后端 API |
| Portal Backend | 8080 | 用户后端 API |
| Frontend | 3000 | 前端应用 |
| Nginx | 80 | 反向代理 |

### 访问地址

- **前端应用**: http://localhost
- **管理员后端 API**: http://localhost/admin-api/
- **用户后端 API**: http://localhost/api/
- **管理员 Swagger**: http://localhost/admin-swagger/
- **用户 Swagger**: http://localhost/swagger/

### 默认账号

账号生成器会自动创建以下测试账号：

- **管理员**: `admin` / `admin123`
- **商家1**: `merchant1` / `merchant123`
- **商家2**: `merchant2` / `merchant123`
- **用户1**: `user1` / `user123`
- **用户2**: `user2` / `user123`

## 环境变量配置

可以通过环境变量或 `.env` 文件配置以下参数：

```env
# 数据库配置
POSTGRES_DB=hgmall_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres123

# Redis 配置（默认无密码）
REDIS_PASSWORD=

# JWT 密钥
JWT_SECRET=TheMallSecretKeyForJWTTokenGeneration2024SecureKey
```

## 构建自定义镜像

如果需要重新构建镜像：

```bash
# 构建所有镜像
docker-compose build

# 构建特定服务镜像
docker-compose build admin-backend
docker-compose build portal-backend
docker-compose build frontend
```

## 数据持久化

数据存储在 Docker 卷中：

- `postgres_data`: PostgreSQL 数据
- `redis_data`: Redis 数据

查看卷：

```bash
docker volume ls
```

备份数据库：

```bash
docker-compose exec postgres pg_dump -U postgres hgmall_db > backup.sql
```

恢复数据库：

```bash
docker-compose exec -T postgres psql -U postgres hgmall_db < backup.sql
```

## 故障排查

### 1. 服务无法启动

检查日志：

```bash
docker-compose logs [service-name]
```

### 2. 数据库连接失败

确保 PostgreSQL 服务已启动并健康：

```bash
docker-compose ps postgres
docker-compose logs postgres
```

### 3. 前端 API 生成失败

确保后端服务已启动：

```bash
# 检查后端服务
curl http://localhost:8080/v3/api-docs
curl http://localhost:8081/v3/api-docs
```

### 4. 端口冲突

如果端口被占用，修改 `docker-compose.yml` 中的端口映射。

## 开发模式

开发模式下，可以使用卷挂载实现代码热更新：

```yaml
# 在 docker-compose.yml 中添加 volumes
volumes:
  - ./hgmall-admin:/app
  - ./hgmall-portal:/app
```

## 生产环境部署建议

1. **使用环境变量文件**: 创建 `.env` 文件管理敏感信息
2. **配置 HTTPS**: 使用 Let's Encrypt 或配置 SSL 证书
3. **资源限制**: 在 `docker-compose.yml` 中配置资源限制
4. **日志管理**: 配置日志轮转和集中日志管理
5. **监控告警**: 集成 Prometheus 和 Grafana
6. **备份策略**: 定期备份数据库和重要数据

## 常见问题

### Q: 账号生成器运行失败？

A: 确保数据库已完全初始化，可以手动运行：

```bash
docker-compose exec account-generator /bin/bash
# 然后手动运行账号生成命令
```

### Q: 前端无法访问后端 API？

A: 检查 Nginx 配置和后端服务健康状态：

```bash
docker-compose ps
curl http://localhost/api/health
```

### Q: 如何重置所有数据？

A: 停止服务并删除数据卷：

```bash
docker-compose down -v
docker-compose --profile init up -d
```

## 技术支持

如有问题，请查看项目文档或提交 Issue。

