package com.example.customer_import_batch.config;

import com.example.customer_import_batch.entity.Customer;
import com.example.customer_import_batch.repository.CustomerRepository;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WriterConfig {
    @Bean
    public RepositoryItemWriter<Customer> writer(CustomerRepository customerRepository) {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }
}
