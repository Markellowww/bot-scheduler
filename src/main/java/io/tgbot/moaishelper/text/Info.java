package io.tgbot.moaishelper.text;

/**
 * @Authors: Markelloww & YDK
 */

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.model.Groupe;
import io.tgbot.moaishelper.model.User;

public class Info {
    // общие ответы
    public static final String TEXT_SUPPORT = EmojiParser.parseToUnicode(
            """
                    Если у вас возникли проблемы либо касаемо работы бота, или есть какие-либо предложения:

                    :arrow_backward:Telegram: @markellowww, @YDKrivoshey

                    :smiley_cat:GitHub: Markelloww, KrivosheyYuriy""");

    public static final String TEXT_IN_DEVELOP = EmojiParser.parseToUnicode("В разработке :disappointed_relieved:");

    public static final String ERROR = EmojiParser.parseToUnicode("❗ Что-то пошло не так ❗");

    // ответы об отклонении операции
    public static final String NOT_SELECTED_ARGUMENT = EmojiParser.parseToUnicode("❗ Операция отменена ❗");

    public static final String USER_NOT_EXISTS =
            EmojiParser.parseToUnicode("❗ Данный пользователь не зарегистрирован в боте ❗");

    public static final String USER_NOT_IN_GROUP =
            EmojiParser.parseToUnicode("❗ Данный пользователь не состоит в группе ❗");

    public static final String USER_ALREADY_NOT_ADMIN =
            EmojiParser.parseToUnicode("❗ Данный пользователь не является админом ❗");

    public static final String USER_ALREADY_IN_GROUP =
            EmojiParser.parseToUnicode("❗ Данный пользователь уже состоит в группе ❗");

    public static final String USER_ALREADY_ADMIN =
            EmojiParser.parseToUnicode("❗ Данный пользователь уже является админом ❗");

    public static final String USER_ALREADY_OWNER =
            EmojiParser.parseToUnicode("❗ Данный пользователь уже владеет одной группой ❗");

    public static final String GROUP_EXIT_FAILED =
            EmojiParser.parseToUnicode(
                    "❗ Вы не сможете выйти из группы, пока не передадите права владения ей другому человеку! ❗");

    public static final String GROUP_NOT_SELECTED = EmojiParser.parseToUnicode("❗ Не выбрана текущая группа ❗");

    public static final String NO_GROUPS = EmojiParser.parseToUnicode("❗ Вы не состоите ни в одной группе ❗");

    // ответы об успешной опреации
    public static final String USER_NOT_ADMIN_SUCCESSFUL =
            EmojiParser.parseToUnicode("Пользователь больше не админ! ✅");

    public static final String USER_KICK_SUCCESSFUL =
            EmojiParser.parseToUnicode("Пользователь успешно исключен из группы! ✅");

    public static final String USER_SET_ADMIN_SUCCESSFUL =
            EmojiParser.parseToUnicode("Пользователь успешно назначен админом группы! ✅");

    public static final String INVITE_SEND_SUCCESSFUL = "Приглашение успешно отправлено! ✅";

    public static final String GIVE_OWNER_SUCCESSFUL = "Пользователь успешно назначен владельцем группы!✅";

    // сообщения указаний
    public static final String GROUP_CREATE = "Введите название для группы ✍️";

    public static final String GROUP_SELECT = EmojiParser.parseToUnicode("Выберите группу из списка :school:\n") +
            "\n" +
            "❗ Учтите, выбрав группу, вы будете получать информацию касаемо только выбранной группы ❗";

    // сообщения с аргументами
    public static String INVITE_REQUEST_DENIED(Groupe group) {
        return String.format("Вы отклонили приглашение в группу \"%s\"", group.getName());
    }

    public static String GROUP_CREATE_SUCCESSFUL(Groupe group) {
        return String.format("Группа \"%s\" была создана! ✅", group.getName());
    }

    public static String GROUP_DELETE_SUCCESSFUL(Groupe group) {
        return String.format("Группа \"%s\" была удалена! ✅", group.getName());
    }

    public static String GROUP_MENU(User user) {
        if (user.getSelectedGroup() == null) {
            return EmojiParser.parseToUnicode("Пока что вы не состоите ни в одной группе :worried:\n" +
                    "\n" +
                    "Вы можете создать свою собственную группу :school:\n" +
                    "\n" +
                    "Или попросить прислать вам приглашение :inbox_tray:");
        }
        return String.format("Текущая группа: \"%s\"", user.getSelectedGroup().getName());
    }

    public static String GROUP_EXIT_SUCCESSFUL(Groupe group) {
        return String.format("Вы успешно вышли из группы \"%s\" ✅", group.getName());
    }

    public static String TEXT_GROUP_EXISTS(Groupe group) {
        return String.format("❗ Вы уже являетесь владельцем группы \"%s\" ❗", group.getName());
    }

    public static String NOT_ADMIN(Groupe group) {
        return String.format("❗ Вы не являетесь администратором группы \"%s\" ❗", group.getName());
    }

    public static String NOT_OWNER(Groupe group) {
        return String.format("❗ Вы не являетесь владельцем группы \"%s\" ❗", group.getName());
    }
}
