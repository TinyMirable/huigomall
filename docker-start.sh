#!/bin/bash
# Docker 一键启动脚本

set -e

echo "=========================================="
echo "  TheMall Docker 一键部署脚本"
echo "=========================================="
echo ""

# 检查 Docker 和 Docker Compose
if ! command -v docker &> /dev/null; then
    echo "错误: 未找到 Docker，请先安装 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "错误: 未找到 Docker Compose，请先安装 Docker Compose"
    exit 1
fi

# 使用 docker compose 或 docker-compose
if docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    DOCKER_COMPOSE="docker-compose"
fi

echo "步骤 1/6: 启动 PostgreSQL 和 Redis..."
$DOCKER_COMPOSE up -d postgres redis

echo ""
echo "等待数据库初始化..."
sleep 10

echo ""
echo "步骤 2/6: 生成测试账号..."
$DOCKER_COMPOSE --profile init up account-generator

echo ""
echo "步骤 3/6: 启动后端服务..."
$DOCKER_COMPOSE up -d admin-backend portal-backend

echo ""
echo "等待后端服务启动（这可能需要1-2分钟）..."
sleep 30

echo ""
echo "步骤 4/6: 启动前端服务..."
$DOCKER_COMPOSE up -d frontend

echo ""
echo "步骤 5/6: 启动 Nginx 反向代理..."
$DOCKER_COMPOSE up -d nginx

echo ""
echo "步骤 6/6: 检查服务状态..."
sleep 5
$DOCKER_COMPOSE ps

echo ""
echo "=========================================="
echo "  部署完成！"
echo "=========================================="
echo ""
echo "访问地址："
echo "  - 前端应用: http://localhost"
echo "  - 管理员后端: http://localhost/admin-api/"
echo "  - 用户后端: http://localhost/api/"
echo "  - 管理员 Swagger: http://localhost/admin-swagger/"
echo "  - 用户 Swagger: http://localhost/swagger/"
echo ""
echo "默认账号："
echo "  - 管理员: admin / admin123"
echo "  - 商家1: merchant1 / merchant123"
echo "  - 商家2: merchant2 / merchant123"
echo "  - 用户1: user1 / user123"
echo "  - 用户2: user2 / user123"
echo ""
echo "查看日志: docker-compose logs -f"
echo "停止服务: docker-compose down"
echo ""

