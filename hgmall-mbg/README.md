# MyBatis Generator 模块

## 功能说明

此模块用于自动生成 MyBatis 的 Mapper 接口、Model 类和 XML 映射文件。

## 特性

1. **自动处理 PostgreSQL 保留关键字**：通过 `PostgreSQLReservedKeywordPlugin` 插件，自动为 PostgreSQL 保留关键字表名（如 `order`、`user`）添加双引号，避免 SQL 语法错误。

2. **中文注释生成**：通过 `CommentGenerator` 自定义注释生成器，自动生成中文注释。

## 使用方法

### 方法一：使用 Maven 插件

```bash
cd hgmall-mbg
mvn mybatis-generator:generate
```

### 方法二：运行 Java 主类

```bash
cd hgmall-mbg
mvn compile exec:java -Dexec.mainClass="com.macro.mall.Generator"
```

### 方法三：在 IDE 中运行

直接运行 `com.macro.mall.Generator` 类的 `main` 方法。

## 配置说明

### generatorConfig.xml

配置文件位于 `src/main/resources/generatorConfig.xml`，主要配置项：

- **数据库连接**：在 `generator.properties` 中配置
- **表配置**：在 `<table>` 标签中配置要生成的表
- **插件配置**：已自动配置 `PostgreSQLReservedKeywordPlugin` 插件

### PostgreSQL 保留关键字处理

`PostgreSQLReservedKeywordPlugin` 插件会自动检测以下保留关键字并添加双引号：

- `order`
- `user`
- `group`
- `select`
- `table`
- 以及其他常见的 PostgreSQL 保留关键字

如果您的表名是保留关键字，插件会自动在生成的 SQL 中添加双引号，例如：

```sql
-- 自动生成
from "order"
insert into "user"
update "order"
delete from "user"
```

## 注意事项

1. **首次运行前**：需要先运行 `mvn install` 将项目安装到本地 Maven 仓库，以便插件能够访问自定义类。

2. **配置文件**：确保 `src/main/resources/generator.properties` 中的数据库连接信息正确。

3. **表名检查**：如果添加了新表，且表名是 PostgreSQL 保留关键字，插件会自动处理，无需手动修改。

4. **覆盖文件**：生成器默认会覆盖已存在的文件，请确保重要修改已提交到版本控制系统。

## 生成的文件

生成的文件位于：

- **Model 类**：`src/main/java/com/macro/mall/model/`
- **Mapper 接口**：`src/main/java/com/macro/mall/mapper/`
- **Mapper XML**：`src/main/resources/mapper/`

## 常见问题

### Q: 生成的 SQL 仍然没有双引号？

A: 确保 `PostgreSQLReservedKeywordPlugin` 插件已正确配置在 `generatorConfig.xml` 中，并且插件类已编译。

### Q: 如何添加新的保留关键字？

A: 编辑 `PostgreSQLReservedKeywordPlugin.java`，在 `RESERVED_KEYWORDS` 集合中添加新的关键字。

### Q: 生成后还需要手动修改吗？

A: 不需要。插件会自动处理保留关键字，生成的代码可以直接使用。




