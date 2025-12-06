package com.javaprgraming.javaproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@org.springframework.scheduling.annotation.EnableScheduling // ⭐ [추가] 스케줄러 활성화
@SpringBootApplication
public class JavaprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaprojectApplication.class, args);
	}

}
