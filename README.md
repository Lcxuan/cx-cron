# cx-cron

一个用于创建、管理和执行定时脚本任务的平台。项目提供任务配置、Quartz 调度、脚本上传执行、执行记录和用户认证等基础能力。

## 功能特性

- 用户登录、Token 刷新与退出登录。
- 定时任务创建、编辑、删除、启用、关闭和手动触发。
- 基于 Quartz 的 Cron 定时调度，服务启动后自动恢复已启用任务。
- 支持上传任务脚本并配置运行命令。
- 支持查看任务执行记录，包括触发方式、执行结果、摘要和执行时间。
- 使用 Redis 锁避免同一任务并发重复执行。
- 前端任务管理页面及 Ant Design Vue 中文化界面。

## 技术栈

### 前端

- Vue 3
- TypeScript
- Vite
- Ant Design Vue
- Pinia
- Vue Router
- Axios

### 后端

- Java 17
- Spring Boot 3
- Quartz
- MyBatis-Plus
- PostgreSQL
- Redis / Redisson
- Sa-Token
- MapStruct

## 项目结构

```text
cx-cron/
├── server/                 # Spring Boot 后端
│   ├── src/main/java/      # 后端源代码
│   └── src/main/resources/ # 配置与 SQL 文件
├── src/                    # Vue 前端源代码
├── docs/                   # 项目文档
├── .env.development        # 前端开发环境配置
└── .env.production         # 前端生产环境配置
```

## 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 20+
- pnpm 9+
- PostgreSQL
- Redis