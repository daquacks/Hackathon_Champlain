package org.example.hackathon;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class DialogueParser {

    public static Map<String, Dialogue> parseDialogues(String jsonContent) {
        Gson gson = new Gson();
        DialogueFile file = gson.fromJson(jsonContent, DialogueFile.class);
        Map<String, Dialogue> map = new HashMap<>();
        for (Dialogue d : file.dialogues) {
            map.put(d.id, d);
        }
        return map;
    }
}

