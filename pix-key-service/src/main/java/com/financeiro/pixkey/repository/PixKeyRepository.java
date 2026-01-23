package com.financeiro.pixkey.repository;

import com.financeiro.pixkey.entity.KeyTypeEnum;
import com.financeiro.pixkey.entity.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PixKeyRepository extends JpaRepository<PixKey, Long> {

    List<PixKey> findByAccountIdAndActiveTrue(Long accountId);
    Optional<PixKey> findByKeyValueAndActiveTrue(String keyValue);
    Long countByAccountIdAndActiveTrue(Long accountId);
    boolean existsByAccountIdAndKeyTypeAndActiveTrue(Long accountId, KeyTypeEnum keyType);
}
