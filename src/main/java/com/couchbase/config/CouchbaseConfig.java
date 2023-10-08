package com.couchbase.config;

import com.couchbase.entity.Document;
import com.couchbase.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.SimpleCouchbaseClientFactory;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.repository.auditing.EnableReactiveCouchbaseAuditing;
import org.springframework.data.couchbase.repository.config.ReactiveRepositoryOperationsMapping;


@Configuration
@EnableReactiveCouchbaseAuditing
@RequiredArgsConstructor
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    private final ApplicationContext applicationContext;

    @Value("${app.couchbase.connection-string}")
    private String connectionString;

    @Value("${app.couchbase.user-name}")
    private String userName;

    @Value("${app.couchbase.password}")
    private String password;

    @Value("${app.couchbase.customer-bucket}")
    private String customerBucket;

    @Value("${app.couchbase.document-bucket}")
    private String documentBucket;

    @Value("${app.couchbase.employee-bucket}")
    private String employeeBucket;

    @Override
    public void configureReactiveRepositoryOperationsMapping(ReactiveRepositoryOperationsMapping baseMapping) {
        try {

            final MappingCouchbaseConverter couchbaseConverter = new MappingCouchbaseConverter();
            couchbaseConverter.setCustomConversions(customConversions());
            couchbaseConverter.afterPropertiesSet();

            final ReactiveCouchbaseTemplate documentTemplate = customReactiveCouchbaseTemplate(customCouchbaseClientFactory(documentBucket), couchbaseConverter);
            documentTemplate.setApplicationContext(applicationContext);
            baseMapping.mapEntity(Document.class, documentTemplate);
            
            final ReactiveCouchbaseTemplate employeeTemplate = customReactiveCouchbaseTemplate(customCouchbaseClientFactory(employeeBucket), couchbaseConverter);
            employeeTemplate.setApplicationContext(applicationContext);
            baseMapping.mapEntity(Employee.class, employeeTemplate);
        
        } catch (Exception e) {
            throw e;
        }
    }

    public ReactiveCouchbaseTemplate customReactiveCouchbaseTemplate(CouchbaseClientFactory couchbaseClientFactory,
                                                                     MappingCouchbaseConverter mappingCouchbaseConverter) {
        return new ReactiveCouchbaseTemplate(couchbaseClientFactory, mappingCouchbaseConverter);
    }

    public CouchbaseClientFactory customCouchbaseClientFactory(String bucketName) {
        return new SimpleCouchbaseClientFactory(getConnectionString(), authenticator(), bucketName);
    }

    @Override
    public String getConnectionString() {
        return connectionString;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getBucketName() {
        return customerBucket;
    }
}
