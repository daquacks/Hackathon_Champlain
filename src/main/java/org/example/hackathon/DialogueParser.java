package org.example.hackathon;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class DialogueParser {

    public static Map<String, Dialogue> parseDialogues(String json) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<Map<String, Dialogue>>() {}.getType();
        return gson.fromJson(json, type);
    }
}

