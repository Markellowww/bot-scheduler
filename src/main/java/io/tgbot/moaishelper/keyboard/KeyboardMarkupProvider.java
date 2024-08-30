package io.tgbot.moaishelper.keyboard;

import io.tgbot.moaishelper.model.GroupUser;
import io.tgbot.moaishelper.model.Groupe;
import io.tgbot.moaishelper.model.User;
import io.tgbot.moaishelper.text.KeyboardText;
import org.hibernate.loader.ast.spi.SingleUniqueKeyEntityLoader;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
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
    public static ReplyKeyboardRemove keyboardRemove() {
        return new ReplyKeyboardRemove(true);
    }

    public static ReplyKeyboardMarkup startMenu() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
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
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        if (isAdmin) {
                KeyboardRow row1 = new KeyboardRow();
                row1.add(KeyboardText.GROUP_HANDLER);
                row1.add(KeyboardText.SHOW_SCHEDULE);

                KeyboardRow row2 = new KeyboardRow();
                row2.add(KeyboardText.BACK_TO_GROUPS);
                row2.add(KeyboardText.LEAVE_GROUP);

                rows.add(row1);
                rows.add(row2);
        }
        else {
            KeyboardRow row1 = new KeyboardRow();
            row1.add(KeyboardText.SHOW_MEMBERS);
            row1.add(KeyboardText.SHOW_SCHEDULE);

            KeyboardRow row2 = new KeyboardRow();
            row2.add(KeyboardText.BACK_TO_GROUPS);
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
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        if (isOwner) {
            KeyboardRow row1 = new KeyboardRow();
            row1.add(KeyboardText.SHOW_MEMBERS);
            row1.add(KeyboardText.DELETE_GROUP);
            row1.add(KeyboardText.GIVE_OWNER);

            KeyboardRow row2 = new KeyboardRow();
            row2.add(KeyboardText.SET_SCHEDULE);
            row2.add(KeyboardText.REMOVE_ADMIN);
            row2.add(KeyboardText.SET_ADMIN);

            KeyboardRow row3 = new KeyboardRow();
            row3.add(KeyboardText.BACK_TO_MENU_GROUPS  );
            row3.add(KeyboardText.KICK_USER);
            row3.add(KeyboardText.INVITE_USER);

            rows.add(row1);
            rows.add(row2);
            rows.add(row3);
        }
        else {
            KeyboardRow row1 = new KeyboardRow();
            row1.add(KeyboardText.KICK_USER);
            row1.add(KeyboardText.INVITE_USER);
            row1.add(KeyboardText.SET_SCHEDULE);

            KeyboardRow row2 = new KeyboardRow();
            row2.add(KeyboardText.BACK_TO_MENU_GROUPS);
            row2.add(KeyboardText.SHOW_MEMBERS);

            rows.add(row1);
            rows.add(row2);
        }

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

    public static InlineKeyboardMarkup addSelectGroupAnswers(List<Groupe> groups) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < Math.ceil(groups.size() / 3.0); i++) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (int j = 0; j < 3 && 3 * i + j < groups.size(); j++) {
                Groupe groupe = groups.get(3 * i + j);
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(groupe.getName());
                button.setCallbackData(String.valueOf(groupe.getId()));
                buttons.add(button);
            }
            rows.add(buttons);
        }
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup addInviteAnswers(User invitor, Groupe group) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton acceptButton = new InlineKeyboardButton();
        acceptButton.setText("Вступить");
        acceptButton.setCallbackData(String.format("1 %d %d", group.getId(), invitor.getChatId()));

        InlineKeyboardButton declineButton = new InlineKeyboardButton();
        declineButton.setText("Отклонить");
        declineButton.setCallbackData(String.format("2 %d %d", group.getId(), invitor.getChatId()));

        rows.add(List.of(declineButton, acceptButton));
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    /**
     * Возвращает клавиатуру с действиями подтверждения и отмены операции
     * @return клавиатура с действиями подтверждения и отмены операции
     */
    public static InlineKeyboardMarkup confirmDenyAnswers() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton confirmButton = new InlineKeyboardButton();
        confirmButton.setText("Подтвердить");
        confirmButton.setCallbackData("accept");

        InlineKeyboardButton denyButton = new InlineKeyboardButton();
        denyButton.setText("Отменить");
        denyButton.setCallbackData("deny");

        rows.add(List.of(confirmButton, denyButton));
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    /**
     * Возвращает клавиатуру со всеми пользователями кроме владельца группы
     * @param group группа
     * @return клавиатура со всеми пользователями кроме владельца группы
     */
    public static InlineKeyboardMarkup userAnswers(Groupe group) { // все пользователи (для запросов владельца)
        List<User> usersExceptOwner = group.getMembers()
                .stream()
                .filter(x -> x.getId() != group.getOwner().getId())
                .toList();

        return getInlineKeyboardMarkup(usersExceptOwner);
    }

    /**
     * Возвращает клавиатуру со всеми админами кроме владельца
     * @param group группа
     * @return клавиатура со всеми админами кроме владельца
     */
    public static InlineKeyboardMarkup adminAnswers(Groupe group) { // только админы (операции владельца с админами)
        List<User> adminsExceptOwner = group.getGroupUsers()
                .stream()
                .filter(x -> x.isAdmin() && x.getUser().getId() != group.getOwner().getId()) // админы без владельца
                .map(GroupUser::getUser)
                .toList();

        return getInlineKeyboardMarkup(adminsExceptOwner);
    }

    /**
     * Возвращает клавиатуру со всеми пользователями группы кроме владельца и админов
     * @param group группа
     * @return клавиатура со всеми пользователями группы кроме владельца и админов
     */
    public static InlineKeyboardMarkup membersAnswers(Groupe group) {
        List<User> membersOnly = group.getGroupUsers()
                .stream()
                .filter(x -> !x.isAdmin())
                .map(GroupUser::getUser)
                .toList();

        return getInlineKeyboardMarkup(membersOnly);
    }

    /**
     * Создает клавиатуру с пользователями
     * @param users список пользователей
     * @return клавиатура с пользователями
     */
    private static InlineKeyboardMarkup getInlineKeyboardMarkup(List<User> users) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < Math.ceil(users.size() / 3.0); i++) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (int j = 0; j < 3 && 3 * i + j < users.size(); j++) {
                User admin = users.get(3 * i + j);
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(admin.getUserName());
                button.setCallbackData(String.valueOf(admin.getId()));
                buttons.add(button);
            }
            rows.add(buttons);
        }
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup inlineGoBackButton() {
        InlineKeyboardButton button = inlineButtonSetter("Назад", "BACK_TO_MAIN_MENU");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(Arrays.asList(button)));
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup inlineContinueButtonToMainMenu() {
        InlineKeyboardButton button = inlineButtonSetter("Продолжить", "BACK_TO_MAIN_GROUPS_MENU");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(Arrays.asList(button)));
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup inlineContinueButtonToGroupSettingMenu() {
        InlineKeyboardButton button = inlineButtonSetter("Продолжить", "BACK_TO_GROUP_SETTING_MENU");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(Arrays.asList(button)));
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup inlineContinueButtonToGroupMenu() {
        InlineKeyboardButton button = inlineButtonSetter("Продолжить", "BACK_TO_GROUP_MENU");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(Arrays.asList(button)));
        return inlineKeyboardMarkup;
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
