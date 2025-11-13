package org.example.hackathon;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class DialogueParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Dialogue> parseDialogues(String jsonContent) throws Exception {
        return mapper.readValue(jsonContent,
                mapper.getTypeFactory().constructMapType(Map.class, String.class, Dialogue.class));
    }
}

