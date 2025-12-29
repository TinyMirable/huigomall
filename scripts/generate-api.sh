#!/bin/bash
# 前端 API 代码生成脚本

set -e

echo "================== 开始生成前端 API 代码 =================="

# 等待后端服务就绪
echo "等待后端服务启动..."
max_attempts=120
attempt=0

# 等待 Portal 后端
PORTAL_READY=false
while [ $attempt -lt $max_attempts ]; do
  if curl -f http://portal-backend:8080/v3/api-docs > /dev/null 2>&1; then
    echo "Portal 后端已就绪"
    PORTAL_READY=true
    break
  fi
  attempt=$((attempt + 1))
  if [ $((attempt % 10)) -eq 0 ]; then
    echo "等待 Portal 后端启动... ($attempt/$max_attempts)"
  fi
  sleep 2
done

if [ "$PORTAL_READY" = false ]; then
  echo "警告: Portal 后端未能在预期时间内启动，继续尝试生成 API..."
fi

# 等待 Admin 后端
attempt=0
ADMIN_READY=false
while [ $attempt -lt $max_attempts ]; do
  if curl -f http://admin-backend:8081/v3/api-docs > /dev/null 2>&1; then
    echo "Admin 后端已就绪"
    ADMIN_READY=true
    break
  fi
  attempt=$((attempt + 1))
  if [ $((attempt % 10)) -eq 0 ]; then
    echo "等待 Admin 后端启动... ($attempt/$max_attempts)"
  fi
  sleep 2
done

if [ "$ADMIN_READY" = false ]; then
  echo "警告: Admin 后端未能在预期时间内启动，继续尝试生成 API..."
fi

# 进入前端目录
cd /app

# 更新配置文件中的 URL（如果存在）
if [ -f .openapi-generator-config-portal.json ]; then
  sed -i 's|http://localhost:8080|http://portal-backend:8080|g' .openapi-generator-config-portal.json || true
fi

if [ -f .openapi-generator-config-admin.json ]; then
  sed -i 's|http://localhost:8081|http://admin-backend:8081|g' .openapi-generator-config-admin.json || true
fi

# 生成 Portal API
if [ "$PORTAL_READY" = true ]; then
  echo "生成 Portal API..."
  npm run generate:api || echo "Portal API 生成失败，继续..."
else
  echo "跳过 Portal API 生成（服务未就绪）"
fi

# 生成 Admin API
if [ "$ADMIN_READY" = true ]; then
  echo "生成 Admin API..."
  npm run generate:api:admin || echo "Admin API 生成失败，继续..."
else
  echo "跳过 Admin API 生成（服务未就绪）"
fi

echo "================== API 代码生成完成 =================="

