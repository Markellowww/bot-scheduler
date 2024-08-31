package io.tgbot.moaishelper.text;

/*
  @Authors: Markelloww & YDK
*/

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.model.Groupe;
import io.tgbot.moaishelper.model.User;
import io.tgbot.moaishelper.service.GroupHandler;


public class Info {

    public static final String TEXT_SUPPORT = EmojiParser.parseToUnicode(
            """
                    Если у вас возникли проблемы либо касаемо работы бота, или есть какие-либо предложения:

                    :arrow_backward:Telegram: @markellowww, @YDKrivoshey

                    :smiley_cat:GitHub: Markelloww, KrivosheyYuriy""");

    public static final String TEXT_IN_DEVELOP = EmojiParser.parseToUnicode("В разработке :disappointed_relieved:");

    public static final String ERROR = "❗ Что-то пошло не так ❗";

    public static String USER_DOESNT_EXISTS(String username) {
        return String.format("❗ Пользователь @%s не зарегистрирован в боте ❗", username);
    }

    public static String USER_IS_NOT_IN_GROUP(String username) {
        return String.format("❗ Пользователь @%s не состоит в группе ❗", username);
    }

    public static String USER_IS_NOT_ADMIN(String username) {
        return String.format("❗ Пользователь @%s не является админом ❗", username);
    }

    public static String USER_ALREADY_IN_GROUP(String username) {
        return String.format("❗ Пользователь @%s уже состоит в группе ❗", username);
    }

    public static String USER_ALREADY_ADMIN(String username) {
        return String.format("❗ Пользователь @%s уже является админом ❗", username);
    }

    public static String USER_ALREADY_OWNER(String username) {
        return String.format("❗ Пользователь @%s уже владеет одной группой ❗", username);
    }

    public static String USER_ALREADY_IN_GROUP(Groupe group) {
        return String.format("❗ Вы состоите в группе \"%s\" ❗", group.getName());
    }

    public static final String GROUP_EXIT_FAILED = "❗ Вы не сможете выйти из группы, пока не передадите права владения ей другому человеку ❗";

    public static final String GROUP_NOT_SELECTED = "❗ Не выбрана текущая группа ❗";

    public static final String GROUP_LIST_EMPTY = "❗ Вы не состоите ни в одной группе ❗";

    public static String TEXT_GROUP_EXISTS(Groupe group) {
        return String.format("❗ Вы уже являетесь владельцем группы \"%s\" ❗", group.getName());
    }

    public static String NOTIFICATION_REMOVE_ADMIN(Groupe group) {
        return String.format("❗ Владелец группы \"%s\" забрал у Вас права администратора ❗", group.getName());
    }

    public static String NOTIFICATION_KICK_USER(User user, Groupe group) {
        return String.format("❗ Пользователь @%s исключил Вас из группы \"%s\" ❗", user.getUserName(), group.getName());
    }

    public static String NOTIFICATION_GIVE_OWNER(Groupe group) {
        return String.format("❗ Теперь вы новый владелец группы \"%s\" ❗", group.getName());
    }

    public static String NOTIFICATION_ADD_ADMIN(Groupe group) {
        return String.format("❗ Вы были назначены администратором группы \"%s\" ❗", group.getName());
    }

    public static String NOTIFICATION_DELETE_GROUP(Groupe group) {
        return String.format("❗ Группа \"%s\" была удалена владельцем ❗", group.getName());
    }

    public static String NOT_ADMIN(Groupe group) {
        return String.format("❗ Вы не являетесь администратором группы \"%s\" ❗", group.getName());
    }

    public static String NOT_OWNER(Groupe group) {
        return String.format("❗ Вы не являетесь владельцем группы \"%s\" ❗", group.getName());
    }

    public static String INVITE_SEND_ACCEPT(User user, Groupe group) {
        return  String.format("❗ Пользователь @%s отклонил приглашение в группу \"%s\" ❗",
                user.getUserName(), group.getName());
    }

    public static final String GROUP_SELECT = EmojiParser.parseToUnicode("Выберите группу из списка :school:\n") +
            "\n" +
            "❗ Учтите, выбрав группу, вы будете получать информацию касаемо только выбранной группы ❗\n\n" +
            "❗ (Уведомления от групп приходят независимо от выбранной группы) ❗";

    public static String INVITE_REQUEST(User user, Groupe group) {
        return String.format("Пользователь @%s приглашает Вас в группу \"%s\"",
                user.getUserName(), group.getName());
    }

    public static String GROUP_EXIT_SUCCESSFUL(Groupe group) {
        return String.format("Вы успешно вышли из группы \"%s\" ✅", group.getName());
    }

    public static String GROUP_CREATE_SUCCESSFUL(Groupe group) {
        return String.format("Группа \"%s\" была создана ✅", group.getName());
    }

    public static String GROUP_DELETE_SUCCESSFUL(String groupName) {
        return String.format("Группа \"%s\" была удалена ✅", groupName);
    }

    public static String USER_NOT_ADMIN_SUCCESSFUL(String username){
        return String.format("Пользователь @%s больше не админ ✅", username);
    }

    public static String USER_KICK_SUCCESSFUL(String username) {
        return String.format("Пользователь @%s успешно исключен из группы ✅", username);
    }

    public static String USER_SET_ADMIN_SUCCESSFUL(String username) {
        return String.format("Пользователь @%s успешно назначен админом группы ✅", username);
    }

    public static String INVITE_SEND_SUCCESSFUL(String username) {
        return String.format("Приглашение пользователю @%s успешно отправлено ✅", username);
    }

    public static String GIVE_OWNER_SUCCESSFUL(String username) {
        return String.format("Пользователь @%s успешно назначен владельцем группы ✅", username);
    }

    public static String USER_JOIN_TO_INVITOR(User user, Groupe group) {
        return String.format("Пользователь @%s успешно добавлен в группу \"%s\" ✅",
                user.getUserName(), group.getName());
    }

    public static String USER_JOIN_TO_INVITED(Groupe group) {
        return String.format("Вы вошли в группу \"%s\" ✅", group.getName());
    }

    public static String SEND_MESSAGE_SUCCESSFUL = "Сообщение было успешно отправлено всем участникам группы ✅";

    public static final String ENTER_GROUP_NAME = "Введите название для группы ✍️";

    public static final String ENTER_USER_NAME = "Введите @Username пользователя ✍️";

    public static final String ENTER_MESSAGE = "Введите сообщение, которое будет отправлено группе ✍️";

    public static final String CHOOSE_MEMBER = EmojiParser.parseToUnicode("Выберите пользователя :point_down:");

    public static final String CHOOSE_SCHEDULE = EmojiParser.parseToUnicode("Выберите интересующее Вас расписание :point_down:");


    public static String INVITE_REQUEST_DENIED(Groupe group) {
        return String.format("Вы отклонили приглашение в группу \"%s\"", group.getName());
    }

    public static String GROUP_MENU(User user) {
        if (user.getSelectedGroup() == null) {
            return EmojiParser.parseToUnicode("""
                    Пока что вы не состоите ни в одной группе :worried:

                    Вы можете создать свою собственную группу :school:

                    Или попросить прислать вам приглашение :inbox_tray:""");
        }
        Groupe group = user.getSelectedGroup();
        String role = GroupHandler.getRole(user, group);
        int membersCount = group.getMembers().size();
        String date = group.getCreatedAt().toString().substring(0, 10);
        return EmojiParser.parseToUnicode(":school: ") + String.format("Текущая группа: %s\n\n", group.getName()) +
                EmojiParser.parseToUnicode(":crown: ") + String.format("Ваша роль: %s\n\n", role) +
                EmojiParser.parseToUnicode(":elephant: ") + String.format("Количество участников: %d\n\n", membersCount) +
                EmojiParser.parseToUnicode(":clock3: ") + String.format("Дата создания группы (г/м/д): %s", date);
    }
}
