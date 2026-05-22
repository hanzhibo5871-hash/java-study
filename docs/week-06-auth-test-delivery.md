# 第 6 周：用户登录、鉴权、测试与交付

这一周把项目变成一个“像真实后端”的作品：用户可以注册登录，任务接口需要登录才能访问，用户不能操作别人的任务，并且项目有测试和说明文档。

## 本周学习成果

完成本周后，你应该能够做到：

- 理解注册、登录、密码加密、JWT 的基本流程。
- 知道为什么接口需要鉴权。
- 理解用户数据隔离。
- 运行自动化测试。
- 根据 README 启动项目并演示核心功能。
- 说清楚这个项目的开发流程和亮点。

## 第 1 天：理解注册

打开：

```text
src/main/java/com/example/taskmanager/controller/AuthController.java
```

注册接口：

```java
@PostMapping("/register")
public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ApiResponse.ok(authService.register(request));
}
```

打开：

```text
src/main/java/com/example/taskmanager/service/AuthService.java
```

重点看：

```java
if (findByUsername(request.username()) != null) {
    throw new BusinessException(HttpStatus.CONFLICT, "Username already exists");
}

User user = new User();
user.setUsername(request.username());
user.setPasswordHash(passwordEncoder.encode(request.password()));
user.setCreatedAt(LocalDateTime.now());
userMapper.insert(user);
```

### 重点理解

- 注册前要检查用户名是否重复。
- 密码不能明文保存。
- `passwordEncoder.encode(...)` 会把密码变成哈希。
- 用户最终保存到 `users` 表。

### 今日练习

注册用户：

```json
{
  "username": "alice",
  "password": "password123"
}
```

再注册一次相同用户名。

### 结果要求

第一次成功。

第二次应该失败，状态码是 409。

你能回答：

- 为什么用户名不能重复？
- 为什么不能把 `password123` 直接存入数据库？
- `users.password_hash` 保存的是什么？

## 第 2 天：理解登录和 JWT

登录接口：

```java
@PostMapping("/login")
public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return ApiResponse.ok(authService.login(request));
}
```

登录逻辑：

```java
User user = findByUsername(request.username());
if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
    throw new BusinessException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
}

String token = jwtService.createToken(user.getId(), user.getUsername());
return new AuthResponse(user.getId(), user.getUsername(), token);
```

### 重点理解

登录不是再次保存用户，而是验证：

```text
用户存在吗？
密码正确吗？
```

验证成功后，后端返回 token。

token 里面包含当前用户的信息，例如用户 ID 和用户名。

### 今日练习

调用登录接口：

```json
{
  "username": "alice",
  "password": "password123"
}
```

再用错误密码登录：

```json
{
  "username": "alice",
  "password": "wrong"
}
```

### 结果要求

正确密码返回 token。

错误密码返回 401。

你能回答：

- 401 表示未认证或认证失败。
- token 是后续访问任务接口的凭证。
- 后端不会把原始密码返回给前端。

## 第 3 天：理解请求鉴权

任务接口需要请求头：

```http
Authorization: Bearer 你的token
```

打开：

```text
src/main/java/com/example/taskmanager/security/JwtAuthenticationFilter.java
```

它会做这些事：

```text
读取 Authorization 请求头
  ↓
判断是否以 Bearer 开头
  ↓
解析 token
  ↓
得到当前用户信息
  ↓
放入 Spring Security 上下文
```

### 今日练习

测试两次：

1. 不带 token 调用：

```http
GET /api/tasks
```

2. 带 token 调用：

```http
GET /api/tasks
Authorization: Bearer 你的token
```

### 结果要求

不带 token 返回 401。

带 token 返回任务列表。

你能回答：

- 为什么任务接口不能允许任何人访问？
- `Bearer` 后面跟的是什么？
- 后端如何知道当前用户是谁？

## 第 4 天：理解用户数据隔离

真实项目中，用户 A 不能看到用户 B 的任务。

关键代码在 `TaskService`：

```java
private Task findOwnedTask(Long userId, Long id) {
    Task task = taskMapper.selectOne(new LambdaQueryWrapper<Task>()
            .eq(Task::getId, id)
            .eq(Task::getUserId, userId));
    if (task == null) {
        throw new BusinessException(HttpStatus.NOT_FOUND, "Task not found");
    }
    return task;
}
```

这里不只是按任务 ID 查，还加了：

```java
.eq(Task::getUserId, userId)
```

### 对应 SQL

```sql
SELECT *
FROM tasks
WHERE id = ?
  AND user_id = 当前用户ID;
```

### 今日练习

1. 注册用户 `alice`。
2. 用 `alice` 创建任务。
3. 注册用户 `bob`。
4. 用 `bob` 的 token 查询 `alice` 的任务 ID。

### 结果要求

`bob` 应该得到 404。

你能回答：

- 为什么这里返回 404，而不是把别人的任务返回出来？
- 为什么所有任务查询都必须带 `user_id` 条件？
- 这就是后端开发中的权限边界。

## 第 5 天：理解统一响应和异常

统一响应类：

```text
src/main/java/com/example/taskmanager/common/ApiResponse.java
```

格式是：

```json
{
  "success": true,
  "message": "OK",
  "data": {}
}
```

异常处理类：

```text
src/main/java/com/example/taskmanager/exception/GlobalExceptionHandler.java
```

它负责把异常变成统一 JSON。

### 今日练习

测试这些错误：

1. 注册时用户名太短。
2. 创建任务时标题为空。
3. 查询不存在的任务 ID。
4. 不带 token 查询任务。

### 结果要求

你能区分：

- 400：请求参数错误。
- 401：没有登录或登录失败。
- 404：资源不存在。
- 409：数据冲突，例如用户名重复。

## 第 6 天：运行测试

测试文件：

```text
src/test/java/com/example/taskmanager/TaskManagerApplicationTests.java
```

运行：

```powershell
cd E:\task-manager-api
mvn test
```

测试会覆盖：

- 重复用户名失败。
- 错误密码登录失败。
- 未登录访问任务接口失败。
- 创建、查询、修改、删除任务。
- 用户不能访问别人的任务。

### 重点理解

测试不是给机器看的形式主义。它帮你确认：

```text
我改代码以后，原来的功能还正常吗？
```

### 今日练习

打开测试代码，找到：

```java
void userCannotAccessAnotherUsersTask()
```

读懂它做了什么。

### 结果要求

你能回答：

- 为什么测试里要创建两个用户？
- 为什么另一个用户访问任务应该返回 404？
- 测试如何模拟 HTTP 请求？

## 第 7 天：项目交付和复盘

现在你要把项目当成一个作品来整理。

你应该能演示：

1. 启动 MySQL。
2. 在 Navicat 中创建数据库和表。
3. 启动 Spring Boot。
4. 打开 Swagger。
5. 注册用户。
6. 登录并复制 token。
7. 创建任务。
8. 查询任务。
9. 修改任务。
10. 删除任务。
11. 运行测试。

## 项目讲述模板

你可以这样介绍这个项目：

```text
这是一个任务管理后端 API。
我先用 Java 控制台版本练习类、对象、集合和方法拆分。
然后用 MySQL 和 Navicat 设计 users、tasks 两张表。
最后用 Spring Boot 实现注册登录、JWT 鉴权和任务 CRUD。
项目采用 Controller、Service、Mapper、DTO、Entity 分层。
测试覆盖了登录失败、未登录访问、任务 CRUD 和用户数据隔离。
```

## 本周综合练习

写一份自己的复盘，回答下面问题：

```text
1. Java 控制台版本和 Spring Boot 版本有什么关系？
2. Task 类和 tasks 表有什么关系？
3. Controller、Service、Mapper 分别负责什么？
4. 为什么需要 JWT？
5. 为什么任务查询必须限制 user_id？
6. 自动化测试帮我确认了哪些功能？
```

## 本周验收清单

完成本周后，请确认你能做到：

- 能注册和登录用户。
- 能解释密码哈希和 JWT 的作用。
- 能带 token 调用任务接口。
- 能解释用户数据隔离。
- 能读懂核心测试用例。
- 能运行 `mvn test`。
- 能完整演示项目从启动到接口调用。
- 能用自己的话讲出这个项目的开发流程。

如果这些都能做到，你已经完成了第一个 Java 后端实战项目。下一阶段可以继续学习：文件上传、角色权限、Redis、Docker、部署、前端联调。
