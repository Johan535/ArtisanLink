
# ArtisanLink 开发规范指南

为保证代码质量、可维护性、安全性与可扩展性，请在开发过程中严格遵循以下规范。

## 一、项目基本信息

- **项目名称**：ArtisanLink (匠心美业O2O平台)
- **工作目录**：`D:\code\匠心美业全栈项目\ArtisanLink`
- **作者**：LENOVO
- **开发环境**：Windows 11, JDK 21.0.8, Maven

## 二、技术栈与工具

- **主框架**：Spring Boot 3.2.4
- **构建工具**：Maven
- **编程语言**：Java 21
- **核心依赖**：
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa` (注：项目实际使用 `mybatis-plus`，详见下文)
  - `mybatis-plus-spring-boot3-starter` (3.5.5)
  - `spring-boot-starter-security`
  - `spring-boot-starter-validation`
  - `lombok` (1.18.30)
  - `knife4j` (4.4.0)
  - `druid` (1.2.20)

## 三、目录结构规范

项目采用多模块 Maven 架构，遵循以下目录结构：

```text
ArtisanLink
├── artisanlink-common          # 公共工具模块
│   └── src/main/java/com/johan/artisanlink/common
│       ├── constant            # 常量定义
│       ├── context             # 上下文
│       ├── exception           # 自定义异常
│       ├── handler             # 处理器（如全局异常）
│       ├── properties          # 配置属性
│       ├── result              # 统一响应结果
│       └── util                # 工具类
├── artisanlink-pojo            # 数据模型模块
│   └── src/main/java/com/johan/artisanlink/pojo
│       ├── dto                 # 数据传输对象
│       ├── po                  # 数据库实体对象
│       └── vo                  # 视图展示对象
├── artisanlink-server          # 核心业务服务模块
│   └── src/main/java/com/johan/artisanlink/server
│       ├── annotation          # 自定义注解
│       ├── aspect              # AOP切面
│       ├── config              # 配置类
│       ├── controller          # 控制器
│       │   ├── admin           # 管理端接口
│       │   └── user            # 用户端接口
│       ├── interceptor         # 拦截器
│       ├── mapper              # MyBatis Mapper接口
│       ├── service             # 业务逻辑接口
│       │   └── impl            # 业务逻辑实现
│       ├── task                # 定时任务
│       └── websocket           # WebSocket处理
│   └── src/main/resources
│       ├── mapper              # MyBatis XML映射文件
│       └── template            # 模板文件
└── ArtisanLink-fronted         # 前端项目目录 (Vue/React等)
```

## 四、分层架构与依赖规范

本项目基于 MyBatis Plus 构建分层架构，依赖关系如下：

| 层级        | 职责说明                         | 开发约束与注意事项                                               |
|-------------|----------------------------------|----------------------------------------------------------------|
| **Controller** | 处理 HTTP 请求与响应，定义 API 接口 | 不得直接访问数据库，必须通过 Service 层调用；使用 `@Valid` 校验输入 |
| **Service**    | 实现业务逻辑、事务管理与数据校验   | 必须通过 Mapper 层访问数据库；建议返回 VO；`@Transactional` 注解仅用于 Service 层 |
| **Mapper**     | MyBatis 数据库访问接口             | 继承 `BaseMapper`；XML 映射文件位于 `resources/mapper` 目录下 |
| **Entity**     | 数据库实体映射                    | 包名统一为 `pojo.po`；使用 `@TableName` 注解映射表名；建议继承 MyBatis Plus 基础类 |
| **DTO/VO**     | 数据传输与视图展示                | DTO 用于接收参数；VO 用于返回结果；必须包含完整的 Getter/Setter (Lombok) |

### 接口与实现分离

- 所有业务逻辑接口需放在接口所在包中，具体实现类需放在 `impl` 子包中。
- 所有 Mapper 接口需放在 `mapper` 包中。

## 五、安全与性能规范

### 输入校验

- 使用 `@Valid` 与 `jakarta.validation.constraints.*` 校验注解（如 `@NotBlank`, `@Size` 等）。
- **禁止** 手动拼接 SQL 字符串，防止 SQL 注入攻击。

### 事务管理

- `@Transactional` 注解仅用于 **Service 层**方法。
- 避免在循环中频繁提交事务，影响性能。

### 安全配置 (Spring Security + JWT)

- 密码加密：使用 `BCryptPasswordEncoder`。
- 身份认证：使用 JWT (`io.jsonwebtoken:jjwt`)。
- 权限控制：通过 Spring Security Filter Chain 配置。

## 六、代码风格规范

### 命名规范

| 类型       | 命名方式             | 示例                  |
|------------|----------------------|-----------------------|
| 类名       | UpperCamelCase       | `UserServiceImpl`     |
| 方法/变量  | lowerCamelCase       | `saveUser()`          |
| 常量       | UPPER_SNAKE_CASE     | `MAX_LOGIN_ATTEMPTS`  |
| 包名       | 全小写点分隔         | `com.johan.artisanlink` |

### 注释规范

- 所有类、方法、字段需添加 **Javadoc** 注释。
- **注释语言**：优先使用中文（与项目文档和用户语言一致）。

### 类型命名规范（阿里巴巴风格）

| 后缀 | 用途说明                     | 示例         |
|------|------------------------------|--------------|
| DTO  | 数据传输对象                 | `UserDTO`    |
| PO   | 数据库实体对象 (MyBatis Plus) | `UserPO`     |
| VO   | 视图展示对象                 | `UserVO`     |
| Mapper | MyBatis 数据库访问接口         | `UserMapper` |

### 实体类简化工具

- 使用 Lombok 注解替代手动编写 getter/setter/构造方法：
  - `@Data`
  - `@NoArgsConstructor`
  - `@AllArgsConstructor`
  - `@Builder`

## 七、配置与扩展规范

### MyBatis Plus 配置

- **主键策略**：`AUTO` (数据库自增)
- **驼峰命名**：自动开启（`map-underscore-to-camel-case: true`）
- **逻辑删除**：字段名为 `deleted` (1=删除, 0=未删除)
- **Mapper 位置**：`classpath:mapper/*.xml`

### 接口文档 (Knife4j)

- 使用 `knife4j-openapi3-jakarta-spring-boot-starter`
- 文档配置项：语言设置为中文 (`language: zh_cn`)

### 分页与缓存

- 分页插件：`PageHelper` (1.4.1)
- 缓存支持：`spring-boot-starter-data-redis`

## 八、扩展性与日志规范

### 日志记录

- 使用 `@Slf4j` 注解代替 `System.out.println` 或 `System.err.println`。
- 日志级别：使用 INFO, WARN, ERROR 等标准级别。

## 九、编码原则总结

| 原则       | 说明                                       |
|------------|--------------------------------------------|
| **SOLID**  | 高内聚、低耦合，增强可维护性与可扩展性     |
| **DRY**    | 避免重复代码，提高复用性                   |
| **KISS**   | 保持代码简洁易懂                           |
| **YAGNI**  | 不实现当前不需要的功能                     |
| **OWASP**  | 防范常见安全漏洞，如 SQL 注入、XSS 等      |
