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

### 1. 配置后端

复制 [application-dev.yml](./server/src/main/resources/application-dev.yml) 为`application-my.yml`，配置 PostgreSQL、Redis 和 RSA 私钥所需的环境变量，RSA 私钥可通过[RsaKeyGeneratorTest.java](./server/src/test/java/com/cxcron/service/auth/RsaKeyGeneratorTest.java) 生成：

```bash
${POSTGRES_PASSWORD} 替换为 PostgreSQL 密码
${REDIS_PASSWORD} 替换为本地 Redis 密码，无密码需要注释
${AUTH_RSA_PRIVATE_KEY} 替换为PKCS#8 格式 RSA 私钥
```

启动后端：

```bash
cd server
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

默认监听 `http://localhost:8080`。

### 2. 启动前端

前端开发代理和接口前缀配置位于 [apps/web-antd/.env.development](./apps/web-antd/.env.development)。默认代理目标为 `http://localhost:8080`，请按实际后端地址调整。

```bash
pnpm install
pnpm dev
```

执行类型检查和生产构建：

```bash
pnpm typecheck
pnpm build
```

## 许可证

本项目采用 [Apache License 2.0](./LICENSE) 许可证。
