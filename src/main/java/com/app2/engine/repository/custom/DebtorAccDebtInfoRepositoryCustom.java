package com.app2.engine.repository.custom;

import com.app2.engine.entity.app.DebtorAccDebtInfo;

public interface DebtorAccDebtInfoRepositoryCustom {
    DebtorAccDebtInfo findByAccountNo(String accountNo);
}
