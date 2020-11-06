package com.app2.engine.service.impl;

import com.app2.engine.entity.app.*;
import com.app2.engine.entity.view.DebtorMapAccount;
import com.app2.engine.repository.*;
import com.app2.engine.repository.custom.AppUserRepositoryCustom;
import com.app2.engine.repository.custom.DocumentRepositoryCustom;
import com.app2.engine.service.DocumentService;
import com.app2.engine.util.AppUtil;
import com.google.gson.*;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    private JsonDeserializer<Date> deserializer = new JsonDeserializer<Date>() {
        public Date deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
            return json == null ? null : new Date(json.getAsLong());
        }
    };
    protected Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Date.class, deserializer).create();

}
