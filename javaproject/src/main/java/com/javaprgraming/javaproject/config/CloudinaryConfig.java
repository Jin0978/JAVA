package com.javaprgraming.javaproject.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

/**
 * Cloudinary 연동을 위한 설정 클래스입니다.
 * application.properties에 저장된 API Key를 읽어 Cloudinary 객체를 생성하고,
 * 이 객체를 Spring Bean으로 등록합니다.
 */
@Configuration
public class CloudinaryConfig {

    // application.properties에 정의한 값을 주입받습니다.
    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    /**
     * Cloudinary 객체를 생성하여 Spring Bean으로 등록합니다.
     * 이제 다른 컨트롤러나 서비스에서 @Autowired로 이 Cloudinary 객체를 주입받아
     * 사용할 수 있게 됩니다.
     * @return
     */
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        
        // (중요) 'secure = true'로 설정해야 https (보안) URL이 반환됩니다.
        config.put("secure", "true"); 
        
        return new Cloudinary(config);
    }
}