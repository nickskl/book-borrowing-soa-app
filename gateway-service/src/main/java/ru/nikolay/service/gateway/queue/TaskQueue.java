package ru.nikolay.service.gateway.queue;

import org.springframework.stereotype.Component;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

@Component
public class TaskQueue implements Runnable {
    private LinkedBlockingQueue<Supplier<Boolean>> taskQueue = new LinkedBlockingQueue<>();

    public void run() {
        while (true) {
            try {
                Supplier<Boolean> task = taskQueue.take();
                if (!task.get()) {
                    taskQueue.put(task);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void addTask(Supplier<Boolean> task) {
        try {
            taskQueue.put(task);
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
