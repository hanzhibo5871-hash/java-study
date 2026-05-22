USE task_manager;

-- 1. 新增用户。真实项目里密码会由 Java 使用 BCrypt 加密，这里只是 SQL 练习。
INSERT INTO users (username, password_hash)
VALUES ('alice', 'demo-password-hash');

-- 2. 新增任务。
INSERT INTO tasks (user_id, title, description, status, due_date)
VALUES (1, '学习 Java 对象', '完成 Task 类练习', 'TODO', '2026-06-01');

INSERT INTO tasks (user_id, title, description, status, due_date)
VALUES (1, '练习 SELECT', '用 Navicat 执行基础查询', 'DOING', '2026-06-03');

-- 3. 查询所有任务。
SELECT id, user_id, title, description, status, due_date, created_at, updated_at
FROM tasks;

-- 4. 按状态查询任务。
SELECT id, title, status
FROM tasks
WHERE status = 'TODO';

-- 5. 模糊搜索标题。
SELECT id, title, status
FROM tasks
WHERE title LIKE '%Java%';

-- 6. 排序和分页。LIMIT 第一个数字表示跳过几条，第二个数字表示取几条。
SELECT id, title, status, created_at
FROM tasks
ORDER BY created_at DESC
LIMIT 0, 10;

-- 7. 修改任务状态。
UPDATE tasks
SET status = 'DONE'
WHERE id = 1;

-- 8. 查询用户和任务的一对多关系。
SELECT u.username, t.title, t.status
FROM users u
JOIN tasks t ON u.id = t.user_id
WHERE u.username = 'alice';

-- 9. 删除任务。
DELETE FROM tasks
WHERE id = 2;

-- 10. 事务练习：两条语句要么都成功，要么都撤销。
START TRANSACTION;

INSERT INTO tasks (user_id, title, description, status)
VALUES (1, '事务练习', '先插入，再决定提交或回滚', 'TODO');

UPDATE tasks
SET status = 'DOING'
WHERE title = '事务练习';

COMMIT;
