# Quick Start
## 初始化数据库
- Install [PostgreSQL](https://www.postgresql.org/).
- Init DataBase
```sh
# 1. 创建数据库
CREATE DATABASE hgmall_db;

# 2. 连接数据库
\c hgmall_db

# 3. 执行初始化脚本

psql -U postgres -d hgmall_db -f docs/sql/init.sql
```
## 创建初始账号
运行创建脚本：

生成结果1个管理员，2个商家，2个用户
```

```
## 生成测试数据(可选)
完成上面操作后运行
```sh
# 用 psql 命令
psql -U postgres -d hgmall_db -f docs/sql/insert_products.sql
```
## 运行后端
  * 先运行 mvn install 将项目安装到本地仓库，需要先配置`application.properties`可以参考`application.properties.example`
  1. 构建 mbg 模块
  ```
  cd hgmall-mbg
  mvn clean install -DskipTests
  ```

  1. 构建 common 模块
  ```
  cd ../hgmall-common
  mvn clean install -DskipTests
  ```

  1. 运行 admin 模块（在 hgmall-admin 目录）
  ```
  cd ../hgmall-admin
  mvn spring-boot:run
  ```

  1. 运行 portal 模块（在 hgmall-portal/mall 目录）
  ```
  cd ../hgmall-portal/mall
  mvn spring-boot:run
  ```
## 运行前端
1. 安装前端软件包
  ```
  cd /hgmall-frontend
  npm install
  ```
1. 生成openapi
   ```
   # 生成用户与商家(销售经理)api
   npm run generate:api
   # 生成管理员api
   npm run generate:api:admin
   ```
2. 运行前端项目
   ```
   npm run dev
   ```
  
