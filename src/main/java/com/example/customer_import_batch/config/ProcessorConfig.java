package com.example.customer_import_batch.config;

import com.example.customer_import_batch.entity.Customer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessorConfig {
    @Bean
    public ItemProcessor<Customer, Customer> processor() {
        return customer -> {
            System.out.println("Processing customer with index " + customer.getIndex() + " on thread: " + Thread.currentThread().getName() + " at " + System.currentTimeMillis());
            return customer;
        };
    }
}
