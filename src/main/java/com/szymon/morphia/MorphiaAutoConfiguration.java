package com.szymon.morphia;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.net.UnknownHostException;

@Configuration
public class MorphiaAutoConfiguration {

    private static final String host = "127.0.0.1";
    private static final String port = "27017";
    private static final String dbname = "auth-ms-db";

    public
    @Bean
    MongoClient mongoClient() throws UnknownHostException {
        return (new MongoClient(host + ":" + port));
    }

    @Bean
    public Datastore datastore() throws UnknownHostException {
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.szymon.entity");
        Datastore datastore = morphia.createDatastore(mongoClient(), dbname);
        datastore.ensureIndexes();
        return datastore;
    }

    public
    @Bean
    MongoDbFactory mongoDbFactory() throws UnknownHostException {
        return new SimpleMongoDbFactory(mongoClient(), dbname);
    }

    public
    @Bean
    MongoTemplate mongoTemplate() throws UnknownHostException {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }
}

