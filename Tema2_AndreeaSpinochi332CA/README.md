# Homeowrk 2 - Task scheduler in a DC #

## Implementation ##
The implementation of the task scheduler is based on the flow explained in
the homework:
## Dispatcher ##
The dispatcher is responsible for assigning the tasks to the hosts. It
assigns the tasks to the hosts depending on which algorithm is used:

1 - Using the ```Round Robin``` algorithm, the dispatcher assigns the tasks to the
```(i + 1)%n``` node, where n is the number of nodes, starting from the node 0. Here
is used the ID as a volatile variable, so it can be modified by multiple threads
at the same time.

2 - Using the ```Shortest Queue``` algorithm, the dispatcher assigns the tasks to the
node with the shortest queue. I used a separate function for this algorithm, which 
checks the size of all the queues and stores the minimum value in a variable. Then, 
it checks the size of all the queues again and two queues have the same size, it
assigns the task to the host with the shortest ID. The size of the queue from the host
is calculated by using the ```size()``` function already implemented in the ```Queue```
and +1 if the current host has a task running.

3 - Using the ```Size Interval Task Assignment``` algorithm, the dispatcher assigns the
tasks to the node based on the task type. There is already an enum implemented for the
types where the tasks are mapped like this: ```SHORT -> 0, MEDIUM -> 1, LONG -> 2```.
So, for every task that comes in dispatcher I take the ordinal mapped to the type and
assign it to the node(because it represents the ID of the node).

4 - Using the ```Least Work Left``` algorithm, the dispatcher assigns the tasks to the
node with the least work left. I used a separate function for this algorithm, which is 
the same one as I used for the ```Shortest Queue``` algorithm, but instead of checking
the size of the queue, it checks the work left of the host and rounded it to one decimal
so that 2,95 and 2,9 will be viewed as the same value. The work left of the host is
calculated by using the ```getWorkLeft()``` function where I take the work left of every
host in the queue and sum them up and add the work left of the current task.

## Host ##
The host is responsible for executing the tasks. It has a priority queue where
the tasks are stored. The priority queue is implemented using a ```PriorityBlockingQueue```
which is a thread-safe implementation of the priority queue. The priority queue is
sorted by the priority of the tasks and if two tasks have the same priority, they are
sorted by ID. The method ```addTask``` is called in the dispatcher and it adds the task
to the priority queue, then it checks if the current task that runs on the node is preemptible
and has a lower priority than the task that just came in. If it is, then it interrupts the
current task. The method ```run``` from the host checks if the node is shutdown and if it is
not, check if the queue is empty. If it is not, it takes the first task from the queue and
executes it. The thread sleeps for the duration of the task if it is not interrupted, it adds
the finish time. If it is interrupted, it calculates the remaining time of the tasks and adds
it back to the queue. 


