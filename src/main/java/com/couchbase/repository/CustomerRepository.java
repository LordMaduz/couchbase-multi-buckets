package com.couchbase.repository;

import com.couchbase.entity.Customer;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends ReactiveCouchbaseRepository<Customer, String> {
}
