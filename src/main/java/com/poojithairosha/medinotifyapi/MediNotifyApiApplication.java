package com.poojithairosha.medinotifyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class MediNotifyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediNotifyApiApplication.class, args);
    }

}
