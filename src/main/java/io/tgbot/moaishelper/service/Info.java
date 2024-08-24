package io.tgbot.moaishelper.service;

/**
 * @Authors: Markelloww & YDK
 */

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.model.Groupe;

public class Info {
    static final String TEXT_SUPPORT = EmojiParser.parseToUnicode(
                "Если у вас возникли проблемы, вопросы по работе бота, или есть какие-либо предложения:\n\n" +
                        ":arrow_backward:Telegram: @fsbrossii\n\n" +
                        ":pushpin:GitHub: https://github.com/Markelloww\n\n" +
                        ":e-mail:Почта: markelloww@internet.ru");

    public static String TEXT_IN_DEVELOP() {
        return EmojiParser.parseToUnicode("В разработке :disappointed_relieved:");
    }

    public static String TEXT_GROUP_EXISTS() {
        return EmojiParser.parseToUnicode("Вы уже являетесь владельцем одной группы.");
    }

    public static String NO_GROUPS() {
        return EmojiParser.parseToUnicode("Вы не состоите ни в одной группе");
    }

    static final String GROUP_EXIT_SUCCESSFUL = EmojiParser.parseToUnicode("Вы успешно вышли из группы!");
    static final String GROUP_EXIT_FAILED = EmojiParser.parseToUnicode("Вы не сможете выйти из группы, пока не передадите права владения ей другому человеку!");
    static final String GROUP_NOT_SELECTED = EmojiParser.parseToUnicode("Не выбрана текущая группа");

    public static String NOT_ADMIN(Groupe selectedGroup) {
        return String.format("Вы не являетесь администратором группы: \"%s\"", selectedGroup.getName());
    }

    public static String NOT_OWNER(Groupe selectedGroup) {
        return String.format("Вы не являетесь владельцем группы: \"%s\"", selectedGroup.getName());
    }

    public static String USER_NOT_EXISTS() {
        return EmojiParser.parseToUnicode("Такой пользователь не зарегистрирован в боте");
    }

    public static String NOT_SELECTED_ARGUMENT() {
        return EmojiParser.parseToUnicode("Пожалуйста, выберите один из предложенных вариантов ответа");
    }
}
