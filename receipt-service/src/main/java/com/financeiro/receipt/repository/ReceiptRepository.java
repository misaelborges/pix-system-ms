package com.financeiro.receipt.repository;

import com.financeiro.receipt.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Optional<Receipt> findByTransactionId(String transactionId);
    List<Receipt> findBySenderAccountId(Long accountId);
    List<Receipt> findByReceiverAccountId(Long accountId);

}
