package io.tgbot.moaishelper.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.tgbot.moaishelper.model.AdminScheduleSettings;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
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

    public static void setTime(long groupId, short lessonNumber, String newTime) {
        String path = String.format("src/main/resources/groups/%d/Время.json", groupId);
        try(Reader reader = Files.newBufferedReader(Paths.get(path))) {
            Gson gson = new Gson();

            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> timetable = gson.fromJson(reader, type);
            timetable.put(String.valueOf(lessonNumber), newTime);

            try (Writer writer = new FileWriter(path)) {
                gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(timetable, writer);
            }
            catch (IOException _) {}
        }
        catch (IOException _) {}
    }
}
