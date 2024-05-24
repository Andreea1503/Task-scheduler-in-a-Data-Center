/* Implement this class. */

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class MyDispatcher extends Dispatcher {
    private volatile int ID = 0;
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
        // check if the scheduling algorithm is round robin and assign the task
        // to the last hostid + 1 mod number of hosts
        if (algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            int index =  (ID + 1) % hosts.size();
            ID = index;
            hosts.get(index).addTask(task);
            // check if the scheduling algorithm is shortest queue and assign
            // the task to the host with the shortest queue
        } else if (algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            int index = minQueueSize(task);
            hosts.get(index).addTask(task);
            // check if the scheduling algorithm is size interval task
            // assignment and assign the task to the host with the same type
        } else if (algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            hosts.get(task.getType().ordinal()).addTask(task);
            // check if the scheduling algorithm is least work left and
            // assign the task to the host with the least work left
        } else if (algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
            int index = minWorkLeft(task);
            hosts.get(index).addTask(task);
        }
    }

    // find the host with the shortest queue
    public int minQueueSize(Task task) {
        int min = Integer.MAX_VALUE;
        // find the minimum queue size
        for (int i = 0; i < hosts.size(); i++) {
            if (hosts.get(i).getQueueSize() < min) {
                min = hosts.get(i).getQueueSize();
            }
        }

        // check if two or more queues have the same size and return the
        // first one
        for (int i = 0; i < hosts.size(); i++) {
            if (hosts.get(i).getQueueSize() == min) {
                return i;
            }
        }
        return 0;
    }

    // round the work left to one decimal place
    public static long roundToOneDecimalPlace(long value) {
        return (long) (Math.round(value * 10) / 10.0);
    }

    // find the host with the least work left
    public int minWorkLeft(Task task) {
        long workLeft;
        long minworkLeft = Long.MAX_VALUE;
        // find the minimum work left
        for (int i = 0; i < hosts.size(); i++) {
            workLeft = roundToOneDecimalPlace(hosts.get(i).getWorkLeft());
            if (workLeft < minworkLeft) {
                minworkLeft = workLeft;
            }
        }

        // check if two or more hosts have the same work left and return
        // the first one
        for (int i = 0; i < hosts.size(); i++) {
            workLeft = roundToOneDecimalPlace(hosts.get(i).getWorkLeft());
            if (workLeft == minworkLeft) {
                return i;
            }
        }
        return 0;
    }
}