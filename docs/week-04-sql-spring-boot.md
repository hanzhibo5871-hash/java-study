# 第 4 周：SQL 进阶与 Spring Boot 入门

这一周你会从“会写 SQL”过渡到“Java 程序如何提供接口”。你不需要一下子理解所有 Spring Boot 源码，先建立整体地图：请求从哪里进来，代码怎么处理，结果怎么返回。

## 本周学习成果

完成本周后，你应该能够做到：

- 理解 `users` 和 `tasks` 为什么分成两张表。
- 理解一对多关系、外键思想、唯一约束、索引。
- 启动 Spring Boot 项目。
- 访问健康检查接口 `/api/health`。
- 看懂 Controller、Entity、DTO 的基本作用。
- 知道数据库字段如何对应 Java 实体类。

## 第 1 天：理解用户和任务的关系

真实项目里，任务不能孤立存在。每个任务都应该属于某个用户。

关系是：

```text
一个用户 -> 多个任务
一个任务 -> 只属于一个用户
```

所以数据库里需要两张表：

```text
users
tasks
```

`tasks` 表里有一个字段：

```sql
user_id BIGINT NOT NULL
```

它保存的是所属用户的 ID。

### 对照例子

`users` 表：

```text
id | username
1  | alice
```

`tasks` 表：

```text
id | user_id | title
1  | 1       | 学习 Java
2  | 1       | 学习 SQL
```

这表示两条任务都属于 `alice`。

### 今日练习

在 Navicat 中执行：

```sql
SELECT u.username, t.title, t.status
FROM users u
JOIN tasks t ON u.id = t.user_id;
```

### 结果要求

你能回答：

- `tasks.user_id` 保存的是什么？
- 为什么任务表不直接保存用户名？
- 一个用户有多个任务时，为什么用两张表更合理？

## 第 2 天：理解约束和索引

看 `users` 表：

```sql
username VARCHAR(50) NOT NULL UNIQUE
```

`UNIQUE` 表示用户名不能重复。

看 `tasks` 表：

```sql
INDEX idx_tasks_user_status (user_id, status)
```

索引用来提高查询速度。

后端经常会查：

```sql
SELECT *
FROM tasks
WHERE user_id = 1 AND status = 'TODO';
```

所以给 `(user_id, status)` 建索引。

### 今日练习

尝试插入两个相同用户名：

```sql
INSERT INTO users (username, password_hash)
VALUES ('same_name', 'demo');

INSERT INTO users (username, password_hash)
VALUES ('same_name', 'demo');
```

第二条应该失败。

### 结果要求

你能回答：

- `UNIQUE` 防止了什么问题？
- 索引不是必须的功能，但能优化查询速度。
- 真实项目里用户名重复会导致登录混乱。

## 第 3 天：启动 Spring Boot

进入项目根目录：

```powershell
cd E:\task-manager-api
```

先确认环境：

```powershell
java -version
mvn -version
```

启动项目：

```powershell
mvn spring-boot:run
```

如果数据库账号密码不是 `root/root`，先修改：

```text
src/main/resources/application.yml
```

重点配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/task_manager
    username: root
    password: root
```

### 今日练习

浏览器访问：

```text
http://localhost:8080/api/health
```

### 结果要求

你应该看到类似 JSON：

```json
{
  "success": true,
  "message": "OK",
  "data": "task-manager-api is running"
}
```

你能回答：

- `8080` 是后端服务端口。
- `/api/health` 是接口地址。
- 返回的是 JSON，不是网页。

## 第 4 天：理解 Controller

打开：

```text
src/main/java/com/example/taskmanager/controller/HealthController.java
```

代码大概是：

```java
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ApiResponse<String> health() {
        return ApiResponse.ok("task-manager-api is running");
    }
}
```

### 逐行理解

- `@RestController`：告诉 Spring，这是一个接口控制器。
- `@RequestMapping("/api/health")`：这个类负责 `/api/health` 路径。
- `@GetMapping`：处理 GET 请求。
- `ApiResponse.ok(...)`：返回统一格式的成功响应。

### 今日练习

新增一个临时接口：

```java
@GetMapping("/hello")
public ApiResponse<String> hello() {
    return ApiResponse.ok("Hello Java");
}
```

访问：

```text
http://localhost:8080/api/health/hello
```

### 结果要求

你应该看到：

```json
{
  "success": true,
  "message": "OK",
  "data": "Hello Java"
}
```

练习完成后可以保留，也可以删除这个临时接口。

## 第 5 天：理解 Entity

打开：

```text
src/main/java/com/example/taskmanager/entity/Task.java
```

它和控制台版的 `Task.java` 很像，但多了数据库相关注解：

```java
@TableName("tasks")
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
}
```

### 重点理解

- `@TableName("tasks")` 表示这个类对应 `tasks` 表。
- `@TableId` 表示主键。
- `Long id` 对应数据库里的 `BIGINT id`。
- `userId` 对应数据库里的 `user_id`。
- `dueDate` 对应数据库里的 `due_date`。

### 今日练习

写出这张对应表：

```text
Java 字段        数据库字段
id              id
userId          user_id
title           title
description     description
status          status
dueDate         due_date
createdAt       created_at
updatedAt       updated_at
```

### 结果要求

你能解释：

- Entity 不是随便写的类，它对应数据库表。
- Java 常用驼峰命名，数据库常用下划线命名。
- MyBatis-Plus 能帮我们做这种映射。

## 第 6 天：理解项目分层

打开这些目录：

```text
controller
service
mapper
entity
dto
```

先记住分工：

```text
Controller：接收请求，返回响应
Service：处理业务逻辑
Mapper：访问数据库
Entity：对应数据库表
DTO：接口请求和响应对象
```

以创建任务为例：

```text
HTTP 请求
  ↓
TaskController
  ↓
TaskService
  ↓
TaskMapper
  ↓
MySQL tasks 表
```

### 今日练习

不要改代码，只做阅读：

1. 找到 `TaskController`。
2. 找到 `TaskService`。
3. 找到 `TaskMapper`。
4. 找到 `Task` Entity。
5. 找到 `TaskCreateRequest` DTO。

### 结果要求

你能用自己的话说出：

- 请求为什么先到 Controller？
- 为什么数据库操作不直接写在 Controller 里？
- DTO 和 Entity 为什么分开？

## 第 7 天：打开 Swagger

启动项目后访问：

```text
http://localhost:8080/swagger-ui.html
```

Swagger 是接口文档页面。它能显示项目提供了哪些接口。

你应该能看到：

- `/api/auth/register`
- `/api/auth/login`
- `/api/tasks`
- `/api/health`

### 今日练习

只观察，不急着调用：

1. 展开 `/api/health`。
2. 查看它的请求方式是不是 GET。
3. 展开 `/api/auth/register`。
4. 观察请求体需要哪些字段。

## 本周综合练习

请用自己的话写一段说明：

```text
当我访问 http://localhost:8080/api/health 时，
浏览器发出一个 GET 请求，
Spring Boot 找到 HealthController，
执行 health 方法，
最后返回 JSON。
```

再写一段：

```text
Task Entity 和 tasks 表的关系是：
...
```

## 本周验收清单

完成本周后，请确认你能做到：

- 能解释 `users` 和 `tasks` 的一对多关系。
- 能解释 `user_id` 的作用。
- 能理解唯一约束和索引的基本用途。
- 能启动 Spring Boot 项目。
- 能访问 `/api/health`。
- 能找到 Controller、Service、Mapper、Entity、DTO。
- 能说清楚一个请求从 Controller 到数据库的大致流程。
