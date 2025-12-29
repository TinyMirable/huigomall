#!/bin/bash
# 数据库初始化脚本

set -e

echo "等待 PostgreSQL 启动..."
until pg_isready -h postgres -U postgres; do
  sleep 1
done

echo "PostgreSQL 已启动，开始初始化数据库..."

# 执行初始化 SQL
psql -h postgres -U postgres -d hgmall_db -f /docker-entrypoint-initdb.d/init.sql

echo "数据库初始化完成！"

