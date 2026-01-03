#!/bin/bash
# Docker 重启脚本

set -e

# 使用 docker compose 或 docker-compose
if docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    DOCKER_COMPOSE="docker-compose"
fi

echo "正在重启服务..."
$DOCKER_COMPOSE restart

echo "服务已重启"
echo ""
echo "查看服务状态: $DOCKER_COMPOSE ps"
echo "查看日志: $DOCKER_COMPOSE logs -f"








