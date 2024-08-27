package io.tgbot.moaishelper.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Authors: Markelloww & YDK
 */

public class KeyboardMarkupProvider {

    // ---------> Reply
    public static ReplyKeyboardMarkup startMenu() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(KeyboardText.NOTIFICATION_SETTING);
        row1.add(KeyboardText.GO_TO_GROUPS);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(KeyboardText.CONTACTS);
        row2.add(KeyboardText.GUIDE);

        rows.add(row1);
        rows.add(row2);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup showGroupsSettingsMenu(boolean isAdmin) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        if (isAdmin) {
                KeyboardRow row1 = new KeyboardRow();
                row1.add(KeyboardText.GROUP_HANDLER);
                row1.add(KeyboardText.SHOW_SCHEDULE);

                KeyboardRow row2 = new KeyboardRow();
                row2.add("Назад");
                row2.add(KeyboardText.LEAVE_GROUP);

                rows.add(row1);
                rows.add(row2);
        }
        else {
            KeyboardRow row1 = new KeyboardRow();
            row1.add(KeyboardText.SHOW_MEMBERS);
            row1.add(KeyboardText.SHOW_SCHEDULE);

            KeyboardRow row2 = new KeyboardRow();
            row2.add("Назад");
            row2.add(KeyboardText.LEAVE_GROUP);

            rows.add(row1);
            rows.add(row2);
        }

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup groupSettingsMenu(boolean isOwner) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        if (isOwner) {
            KeyboardRow row1 = new KeyboardRow();
            row1.add(KeyboardText.DELETE_GROUP);
            row1.add(KeyboardText.GIVE_OWNER);

            KeyboardRow row2 = new KeyboardRow();
            row2.add(KeyboardText.REMOVE_ADMIN);
            row2.add(KeyboardText.SET_ADMIN);

            rows.add(row1);
            rows.add(row2);
        }

        KeyboardRow row3 = new KeyboardRow();
        row3.add(KeyboardText.KICK_USER);
        row3.add(KeyboardText.INVITE_USER);

        KeyboardRow row4 = new KeyboardRow();
        row4.add(KeyboardText.SHOW_MEMBERS);
        row4.add(KeyboardText.SET_SCHEDULE);

        rows.add(row3);
        rows.add(row4);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
    // <--------- Reply

    // ---------> Inline
    private static InlineKeyboardButton inlineButtonSetter(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callbackData);
        return button;
    }

    public static InlineKeyboardMarkup scheduleMenu() {
        InlineKeyboardButton button1 = inlineButtonSetter("Сегодня", "TODAY");
        InlineKeyboardButton button2 = inlineButtonSetter("Завтра", "TOMORROW");
        InlineKeyboardButton button3 = inlineButtonSetter("Текущая неделя", "THIS_WEEK");
        InlineKeyboardButton button4 = inlineButtonSetter("Следующая неделя", "NEXT_WEEK");
        InlineKeyboardButton button5 = inlineButtonSetter("На семестр", "SEMESTER");

        List<List<InlineKeyboardButton>> rows = Arrays.asList(
                Arrays.asList(button1, button2),
                Arrays.asList(button3, button4),
                Arrays.asList(button5)
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup notificationMenu() {
        InlineKeyboardButton button1 = inlineButtonSetter("Вкл/Выкл ежедневные оповещения", "ON/OFF_EVERYDAY");
        InlineKeyboardButton button2 = inlineButtonSetter("Вкл/Выкл оповещения о следующей паре", "ON/OFF_NEXT");
        InlineKeyboardButton button3 = inlineButtonSetter("Назад", "BACK_TO_MAIN_MENU");

        List<List<InlineKeyboardButton>> rows = Arrays.asList(
                Arrays.asList(button1, button2),
                Arrays.asList(button3)
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup groupsMenu(boolean groupChosen, boolean groupCreated) {
        InlineKeyboardButton button1 = inlineButtonSetter("Создать группу", "CREATE_GROUP");
        InlineKeyboardButton button2 = inlineButtonSetter("Меню группы", "GROUP_MENU");
        InlineKeyboardButton button3 = inlineButtonSetter("Назад", "BACK_TO_MAIN_MENU");
        InlineKeyboardButton button4 = inlineButtonSetter("Выбрать группу", "SELECT_GROUP");

        // Не выбрана группа + не создана личная группа
        if (!groupChosen && !groupCreated) {
            List<List<InlineKeyboardButton>> rows = Arrays.asList(
                    Arrays.asList(button1),
                    Arrays.asList(button3)
            );

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rows);

            return inlineKeyboardMarkup;
        }
        // Выбрана группа + создана личная группа
        else if (groupChosen && groupCreated) {
            List<List<InlineKeyboardButton>> rows = Arrays.asList(
                    Arrays.asList(button2),
                    Arrays.asList(button3, button4)
            );

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rows);

            return inlineKeyboardMarkup;
        }
        // Выбрана группа + не создана личная группа
        else {
            List<List<InlineKeyboardButton>> rows = Arrays.asList(
                    Arrays.asList(button1, button2),
                    Arrays.asList(button3, button4)
            );

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rows);

            return inlineKeyboardMarkup;
        }
    }
    // <--------- Inline
}
