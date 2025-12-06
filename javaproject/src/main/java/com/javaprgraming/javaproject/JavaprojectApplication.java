package com.javaprgraming.javaproject;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@org.springframework.scheduling.annotation.EnableScheduling // ⭐ [추가] 스케줄러 활성화
@SpringBootApplication
public class JavaprojectApplication {

	@PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        System.out.println("✅ 현재 시간대: " + TimeZone.getDefault().getID());
        System.out.println("✅ 현재 시간: " + java.time.LocalDateTime.now());
    }

	public static void main(String[] args) {
		SpringApplication.run(JavaprojectApplication.class, args);
	}

}
