package com.financeiro.payment.repository;

import com.financeiro.payment.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findBySenderAccountId(Long senderAccountId);
    List<Transaction> findByReceiverAccountId(Long receiverAccountId);
    List<Transaction> findByStatusTransaction(String statusTransaction);
}
