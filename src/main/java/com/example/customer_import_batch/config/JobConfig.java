package com.example.customer_import_batch.config;

import com.example.customer_import_batch.entity.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
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
    private final int chunkSize;

    @Autowired
    public JobConfig(FlatFileItemReader<Customer> reader, ItemProcessor<Customer, Customer> processor, RepositoryItemWriter<Customer> writer, JobRepository jobRepository, JpaTransactionManager transactionManager, TaskExecutor taskExecutor,@Value("${spring.batch.chunk.size}") int chunkSize) {
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.taskExecutor = taskExecutor;
        this.chunkSize = chunkSize;
    }

    @Bean
    public Job runJob() {
        return new JobBuilder("customer", jobRepository)
                .start(step())
                .build();

    }

    @Bean
    public Step step() {
        return new StepBuilder("customerCsvStep", jobRepository).<Customer, Customer>chunk(chunkSize, transactionManager)
                .reader(reader)
                .writer(writer)
                .taskExecutor(taskExecutor)
                .build();
    }
}
