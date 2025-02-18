package com.example.carteiradigital.repository;

import com.example.carteiradigital.entity.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PixKeyRepository extends JpaRepository<PixKey, Long> {

    Optional<PixKey> findByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PixKey p WHERE p.dataExpiracao < :now")
    void deleteExpiredKeys(LocalDateTime now);
}
