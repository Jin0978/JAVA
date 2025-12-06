package com.javaprgraming.javaproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javaprgraming.javaproject.table.User;

/**
 * 'User' 테이블의 데이터베이스 작업을 처리하는 JPA 인터페이스입니다.
 * JpaRepository<User, Long>: 'User' 엔티티를 관리하며, 기본 키(ID)의 타입은 'Long'입니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 'username'(아이디)으로 사용자를 조회합니다.
     * (로그인 시 사용)
     * 
     * @param username (조회할 아이디)
     * @return Optional<User> (사용자가 없을 수도 있으므로 Optional로 감싸서 반환)
     */
    Optional<User> findByUsername(String username);

    /**
     * 'username'(아이디)이 DB에 이미 존재하는지 확인합니다.
     * (회원가입 시 아이디 중복 검사에 사용)
     * 
     * @param username (검사할 아이디)
     * @return boolean (존재하면 true, 없으면 false)
     */
    boolean existsByUsername(String username);

    /**
     * 'email'(이메일)로 사용자를 조회합니다.
     * (소셜 로그인 시 사용)
     */
    Optional<User> findByEmail(String email);

}