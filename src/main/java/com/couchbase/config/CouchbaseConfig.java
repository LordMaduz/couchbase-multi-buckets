package com.couchbase.config;

import com.couchbase.entity.Document;
import com.couchbase.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.SimpleCouchbaseClientFactory;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.CouchbaseCustomConversions;
import org.springframework.data.couchbase.core.convert.DateConverters;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.repository.auditing.EnableReactiveCouchbaseAuditing;
import org.springframework.data.couchbase.repository.config.ReactiveRepositoryOperationsMapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;

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

            MappingCouchbaseConverter couchbaseConverter = new MappingCouchbaseConverter();
            couchbaseConverter.setCustomConversions(customConversions());
            couchbaseConverter.afterPropertiesSet();

            ReactiveCouchbaseTemplate documentTemplate = customReactiveCouchbaseTemplate(customCouchbaseClientFactory(documentBucket), couchbaseConverter);
            documentTemplate.setApplicationContext(applicationContext);
            baseMapping.mapEntity(Document.class, documentTemplate);
            ReactiveCouchbaseTemplate employeeTemplate = customReactiveCouchbaseTemplate(customCouchbaseClientFactory(employeeBucket), couchbaseConverter);
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
    public CouchbaseCustomConversions customConversions() {
        return new CouchbaseCustomConversions(Arrays.asList(
                LocalDateTimeToLongConverter.INSTANCE,
                LongToLocalDateTimeConverter.INSTANCE));
    }

    private CouchbaseCustomConversions customBucketsConversions() {
        return new CouchbaseCustomConversions(Arrays.asList(
                LocalDateTimeToLongConverter.INSTANCE,
                LongToLocalDateTimeConverter.INSTANCE));
    }

    @WritingConverter
    public  enum LocalDateTimeToLongConverter implements Converter<LocalDateTime, Long> {
        INSTANCE;

        public Long convert(LocalDateTime source) {
            return DateConverters.DateToLongConverter.INSTANCE.convert(
                    Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(source));
        }
    }

    @ReadingConverter
    public enum LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime> {
        INSTANCE;
        public LocalDateTime convert(Long source) {
            return Jsr310Converters.InstantToLocalDateTimeConverter.INSTANCE.convert(Instant.ofEpochMilli(source));
        }
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