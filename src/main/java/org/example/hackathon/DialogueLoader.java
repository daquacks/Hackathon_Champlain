package org.example.hackathon;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DialogueLoader {
    public static Map<String, Dialogue> loadFromResource(String resourcePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = DialogueLoader.class.getResourceAsStream(resourcePath)) {
            DialogueBundle bundle = mapper.readValue(is, DialogueBundle.class);
            Map<String, Dialogue> map = new HashMap<>();
            if (bundle != null && bundle.dialogues != null) {
                for (Dialogue d : bundle.dialogues) map.put(d.id, d);
            }
            return map;
        }
    }
}
