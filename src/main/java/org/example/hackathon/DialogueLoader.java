package org.example.hackathon;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DialogueLoader {

    // Wrapper class to match the JSON structure {"dialogues": [...]}
    private static class DialogueFile {
        List<Dialogue> dialogues;
    }

    public static Map<String, Dialogue> loadFromResource(String resourcePath) throws Exception {
        Gson gson = new Gson();
        try (InputStream is = DialogueLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new Exception("Cannot find resource: " + resourcePath);
            }
            InputStreamReader reader = new InputStreamReader(is);
            DialogueFile dialogueFile = gson.fromJson(reader, DialogueFile.class);

            if (dialogueFile == null || dialogueFile.dialogues == null) {
                return new HashMap<>();
            }

            // Convert the list of dialogues into a map, keyed by dialogue ID
            return dialogueFile.dialogues.stream()
                    .collect(Collectors.toMap(d -> d.id, d -> d));
        }
    }
}
