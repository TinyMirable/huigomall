package com.macro.mall.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.macro.mall"})
public class HgmallAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(HgmallAdminApplication.class, args);
	}

}
