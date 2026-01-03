# HuiGoMall


[![GitHub Stars](https://img.shields.io/github/stars/YOUR_USERNAME/HuiGoRAG_2?style=flat-square&logo=github&color=yellow)](https://github.com/YOUR_USERNAME/HuiGoRAG_2/stargazers)
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-6DB33F?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue.js-3.5+-4FC08D?style=flat-square&logo=vue.js&logoColor=white)](https://vuejs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16.2-336791?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-支持-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)
[![Redis](https://img.shields.io/badge/Redis-7.x-red?style=flat-square&logo=redis&logoColor=white)](https://redis.io/)

代码的架构借鉴自[mall]("https://github.com/macrozheng/mall/tree/master")

scut网络应用架构课程作品

学号:202366450351
姓名：程柱豪

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

系统设计参考 [/docs/works.md](/docs/works.md)

使用说明参考 [/docs/manual.md](docs/manual.md)

## 代码说明

**hgmall-common模块**：系统的公共基础模块，包含了所有其他模块共享的工具类和业务逻辑。该模块主要包含以下几个子包：
  - util包：提供了JWT工具类（JwtUtil）、密码加密工具类（PasswordUtil）、邮件发送工具类（EmailUtil）等通用工具
  - service包：提供了认证服务（AuthService），实现了统一的登录、注册、验证码发送等逻辑
  - domain包：定义了通用的数据传输对象，如LoginResult、CommonResult等
  - config包：提供了JWT配置类，从application.properties读取配置信息
  - 这种设计使得认证逻辑、工具方法等公共功能只需在common模块中实现一次，其他模块通过Maven依赖引入即可使用，避免了代码重复

  **hgmall-mbg模块**：MyBatis Generator代码生成模块，负责根据数据库表结构自动生成对应的实体类（Model）、Mapper接口和XML映射文件。
  - 该模块的generator.properties配置文件定义了数据库连接信息、代码生成路径、表名映射规则等
  - 通过运行Maven插件，可以自动为14张数据表生成对应的CRUD操作代码
  - 生成的代码遵循统一的命名规范和代码风格，便于后续维护
  - 除了MyBatis Generator自动生成的代码外，还在Mapper XML文件中手动编写了一些自定义查询方法，以满足特定的业务需求：
    - 在ProductMapper.xml中编写了selectByCategoriesWithPaging方法，用于支持按多个分类筛选商品并分页查询
    - 编写了selectTopSales方法，用于查询全站销量Top N的商品
    - 编写了countByCategories方法，用于统计分类下的商品总数


  **hgmall-admin模块**：管理员后端服务，运行在8081端口，提供管理员相关的所有API接口。该模块不仅包含控制器，还包含管理员特有的业务逻辑、数据传输对象、配置类和工具类：
  - controller包：包含多个控制器，处理管理员相关的HTTP请求
    - AdminAuthController：处理管理员登录认证
    - AdminMerchantController：处理商家管理（查看商家详情、审核商家申请、管理商家状态）
    - AdminUserController：处理用户管理（查看用户列表、管理用户状态、查看用户订单历史）
    - AdminCategoryController：处理商品分类管理（创建、修改、删除分类，查看分类树）
    - AdminOrderController：处理订单管理（查看所有订单、订单详情、取消订单、订单统计）
    - AdminAuditLogController：处理审计日志查看
    - AdminConfigController：处理系统配置管理
    - AdminProductController：处理商品管理（管理员强制下架商品）
    - AdminShopController：处理店铺管理（管理员强制关闭店铺）
  - service包：包含管理员特有的业务逻辑服务层，   
    - AdminUserService（用户管理业务逻辑
    - AdminMerchantService（商家管理业务逻辑）
    - AdminCategoryService（分类管理业务逻辑）
    - AdminOrderService（订单管理业务逻辑）
    - AdminAuditLogService（审计日志业务逻辑）
    - AdminConfigService（系统配置业务逻辑）
  
    这些服务实现了管理员特有的业务逻辑，如用户状态管理、商家审核、订单统计等
  - domain包：定义了管理员模块特有的数据传输对象，如UserVO、MerchantVO、CategoryVO、AuditLogVO等视图对象，以及各种请求对象如UpdateUserStatusRequest、AuditMerchantRequest、CreateCategoryRequest等，用于前后端数据交互
  - config包：包含模块特有的配置类，如MyBatisConfig（MyBatis配置）、OpenApiConfig（Swagger配置）、SecurityConfig（安全配置）等，这些配置针对管理员模块的需求进行了定制
  - util包：包含管理员模块特有的工具类，如AdminControllerUtil（提供权限验证和token提取方法）
  - 该模块通过依赖hgmall-common模块获取认证服务和通用工具类，通过依赖hgmall-mbg模块获取数据库操作能力

  **hgmall-portal模块**：用户和商家后端服务，运行在8080端口，提供前台用户和商家相关的API接口。该模块不仅包含控制器，还包含用户和商家特有的业务逻辑、数据传输对象、配置类和工具类：
  - controller包：包含多个控制器，处理用户和商家相关的HTTP请求
    - AuthController：处理用户注册、登录、忘记密码等认证相关功能
    - UserController：处理用户信息管理（修改用户名、邮箱、手机号等）
    - AddressController：处理用户地址的增删改查
    - ProductController：处理商品展示（首页商品、分类商品列表、商品详情）
    - CartController：处理购物车操作
    - OrderController：处理订单创建、支付、确认收货等
    - ShopController：处理店铺管理（创建店铺、修改店铺信息、管理店铺状态）
    - ShopProductController：处理商家商品管理
    - MerchantOrderController：处理商家订单管理
    - CategoryController：处理分类查询
  - domain包：定义了用户和商家模块特有的数据传输对象，用于前后端数据交互
  - config包：包含模块特有的配置类，如MyBatisConfig（MyBatis配置）、OpenApiConfig（Swagger配置）、SecurityConfig（安全配置）等，这些配置与admin模块的配置类似，但针对用户和商家的业务场景进行了定制，如允许公开访问商品展示接口、购物车接口等
  - util包：包含用户和商家模块特有的工具类
  - component包：包含一些组件类
  - 该模块同样依赖common和mbg模块，业务逻辑主要使用common模块中的服务（如AuthService、ProductService、OrderService、CartService等），控制器层负责调用这些服务并处理HTTP请求响应

  **hgmall-frontend模块**：前端Vue 3应用，使用TypeScript编写，采用组件化开发模式。src目录下包含：
  - api目录：存放所有API调用封装，通过OpenAPI Generator根据后端Swagger文档自动生成类型安全的API客户端代码
  - components目录：存放可复用的Vue组件，如ProductCard、AddressCard、Header、Footer等
  - pages目录：存放页面组件，如HomePage、ProductDetailPage、OrderPage等
  - router.ts：定义了前端路由配置
  - utils目录：存放工具函数，如缓存管理、加密解密等
  - 前端通过axios发送HTTP请求与后端通信，使用JWT token进行身份认证
## 环境搭建

### 快速开始

#### 使用Docker一键部署（推荐）

本地部署参考 [/docs/quick_start.md](/docs/quick_start.md)

docker部署说明请参考 [docker_start.md](/docs/docker_start.md)

