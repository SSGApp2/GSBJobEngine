package com.app2.engine.repository;

import com.app2.engine.entity.app.Debtor;
import com.app2.engine.entity.app.DebtorAccDebtInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DebtorAccDebtInfoRepository extends JpaRepository<DebtorAccDebtInfo,Long>,JpaSpecificationExecutor<DebtorAccDebtInfo> {

    @Query("select dad from DebtorAccDebtInfo dad where dad.accountNo = :accountNo")
    DebtorAccDebtInfo findByAccountNo(@Param("accountNo")String accountNo);

    List<DebtorAccDebtInfo> findByDebtor(@Param("debtor") Debtor debtor);
}