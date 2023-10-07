package com.couchbase.repository;

import com.couchbase.entity.Employee;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository  extends ReactiveCouchbaseRepository<Employee, String> {
}
