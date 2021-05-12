package com.work.movierec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
//@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@EnableMongoRepositories
public class MovieRecApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieRecApplication.class, args);
	}

}
