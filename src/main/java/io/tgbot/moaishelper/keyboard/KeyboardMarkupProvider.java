package io.tgbot.moaishelper.keyboard;

import io.tgbot.moaishelper.service.Info;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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

    public static InlineKeyboardMarkup notificationSetting() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> buttonsFirstRow = new ArrayList<>();
        List<InlineKeyboardButton> buttonsSecondRow = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Вкл/Выкл ежедневные оповещения");
        button1.setCallbackData("ON/OF_EVERYDAY");
        buttonsFirstRow.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Вкл/Выкл оповещения о следующей паре");
        button2.setCallbackData("ON/OFF_NEXT");
        buttonsFirstRow.add(button2);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Назад");
        button3.setCallbackData("BACK");
        buttonsSecondRow.add(button3);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(buttonsFirstRow);
        rows.add(buttonsSecondRow);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }
}
