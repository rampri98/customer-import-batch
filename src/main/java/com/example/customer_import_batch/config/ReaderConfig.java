package com.example.customer_import_batch.config;

import com.example.customer_import_batch.entity.Customer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class ReaderConfig {
    @Bean
    public FlatFileItemReader<Customer> reader(@Value("${spring.import.file}") String csv_file) {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("src/main/resources/input/"+csv_file));
        reader.setName("customerReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("Index", "Customer Id", "First Name", "Last Name", "Company", "City", "Country", "Phone 1", "Phone 2", "Email", "Subscription Date", "Website");
                setDelimiter(",");
            }});
            setFieldSetMapper(fieldSet -> {
                Customer customer = new Customer();
                customer.setIndex(fieldSet.readInt("Index"));
                customer.setCustomerId(fieldSet.readString("Customer Id"));
                customer.setFirstName(fieldSet.readString("First Name"));
                customer.setLastName(fieldSet.readString("Last Name"));
                customer.setCompany(fieldSet.readString("Company"));
                customer.setCity(fieldSet.readString("City"));
                customer.setCountry(fieldSet.readString("Country"));
                customer.setPhone1(fieldSet.readString("Phone 1"));
                customer.setPhone2(fieldSet.readString("Phone 2"));
                customer.setEmail(fieldSet.readString("Email"));
                customer.setSubscriptionDate(LocalDate.parse(fieldSet.readString("Subscription Date"), DateTimeFormatter.ISO_LOCAL_DATE));
                customer.setWebsite(fieldSet.readString("Website"));
                return customer;
            });
        }});
        return reader;
    }
}
