package io.tgbot.moaishelper.service;

/**
 * @Authors: Markelloww & YDK
 */

import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.model.*;
import io.tgbot.moaishelper.text.Info;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class GroupHandler {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final StatusRepository statusRepository;
    private final TelegramBot bot;
    private final MessageHandler messageHandler;

    public GroupHandler(UserRepository userRepository,
                        GroupRepository groupRepository,
                        StatusRepository statusRepository,
                        TelegramBot bot,
                        MessageHandler messageHandler) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.statusRepository = statusRepository;
        this.bot = bot;
        this.messageHandler = messageHandler;
    }

    // ---------> Команда /setadmin
    protected void handleSetAdminCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = user.getSelectedGroup();
        if (selectedGroup == null) {
            messageHandler.sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        if (selectedGroup.getOwner().getId() != user.getId()) {
            messageHandler.sendMessage(chatId, Info.NOT_OWNER(selectedGroup));
            return;
        }
        messageHandler.sendMessage(chatId, "Введите @UserName человека, которого вы хотите назначить админом группы:");
        user.setStatus(statusRepository.findById(7));
        userRepository.save(user);
    }

    protected void handleSetAdminInput(long chatId, String newOwnerName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        if (userRepository.findByUserName(newOwnerName) == null) {
            messageHandler.sendMessage(chatId, Info.USER_NOT_EXISTS);
            return;
        }
        User newAdmin = userRepository.findByUserName(newOwnerName);
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == newAdmin.getId())) {
            messageHandler.sendMessage(chatId, "Пользователь не состоит в группе");
            return;
        }
        if (group.getAdmins().stream().anyMatch(admin -> admin.getId() == newAdmin.getId())) {
            messageHandler.sendMessage(chatId, "Пользователь уже является админом группы");
            return;
        }
        group.setAdmin(newAdmin);

        groupRepository.save(group);
        messageHandler.sendMessage(chatId, "Пользователь успешно назначен админом группы!");
    }
    // <--------- Команда /setadmin

    // ---------> Команда /removeadmin
    protected void handleRemoveAdminCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = user.getSelectedGroup();
        if (selectedGroup == null) {
            messageHandler.sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        if (selectedGroup.getOwner().getId() != user.getId()) {
            messageHandler.sendMessage(chatId, Info.NOT_OWNER(selectedGroup));
            return;
        }
        messageHandler.sendMessage(chatId, "Введите @UserName удаляемого админа:");
        user.setStatus(statusRepository.findById(8));
        userRepository.save(user);
    }

    protected void handleRemoveAdminInput(long chatId, String newOwnerName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        if (userRepository.findByUserName(newOwnerName) == null) {
            messageHandler.sendMessage(chatId, Info.USER_NOT_EXISTS);
            return;
        }
        User removedAdmin = userRepository.findByUserName(newOwnerName);
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == removedAdmin.getId())) {
            messageHandler.sendMessage(chatId, "Пользователь не состоит в группе");
            return;
        }
        if (group.getAdmins().stream().noneMatch(admin -> admin.getId() == removedAdmin.getId())) {
            messageHandler.sendMessage(chatId, "Пользователь не является админом группы");
            return;
        }
        else if (group.getOwner().getId() == removedAdmin.getId()) {
            messageHandler.sendMessage(chatId, "Владелец группы всегда является админом");
            return;
        }
        group.removeAdmin(removedAdmin);

        groupRepository.save(group);
        messageHandler.sendMessage(chatId, "Пользователь больше не админ группы!");
    }
    // <--------- Команда /removeadmin

    // ---------> Команда /giveowner
    protected void handleOwnerCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = user.getSelectedGroup();
        // Проверяем выбрана или группа
        if (selectedGroup == null) {
            messageHandler.sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        // Проверяем владелец ли он группы
        if (selectedGroup.getOwner().getId() != user.getId()) {
            messageHandler.sendMessage(chatId, Info.NOT_OWNER(selectedGroup));
            return;
        }
        messageHandler.sendMessage(chatId, "Введите @UserName человека, которого вы хотите назначить владельцем группы:");
        user.setStatus(statusRepository.findById(6));
        userRepository.save(user);
    }

    protected void handleGiveOwnerInput(long chatId, String newOwnerName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        // Проверяем есть ли такой человек в боте
        if (userRepository.findByUserName(newOwnerName) != null) {
            User newOwner = userRepository.findByUserName(newOwnerName);
            Groupe group = user.getSelectedGroup();
            //Проверяем состоит ли указанный пользователь в группе
            if (group.getMembers().stream().noneMatch(u -> u.getId() == newOwner.getId())) {
                messageHandler.sendMessage(chatId, "Пользователь не состоит в группе");
                return;
            }
            // Проверяем является ли он владельцев уже каких-либо других групп
            if (newOwner.getGroupUsers().stream().anyMatch(u -> u.getGroup().getOwner().getId() == newOwner.getId())) {
                messageHandler.sendMessage(chatId, "Пользователь уже является владельцем группы, укажите другого");
                return;
            }
            group.setOwner(newOwner);

            groupRepository.save(group);
            messageHandler.sendMessage(chatId, "Пользователь успешно назначен владельцем группы!");
        }
        else
            messageHandler.sendMessage(chatId, Info.USER_NOT_EXISTS);
    }
    // <--------- Команда /giveowner

    // ---------> Команда /kick
    protected void handleKickCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user.getSelectedGroup() != null) {
            Groupe selectedGroup = user.getSelectedGroup();
            if (selectedGroup.getAdmins().stream().anyMatch(admin -> admin.getId() == user.getId())) {
                messageHandler.sendMessage(chatId, "Введите @UserName исключаемого из группы человека");
                user.setStatus(statusRepository.findById(5));
                userRepository.save(user);
            }
            else
                messageHandler.sendMessage(chatId, Info.NOT_ADMIN(selectedGroup));
        }
        else
            messageHandler.sendMessage(chatId, Info.GROUP_NOT_SELECTED);
    }

    protected void handleKickUserInput(long chatId, String removedUserName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        if (userRepository.findByUserName(removedUserName) != null) {
            User removedUser = userRepository.findByUserName(removedUserName);
            Groupe group = user.getSelectedGroup();
            if (group.getMembers().stream().noneMatch(u -> u.getId() == removedUser.getId())) {
                messageHandler.sendMessage(chatId, "Пользователь не состоит в группе");
                return;
            }
            if (group.getOwner().getId() == removedUser.getId()) {
                messageHandler.sendMessage(chatId, "Вы не можете исключить владельца");
                return;
            }
            if (group.getAdmins().stream().anyMatch(a -> a.getId() == removedUser.getId())) {
                messageHandler.sendMessage(chatId, "Вы не можете исключить админа");
                return;
            }
            group.removeMember(removedUser);
            groupRepository.save(group);
            messageHandler.sendMessage(chatId, "Пользователь успешно исключен из группы!");
        }
        else
            messageHandler.sendMessage(chatId, Info.USER_NOT_EXISTS);
    }
    // <--------- Команда /kick

    // ---------> Команда /invite
    protected void handleInviteCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user.getSelectedGroup() != null) {
            Groupe selectedGroup = user.getSelectedGroup();
            if (selectedGroup.getAdmins().stream().anyMatch(admin -> admin.getId() == user.getId())) {
                messageHandler.sendMessage(chatId, "Введите @UserName приглашаемого в группу человека");
                user.setStatus(statusRepository.findById(3));
                userRepository.save(user);
            }
            else
                messageHandler.sendMessage(chatId, Info.NOT_ADMIN(selectedGroup));
        }
        else
            messageHandler.sendMessage(chatId, Info.GROUP_NOT_SELECTED);
    }

    protected void handleInviteUserInput(long chatId, String invitedUserName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        if (userRepository.findByUserName(invitedUserName) != null) {
            User invitedUser = userRepository.findByUserName(invitedUserName);
            Groupe group = user.getSelectedGroup();
            if (group.getMembers().stream().anyMatch(u -> u.getId() == invitedUser.getId())) {
                messageHandler.sendMessage(chatId, "Пользователь уже состоит в группе");
                return;
            }
            sendInviteMessage(invitedUser, user, group);
            messageHandler.sendMessage(chatId, "Приглашение отправлено пользователю");
        }
        else
            messageHandler.sendMessage(chatId, Info.USER_NOT_EXISTS);
    }

    protected void sendInviteMessage(User invitedUser, User invitor, Groupe group) {
        System.out.println(invitedUser);
        SendMessage message = new SendMessage();
        message.setChatId(invitedUser.getChatId());
        message.setText(String.format("Пользователь %s приглашает Вас в группу \"%s\"",
                invitor.getUserName(), group.getName()));
        InlineKeyboardMarkup answerKeyBoard = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton acceptButton = new InlineKeyboardButton();
        acceptButton.setText("Вступить");
        acceptButton.setCallbackData(String.format("1 %d %d", group.getId(), invitor.getChatId()));

        InlineKeyboardButton declineButton = new InlineKeyboardButton();
        declineButton.setText("Отклонить");
        declineButton.setCallbackData(String.format("2 %d %d", group.getId(), invitor.getChatId()));

        rows.add(List.of(declineButton, acceptButton));
        answerKeyBoard.setKeyboard(rows);
        message.setReplyMarkup(answerKeyBoard);
        System.out.println(message);
        try {
            bot.execute(message);
        } catch (TelegramApiException _) {}
    }

    protected void addUserToGroup(User invitedUser, Groupe group, long invitorChatId) {
        System.out.println(invitorChatId);
        if (group.getMembers().stream().noneMatch(u -> u.getId() == invitedUser.getId())) {
            group.addMember(invitedUser, false);
            groupRepository.save(group);
            messageHandler.sendMessage(invitorChatId,
                    String.format("Пользователь %s успешно добавлен в группу \"%s\"!",
                            invitedUser.getUserName(), group.getName()));
            messageHandler.sendMessage(invitedUser.getChatId(), String.format("Вы вошли в группу \"%s\"", group.getName()));
            invitedUser.setSelectedGroup(group);
            userRepository.save(invitedUser);
            return;
        }
        messageHandler.sendMessage(invitedUser.getChatId(), "Вы уже состоите в этой группе");
    }

    protected void declineInvite(User invitedUser, Groupe group, long invitorChatId) {
        if (group.getMembers().stream().noneMatch(u -> u.getId() == invitedUser.getId()))
            messageHandler.sendMessage(invitorChatId,
                    String.format("Пользователь %s отклонил приглашение в группу \"%s\"!",
                            invitedUser.getUserName(), group.getName()));
    }
    // <--------- Команда /invite

    // ---------> Команда /members
    /**
     * Отправляет пользователю список участников выбранной группы с указанием их статуса (владелец или админ).
     *
     * @param chatId идентификатор чата пользователя, для которого отображается список участников
     */
    protected void showMembers(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user.getSelectedGroup() == null) {
            messageHandler.sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        Groupe selectedGroup = user.getSelectedGroup();
        List<User> groupUsers = selectedGroup.getMembers();
        String message = IntStream.range(0, groupUsers.size())
                .mapToObj(i -> (i + 1) + ". @" + groupUsers.get(i).getUserName() + isAdmin(selectedGroup, groupUsers.get(i)))
                .collect(Collectors.joining("\n"));
        messageHandler.sendMessage(chatId, message);
    }

    // <--------- Команда /members

    // ---------> Команда /creategroup
    protected void handleCreateGroupCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (groupRepository.findByOwnerId(user.getId()) == null) {
            messageHandler.sendMessage(chatId, Info.GROUP_CREATE);
            user.setStatus(statusRepository.findById(2));
            userRepository.save(user);
        }
        else {
            messageHandler.sendMessage(chatId, Info.TEXT_GROUP_EXISTS(groupRepository.findByOwnerId(user.getId())));
        }
    }

    protected void handleGroupNameInput(long chatId, String groupName) {
        User creator = userRepository.findByChatId(chatId);
        Groupe group = new Groupe(creator, groupName, new Timestamp(System.currentTimeMillis()));
        System.out.println(group.getId());
        groupRepository.save(group);

        new File(String.format("src/main/resources/groups/%d",
                groupRepository.findByOwnerId(creator.getId()).getId())).mkdirs();

        new File(String.format("src/main/resources/groups/%d/homeworks",
                groupRepository.findByOwnerId(creator.getId()).getId())).mkdirs();

        new File(String.format("src/main/resources/groups/%d/lectures",
                groupRepository.findByOwnerId(creator.getId()).getId())).mkdirs();

        creator.setSelectedGroup(group);
        creator.setStatus(statusRepository.findById(1));
        userRepository.save(creator);

        boolean chosen = groupChosen(creator);
        boolean created = groupCreated(creator);
        messageHandler.sendMessage(chatId, Info.GROUP_CREATE_SUCCESSFUL(group));
        messageHandler.sendMessageWithKeyboardMarkup(chatId, Info.GROUP_MENU(creator), KeyboardMarkupProvider.groupsMenu(chosen, created));
    }
    // <--------- Команда /creategroup

    // ---------> Команда /selectgroup
    protected void handleGroupSelectCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);

        if (user.getGroupUsers().isEmpty()) {
            messageHandler.sendMessage(chatId, Info.NO_GROUPS);
            return;
        }

        user.setStatus(statusRepository.findById(4));
        userRepository.save(user);

        List<Groupe> groups = user.getGroupUsers().stream().map(GroupUser::getGroup).toList();
        InlineKeyboardMarkup inlineKeyboardMarkup = KeyboardMarkupProvider.addSelectGroupAnswers(groups);
        messageHandler.sendMessageWithKeyboardMarkup(chatId,Info.GROUP_SELECT, inlineKeyboardMarkup);
    }

    protected void handleGroupSelectInput(long chatId, String stringGroupId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = groupRepository.findById(Long.parseLong(stringGroupId)).get();
        user.setSelectedGroup(selectedGroup);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        boolean chosen = groupChosen(user), created = groupCreated(user);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, Info.GROUP_MENU(user), KeyboardMarkupProvider.groupsMenu(chosen, created));
    }
    // <--------- Команда /selectgroup

    // ---------> Команда /leavegroup
    /**
     * Удаляет пользователя из выбранной группы.
     * Если пользователь является владельцем группы и в группе только он один, то группа удаляется.
     * Если в группе есть другие участники, то пользователь должен передать роль владельца другому участнику,
     * иначе операция выхода не выполнится.
     *
     * @param chatId идентификатор чата пользователя, который хочет выйти из группы
     * @param selectedGroup группа, из которой пользователь хочет выйти
     */
    protected boolean leaveGroup(long chatId, Groupe selectedGroup) {
        if (selectedGroup == null) {
            messageHandler.sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return false;
        }
        User user = userRepository.findByChatId(chatId);
        if (user.getId() == selectedGroup.getOwner().getId()){
            if (selectedGroup.getMembers().size() == 1) {
                deleteGroup(chatId, selectedGroup);
                return false;
            }
            else if ((selectedGroup.getMembers().size() > 1)) {
                messageHandler.sendMessageWithKeyboardMarkup(chatId, Info.GROUP_EXIT_FAILED, KeyboardMarkupProvider.inlineContinueButton());
                return false;
            }
        }
        user.setSelectedGroup(null);
        selectedGroup.removeMember(user);
        userRepository.save(user);
        groupRepository.save(selectedGroup);
        messageHandler.sendMessage(chatId, Info.GROUP_EXIT_SUCCESSFUL(selectedGroup));
        return true;
    }
    // <--------- Команда /leavegroup

    // ---------> Команда /deletegroup
    /**
     * Удаляет группу из базы данных и обнуляет все связи с ней.
     * (Необходимо быть владельцем группы).
     *
     * @param chatId идентификатор чата
     * @param selectedGroup группа, которую надо удалить
     */
    protected void deleteGroup(long chatId, Groupe selectedGroup) {
        if (selectedGroup == null) {
            messageHandler.sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        User user = userRepository.findByChatId(chatId);
        if (user.getId() != selectedGroup.getOwner().getId()) {
            messageHandler.sendMessage(chatId, Info.NOT_OWNER(selectedGroup));
            return;
        }
        for (User groupUser : selectedGroup.getMembers()) {
            if (user.getSelectedGroup().getId() == selectedGroup.getId()) {
                groupUser.setSelectedGroup(null);
                userRepository.save(groupUser);
            }
        }
        messageHandler.sendMessageWithKeyboardMarkup(chatId, Info.GROUP_DELETE_SUCCESSFUL(selectedGroup),
                KeyboardMarkupProvider.inlineContinueButton());
        groupRepository.deleteById(selectedGroup.getId());
        try {
            FileUtils.deleteDirectory(new File(String.format("src/main/resources/groups/%d",
                    selectedGroup.getId())));
        }
        catch (IOException _) {}
    }
    // <--------- Команда /deletegroup

    protected boolean groupChosen(User user) {
        return user.getSelectedGroup() != null;
    }

    protected boolean groupCreated(User user) {
        return groupRepository.findByOwnerId(user.getId()) != null;
    }

    private String isAdmin(Groupe selectedGroup, User user) {
        if (selectedGroup.getOwner().getId() == user.getId()) {
            return " (Владелец)";
        }
        if (selectedGroup.getAdmins().stream().anyMatch(u -> u.equals(user))) {
            return " (Админ)";
        }
        return "";
    }
}
