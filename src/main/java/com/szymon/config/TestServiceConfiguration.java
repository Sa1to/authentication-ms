package com.szymon.config;

import com.mongodb.MongoClient;
import com.sendgrid.SendGrid;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.net.UnknownHostException;

@Configuration
@Profile("test")
public class TestServiceConfiguration {

    private static final String host = "127.0.0.1";
    private static final String port = "27017";
    private static final String dbname = "test-auth";

    @Bean
    public MongoClient mongoClient() throws UnknownHostException {
        return (new MongoClient(host + ":" + port));
    }

    @Bean
    public Datastore datastore() throws UnknownHostException {
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.szymon.domain");
        Datastore datastore = morphia.createDatastore(mongoClient(), dbname);
        datastore.ensureIndexes();
        return datastore;
    }

    @Bean
    public MongoDbFactory mongoDbFactory() throws UnknownHostException {
        return new SimpleMongoDbFactory(mongoClient(), dbname);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }

    @Bean
    public SendGrid sendGrid(){
        SendGrid sendgrid = new SendGrid(System.getProperty("SEND_GRID_API_KEY"));
        return sendgrid;
    }
}


