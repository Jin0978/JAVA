package com.javaprgraming.javaproject.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.javaprgraming.javaproject.repository.UserRepository;
import com.javaprgraming.javaproject.table.User;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin1234"));
                admin.setEmail("admin@example.com");
                admin.setBirthdate("2000-01-01");
                admin.setPhone("010-0000-0000");
                admin.setRole("ADMIN");
                admin.setPoints(1000000); // 관리자 초기 포인트
                userRepository.save(admin);
                System.out.println("✅ 기본 관리자 계정 생성 완료: admin / admin1234");
            }
        };
    }
}
