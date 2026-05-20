package com.ilham.authservice.repository;

import com.ilham.authservice.entity.RefreshTokenEntity;
import com.ilham.authservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    Optional<RefreshTokenEntity> findByUser(UserEntity user);

    void deleteByUserId(Long userId);

}
