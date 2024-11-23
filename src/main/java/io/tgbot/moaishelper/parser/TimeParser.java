package io.tgbot.moaishelper.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @Authors: Markelloww & YDK
 */
public class TimeParser {

    public static String getOrderByTime(String path, String time) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(path));
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> timeMap = gson.fromJson(reader, type);

        for (Map.Entry<String, String> entry : timeMap.entrySet()) {
            if (entry.getValue().equals(time)) {
                return entry.getKey();
            }
        }

        return "null";
    }
}
