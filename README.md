# cx-cron

一个用于创建、管理和执行定时脚本任务的平台。项目提供任务配置、Quartz 调度、脚本上传执行、执行记录与用户认证等能力。

## 功能特性

- 定时任务创建、编辑、删除、启用、关闭和手动触发。
- 基于 Quartz 的 Cron 定时调度；服务启动后自动恢复已启用任务。
- 支持上传任务脚本并配置运行命令。
- 支持查看任务执行记录，包括触发方式、执行结果、摘要和执行时间。
- 使用 Redis 锁避免同一任务并发重复执行。
- 基于 Vben Admin 与 Ant Design Vue 的中文任务管理界面。

## 技术栈

### 前端

- Vue 3、TypeScript、Vite
- Vben Admin v5
- Ant Design Vue、Pinia、Vue Router
- Axios、pnpm Workspace

### 后端

- Java 17、Spring Boot 3
- Quartz
- MyBatis-Plus、PostgreSQL
- Redis、Redisson
- Sa-Token
- BCrypt、RSA-OAEP
- MapStruct、Knife4j / OpenAPI

## 项目结构

```text
cx-cron/
├── apps/
│   └── web-antd/            # Vben Admin + Ant Design Vue 前端应用
├── internal/                 # Vben 构建、Tailwind、TypeScript 基础配置
├── packages/                 # Vben 官方运行时与 UI 工作区包
├── server/                   # Spring Boot 后端
│   ├── src/main/java/        # 后端源代码
│   └── src/main/resources/   # 配置、Mapper 与 SQL 文件
├── docs/                     # 项目文档
├── package.json              # 前端工作区脚本
└── pnpm-workspace.yaml       # pnpm 工作区与依赖目录
```

## 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 20.19+
- pnpm 11+
- PostgreSQL
- Redis

## 本地运行

### 1. 配置并启动后端

#### 1.1. 创建本地配置文件

将 [application-dev.yml](./server/src/main/resources/application-dev.yml) 复制为 `application-my.yml`，再根据本机环境修改数据库、Redis 和密钥配置。

#### 1.2. 配置 PostgreSQL 和 Redis

在 `application-my.yml` 中填写 PostgreSQL 和 Redis 的连接信息，并配置对应密码：

- `${POSTGRES_PASSWORD}`：PostgreSQL 密码。
- `${REDIS_PASSWORD}`：Redis 密码；如果 Redis 未设置密码，注释即可。

#### 1.3. 配置 RSA 私钥

认证功能需要 PKCS#8 格式的 RSA 私钥。可运行 [RsaKeyGeneratorTest.java](./server/src/test/java/com/cxcron/service/auth/RsaKeyGeneratorTest.java) 生成密钥，并将私钥配置到 `${AUTH_RSA_PRIVATE_KEY}`。

#### 1.4. 配置邮件加密密钥

邮件配置中的 SMTP 密码使用该密钥加密。可运行 [EmailEncryptionKeyGeneratorTest.java](./server/src/test/java/com/cxcron/service/email/EmailEncryptionKeyGeneratorTest.java) 生成密钥，并将其配置到 `${CX_CRON_EMAIL_ENCRYPTIONKEY}`。

#### 1.5. 启动后端

在项目根目录执行：

```bash
cd server
mvn spring-boot:run -Dspring-boot.run.profiles=my
```

后端默认监听 `http://localhost:8080`。

### 2. 配置并启动前端

前端开发代理和接口前缀配置位于 [apps/web-antd/.env.development](./apps/web-antd/.env.development)。默认代理目标为 `http://localhost:8080`；如果后端地址不同，请先调整代理配置。

在项目根目录安装依赖并启动前端：

```bash
pnpm install
pnpm dev
```

运行前端类型检查和生产构建：

```bash
pnpm typecheck
pnpm build
```

## 许可证

本项目采用 [Apache License 2.0](./LICENSE) 许可证。
