#!/bin/bash
# 账号生成脚本

set -e

echo "================== 开始生成账号 =================="

# 等待数据库就绪
echo "等待数据库就绪..."
max_attempts=60
attempt=0
until pg_isready -h ${DB_HOST:-postgres} -U ${DB_USER:-postgres} || [ $attempt -ge $max_attempts ]; do
  attempt=$((attempt + 1))
  echo "等待数据库启动... ($attempt/$max_attempts)"
  sleep 2
done

if [ $attempt -eq $max_attempts ]; then
  echo "错误: 数据库未能在预期时间内启动"
  exit 1
fi

# 等待几秒确保数据库完全启动
sleep 5

# 设置数据库连接信息
export DB_HOST=${DB_HOST:-postgres}
export DB_PORT=${DB_PORT:-5432}
export DB_NAME=${DB_NAME:-hgmall_db}
export DB_USER=${DB_USER:-postgres}
export DB_PASSWORD=${DB_PASSWORD:-postgres123}

# 进入 mbg 目录
cd /app/hgmall-mbg

# 创建配置文件
cat > src/main/resources/generator.properties <<EOF
jdbc.driverClass=org.postgresql.Driver
jdbc.connectionURL=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
jdbc.userId=${DB_USER}
jdbc.password=${DB_PASSWORD}
EOF

# 编译并运行账号生成器
echo "编译项目..."
mvn clean compile -DskipTests

echo "运行账号生成器..."
mvn exec:java -Dexec.mainClass="com.macro.mall.AccountGenerator" -Dexec.classpathScope=compile || {
  echo "使用备用方式运行账号生成器..."
  CLASSPATH="target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)"
  java -cp "$CLASSPATH" com.macro.mall.AccountGenerator
}

echo "================== 账号生成完成 =================="

