package com.app2.engine.repository.custom;

import com.app2.engine.entity.app.AppUser;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AppUserRepositoryCustom {
    List<AppUser> updateStatusRetire(List<String> empActive);

    int updateUserInternalToReject(@Param("date") Date date);
}
