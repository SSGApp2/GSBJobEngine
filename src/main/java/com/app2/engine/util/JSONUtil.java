package com.app2.engine.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

public class JSONUtil {
    private static JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
        public Date deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
            return json == null ? null : new Date(json.getAsLong());
        }
    };
    private static Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm").registerTypeAdapter(Date.class, deser).create();

    public  static String toJSON(Object src){
        return gson.toJson(src).replace("\\n", "");
    }
}
