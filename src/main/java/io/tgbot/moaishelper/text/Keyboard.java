package io.tgbot.moaishelper.text;

import com.vdurmont.emoji.EmojiParser;

/**
 * @Authors: Markelloww & YDK
 */
public class Keyboard {
    public static final String CONTACTS = EmojiParser.parseToUnicode(":telephone_receiver: Контакты");
    public static final String ABOUT = EmojiParser.parseToUnicode(":closed_book: О видах расписания");
    public static final String NOTIFICATION_SETTING = EmojiParser.parseToUnicode(":bell: Настройки уведомлений");

    public static final String GO_TO_GROUPS = EmojiParser.parseToUnicode(":school: Перейти к группам");
    public static final String BACK_TO_GROUPS = EmojiParser.parseToUnicode(":school: Вернуться к группам");
    public static final String BACK_TO_GROUP_SETTINGS = EmojiParser.parseToUnicode(":school: Вернуться назад");
    public static final String BACK_TO_MENU_GROUPS = EmojiParser.parseToUnicode(":school: Вернуться в меню группы");
    public static final String NOTIFICATION_FOR_ALL = EmojiParser.parseToUnicode(":incoming_envelope: Сообщение группе");

    public static final String SHOW_SCHEDULE = EmojiParser.parseToUnicode(":date: К расписанию");
    public static final String LEAVE_GROUP = EmojiParser.parseToUnicode(":x: Покинуть группу");
    public static final String SHOW_MEMBERS = EmojiParser.parseToUnicode(":memo: Показать участников");
    public static final String DELETE_GROUP = EmojiParser.parseToUnicode("❗Удалить группу");

    public static final String GIVE_OWNER = EmojiParser.parseToUnicode("❗Передать роль Владельца");

    public static final String REMOVE_ADMIN = EmojiParser.parseToUnicode(":x: Удалить админа");
    public static final String SET_ADMIN = EmojiParser.parseToUnicode("✅ Добавить админа");

    public static final String KICK_USER = EmojiParser.parseToUnicode(":x:Удалить пользователя");
    public static final String INVITE_USER = EmojiParser.parseToUnicode("✅ Пригласить пользователя");

    public static final String SET_SCHEDULE = EmojiParser.parseToUnicode("⚙️ Установить расписание");
    public static final String EXCEL_SPREADSHEET = EmojiParser.parseToUnicode("⚙️ Excel-таблицей");
    public static final String MANUAL_MODIFICATION = EmojiParser.parseToUnicode("⚙️ Ручное изменение");
    public static final String GROUP_HANDLER = EmojiParser.parseToUnicode("⚙️ Управление группой");
}
