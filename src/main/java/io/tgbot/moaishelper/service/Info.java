package io.tgbot.moaishelper.service;

/**
 * @Authors: Markelloww & YDK
 */

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.model.Groupe;
import io.tgbot.moaishelper.model.User;

public class Info {
    public static final String TEXT_SUPPORT = EmojiParser.parseToUnicode(
            """
                    Если у вас возникли проблемы, вопросы по работе бота, или есть какие-либо предложения:

                    :arrow_backward:Telegram: @fsbrossii

                    :smiley_cat:GitHub: github.com/Markelloww

                    :incoming_envelope:Почта: markelloww@internet.ru""");

    public static final String TEXT_IN_DEVELOP = EmojiParser.parseToUnicode("В разработке :disappointed_relieved:");

    public static final String NO_GROUPS = EmojiParser.parseToUnicode("Вы не состоите ни в одной группе");

    public static final String USER_NOT_EXISTS = EmojiParser.parseToUnicode("Такой пользователь не зарегистрирован в боте");

    public static final String NOT_SELECTED_ARGUMENT = EmojiParser.parseToUnicode("Пожалуйста, выберите один из предложенных вариантов ответа");

    public static final String GROUP_EXIT_FAILED = EmojiParser.parseToUnicode("Вы не сможете выйти из группы, пока не передадите права владения ей другому человеку!");

    public static final String GROUP_NOT_SELECTED = EmojiParser.parseToUnicode("Не выбрана текущая группа");

    public static String GROUP_MENU(User user) {
        String groupName;
        if (user.getSelectedGroup() == null) {
            return "Текущая группа не выбрана";
        }
        return String.format("Текущая выбранная группа: \"%s\"", user.getSelectedGroup().getName());
    }

    public static String GROUP_EXIT_SUCCESSFUL(Groupe group) {
        return String.format("Вы успешно вышли из группы \"%s\"", group.getName());
    }

    public static String TEXT_GROUP_EXISTS(Groupe group) {
        return String.format("Вы уже являетесь владельцем группы \"%s\"", group.getName());
    }

    public static String NOT_ADMIN(Groupe group) {
        return String.format("Вы не являетесь администратором группы \"%s\"", group.getName());
    }

    public static String NOT_OWNER(Groupe group) {
        return String.format("Вы не являетесь владельцем группы \"%s\"", group.getName());
    }
}
