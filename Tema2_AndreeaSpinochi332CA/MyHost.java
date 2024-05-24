import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;

/* Implement this class. */
public class MyHost extends Host {
    private double lastFinish = 0;
    private boolean isShutdown = false;
    private PriorityBlockingQueue<Task> tasks = new PriorityBlockingQueue<>(20, new Comparator<Task>() {
        @Override
        // compare descending by priority, then ascending by id
        public int compare(Task task1, Task task2) {
            if (task1.getPriority() == task2.getPriority()) {
                return task1.getId() - task2.getId();
            } else {
                return task2.getPriority() - task1.getPriority();
            }
        }
    });

    private Task currentTask = null;


    @Override
    public void run() {
        // check if the host is not shutdown and the queue is not empty
        while (!isShutdown) {
            if (tasks.isEmpty()) {
                continue;
            }
            try {
                // get the time of the current task
                lastFinish = Timer.getTimeDouble();
                currentTask = tasks.poll();
                // sleep for the duration of the current task
                sleep(currentTask.getLeft());
                // add the finish time of the current task
                currentTask.finish();
                currentTask = null;
            } catch (InterruptedException e) {
                // if the task is interrupted, set the time remaining and add it back to the queue
                long elapsedTime = Math.round(Timer.getTimeDouble() - lastFinish) * 1000;
                currentTask.setLeft(currentTask.getDuration() - elapsedTime);
                tasks.offer(currentTask);
            }
        }
    }

    @Override
    public void addTask(Task task) {
        // add the task to the queue
        tasks.offer(task);

        // check if the current task is preemptible and has a lower priority than the new task
        if (currentTask != null && currentTask.isPreemptible() && currentTask.getPriority() < task.getPriority()) {
            interrupt();
        }
    }

    @Override
    public int getQueueSize() {
        return tasks.size() + (currentTask == null ? 0 : 1);
    }

    @Override
    public long getWorkLeft() {
        long workLeft = 0;
        for (Task task : tasks) {
            workLeft += task.getLeft();
        }
        // add the time remaining time of the current task to the work left
        long elapsedTime = Math.round(Timer.getTimeDouble() - lastFinish) * 1000;
        workLeft += currentTask == null ? 0 : currentTask.getDuration() - elapsedTime;
        return workLeft;
    }

    @Override
    public void shutdown() {
        isShutdown = true;
    }
}
