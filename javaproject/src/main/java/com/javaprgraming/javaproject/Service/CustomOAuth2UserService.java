package com.javaprgraming.javaproject.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.javaprgraming.javaproject.repository.UserRepository;
import com.javaprgraming.javaproject.table.User;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 서비스 구분 (google, github)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인 진행 시 키가 되는 필드값 (PK)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 사용자 정보 추출 및 저장/업데이트
        User user = saveOrUpdateUser(registrationId, attributes);

        // 세션에 저장할 사용자 정보 생성 (여기서는 email을 키로 사용)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                attributes,
                userNameAttributeName);
    }

    private User saveOrUpdateUser(String registrationId, Map<String, Object> attributes) {
        String email;
        String name;
        String providerId;

        if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            providerId = (String) attributes.get("sub");
        } else if ("github".equals(registrationId)) {
            email = (String) attributes.get("email"); // GitHub는 이메일이 없을 수도 있음 (설정 필요)
            name = (String) attributes.get("login"); // GitHub username
            providerId = String.valueOf(attributes.get("id"));
            if (email == null) {
                // 이메일이 비공개인 경우 처리 필요하지만, 일단은 더미 이메일 생성
                email = name + "@github.com";
            }
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 기존 유저라면 정보 업데이트 (필요 시)
            user.setProvider(registrationId);
            user.setProviderId(providerId);
            userRepository.save(user);
        } else {
            // 신규 유저 생성
            user = new User();
            user.setUsername(email); // 소셜 로그인은 이메일을 아이디로 사용
            user.setEmail(email);
            user.setPassword(""); // 비밀번호 없음
            user.setRole("USER");
            user.setProvider(registrationId);
            user.setProviderId(providerId);
            user.setPoints(0);
            userRepository.save(user);
        }
        return user;
    }
}
