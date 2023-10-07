package com.couchbase.repository;

import com.couchbase.entity.Document;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository  extends ReactiveCouchbaseRepository<Document, String> {
}
