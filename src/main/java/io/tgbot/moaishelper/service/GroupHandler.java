package io.tgbot.moaishelper.service;

import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.model.*;
import io.tgbot.moaishelper.parser.ExcelParser;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.tgbot.moaishelper.service.MessageHandler.notificationMessage;
import static io.tgbot.moaishelper.text.Info.*;

/**
 * @Authors: Markelloww & YDK
 */
@Component
public class GroupHandler {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final StatusRepository statusRepository;
    private final MessageHandler messageHandler;

    public GroupHandler(UserRepository userRepository,
                        GroupRepository groupRepository,
                        StatusRepository statusRepository,
                        MessageHandler messageHandler) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.statusRepository = statusRepository;
        this.messageHandler = messageHandler;
    }

    // ---------> Команда /setadmin
    protected void handleSetAdminCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = user.getSelectedGroup();

        if (selectedGroup == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_NOT_SELECTED,
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        if (selectedGroup.getOwner().getId() != user.getId()) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, NOT_OWNER(selectedGroup),
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        messageHandler.sendMessageWithKeyboardMarkup(chatId, CHOOSE_MEMBER,
                KeyboardMarkupProvider.membersAnswers(9, selectedGroup));
    }

    protected void handleSetAdminInput(long chatId, long adminId) {
        User user = userRepository.findByChatId(chatId);

        User newAdmin = userRepository.findById(adminId).get();
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == newAdmin.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_IN_GROUP(newAdmin.getUserName()),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        if (group.getAdmins().stream().anyMatch(admin -> admin.getId() == newAdmin.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_ALREADY_ADMIN(newAdmin.getUserName()),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        group.setAdmin(newAdmin);
        messageHandler.sendMessage(newAdmin.getChatId(), NOTIFICATION_ADD_ADMIN(group));
        groupRepository.save(group);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_SET_ADMIN_SUCCESSFUL(newAdmin.getUserName()),
                KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
    }
    // <--------- Команда /setadmin

    // ---------> Команда /removeadmin
    protected void handleRemoveAdminCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = user.getSelectedGroup();
        if (selectedGroup == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_NOT_SELECTED,
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        if (selectedGroup.getOwner().getId() != user.getId()) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, NOT_OWNER(selectedGroup),
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        messageHandler.sendMessageWithKeyboardMarkup(chatId, CHOOSE_MEMBER,
                KeyboardMarkupProvider.adminAnswers(10, selectedGroup));
    }

    protected void handleRemoveAdminInput(long chatId, long adminId) {
        User user = userRepository.findByChatId(chatId);

        User removedAdmin = userRepository.findById(adminId).get();
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == removedAdmin.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_IN_GROUP(removedAdmin.getUserName()),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        if (group.getAdmins().stream().noneMatch(admin -> admin.getId() == removedAdmin.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_ADMIN(removedAdmin.getUserName()),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        else if (group.getOwner().getId() == removedAdmin.getId()) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, ERROR,
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        group.removeAdmin(removedAdmin);
        messageHandler.sendMessage(removedAdmin.getChatId(), NOTIFICATION_REMOVE_ADMIN(group));

        groupRepository.save(group);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_NOT_ADMIN_SUCCESSFUL(removedAdmin.getUserName()),
                KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
    }
    // <--------- Команда /removeadmin

    // ---------> Команда /message
    protected void handleMessageCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = user.getSelectedGroup();
        if (selectedGroup == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_NOT_SELECTED,
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        if (selectedGroup.getAdmins().stream().noneMatch(admin -> admin.getId() == user.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_ADMIN(user.getUserName()),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        messageHandler.sendMessageWithKeyboardMarkup(chatId, ENTER_MESSAGE, KeyboardMarkupProvider.denyInput());
        user.setStatus(statusRepository.findById(4));
        userRepository.save(user);
    }

    protected void handleMessageInput(long chatId, String textToSend) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        Groupe selectedGroup = user.getSelectedGroup();
        selectedGroup.getMembers().stream().filter(u -> u.getChatId() != chatId).forEach(u ->
                messageHandler.sendMessage(u.getChatId(), notificationMessage(user, selectedGroup, textToSend))
        );
        messageHandler.sendMessageWithKeyboardMarkup(chatId, SEND_MESSAGE_SUCCESSFUL,
                KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
    }
    // <--------- Команда /message

    protected void handleScheduleTwoColumnsInput(Message message, long chatId, Groupe group) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);
        if (messageHandler.downloadSchedule(message, group.getId())) {
            messageHandler.deleteMessage(chatId, message.getMessageId());
            if (ExcelParser.parseTwoColumnsSchedule(group.getId())) {
                messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_SET_SUCCESSFUL(group),
                        KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
                try {
                    FileUtils.delete(new File(String.format("src/main/resources/groups/%d/Schedule.xlsx",
                            group.getId())));
                } catch (IOException _) {
                }
            }
            else
                messageHandler.sendMessage(chatId, ERROR);
        }
    }

    protected void handleScheduleOneColumnInput(Message message, long chatId, Groupe group) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);
        if (messageHandler.downloadSchedule(message, group.getId())) {
            messageHandler.deleteMessage(chatId, message.getMessageId());
            if (ExcelParser.parseOneColumnSchedule(group.getId())) {
                messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_SET_SUCCESSFUL(group),
                        KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
                try {
                    FileUtils.delete(new File(String.format("src/main/resources/groups/%d/Schedule.xlsx",
                            group.getId())));
                } catch (IOException _) {
                }
            }
            else
                messageHandler.sendMessage(chatId, ERROR);
        }
    }

    // ---------> Команда /giveowner
    protected void handleOwnerCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = user.getSelectedGroup();
        if (selectedGroup == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_NOT_SELECTED,
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        if (selectedGroup.getOwner().getId() != user.getId()) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, NOT_OWNER(selectedGroup),
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        messageHandler.sendMessageWithKeyboardMarkup(chatId, CHOOSE_MEMBER,
                KeyboardMarkupProvider.userAnswers(8, selectedGroup));
    }

    protected void handleGiveOwnerInput(long chatId, long newOwnerId) {
        User user = userRepository.findByChatId(chatId);

        User newOwner = userRepository.findById(newOwnerId).get();
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == newOwner.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_IN_GROUP(newOwner.getUserName()),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
            return;
        }
        if (newOwner.getGroupUsers().stream().anyMatch(u -> u.getGroup().getOwner().getId() ==
                newOwner.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_ALREADY_OWNER(newOwner.getUserName()),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
            return;
        }
        group.setOwner(newOwner);
        messageHandler.sendMessage(newOwner.getChatId(), NOTIFICATION_GIVE_OWNER(group));
        groupRepository.save(group);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, GIVE_OWNER_SUCCESSFUL(newOwner.getUserName()),
                KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
    }
    // <--------- Команда /giveowner

    // ---------> Команда /kick
    protected void handleKickCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user.getSelectedGroup() == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_NOT_SELECTED,
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        Groupe selectedGroup = user.getSelectedGroup();
        if (selectedGroup.getAdmins().stream().noneMatch(admin -> admin.getId() == user.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, NOT_ADMIN(selectedGroup),
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        messageHandler.sendMessageWithKeyboardMarkup(chatId, CHOOSE_MEMBER,
                KeyboardMarkupProvider.membersAnswers(7, selectedGroup));
    }

    protected void handleKickUserInput(long chatId, long removedUserId) {
        User user = userRepository.findByChatId(chatId);

        User removedUser = userRepository.findById(removedUserId).get();
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == removedUser.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_IN_GROUP(removedUser.getUserName()),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        if (group.getOwner().getId() == removedUser.getId()) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, ERROR,
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        if (group.getAdmins().stream().anyMatch(a -> a.getId() == removedUser.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, ERROR,
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        group.removeMember(removedUser);
        messageHandler.sendMessage(removedUser.getChatId(), NOTIFICATION_KICK_USER(user, group));
        groupRepository.save(group);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_KICK_SUCCESSFUL(removedUser.getUserName()),
                KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
    }
    // <--------- Команда /kick

    // ---------> Команда /invite
    protected void handleInviteCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user.getSelectedGroup() == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_NOT_SELECTED,
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        Groupe selectedGroup = user.getSelectedGroup();
        if (selectedGroup.getAdmins().stream().noneMatch(admin -> admin.getId() == user.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, NOT_ADMIN(selectedGroup),
                    KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
            return;
        }
        messageHandler.sendMessageWithKeyboardMarkup(chatId, ENTER_USER_NAME, KeyboardMarkupProvider.denyInput());
        user.setStatus(statusRepository.findById(3));
        userRepository.save(user);
    }

    protected void handleInviteUserInput(long chatId, String invitedUserName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);
        if (userRepository.findByUserName(invitedUserName) == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_DOESNT_EXISTS(invitedUserName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
            return;
        }
        User invitedUser = userRepository.findByUserName(invitedUserName);
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().anyMatch(u -> u.getId() == invitedUser.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_ALREADY_IN_GROUP(invitedUserName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
            return;
        }
//        if (invitedUser.getStatus().getId() != 1) {
//            messageHandler.sendMessageWithKeyboardMarkup(chatId, ERROR,
//                    KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
//            return;
//        }
        sendInviteMessage(invitedUser, user, group);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, INVITE_SEND_SUCCESSFUL(invitedUserName),
                KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
    }

    protected void sendInviteMessage(User invitedUser, User invitor, Groupe group) {
        messageHandler.sendMessageWithKeyboardMarkup(invitedUser.getChatId(), INVITE_REQUEST(invitor, group),
                    KeyboardMarkupProvider.addInviteAnswers(invitor, group));
    }

    protected void addUserToGroup(User invitedUser, Groupe group, long invitorChatId) {
        if (group.getMembers().stream().anyMatch(u -> u.getId() == invitedUser.getId())) {
            messageHandler.sendMessage(invitedUser.getChatId(), USER_ALREADY_IN_GROUP(group));
            return;
        }
        group.addMember(invitedUser, false);
        groupRepository.save(group);
        messageHandler.sendMessage(invitorChatId, USER_JOIN_TO_INVITOR(invitedUser, group));
        messageHandler.sendMessageWithKeyboardMarkup(invitedUser.getChatId(), USER_JOIN_TO_INVITED(group),
                KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
        invitedUser.setSelectedGroup(group);
        userRepository.save(invitedUser);
    }

    protected void declineInvite(User invitedUser, Groupe group, long invitorChatId) {
        if (group.getMembers().stream().noneMatch(u -> u.getId() == invitedUser.getId())) {
            messageHandler.sendMessage(invitorChatId, INVITE_SEND_ACCEPT(invitedUser, group));
        }
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
            messageHandler.sendMessage(chatId, GROUP_NOT_SELECTED);
            return;
        }
        Groupe selectedGroup = user.getSelectedGroup();
        List<User> groupUsers = selectedGroup.getMembers();
        String message = IntStream.range(0, groupUsers.size())
                .mapToObj(i -> (i + 1) + ". @" + groupUsers.get(i).getUserName() +
                        isAdmin(selectedGroup, groupUsers.get(i)))
                .collect(Collectors.joining("\n"));
        messageHandler.sendMessageWithKeyboardMarkup(chatId, message,
                KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
    }

    // <--------- Команда /members

    // ---------> Команда /creategroup
    protected void handleCreateGroupCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (groupRepository.findByOwnerId(user.getId()) == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, ENTER_GROUP_NAME, KeyboardMarkupProvider.denyInput());
            user.setStatus(statusRepository.findById(2));
            userRepository.save(user);
        }
        else {
            messageHandler.sendMessage(chatId, TEXT_GROUP_EXISTS(groupRepository.findByOwnerId(user.getId())));
        }
    }

    protected void handleGroupNameInput(long chatId, String groupName) {
        User creator = userRepository.findByChatId(chatId);
        if (groupRepository.findByOwnerId(creator.getId()) != null) {
            creator.setStatus(statusRepository.findById(1));
            messageHandler.sendMessage(chatId, TEXT_GROUP_EXISTS(groupRepository.findByOwnerId(creator.getId())));            creator.setStatus(statusRepository.findById(1));
            userRepository.save(creator);
            return;
        }
        Groupe group = new Groupe(creator, groupName, new Timestamp(System.currentTimeMillis()));
        groupRepository.save(group);

        Groupe createdGroup = groupRepository.findByOwnerId(creator.getId());
        GroupUser owner = createdGroup.getGroupUsers().getFirst();
        owner.setSettings(new AdminScheduleSettings());
        groupRepository.save(createdGroup);

        new File(String.format("src/main/resources/groups/%d",
                groupRepository.findByOwnerId(creator.getId()).getId())).mkdirs();

        creator.setSelectedGroup(group);
        creator.setStatus(statusRepository.findById(1));
        userRepository.save(creator);

        boolean chosen = groupChosen(creator);
        boolean created = groupCreated(creator);
        if (created) {
            long groupId = groupRepository.findByOwnerId(creator.getId()).getId();
            try {
                FileUtils.copyFileToDirectory(new File("src/main/resources/Schedule.xlsx"),
                        new File(String.format("src/main/resources/groups/%d", groupId)));
            }
            catch (IOException _) {
            }
            ExcelParser.parseTwoColumnsSchedule(group.getId());
            try {
                FileUtils.delete(new File(String.format("src/main/resources/groups/%d/Schedule.xlsx",
                        groupId)));
            } catch (IOException _) {
            }
        }
        messageHandler.sendMessage(chatId, GROUP_CREATE_SUCCESSFUL(group));
        messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_MENU(creator),
                KeyboardMarkupProvider.groupsMenu(chosen, created));
    }
    // <--------- Команда /creategroup

    // ---------> Команда /selectgroup
    protected void handleGroupSelectCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);

        if (user.getGroupUsers().isEmpty()) {
            messageHandler.sendMessage(chatId, GROUP_LIST_EMPTY);
            return;
        }

        List<Groupe> groups = user.getGroupUsers().stream().map(GroupUser::getGroup).toList();
        InlineKeyboardMarkup inlineKeyboardMarkup = KeyboardMarkupProvider.addSelectGroupAnswers(groups);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_SELECT, inlineKeyboardMarkup);
    }

    protected void handleGroupSelectInput(long chatId, long groupId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = groupRepository.findById(groupId).get();
        user.setSelectedGroup(selectedGroup);
        userRepository.save(user);

        boolean chosen = groupChosen(user), created = groupCreated(user);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_MENU(user),
                KeyboardMarkupProvider.groupsMenu(chosen, created));
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
    protected void leaveGroup(long chatId, Groupe selectedGroup) {
        if (selectedGroup == null) {
            messageHandler.sendMessage(chatId, GROUP_NOT_SELECTED);
            return;
        }
        User user = userRepository.findByChatId(chatId);
        if (user.getId() == selectedGroup.getOwner().getId()){
            if (selectedGroup.getMembers().size() == 1) {
                deleteGroup(chatId, selectedGroup);
                return;
            }
            else if ((selectedGroup.getMembers().size() > 1)) {
                messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_EXIT_FAILED,
                        KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
                return;
            }
        }
        user.setSelectedGroup(null);
        selectedGroup.removeMember(user);
        userRepository.save(user);
        groupRepository.save(selectedGroup);

        user = userRepository.findByChatId(chatId);
        if (user.getGroupUsers() != null && !user.getGroupUsers().isEmpty()) {
            Groupe group = user.getGroupUsers().getFirst().getGroup();
            user.setSelectedGroup(group);
            userRepository.save(user);
        }
        messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_EXIT_SUCCESSFUL(selectedGroup),
                KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
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
            messageHandler.sendMessage(chatId, GROUP_NOT_SELECTED);
            return;
        }
        User user = userRepository.findByChatId(chatId);
        if (user.getId() != selectedGroup.getOwner().getId()) {
            messageHandler.sendMessage(chatId, NOT_OWNER(selectedGroup));
            return;
        }
        for (User groupUser : selectedGroup.getMembers()) {
            Groupe userSelectedGroup = groupUser.getSelectedGroup();
            if (userSelectedGroup != null && userSelectedGroup.getId() == selectedGroup.getId()) {
                if (groupUser.getGroupUsers() != null && groupUser.getGroupUsers().size() > 1) {
                    Groupe group = groupUser.getGroupUsers().getFirst().getGroup();
                    if (group.equals(selectedGroup))
                        group = groupUser.getGroupUsers().get(1).getGroup();
                    groupUser.setSelectedGroup(group);
                    userRepository.save(groupUser);
                }
                else {
                    groupUser.setSelectedGroup(null);
                    userRepository.save(groupUser);
                }
            }
        }
        String groupName = selectedGroup.getName();
        try {
            FileUtils.deleteDirectory(new File(String.format("src/main/resources/groups/%d",
                    selectedGroup.getId())));
        }
        catch (IOException _) {}
        List<User> members = selectedGroup.getMembers();
        for (User member : members) {
            if (member.getId() != selectedGroup.getOwner().getId()) {
                messageHandler.sendMessage(member.getChatId(), NOTIFICATION_DELETE_GROUP(selectedGroup));
            }
        }
        groupRepository.deleteById(selectedGroup.getId());
        messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_DELETE_SUCCESSFUL(groupName),
                KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
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

    public static String getRole(User user, Groupe group) {
        if (group.getOwner().getId() == user.getId()) {
            return "Владелец";
        }
        else if (group.getAdmins().stream().anyMatch(u -> u.getId() == user.getId())) {
            return "Администратор";
        }
        return "Пользователь";
    }

}
