package io.tgbot.moaishelper.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @Authors: Markelloww & YDK
 */

// Общее и описательное имя для класса,
// который будет предоставлять различные конфигурации клавиатуры
public class KeyboardMarkupProvider {
    public static ReplyKeyboardMarkup start() {
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
}
