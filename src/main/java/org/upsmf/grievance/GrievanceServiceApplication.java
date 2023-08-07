package org.upsmf.grievance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
public class GrievanceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrievanceServiceApplication.class, args);
	}

}
