package com.app2.engine.repository.custom;

import com.app2.engine.entity.app.ZoneMapArea;

import java.util.List;

public interface ZoneMapAreaRepositoryCustom {

    List<ZoneMapArea> findByZoneCodeAndAreaCode(String zoneCode, String areaCode);
}
