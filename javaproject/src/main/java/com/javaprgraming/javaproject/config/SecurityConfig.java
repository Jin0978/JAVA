package com.javaprgraming.javaproject.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security (스프링 웹 보안) 설정을 위한 클래스입니다.
 * 
 * @Configuration: 이 클래스가 Spring의 '설정 파일'임을 나타냅니다.
 * @EnableWebSecurity: Spring Security의 웹 보안 기능을 활성화합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private com.javaprgraming.javaproject.service.CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private com.javaprgraming.javaproject.service.UserService userService; // UserDetailsService 구현체

    /**
     * 비밀번호를 암호화하는 방식을 결정합니다.
     * 
     * @Bean: 이 메서드가 반환하는 객체(PasswordEncoder)를 Spring이 관리하도록 등록합니다.
     *        이렇게 등록하면 다른 Service 등에서 @Autowired로 주입받아 사용할 수 있습니다.
     * @return BCryptPasswordEncoder (현재 많이 사용되는 강력한 암호화 방식)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * HTTP 요청에 대한 보안 규칙을 설정하는 핵심 메서드입니다.
     * 
     * @param http HttpSecurity 객체 (보안 설정을 구성하는 빌더)
     * @return SecurityFilterChain (설정된 보안 규칙의 체인)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF 보호 비활성화
                .csrf(csrf -> csrf.disable())

                // 3. HTTP 요청 권한 설정
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers(
                                "/",
                                "/login.html",
                                "/main.html",
                                "/auction_site_main.html",
                                "/auction_mypage.html",
                                "/auction_register_page.html",
                                "/admin_page.html",
                                "/receive_item.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**")
                        .permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()) // 일단 모두 허용 (테스트 편의상)

                // 4. ⭐ [추가] OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login.html") // 커스텀 로그인 페이지
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 사용자 정보 처리 서비스 등록
                        )
                        .defaultSuccessUrl("/auction_site_main.html", true) // 로그인 성공 시 이동할 페이지
                )

                // 5. ⭐ [추가] Remember Me (로그인 상태 유지) 설정
                .rememberMe(remember -> remember
                        .key("uniqueAndSecretKey") // 쿠키 암호화 키
                        .tokenValiditySeconds(60 * 60 * 24 * 7) // 7일간 유지
                        .userDetailsService(userService) // 사용자 정보 로드 서비스
                        .rememberMeParameter("remember-me") // HTML 체크박스 name 속성
                );

        // H2 콘솔 사용 설정
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    /**
     * CORS(Cross-Origin Resource Sharing) 설정을 정의합니다.
     * (핵심 역할): "다른 주소(도메인/포트)에서 온 요청"을 허용해주는 정책입니다.
     * 예: 프론트엔드(localhost:5500)가 백엔드(localhost:8080) API를 호출할 수 있게 해줍니다.
     * 
     * @Bean: 이 설정 객체도 Spring이 관리하도록 등록합니다.
     * @return CorsConfigurationSource (CORS 설정 소스)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // (1) 허용할 출처(Origin) 설정:
        configuration.addAllowedOriginPattern("*"); // Allow all origins for remote access

        // (2) 허용할 HTTP 메서드(동작) 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // (3) 허용할 HTTP 헤더 설정 ("*") -> 모든 헤더 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // (4) 자격증명(쿠키 등) 허용 여부
        configuration.setAllowCredentials(true);

        // (5) 이 설정을 "/**" (모든 URL 경로)에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}