package com.example.customer_import_batch.config;

import com.example.customer_import_batch.entity.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class JobConfig {
    private final FlatFileItemReader<Customer> reader;
    private final ItemProcessor<Customer, Customer> processor;
    private final RepositoryItemWriter<Customer> writer;
    private final JobRepository jobRepository;
    private final JpaTransactionManager transactionManager;
    private final TaskExecutor taskExecutor;
    private final Partitioner partitioner;
    private final int chunkSize;

    @Autowired
    public JobConfig(FlatFileItemReader<Customer> reader, ItemProcessor<Customer, Customer> processor, RepositoryItemWriter<Customer> writer, JobRepository jobRepository, JpaTransactionManager transactionManager, TaskExecutor taskExecutor, Partitioner partitioner, @Value("${spring.batch.chunk.size}") int chunkSize) {
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.taskExecutor = taskExecutor;
        this.partitioner = partitioner;
        this.chunkSize = chunkSize;
    }

    @Bean
    public Job runJob() {
        return new JobBuilder("customer", jobRepository)
                .start(masterStep())
                .build();

    }

    @Bean
    public Step slaveStep() {
        return new StepBuilder("slaveStep", jobRepository).<Customer, Customer>chunk(chunkSize, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }


    @Bean
    public Step masterStep() {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner(slaveStep().getName(), partitioner)
                .partitionHandler(partitionHandler())
                .build();
    }

    public PartitionHandler partitionHandler() {
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(4);
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor);
        taskExecutorPartitionHandler.setStep(slaveStep());

        return taskExecutorPartitionHandler;
    }
}
