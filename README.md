# task-manager-api

这是一个面向 Java 初学者的 6 周任务驱动项目。你会先用 Java 写控制台版任务管理器，再用 MySQL 和 Navicat 学 SQL，最后把项目升级成 Spring Boot 后端 API。

## 新手先看这里

如果你不知道如何开始，先打开：

```text
START_HERE.md
```

学习时不要一上来就看全部源码。推荐顺序是：

```text
第 1 周 Java 对象
第 2 周 控制台 CRUD
第 3 周 SQL 基础
第 4 周 Spring Boot 入门
第 5 周 任务 API
第 6 周 登录鉴权和测试
```

每周文档都已经拆成“每天做什么、怎么做、看到什么结果、需要能回答什么问题”。

## 你会做出什么

- 控制台版任务管理器：练习类、对象、集合、枚举和方法拆分。
- MySQL 数据库：练习建表、CRUD、查询、索引、事务和表关系。
- Spring Boot 后端：注册登录、JWT 鉴权、任务增删改查、筛选、分页、接口文档和测试。

## 环境准备

需要安装：

- JDK 17
- Maven 3.9+
- MySQL 8+
- Navicat

安装后在终端验证：

```powershell
java -version
mvn -version
mysql --version
```

当前机器如果提示 `java` 或 `mvn` 找不到，说明还没有装好，或没有把安装目录加入 PATH。

## 运行 SQL

在 Navicat 中连接 MySQL，然后依次运行：

1. `sql/01_create_database.sql`
2. `sql/02_create_tables.sql`
3. `sql/03_practice_crud.sql`

如果想清空练习数据，运行：

```sql
source sql/04_reset_demo_data.sql;
```

## 运行后端项目

先确认 `src/main/resources/application.yml` 里的数据库账号密码正确：

```yaml
spring:
  datasource:
    username: root
    password: root
```

启动项目：

```powershell
mvn spring-boot:run
```

健康检查：

```powershell
curl http://localhost:8080/api/health
```

Swagger 接口文档：

```text
http://localhost:8080/swagger-ui.html
```

## 常用接口

注册：

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "alice",
  "password": "password123"
}
```

登录：

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "password123"
}
```

创建任务：

```http
POST /api/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "学习 Java 对象",
  "description": "完成 Task 类练习",
  "status": "TODO",
  "dueDate": "2026-06-01"
}
```

查询任务：

```http
GET /api/tasks?status=TODO&keyword=Java&page=1&size=10
Authorization: Bearer <token>
```

## 测试

```powershell
mvn test
```

测试使用 H2 内存数据库，不会改动你的 MySQL 数据。

## 学习顺序

按这个顺序走：

1. `docs/week-01-java-object.md`
2. `docs/week-02-console-crud.md`
3. `docs/week-03-sql-basics.md`
4. `docs/week-04-sql-spring-boot.md`
5. `docs/week-05-api-crud.md`
6. `docs/week-06-auth-test-delivery.md`

每周文档都有目标、任务和验收标准。不要只看代码，尽量自己先写，再对照项目源码。
