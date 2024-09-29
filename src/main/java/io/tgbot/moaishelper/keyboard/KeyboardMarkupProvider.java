package io.tgbot.moaishelper.keyboard;

import io.tgbot.moaishelper.model.GroupUser;
import io.tgbot.moaishelper.model.Groupe;
import io.tgbot.moaishelper.model.User;
import io.tgbot.moaishelper.text.Keyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static io.tgbot.moaishelper.text.Button.*;

/**
 * @Authors: Markelloww & YDK
 */

public class KeyboardMarkupProvider {
    // ---------> Reply

    /**
     * Создает стартовое меню
     * @return стартовое меню
     */
    public static ReplyKeyboardMarkup startMenu() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(Keyboard.NOTIFICATION_SETTING);
        row1.add(Keyboard.GO_TO_GROUPS);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(Keyboard.CONTACTS);
//        row2.add(Keyboard.GUIDE);

        rows.add(row1);
        rows.add(row2);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    /**
     * Создает меню для взаимодействия с группой пользователей и админов
     * @param isAdmin является ли админом
     * @return меню для взаимодействия с группой пользователей и админов
     */
    public static ReplyKeyboardMarkup showGroupsSettingsMenu(boolean isAdmin) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        if (isAdmin) {
                KeyboardRow row1 = new KeyboardRow();
                row1.add(Keyboard.GROUP_HANDLER);
                row1.add(Keyboard.NOTIFICATION_FOR_ALL);
                row1.add(Keyboard.SHOW_SCHEDULE);

                KeyboardRow row2 = new KeyboardRow();
                row2.add(Keyboard.BACK_TO_GROUPS);
                row2.add(Keyboard.LEAVE_GROUP);

                rows.add(row1);
                rows.add(row2);
        }
        else {
            KeyboardRow row1 = new KeyboardRow();
            row1.add(Keyboard.SHOW_MEMBERS);
            row1.add(Keyboard.SHOW_SCHEDULE);

            KeyboardRow row2 = new KeyboardRow();
            row2.add(Keyboard.BACK_TO_GROUPS);
            row2.add(Keyboard.LEAVE_GROUP);

            rows.add(row1);
            rows.add(row2);
        }

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    /**
     * Создает меню настроек для владельца и админов группы
     * @param isOwner является ли владельцем группы
     * @return меню для владельца и админов группы
     */
    public static ReplyKeyboardMarkup groupSettingsMenu(boolean isOwner) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        if (isOwner) {
            row1.add(Keyboard.SHOW_MEMBERS);
            row1.add(Keyboard.DELETE_GROUP);
            row1.add(Keyboard.GIVE_OWNER);

            KeyboardRow row2 = new KeyboardRow();
            row2.add(Keyboard.SET_SCHEDULE);
            row2.add(Keyboard.REMOVE_ADMIN);
            row2.add(Keyboard.SET_ADMIN);

            KeyboardRow row3 = new KeyboardRow();
            row3.add(Keyboard.BACK_TO_MENU_GROUPS);
            row3.add(Keyboard.KICK_USER);
            row3.add(Keyboard.INVITE_USER);

            rows.add(row1);
            rows.add(row2);
            rows.add(row3);
        }
        else {
            row1.add(Keyboard.KICK_USER);
            row1.add(Keyboard.INVITE_USER);
            row1.add(Keyboard.SET_SCHEDULE);

            KeyboardRow row2 = new KeyboardRow();
            row2.add(Keyboard.BACK_TO_MENU_GROUPS);
            row2.add(Keyboard.SHOW_MEMBERS);

            rows.add(row1);
            rows.add(row2);
        }

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }
    // <--------- Reply

    // ---------> Inline

    /**
     * Создает кнопку с заданным текстом и возвращаемыми данными
     * @param text текст для кнопки
     * @param callbackData данные, возвращаемые при нажатии на кнопку
     * @return кнопка с заданным текстом и возвращаемыми данными
     */
    private static InlineKeyboardButton inlineButtonSetter(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callbackData);
        return button;
    }

    /**
     * Создает клавиатуру для выбора групп
     * @param groups список групп
     * @return клавиатура для выбора групп
     */
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

    /**
     * Создает клавиатуру для принятия/отклонения приглашения в группу
     * @param invitor пригласивший пользователь
     * @param group группа для приглашения
     * @return клавиатура для принятия/отклонения приглашения в группу
     */
    public static InlineKeyboardMarkup addInviteAnswers(User invitor, Groupe group) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton acceptButton = new InlineKeyboardButton();
        acceptButton.setText(JOIN);
        acceptButton.setCallbackData(String.format("1 %d %d", group.getId(), invitor.getChatId()));

        InlineKeyboardButton declineButton = new InlineKeyboardButton();
        declineButton.setText(DENY);
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
        confirmButton.setText(ACCEPT);
        confirmButton.setCallbackData("ACCEPT");

        InlineKeyboardButton denyButton = new InlineKeyboardButton();
        denyButton.setText(REJECT);
        denyButton.setCallbackData("DENY");

        rows.add(List.of(confirmButton, denyButton));
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    /**
     * Возвращает клавиатуру со всеми пользователями кроме владельца группы
     * @param group группа
     * @return клавиатура со всеми пользователями кроме владельца группы
     */
    public static InlineKeyboardMarkup userAnswers(Groupe group) {
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
    public static InlineKeyboardMarkup adminAnswers(Groupe group) {
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
                User user = users.get(3 * i + j);
                InlineKeyboardButton button = inlineButtonSetter(user.getUserName(),
                        user.getUserName());
                buttons.add(button);
            }
            rows.add(buttons);
        }
        InlineKeyboardButton goBackButton = inlineButtonSetter(BACK, "BACK_TO_GROUP_SETTING_MENU");
        rows.add(List.of(goBackButton));
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    /**
     * Создает клавиатуру
     * @return
     */
    public static InlineKeyboardMarkup inlineGoBackButton() {
        InlineKeyboardButton button = inlineButtonSetter(BACK, "BACK_TO_MAIN_MENU");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(Arrays.asList(button)));
        return inlineKeyboardMarkup;
    }

    /**
     * Создает клавиатуру с кнопкой для перехода в главное меню
     * @return клавиатура с кнопкой для перехода в главное меню
     */
    public static InlineKeyboardMarkup inlineContinueButtonToMainMenu() {
        InlineKeyboardButton button = inlineButtonSetter(CONTINUE, "BACK_TO_MAIN_GROUPS_MENU");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(Arrays.asList(button)));
        return inlineKeyboardMarkup;
    }

    /**
     * Создает клавиатуру с кнопкой для перехода в меню настроек группы
     * @return клавиатура с кнопкой для перехода в меню настроек группы
     */
    public static InlineKeyboardMarkup inlineContinueButtonToGroupSettingMenu() {
        InlineKeyboardButton button = inlineButtonSetter(CONTINUE, "BACK_TO_GROUP_SETTING_MENU");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(Arrays.asList(button)));
        return inlineKeyboardMarkup;
    }

    /**
     * Создает клавиатуру с кнопкой для перехода в меню группы
     * @return клавиатура с кнопкой для перехода в меню группы
     */
    public static InlineKeyboardMarkup inlineContinueButtonToGroupMenu() {
        InlineKeyboardButton button = inlineButtonSetter(CONTINUE, "BACK_TO_GROUP_MENU");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(Arrays.asList(button)));
        return inlineKeyboardMarkup;
    }

    /**
     * Создает клавиатуру для запроса расписания группы
     * @return клавиатура для запроса расписания группы
     */
    public static InlineKeyboardMarkup scheduleMenu() {
        InlineKeyboardButton button1 = inlineButtonSetter(TODAY, "TODAY");
        InlineKeyboardButton button2 = inlineButtonSetter(TOMORROW, "TOMORROW");
        InlineKeyboardButton button3 = inlineButtonSetter(THIS_WEEK, "THIS_WEEK");
        InlineKeyboardButton button4 = inlineButtonSetter(NEXT_WEEK, "NEXT_WEEK");
        InlineKeyboardButton button5 = inlineButtonSetter(BACK, "GROUP_MENU");

        List<List<InlineKeyboardButton>> rows = Arrays.asList(
                Arrays.asList(button1, button2),
                Arrays.asList(button3, button4),
                List.of(button5)
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    /**
     * Создает клавиатуру для настроек уведомлений
     * @return клавиатура для настроек уведомлений
     */
    public static InlineKeyboardMarkup notificationMenu() {
        InlineKeyboardButton button1 = inlineButtonSetter(ON_OFF_EVERYDAY, "ON/OFF_EVERYDAY");
        InlineKeyboardButton button2 = inlineButtonSetter(ON_OFF_NEXT, "ON/OFF_NEXT");
        InlineKeyboardButton button3 = inlineButtonSetter(BACK, "BACK_TO_MAIN_MENU");

        List<List<InlineKeyboardButton>> rows = Arrays.asList(
                Arrays.asList(button1, button2),
                Arrays.asList(button3)
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    /**
     * Создает клавиатуру для выбора часового пояса
     * @return клавиатура для выбора часового пояса
     */
    public static InlineKeyboardMarkup timezonesMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton button1 = inlineButtonSetter("UTC +2\n(Калининград)", "3 -1");
        InlineKeyboardButton button2 = inlineButtonSetter("UTC +3\n(Москва)", "3 0");
        InlineKeyboardButton button3 = inlineButtonSetter("UTC +4\n(Самара)", "3 1");
        rows.add(List.of(button1, button2, button3));

        InlineKeyboardButton button4 = inlineButtonSetter("UTC +5\n(Екатеринбург)", "3 2");
        InlineKeyboardButton button5 = inlineButtonSetter("UTC +6\n(Омск)", "3 3");
        InlineKeyboardButton button6 = inlineButtonSetter("UTC +7\n(Новосибирск)", "3 4");
        rows.add(List.of(button4, button5, button6));

        InlineKeyboardButton button7 = inlineButtonSetter("UTC +8\n(Иркутск)", "3 5");
        InlineKeyboardButton button8 = inlineButtonSetter("UTC +9\n(Якутск)", "3 6");
        InlineKeyboardButton button9 = inlineButtonSetter("UTC +10\nВладивосток", "3 7");
        rows.add(List.of(button7, button8, button9));

        InlineKeyboardButton button10 = inlineButtonSetter("UTC +11\n(Магадан)", "3 8");
        InlineKeyboardButton button11 = inlineButtonSetter("UTC +12\n(Анадырь)", "3 9");
        rows.add(List.of(button10, button11));

        InlineKeyboardButton back = inlineButtonSetter(BACK, "BACK_TO_SETTINGS");
        rows.add(List.of(back));

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    /**
     * Создает клавиатуру для выбора часов уведомлений
     * @return клавиатура для выбора часов уведомлений
     */
    public static InlineKeyboardMarkup hoursMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                InlineKeyboardButton button = inlineButtonSetter(String.valueOf(i * 3 + j),
                        String.format("4 %d", 3 * i + j));
                row.add(button);
            }
            rows.add(row);
        }

        InlineKeyboardButton back = inlineButtonSetter(BACK, "BACK_TO_SETTINGS");
        rows.add(List.of(back));

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    /**
     * Создает клавиатуру для выбора минут уведомлений
     * @return клавиатура для выбора минут уведомлений
     */
    public static InlineKeyboardMarkup minutesMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            InlineKeyboardButton button = inlineButtonSetter(String.valueOf(i * 15),
                    String.format("5 %d", i * 15));
            row.add(button);
        }
        rows.add(row);

        InlineKeyboardButton back = inlineButtonSetter(BACK, "BACK_TO_SETTINGS");
        rows.add(List.of(back));

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    /**
     * Создает клавиатуру для взаимодействия с группами
     * @param groupChosen есть ли выбранная группа
     * @param groupCreated создавал ли пользователь группу
     * @return клавиатура для взаимодействия с группами
     */
    public static InlineKeyboardMarkup groupsMenu(boolean groupChosen, boolean groupCreated) {
        InlineKeyboardButton button1 = inlineButtonSetter(CREATE_GROUP, "CREATE_GROUP");
        InlineKeyboardButton button2 = inlineButtonSetter(GROUP_MENU, "GROUP_MENU");
        InlineKeyboardButton button3 = inlineButtonSetter(BACK, "BACK_TO_MAIN_MENU");
        InlineKeyboardButton button4 = inlineButtonSetter(SELECT_GROUP, "SELECT_GROUP");

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
        // Выбрана группа + создана личная группа или не выбрана группа + создана личная группа
        else if ((groupChosen && groupCreated) || (!groupChosen && groupCreated)) {
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
