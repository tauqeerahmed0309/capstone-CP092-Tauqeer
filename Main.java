import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static ArrayList<Task> tasks = new ArrayList<>();

    static int nextId = 1;

    static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // TASK CLASS
    static class Task {

        int id;
        String title;
        LocalDateTime dueDate;

        boolean important;
        double effort;

        boolean completed;
        LocalDateTime completedAt;

        Task(int id, String title, LocalDateTime dueDate,
             boolean important, double effort) {

            this.id = id;
            this.title = title;
            this.dueDate = dueDate;
            this.important = important;
            this.effort = effort;

            this.completed = false;
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== PRIORITY PRO =====");
            System.out.println("1. Add Task");
            System.out.println("2. View Prioritised Tasks");
            System.out.println("3. Complete Task");
            System.out.println("4. View Overdue Tasks");
            System.out.println("5. Weekly Productivity Report");
            System.out.println("6. Exit");

            System.out.print("Enter choice: ");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
                continue;
            }

            switch (choice) {

                case 1:
                    addTask();
                    break;

                case 2:
                    showTasks();
                    break;

                case 3:
                    completeTask();
                    break;

                case 4:
                    showOverdueTasks();
                    break;

                case 5:
                    weeklyReport();
                    break;

                case 6:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // 1. ADD TASK
    static void addTask() {

        System.out.print("Enter task title: ");
        String title = sc.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty.");
            return;
        }

        LocalDateTime dueDate;

        try {
            System.out.print("Enter due date (yyyy-MM-dd HH:mm): ");

            dueDate = LocalDateTime.parse(
                    sc.nextLine(), formatter
            );

        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return;
        }

        System.out.print("Is it important? (yes/no): ");
        String answer = sc.nextLine().trim();

        if (!answer.equalsIgnoreCase("yes")
                && !answer.equalsIgnoreCase("no")) {
            System.out.println("Enter yes or no.");
            return;
        }

        boolean important = answer.equalsIgnoreCase("yes");

        System.out.print("Enter effort in hours: ");

        double effort;

        try {
            effort = Double.parseDouble(sc.nextLine());

            if (effort <= 0) {
                System.out.println("Effort must be positive.");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid effort.");
            return;
        }

        Task task = new Task(
                nextId++, title, dueDate, important, effort
        );

        tasks.add(task);

        System.out.println("Task added successfully!");
        System.out.println("Quadrant: " + getQuadrant(task));
    }

    // 2. CHECK WHETHER TASK IS URGENT
    static boolean isUrgent(Task task) {

        LocalDateTime now = LocalDateTime.now();

        return !task.dueDate.isAfter(now.plusHours(24));
    }

    // 3. CLASSIFY INTO FOUR QUADRANTS
    static String getQuadrant(Task task) {

        boolean urgent = isUrgent(task);

        if (urgent && task.important) {
            return "Q1 - DO FIRST";
        }

        else if (!urgent && task.important) {
            return "Q2 - SCHEDULE";
        }

        else if (urgent && !task.important) {
            return "Q3 - DELEGATE";
        }

        else {
            return "Q4 - DO LATER";
        }
    }

    // 4. ASSIGN PRIORITY
    static int getPriority(Task task) {

        boolean urgent = isUrgent(task);

        if (urgent && task.important) {
            return 1;
        }

        else if (!urgent && task.important) {
            return 2;
        }

        else if (urgent && !task.important) {
            return 3;
        }

        else {
            return 4;
        }
    }

    // 5. SORT TASKS BY PRIORITY AND DUE DATE
    static ArrayList<Task> sortedTasks() {

        ArrayList<Task> sorted = new ArrayList<>(tasks);

        Collections.sort(sorted, (a, b) -> {

            // First compare priority
            int result = Integer.compare(
                    getPriority(a), getPriority(b)
            );

            // If priority is same, compare due dates
            if (result == 0) {
                return a.dueDate.compareTo(b.dueDate);
            }

            return result;
        });

        return sorted;
    }

    // 6. DISPLAY PRIORITISED TASKS
    static void showTasks() {

        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        ArrayList<Task> sorted = sortedTasks();

        System.out.println("\n===== PRIORITISED TASKS =====");

        for (Task task : sorted) {

            String status = task.completed
                    ? "COMPLETED" : "PENDING";

            System.out.println("\nTask ID: " + task.id);
            System.out.println("Title: " + task.title);
            System.out.println("Quadrant: " + getQuadrant(task));

            System.out.println("Due Date: "
                    + task.dueDate.format(formatter));

            System.out.println("Effort: "
                    + task.effort + " hours");

            System.out.println("Status: " + status);

            if (!task.completed
                    && task.dueDate.isBefore(LocalDateTime.now())) {
                System.out.println("OVERDUE!");
            }
        }
    }

    // 7. MARK TASK AS COMPLETED
    static void completeTask() {

        showTasks();

        System.out.print("\nEnter Task ID to complete: ");

        int id;

        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid task ID.");
            return;
        }

        for (Task task : tasks) {

            if (task.id == id && !task.completed) {

                task.completed = true;

                // Store completion date and time
                task.completedAt = LocalDateTime.now();

                System.out.println("Task completed successfully!");
                return;
            }
        }

        System.out.println("Task not found or already completed.");
    }

    // 8. DISPLAY OVERDUE TASKS
    static void showOverdueTasks() {

        System.out.println("\n===== OVERDUE TASKS =====");

        boolean found = false;

        for (Task task : tasks) {

            if (!task.completed
                    && task.dueDate.isBefore(LocalDateTime.now())) {

                System.out.println("ID: " + task.id
                        + " | Title: " + task.title
                        + " | Due: " + task.dueDate.format(formatter));

                found = true;
            }
        }

        if (!found) {
            System.out.println("No overdue tasks!");
        }
    }

    // 9. WEEKLY PRODUCTIVITY REPORT
    static void weeklyReport() {

        LocalDate today = LocalDate.now();

        // Get Monday of current week
        LocalDate startOfWeek = today.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );

        // Get Sunday of current week
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        int completedCount = 0;
        int onTimeCount = 0;

        for (Task task : tasks) {

            if (task.completed
                    && task.completedAt != null) {

                LocalDate completedDate =
                        task.completedAt.toLocalDate();

                // Check whether completed this week
                if (!completedDate.isBefore(startOfWeek)
                        && !completedDate.isAfter(endOfWeek)) {

                    completedCount++;

                    // Check whether completed before deadline
                    if (!task.completedAt.isAfter(task.dueDate)) {
                        onTimeCount++;
                    }
                }
            }
        }

        double percentage = 0;

        if (completedCount > 0) {
            percentage = (onTimeCount * 100.0)
                    / completedCount;
        }

        System.out.println("\n===== WEEKLY REPORT =====");

        System.out.println("Week: " + startOfWeek
                + " to " + endOfWeek);

        System.out.println("Completed Tasks: "
                + completedCount);

        System.out.println("Completed On Time: "
                + onTimeCount);

        System.out.println("Completed Late: "
                + (completedCount - onTimeCount));

        System.out.printf("On-Time Percentage: %.2f%%\n",
                percentage);
    }
}