package com.app2.engine.repository.custom;

import com.app2.engine.entity.app.DebtorAccDebtInfo;

import java.util.List;
import java.util.Map;

public interface DebtorAccDebtInfoRepositoryCustom {
    DebtorAccDebtInfo findByAccountNo(String accountNo);
}
