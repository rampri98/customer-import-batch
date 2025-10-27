package com.example.customer_import_batch.config;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PartitionerConfig {
    @Bean
    public Partitioner partitioner() {
        return (int gridSize)->{
            int min = 1;
            int max = 1000;
            int targetSize = 500; // fixed chunk size
            Map<String, ExecutionContext> result = new HashMap<>();

            int number = 0;
            int start = min;
            int end = start + targetSize - 1;

            while (start <= max) {
                ExecutionContext context = new ExecutionContext();
                if (end > max) end = max;

                context.putInt("minValue", start);
                context.putInt("maxValue", end);

                result.put("partition" + number, context);

                start += targetSize;
                end += targetSize;
                number++;
            }

//        System.out.println("Partition result: " + result);
            return result;
        };
    }
}
