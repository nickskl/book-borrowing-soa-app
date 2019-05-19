package ru.nikolay.service.gateway.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import ru.nikolay.service.gateway.queue.TaskQueue;

@Configuration
public class AppConfig {
    @Bean
    public TaskExecutor taskExecutor(){
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public CommandLineRunner schedulingRunner(TaskExecutor executor, @Qualifier("retryRequestQueue") TaskQueue taskQueue) {
        return args -> executor.execute(taskQueue);
    }

    @Bean
    public CommandLineRunner schedulingStatRunner(TaskExecutor executor, @Qualifier("statisticsQueue") TaskQueue taskQueue) {
        return args -> executor.execute(taskQueue);
    }

    @Bean(name = "retryRequestQueue")
    @Scope("singleton")
    public  TaskQueue taskQueue() {
        return new TaskQueue();
    }

    @Bean(name = "statisticsQueue")
    @Scope("singleton")
    public  TaskQueue statisticsQueue() {
        return new TaskQueue();
    }

}
