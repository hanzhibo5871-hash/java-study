import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskManagerConsoleApp {
    private final List<Task> tasks = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);
    private int nextId = 1;

    public static void main(String[] args) {
        new TaskManagerConsoleApp().run();
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> createTask();
                case "2" -> listTasks();
                case "3" -> updateTaskStatus();
                case "4" -> deleteTask();
                case "0" -> running = false;
                default -> System.out.println("请输入菜单中的数字。");
            }
        }
        System.out.println("已退出任务管理器。");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== 控制台任务管理器 ===");
        System.out.println("1. 新增任务");
        System.out.println("2. 查看任务列表");
        System.out.println("3. 修改任务状态");
        System.out.println("4. 删除任务");
        System.out.println("0. 退出");
        System.out.print("请选择：");
    }

    private void createTask() {
        System.out.print("任务标题：");
        String title = scanner.nextLine();
        System.out.print("任务描述：");
        String description = scanner.nextLine();
        System.out.print("截止日期（例如 2026-06-01，可留空）：");
        String dueDateText = scanner.nextLine();

        LocalDate dueDate = dueDateText.isBlank() ? null : LocalDate.parse(dueDateText);
        Task task = new Task(nextId++, title, description, dueDate);
        tasks.add(task);
        System.out.println("新增成功：" + task);
    }

    private void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("暂无任务。");
            return;
        }
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private void updateTaskStatus() {
        Task task = findTaskByInputId();
        if (task == null) {
            return;
        }

        System.out.print("新状态（TODO/DOING/DONE）：");
        String statusText = scanner.nextLine();
        try {
            task.setStatus(TaskStatus.valueOf(statusText));
            System.out.println("修改成功：" + task);
        } catch (IllegalArgumentException exception) {
            System.out.println("状态只能是 TODO、DOING 或 DONE。");
        }
    }

    private void deleteTask() {
        Task task = findTaskByInputId();
        if (task == null) {
            return;
        }
        tasks.remove(task);
        System.out.println("删除成功。");
    }

    private Task findTaskByInputId() {
        System.out.print("请输入任务 ID：");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException exception) {
            System.out.println("ID 必须是数字。");
            return null;
        }

        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        System.out.println("没有找到这个任务。");
        return null;
    }
}
