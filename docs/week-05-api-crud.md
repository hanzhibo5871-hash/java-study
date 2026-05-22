# 第 5 周：数据库接入与任务 CRUD API

这一周开始做真正的后端开发：前端或接口工具发来 HTTP 请求，Spring Boot 接收请求，Java 代码处理业务，MyBatis-Plus 操作 MySQL，最后返回 JSON。

CRUD 是后端最核心的基本功：

```text
Create：新增
Read：查询
Update：修改
Delete：删除
```

## 本周学习成果

完成本周后，你应该能够做到：

- 用 Swagger 或接口工具调用任务接口。
- 理解新增任务接口从请求到数据库的完整流程。
- 理解查询、修改、删除任务的代码。
- 看懂分页、状态筛选、关键词搜索。
- 明白 Controller、Service、Mapper、DTO、Entity 的分工。

## 第 1 天：先看接口全貌

启动项目：

```powershell
cd E:\task-manager-api
mvn spring-boot:run
```

打开 Swagger：

```text
http://localhost:8080/swagger-ui.html
```

你会看到任务接口：

```text
GET    /api/tasks
POST   /api/tasks
GET    /api/tasks/{id}
PUT    /api/tasks/{id}
DELETE /api/tasks/{id}
```

### 接口和 SQL 的对应关系

```text
POST /api/tasks        -> INSERT INTO tasks
GET /api/tasks         -> SELECT FROM tasks
GET /api/tasks/{id}    -> SELECT FROM tasks WHERE id = ?
PUT /api/tasks/{id}    -> UPDATE tasks SET ...
DELETE /api/tasks/{id} -> DELETE FROM tasks WHERE id = ?
```

### 今日练习

先只观察 Swagger，不急着调用。

找到 `TaskController.java`，对照这些方法：

```java
list(...)
create(...)
getById(...)
update(...)
delete(...)
```

### 结果要求

你能说出：

- 一个接口由请求方式和路径组成。
- `POST` 常用于新增。
- `GET` 常用于查询。
- `PUT` 常用于修改。
- `DELETE` 常用于删除。

## 第 2 天：理解创建任务接口

打开：

```text
src/main/java/com/example/taskmanager/controller/TaskController.java
```

找到：

```java
@PostMapping
public ApiResponse<TaskResponse> create(@Valid @RequestBody TaskCreateRequest request) {
    Long userId = SecurityUtils.currentUser().id();
    return ApiResponse.ok(taskService.create(userId, request));
}
```

### 逐句理解

- `@PostMapping`：处理 `POST /api/tasks`。
- `@RequestBody`：从 JSON 请求体读取数据。
- `@Valid`：校验请求数据。
- `TaskCreateRequest`：新增任务时需要的字段。
- `SecurityUtils.currentUser().id()`：拿到当前登录用户 ID。
- `taskService.create(...)`：调用业务层创建任务。

打开：

```text
src/main/java/com/example/taskmanager/dto/TaskCreateRequest.java
```

你会看到：

```java
public record TaskCreateRequest(
        @NotBlank
        @Size(max = 100)
        String title,

        @Size(max = 500)
        String description,

        TaskStatus status,

        LocalDate dueDate
) {
}
```

### 今日练习

解释这个 JSON 如何变成 `TaskCreateRequest`：

```json
{
  "title": "练习 Spring Boot",
  "description": "完成任务新增接口",
  "status": "TODO",
  "dueDate": "2026-06-01"
}
```

### 结果要求

你能回答：

- JSON 里的 `title` 对应 `TaskCreateRequest.title()`。
- `@NotBlank` 表示标题不能为空。
- `status` 最终会变成 `TaskStatus` 枚举。

## 第 3 天：理解 Service 创建逻辑

打开：

```text
src/main/java/com/example/taskmanager/service/TaskService.java
```

找到：

```java
public TaskResponse create(Long userId, TaskCreateRequest request) {
    Task task = new Task();
    task.setUserId(userId);
    task.setTitle(request.title());
    task.setDescription(request.description());
    task.setStatus(request.status() == null ? TaskStatus.TODO : request.status());
    task.setDueDate(request.dueDate());
    task.setCreatedAt(LocalDateTime.now());
    task.setUpdatedAt(LocalDateTime.now());
    taskMapper.insert(task);
    return TaskResponse.from(task);
}
```

### 逐句理解

- `new Task()` 创建数据库实体对象。
- `setUserId(userId)` 表示这个任务属于当前用户。
- 如果请求没有传状态，就默认 `TODO`。
- `LocalDateTime.now()` 设置创建和更新时间。
- `taskMapper.insert(task)` 插入数据库。
- `TaskResponse.from(task)` 把 Entity 转成接口响应。

### 今日练习

画出新增任务流程：

```text
JSON 请求
  ↓
TaskCreateRequest
  ↓
TaskController.create
  ↓
TaskService.create
  ↓
Task Entity
  ↓
taskMapper.insert
  ↓
tasks 表新增一行
  ↓
TaskResponse
  ↓
JSON 响应
```

### 结果要求

你能回答：

- 为什么 Controller 不直接调用 `taskMapper.insert`？
- 为什么请求对象是 DTO，数据库对象是 Entity？
- 为什么返回给前端的是 `TaskResponse`？

## 第 4 天：实际调用创建任务

先注册用户：

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "alice",
  "password": "password123"
}
```

响应里会有：

```json
{
  "data": {
    "userId": 1,
    "username": "alice",
    "token": "..."
  }
}
```

复制 `token`。

创建任务：

```http
POST /api/tasks
Authorization: Bearer 你的token
Content-Type: application/json

{
  "title": "练习 Spring Boot",
  "description": "完成任务新增接口",
  "status": "TODO",
  "dueDate": "2026-06-01"
}
```

### 今日练习

新增 3 条任务：

```text
练习 Spring Boot    TODO
练习 MyBatis-Plus  DOING
整理项目 README     DONE
```

然后打开 Navicat 查看 `tasks` 表。

### 结果要求

你应该能在数据库里看到新增的任务。

你能回答：

- 接口调用成功后，数据真的写入 MySQL。
- `Authorization: Bearer token` 用于证明你是谁。
- `user_id` 自动来自当前登录用户，不应该由前端随便传。

## 第 5 天：理解查询任务

Controller：

```java
@GetMapping
public ApiResponse<List<TaskResponse>> list(
        @RequestParam(required = false) TaskStatus status,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
) {
    Long userId = SecurityUtils.currentUser().id();
    return ApiResponse.ok(taskService.list(userId, status, keyword, page, size));
}
```

Service：

```java
LambdaQueryWrapper<Task> query = new LambdaQueryWrapper<Task>()
        .eq(Task::getUserId, userId)
        .eq(status != null, Task::getStatus, status)
        .like(StringUtils.hasText(keyword), Task::getTitle, keyword)
        .orderByDesc(Task::getCreatedAt);
```

### 对应 SQL 思维

```sql
SELECT *
FROM tasks
WHERE user_id = 当前用户ID
  AND status = ?
  AND title LIKE ?
ORDER BY created_at DESC
LIMIT ?, ?;
```

### 今日练习

调用：

```http
GET /api/tasks
Authorization: Bearer 你的token
```

再调用：

```http
GET /api/tasks?status=TODO
Authorization: Bearer 你的token
```

再调用：

```http
GET /api/tasks?keyword=Spring
Authorization: Bearer 你的token
```

### 结果要求

你能回答：

- `@RequestParam` 读取 URL 后面的查询参数。
- `status` 不传时，不按状态筛选。
- `keyword` 不传时，不按标题模糊搜索。
- 后端总是加上 `user_id = 当前用户ID`，防止查到别人的任务。

## 第 6 天：理解修改和删除

修改接口：

```http
PUT /api/tasks/{id}
```

删除接口：

```http
DELETE /api/tasks/{id}
```

`{id}` 是路径参数，例如：

```http
PUT /api/tasks/1
DELETE /api/tasks/1
```

Service 里都会先调用：

```java
Task task = findOwnedTask(userId, id);
```

这个方法保证：

- 任务存在。
- 任务属于当前用户。

如果找不到，就返回 404。

### 今日练习

1. 查询任务列表，拿到一个任务 ID。
2. 修改它的状态为 `DONE`。
3. 查询确认状态变了。
4. 删除这个任务。
5. 再查询这个 ID，应该返回 404。

### 结果要求

你能回答：

- `@PathVariable Long id` 从路径中读取 ID。
- 修改和删除之前必须先确认任务属于当前用户。
- 404 表示资源不存在，或当前用户无权看到它。

## 第 7 天：本周复盘

把任务 CRUD 和 SQL 对应起来：

```text
创建任务：taskMapper.insert(task)
查询任务：taskMapper.selectPage(...)
查询单个：taskMapper.selectOne(...)
修改任务：taskMapper.updateById(task)
删除任务：taskMapper.deleteById(task.getId())
```

再把项目分层对应起来：

```text
Controller：接收 HTTP
DTO：承接请求数据
Service：业务判断
Entity：数据库对象
Mapper：执行数据库操作
Response：返回给调用方
```

## 本周综合练习

完成一个小需求：给任务新增“修改标题”的接口行为测试。

操作步骤：

1. 创建任务，标题是 `旧标题`。
2. 调用 `PUT /api/tasks/{id}`，传：

```json
{
  "title": "新标题"
}
```

3. 查询任务详情，确认标题变成 `新标题`。

## 本周验收清单

完成本周后，请确认你能做到：

- 能注册、登录、复制 token。
- 能带 token 调用任务接口。
- 能新增、查询、修改、删除任务。
- 能在 Navicat 里看到接口写入的数据。
- 能解释 Controller、Service、Mapper、DTO、Entity 的关系。
- 能把接口操作对应到 SQL 操作。
- 能说明为什么用户只能操作自己的任务。
