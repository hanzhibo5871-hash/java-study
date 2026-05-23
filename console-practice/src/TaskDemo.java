public class TaskDemo {
    public static void main(String[] args){
        Task task = new Task(1,"学习JAVA对象","简介",null);
        task.setStatus(TaskStatus.TODO);
        task.setTitle("学习 Java 封装");
        task.setDescription("理解 private 和 get/set");
        task.setStatus(TaskStatus.DOING);
        System.out.println(task);
    }
}