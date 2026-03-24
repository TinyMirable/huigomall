package com.huigo.mall.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.huigo.mall"})
public class HgmallAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(HgmallAdminApplication.class, args);
	}

}
