package com.app2.engine.repository.custom;

import java.util.List;
import java.util.Map;

public interface DCMSRepositoryCustom {
    List<Map> findAcnEndLegal();

    List<Map> litigationUpdateBKC(String date);

    List<Map> litigationUpdateBKO(String date);

    List<Map> litigationUpdateCVC(String date);

    List<Map> litigationUpdateCVO(String date);
}
