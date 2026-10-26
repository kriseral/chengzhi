# chengzhi 项目

**Permission-aware cited knowledge QA 系统**

## 项目简介
chengzhi 是一个带权限控制的引用知识问答系统。支持文档上传、ACL 权限控制（用户、部门、角色）、向量检索 RAG QA。

## 当前状态
- **审批策略**：`never`（任何创建/更新文档操作均被拒绝）
- **后端**：Spring Boot Java
- **前端**：Vite + Vue.js
- **数据库**：PostgreSQL + Flyway
- **最新修改**：2025-09-25 审批策略从 "ask" 改为 "never"

## 技术栈
- Spring Boot 3.3.5
- Java 17
- MyBatis-Plus
- PostgreSQL
- Vite + Vue

## 快速启动
1. 启动 PostgreSQL
2. `cd chengzhi/backend && mvn clean compile`
3. 修改 `application.yml` 中的配置
4. `mvn spring-boot:run`

## 变更记录
[查看 CHANGELOG.md]
