package com.zs.zsdevutil.etc;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StatePersistence {
    private static final String STATE_FILE = "zsdevutil_state.json";

    public static void saveState(String tool, JSONObject state) throws IOException {
        JSONObject fullState = loadFullState();
        fullState.put(tool, state);
        Files.write(Paths.get(STATE_FILE), fullState.toString(2).getBytes());
    }

    public static JSONObject loadState(String tool) throws IOException {
        JSONObject fullState = loadFullState();
        return fullState.optJSONObject(tool, new JSONObject());
    }

    private static JSONObject loadFullState() throws IOException {
        File stateFile = new File(STATE_FILE);
        if (stateFile.exists()) {
            String content = new String(Files.readAllBytes(Paths.get(STATE_FILE)));
            return new JSONObject(content);
        }
        return new JSONObject();
    }
}
