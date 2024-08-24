package io.tgbot.moaishelper.service;

/**
 * @Authors: Markelloww & YDK
 */

import com.vdurmont.emoji.EmojiParser;

public class Info {
    public static String TEXT_ABOUT() {
        return EmojiParser.parseToUnicode(
                "Если у вас возникли проблемы, вопросы по работе бота, или есть какие-либо предложения:\n\n" +
                        ":arrow_backward:Telegram: @fsbrossii\n\n" +
                        ":pushpin:GitHub: https://github.com/Markelloww\n\n" +
                        ":e-mail:Почта: markelloww@internet.ru");
    }

    public static String TEXT_IN_DEVELOP() {
        return EmojiParser.parseToUnicode("В разработке :disappointed_relieved:");
    }

    public static String TEXT_GROUP_EXISTS() {
        return EmojiParser.parseToUnicode("Вы уже являетесь владельцем одной группы.");
    }

    public static String NO_GROUPS() {
        return EmojiParser.parseToUnicode("Вы не состоите ни в одной группе");
    }

    public static String GROUP_NOT_SELECTED() {
        return EmojiParser.parseToUnicode("Не выбрана текущая группа");
    }

    public static String NOT_ADMIN() {
        return EmojiParser.parseToUnicode("Вы не являетесь администратором данной группы");
    }

    public static String USER_NOT_EXISTS() {
        return EmojiParser.parseToUnicode("Такой пользователь не зарегистрирован в боте");
    }

    public static String NOT_SELECTED_ARGUMENT() {
        return EmojiParser.parseToUnicode("Пожалуйста, выберите один из предложенных вариантов ответа");
    }
}
