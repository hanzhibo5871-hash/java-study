# 第 3 周：MySQL 与 SQL 基础

前两周的数据都保存在 `ArrayList` 里，程序一关就没了。从这一周开始，你要学习数据库：把任务永久保存下来。

你已经用过 Navicat，所以这一周不要求你先背大量 SQL。我们会从“看得见的表格”开始，再逐步写 SQL。

## 本周学习成果

完成本周后，你应该能够做到：

- 用 Navicat 连接 MySQL。
- 创建 `task_manager` 数据库。
- 创建 `users` 和 `tasks` 两张表。
- 手写 `INSERT`、`SELECT`、`UPDATE`、`DELETE`。
- 理解主键、字段、数据类型、条件查询、排序、分页。
- 明白 Java 对象和数据库表之间的关系。

## 第 1 天：数据库、表、行、列

先建立一个直觉：

```text
数据库：task_manager
表：tasks
一行：一个任务
一列：任务的一个属性
```

对照 Java：

```text
Task 类           -> tasks 表
Task 对象         -> tasks 表中的一行
title 字段        -> title 列
status 字段       -> status 列
dueDate 字段      -> due_date 列
```

### 今日操作

打开 Navicat：

1. 连接你的 MySQL。
2. 新建查询窗口。
3. 打开并执行：

```text
sql/01_create_database.sql
```

内容是：

```sql
CREATE DATABASE IF NOT EXISTS task_manager
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE task_manager;
```

### 结果要求

Navicat 左侧应该出现 `task_manager` 数据库。

你能回答：

- `CREATE DATABASE` 是创建什么？
- `IF NOT EXISTS` 为什么有用？
- `utf8mb4` 为什么适合保存中文？

## 第 2 天：创建表

执行：

```text
sql/02_create_tables.sql
```

这会创建两张表：

- `users`
- `tasks`

先重点看 `tasks`：

```sql
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务 ID',
    user_id BIGINT NOT NULL COMMENT '所属用户 ID',
    title VARCHAR(100) NOT NULL COMMENT '任务标题',
    description VARCHAR(500) NULL COMMENT '任务描述',
    status VARCHAR(20) NOT NULL DEFAULT 'TODO' COMMENT '任务状态：TODO/DOING/DONE',
    due_date DATE NULL COMMENT '截止日期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

### 重点理解

- `id` 是主键，唯一标识一条任务。
- `BIGINT` 是整数类型，适合做 ID。
- `VARCHAR(100)` 是最多 100 个字符的字符串。
- `NOT NULL` 表示不能为空。
- `NULL` 表示可以为空。
- `DEFAULT 'TODO'` 表示默认值。
- `DATE` 保存日期。
- `DATETIME` 保存日期和时间。

### 今日练习

在 Navicat 中打开 `tasks` 表设计，逐个查看字段：

```text
id
user_id
title
description
status
due_date
created_at
updated_at
```

### 结果要求

你能把每个字段和 `Task.java` 对应起来：

```text
Task.id          -> tasks.id
Task.title       -> tasks.title
Task.description -> tasks.description
Task.status      -> tasks.status
Task.dueDate     -> tasks.due_date
```

你也要注意：数据库里多了 `user_id`、`created_at`、`updated_at`，这是为了真实项目需要。

## 第 3 天：新增数据 `INSERT`

先插入一个用户：

```sql
INSERT INTO users (username, password_hash)
VALUES ('alice', 'demo-password-hash');
```

再插入一个任务：

```sql
INSERT INTO tasks (user_id, title, description, status, due_date)
VALUES (1, '学习 Java 对象', '完成 Task 类练习', 'TODO', '2026-06-01');
```

### 重点理解

`INSERT INTO tasks (...)` 表示向 `tasks` 表新增数据。

括号里的字段：

```sql
(user_id, title, description, status, due_date)
```

必须和 `VALUES` 中的数据顺序对应：

```sql
(1, '学习 Java 对象', '完成 Task 类练习', 'TODO', '2026-06-01')
```

### 今日练习

自己再新增两条任务：

```sql
INSERT INTO tasks (user_id, title, description, status, due_date)
VALUES (1, '学习 SQL 查询', '练习 SELECT 语句', 'DOING', '2026-06-03');

INSERT INTO tasks (user_id, title, description, status, due_date)
VALUES (1, '整理学习笔记', '总结 Java 和 SQL 的关系', 'DONE', '2026-06-05');
```

### 结果要求

`tasks` 表里至少有 3 条任务。

你能回答：

- `INSERT` 是新增数据。
- 字段顺序和数据顺序必须对应。
- `user_id = 1` 表示这些任务属于 ID 为 1 的用户。

## 第 4 天：查询数据 `SELECT`

查询所有任务：

```sql
SELECT id, user_id, title, description, status, due_date
FROM tasks;
```

只查询标题和状态：

```sql
SELECT title, status
FROM tasks;
```

### 重点理解

`SELECT` 后面写你想看的列。

`FROM` 后面写你从哪张表查。

### 今日练习

写 3 条查询：

```sql
SELECT * FROM tasks;

SELECT id, title FROM tasks;

SELECT title, due_date FROM tasks;
```

### 结果要求

你能解释：

- `*` 表示所有列。
- 不同 `SELECT` 可以返回不同列。
- 查询不会修改数据，只是查看数据。

## 第 5 天：条件查询 `WHERE`

按状态查询：

```sql
SELECT id, title, status
FROM tasks
WHERE status = 'TODO';
```

按 ID 查询：

```sql
SELECT id, title, status
FROM tasks
WHERE id = 1;
```

模糊搜索标题：

```sql
SELECT id, title, status
FROM tasks
WHERE title LIKE '%Java%';
```

### 重点理解

- `WHERE` 表示筛选条件。
- `=` 表示精确相等。
- `LIKE '%Java%'` 表示标题中包含 `Java`。

### 今日练习

写出这些查询：

```sql
-- 查询 DOING 状态的任务

-- 查询 DONE 状态的任务

-- 查询标题里包含 SQL 的任务
```

### 结果要求

你能把控制台项目中的“按状态筛选任务”和 SQL 对应起来：

```java
if (task.getStatus() == status)
```

对应 SQL：

```sql
WHERE status = 'TODO'
```

## 第 6 天：修改和删除 `UPDATE` / `DELETE`

修改任务状态：

```sql
UPDATE tasks
SET status = 'DONE'
WHERE id = 1;
```

删除任务：

```sql
DELETE FROM tasks
WHERE id = 2;
```

### 极其重要

`UPDATE` 和 `DELETE` 一定要带 `WHERE`。

危险写法：

```sql
UPDATE tasks SET status = 'DONE';
DELETE FROM tasks;
```

第一条会把所有任务都改成已完成。

第二条会删除所有任务。

### 今日练习

1. 新增一条测试任务。
2. 查询它的 ID。
3. 用 `UPDATE` 修改它的状态。
4. 用 `DELETE` 删除它。
5. 再查询确认它不存在。

### 结果要求

你能回答：

- 为什么修改和删除必须带 `WHERE`？
- `WHERE id = ?` 为什么常用于定位一条数据？
- SQL 里的删除和 Java 里的 `tasks.remove(task)` 有什么相似之处？

## 第 7 天：排序、分页、事务

按创建时间倒序：

```sql
SELECT id, title, status, created_at
FROM tasks
ORDER BY created_at DESC;
```

分页：

```sql
SELECT id, title, status, created_at
FROM tasks
ORDER BY created_at DESC
LIMIT 0, 10;
```

事务：

```sql
START TRANSACTION;

INSERT INTO tasks (user_id, title, description, status)
VALUES (1, '事务练习', '先插入，再提交', 'TODO');

UPDATE tasks
SET status = 'DOING'
WHERE title = '事务练习';

COMMIT;
```

### 重点理解

- `ORDER BY created_at DESC` 表示按创建时间从新到旧排序。
- `LIMIT 0, 10` 表示从第 0 条开始，取 10 条。
- 事务可以把多条 SQL 当作一个整体。

## 本周综合练习

不用复制现成脚本，自己手写完成：

1. 新增一个用户 `bob`。
2. 给 `bob` 新增 3 条任务。
3. 查询 `bob` 的所有任务。
4. 查询 `TODO` 状态的任务。
5. 把其中一条任务改成 `DONE`。
6. 删除一条任务。
7. 按创建时间倒序查询剩余任务。

## 本周验收清单

完成本周后，请确认你能做到：

- 能用 Navicat 执行 SQL。
- 能创建数据库和表。
- 能解释主键、字段、数据类型、默认值。
- 能手写 `INSERT`、`SELECT`、`UPDATE`、`DELETE`。
- 能使用 `WHERE`、`LIKE`、`ORDER BY`、`LIMIT`。
- 能说清楚 Java 对象和数据库表之间的对应关系。
- 能理解为什么后端项目需要数据库。
