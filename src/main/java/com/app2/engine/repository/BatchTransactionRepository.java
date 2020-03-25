package com.app2.engine.repository;

import com.app2.engine.entity.app.BatchTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchTransactionRepository extends JpaRepository<BatchTransaction, Long> {
}
