package com.couchbase;

import com.couchbase.entity.Customer;
import com.couchbase.entity.Document;
import com.couchbase.entity.Employee;
import com.couchbase.repository.CustomerRepository;
import com.couchbase.repository.DocumentRepository;
import com.couchbase.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.couchbase.core.convert.DateConverters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestController {

    private final CustomerRepository customerRepository;
    private final DocumentRepository documentRepository;

    private final EmployeeRepository employeeRepository;

    @GetMapping
    public Mono<String> test(){
        Customer customer = Customer.builder().build();
        Document document = Document.builder().build();
        Employee employee = Employee.builder().build();

        return customerRepository.save(customer).flatMap(cus->
                documentRepository.save(document)).flatMap(doc-> employeeRepository.save(employee)).then(Mono.just("Hello"));
    }

    @GetMapping("/{id}")
    public Mono<Document> test(@PathVariable final String id){
        return documentRepository.findById(id);
    }

}
