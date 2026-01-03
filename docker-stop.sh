#!/bin/bash
# Docker 停止脚本

set -e

# 使用 docker compose 或 docker-compose
if docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    DOCKER_COMPOSE="docker-compose"
fi

echo "正在停止所有服务..."
$DOCKER_COMPOSE down

echo "服务已停止"
echo ""
echo "如需删除数据卷（会删除数据库数据），请运行:"
echo "  $DOCKER_COMPOSE down -v"








