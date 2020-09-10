package com.app2.engine.repository.custom;

import com.app2.engine.entity.app.AppUser;

import java.util.List;

public interface AppUserRepositoryCustom {
    List<AppUser> updateStatusRetire(List<String> empActive);
}
