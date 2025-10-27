package com.example.customer_import_batch.partition;

import io.micrometer.common.lang.NonNull;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class CustomPartitioner implements Partitioner {
    @Override
    @NonNull
    public Map<String, ExecutionContext> partition(int gridSize) {
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
    }
}
