# HuiGOMall

This is a mall


[![GitHub Stars](https://img.shields.io/github/stars/YOUR_USERNAME/HuiGoRAG_2?style=flat-square&logo=github&color=yellow)](https://github.com/YOUR_USERNAME/HuiGoRAG_2/stargazers)
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-6DB33F?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue.js-3.5+-4FC08D?style=flat-square&logo=vue.js&logoColor=white)](https://vuejs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16.2-336791?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-支持-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)
[![Redis](https://img.shields.io/badge/Redis-7.x-red?style=flat-square&logo=redis&logoColor=white)](https://redis.io/)

代码的架构借鉴自[mall]("https://github.com/macrozheng/mall/tree/master")

## 项目介绍

HuiGOMall项目是一套电商系统，包括前台商城系统及后台管理系统，基于SpringBoot+MyBatis实现，采用Docker容器化部署。前台商城系统包含首页门户、商品推荐、商品搜索、商品展示、购物车、订单流程、会员中心、客户服务、帮助中心等模块。后台管理系统包含商家管理、店铺管理、商品管理、订单管理、会员管理、促销管理、运营管理、内容管理、统计报表、权限管理、设置等模块。

## 组织结构

```
HuiGOMall
├── hgmall-common      -- 工具类及通用代码（包含Spring Security封装模块）
├── hgmall-mbg         -- MyBatisGenerator生成的数据库操作代码
├── hgmall-admin       -- 后台商城管理系统接口
├── hgmall-portal      -- 前台商城系统接口
└── hgmall-frontend    -- 前端项目（Vue 3 + TypeScript + Vite）
```

## 技术选型

### 后端技术

| 技术 | 说明 | 官网 |
| --- | --- | --- |
| Spring Boot | Web应用开发框架 | https://spring.io/projects/spring-boot |
| Spring Security | 认证和授权框架 | https://spring.io/projects/spring-security |
| Spring Data JPA | 数据访问层框架 | https://spring.io/projects/spring-data-jpa |
| MyBatis | ORM框架 | http://www.mybatis.org/mybatis-3/zh/index.html |
| MyBatis Generator | 数据层代码生成器 | http://www.mybatis.org/generator/index.html |
| PostgreSQL | 关系型数据库 | https://www.postgresql.org/ |
| Redis | 内存数据存储 | https://redis.io/ |
| Elasticsearch | 搜索引擎（已引入依赖） | https://github.com/elastic/elasticsearch |
| Docker | 应用容器引擎 | https://www.docker.com |
| JWT | JWT登录支持 | https://github.com/jwtk/jjwt |
| Lombok | Java语言增强库 | https://github.com/rzwitserloot/lombok |
| Spring Mail | 邮件服务 | https://spring.io/projects/spring-framework |

### 前端技术

| 技术 | 说明 | 官网 |
| --- | --- | --- |
| Vue | 前端框架 | https://vuejs.org/ |
| Vue Router | 路由框架 | https://router.vuejs.org/ |
| TypeScript | JavaScript超集 | https://www.typescriptlang.org/ |
| Vite | 前端构建工具 | https://vitejs.dev/ |
| TailwindCSS | CSS框架 | https://tailwindcss.com/ |
| DaisyUI | TailwindCSS组件库 | https://daisyui.com/ |
| OpenAPI Generator | API代码生成工具 | https://openapi-generator.tech/ |

## 模块介绍

### 后台管理系统 hgmall-admin

- **商家管理**：商家注册审核、商家状态管理、审核开关配置
- **店铺管理**：店铺审核、店铺状态管理、审核开关配置
- **商品管理**：全局商品分类管理、商品信息查看
- **订单管理**：查看所有订单、订单详情查看
- **用户管理**：用户信息查看、用户状态管理
- **操作审计**：查看所有操作日志

### 前台商城系统 hgmall-portal

- **用户认证**：用户注册、登录、注销（支持手机号/邮箱验证码、短信验证码免密码登录）
- **商品浏览**：首页展示、商品列表、商品详情、分类筛选、排序
- **购物车**：添加商品、删除商品、修改数量、持久化存储
- **订单管理**：创建订单、查看订单历史、取消订单
- **收货地址**：添加、编辑、删除、设为默认
- **个人中心**：查看个人信息、修改个人信息、修改密码
- **商家功能**：店铺管理、商品管理、订单管理、销售报表

## 功能介绍

已经实现和未实现的功能[/docs/works.md](/docs/works.md)

系统设计参考 [/docs/works.md]
## 环境搭建

### 快速开始

#### 使用Docker一键部署（推荐）

本地部署参考 [/docs/quick_start.md](/docs/quick_start.md)

docker部署说明请参考 [docker_start.md](/docs/docker_start.md)

