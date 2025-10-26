package com.example.customer_import_batch.controller;

import com.example.customer_import_batch.entity.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/import-customer-csv")
public class ImportCustomerCsvController {
    private JobLauncher jobLauncher;
    private Job job;
    private String csv_file;

    @Autowired
    public ImportCustomerCsvController(
            JobLauncher jobLauncher,
            Job job,
            @Value("${spring.import.file}") String csv_file
    ) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.csv_file = csv_file;
    }

    @GetMapping
    public ResponseEntity<String> getCustomers() {
        JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            return new ResponseEntity<>(csv_file, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(csv_file, HttpStatus.CREATED);
    }

}
