package com.kfozla.httpserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

public class Json {
    private static ObjectMapper objectMapper = createObjectMapper();
    public static ObjectMapper createObjectMapper(){
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return om;
    }
    public static JsonNode parse(String jsonSource) throws JsonProcessingException {
        return objectMapper.readTree(jsonSource);
    }
    public static <A> A fromJson(JsonNode node,Class<A> clas) throws JsonProcessingException {
        return objectMapper.treeToValue(node,clas);
    }
    public static JsonNode toJson(Object obj){
        return objectMapper.valueToTree(obj);
    }
    private static String generateJson(Object obj, boolean pretty) throws JsonProcessingException {
        ObjectWriter objectWriter = objectMapper.writer();
        if (pretty){
            objectWriter = objectWriter.with(SerializationFeature.INDENT_OUTPUT);
        }
        return objectWriter.writeValueAsString(obj);
    }
    public static String stringify(JsonNode node) throws JsonProcessingException {
        return generateJson(node,false);
    }
    public static String stringifyPretty(JsonNode node) throws JsonProcessingException {
        return generateJson(node,true);
    }
}
