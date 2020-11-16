package com.app2.engine.repository.custom;


import com.app2.engine.entity.app.DocumentProgress;

import java.util.List;
import java.util.Map;

public interface DocumentProgressRepositoryCustom {
    List<DocumentProgress> findByDocumentId(Long documentId);
}
