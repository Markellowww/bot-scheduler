package io.tgbot.moaishelper.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.model.*;
import io.tgbot.moaishelper.schedule.Day;
import io.tgbot.moaishelper.schedule.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider.lessonCreateSettings;
import static io.tgbot.moaishelper.text.Info.*;

/**
 * @Authors: Markelloww & YDK
 */
@Component
public class ScheduleHandler {

    private final UserRepository userRepository;
    private final MessageHandler messageHandler;
    private final ScheduleHandler scheduleHandler;

    @Autowired
    public ScheduleHandler(UserRepository userRepository,
                           MessageHandler messageHandler,
                           @Lazy ScheduleHandler scheduleHandler) {
        this.userRepository = userRepository;
        this.messageHandler = messageHandler;
        this.scheduleHandler = scheduleHandler;
    }

    public void setLessonName(long chatId, AdminScheduleSettings settings, String inputText) throws IOException {
        Groupe selectedGroup = userRepository.findByChatId(chatId).getSelectedGroup();
        Schedule schedule = scheduleFromJson(settings.getWeekNum(), selectedGroup.getId());
        String dayOfWeek = Day.values()[settings.getWeekDay()].getTranslation();

        schedule.updateLessonName(dayOfWeek, settings.getLessonNum(), inputText);

        scheduleToJson(schedule, selectedGroup.getId(), settings.getWeekNum());

        messageHandler.sendMessageWithKeyboardMarkup(chatId, TEXT_IN_DEVELOP,
                lessonCreateSettings());
    }

    public void setTeacherName(long chatId, AdminScheduleSettings settings, String inputText) throws IOException {
        Groupe selectedGroup = userRepository.findByChatId(chatId).getSelectedGroup();
        Schedule schedule = scheduleFromJson(settings.getWeekNum(), selectedGroup.getId());
        String dayOfWeek = Day.values()[settings.getWeekDay()].getTranslation();

        schedule.updateTeacherName(dayOfWeek, settings.getLessonNum(), inputText);

        scheduleToJson(schedule, selectedGroup.getId(), settings.getWeekNum());

        messageHandler.sendMessageWithKeyboardMarkup(chatId, TEXT_IN_DEVELOP,
                lessonCreateSettings());
    }

    public void setAuditorium(long chatId, AdminScheduleSettings settings, String inputText) throws IOException {
        Groupe selectedGroup = userRepository.findByChatId(chatId).getSelectedGroup();
        Schedule schedule = scheduleFromJson(settings.getWeekNum(), selectedGroup.getId());
        String dayOfWeek = Day.values()[settings.getWeekDay()].getTranslation();

        schedule.updateAuditorium(dayOfWeek, settings.getLessonNum(), inputText);

        scheduleToJson(schedule, selectedGroup.getId(), settings.getWeekNum());

        messageHandler.sendMessageWithKeyboardMarkup(chatId, TEXT_IN_DEVELOP,
                lessonCreateSettings());
    }

        /**
         * Первый символ "!" - спец. символ, который говорит, что происходит удаление предмета
         * Второй символ - четность недели: 1 - Числитель, 0 - Знаменатель
         * Третий символ - порядковый номер дня недели (исходя из enum Day)
         * Четвертый символ - порядковый номер начала пары
         */
    public void removeLessonHandler(String callback, long groupId) throws IOException {
        boolean weekNum = (callback.charAt(1) == '1');
        Schedule schedule = scheduleFromJson(weekNum, groupId);

        short weekDay = Short.parseShort(String.valueOf(callback.charAt(2)));
        schedule.removeLessonByOrder(Day.values()[weekDay].getTranslation(),
                String.valueOf(callback.charAt(3)), groupId);

        scheduleToJson(schedule, groupId, weekNum);
    }

    public void addLessonHandler(long chatId, AdminScheduleSettings settings) throws IOException {
        long groupId = userRepository.findByChatId(chatId).getSelectedGroup().getId();
        Schedule schedule = scheduleFromJson(settings.getWeekNum(), groupId);

        if (schedule == null) {
            return;
        }
        String path = String.format("src/main/resources/groups/%d/Время.json", groupId);
        List<String> lessonNames = schedule.getLessonNames(groupId,
                Day.values()[settings.getWeekDay()].getTranslation(), path);

        messageHandler.sendMessageWithKeyboardMarkup(chatId, LESSON_CHOSE_INFO,
                KeyboardMarkupProvider.chooseOrderOfLesson(lessonNames));
    }

    public void printLessonList(final long chatId,
                                final Boolean weekNum,
                                final Short weekDay,
                                final long groupId) throws IOException {

        Schedule schedule = scheduleFromJson(weekNum, groupId);

        if (schedule != null) {
            String path = String.format("src/main/resources/groups/%d/Время.json", groupId);
            List<String> lessonNames = schedule.getLessonNames(groupId, Day.values()[weekDay].getTranslation(), path);

            messageHandler.sendMessageWithKeyboardMarkup(chatId,
                    "Выберите предмет, который будет удален:",
                    KeyboardMarkupProvider.chooseDeleteLesson(lessonNames, weekNum, weekDay));
        }
        else {
            scheduleHandler.handleScheduleSetCommand(chatId, true);
        }
    }

    public static Schedule scheduleFromJson(final Boolean weekNum,
                                     final long groupId) throws IOException {

        String weekNumber = weekNum ? "Числитель" : "Знаменатель";
        Reader reader = Files.newBufferedReader(Paths.get(String.format("src/main/resources/groups/%d/%s.json",
                groupId, weekNumber)));

        Gson gson = new Gson();
        return gson.fromJson(reader, Schedule.class);
    }

    public static void scheduleToJson(Schedule schedule, long groupId, boolean weekNum) throws IOException {
        String weekNumber = weekNum ? "Числитель" : "Знаменатель";
        try (Writer writer = new FileWriter(String.format("src/main/resources/groups/%d/%s.json",
                groupId, weekNumber))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(schedule, writer);
        }
    }

    protected void handleScheduleSetCommand(final long chatId, final boolean isManual) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = user.getSelectedGroup();

        if (selectedGroup == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_NOT_SELECTED,
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        if (selectedGroup.getAdmins().stream().noneMatch(admin -> admin.getId() == user.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_ADMIN(user.getUserName()),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }

        if (isManual) {
            GroupUser groupUser = selectedGroup.getGroupUsers().stream()
                            .filter(u -> u.getUser().getChatId() == chatId).findFirst().get();
            AdminScheduleSettings settings = groupUser.getSettings();
            messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_MANUAL_WARNING(settings.getWeekNum()),
                    KeyboardMarkupProvider.chooseDayOfWeek());
        }
        else {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_EXCEL_WARNING,
                    KeyboardMarkupProvider.excelScheduleSettings());
        }
    }
}