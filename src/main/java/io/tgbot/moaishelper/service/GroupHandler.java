package io.tgbot.moaishelper.service;

import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.model.*;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.tgbot.moaishelper.text.Info.*;

/**
 * @Authors: Markelloww & YDK
 */

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
                KeyboardMarkupProvider.membersAnswers(selectedGroup));
        user.setStatus(statusRepository.findById(7));
        userRepository.save(user);
    }

    protected void handleSetAdminInput(long chatId, String adminName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        if (userRepository.findByUserName(adminName) == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_DOESNT_EXISTS(adminName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        User newAdmin = userRepository.findByUserName(adminName);
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == newAdmin.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_IN_GROUP(adminName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        if (group.getAdmins().stream().anyMatch(admin -> admin.getId() == newAdmin.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_ALREADY_ADMIN(adminName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        group.setAdmin(newAdmin);

        groupRepository.save(group);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_SET_ADMIN_SUCCESSFUL(adminName),
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
                KeyboardMarkupProvider.adminAnswers(selectedGroup));
        user.setStatus(statusRepository.findById(8));
        userRepository.save(user);
    }

    protected void handleRemoveAdminInput(long chatId, String adminName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        if (userRepository.findByUserName(adminName) == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_DOESNT_EXISTS(adminName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        User removedAdmin = userRepository.findByUserName(adminName);
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == removedAdmin.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_IN_GROUP(adminName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        if (group.getAdmins().stream().noneMatch(admin -> admin.getId() == removedAdmin.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_ADMIN(adminName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        else if (group.getOwner().getId() == removedAdmin.getId()) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, ERROR,
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        group.removeAdmin(removedAdmin);

        groupRepository.save(group);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_NOT_ADMIN_SUCCESSFUL(adminName),
                KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
    }
    // <--------- Команда /removeadmin

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
                KeyboardMarkupProvider.userAnswers(selectedGroup));
        user.setStatus(statusRepository.findById(6));
        userRepository.save(user);
    }

    protected void handleGiveOwnerInput(long chatId, String newOwnerName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);
        if (userRepository.findByUserName(newOwnerName) == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_DOESNT_EXISTS(newOwnerName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
            return;
        }
        User newOwner = userRepository.findByUserName(newOwnerName);
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == newOwner.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_IN_GROUP(newOwnerName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
            return;
        }
        if (newOwner.getGroupUsers().stream().anyMatch(u -> u.getGroup().getOwner().getId() == newOwner.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_ALREADY_OWNER(newOwnerName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
            return;
        }
        group.setOwner(newOwner);
        groupRepository.save(group);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, GIVE_OWNER_SUCCESSFUL(newOwnerName),
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
                KeyboardMarkupProvider.membersAnswers(selectedGroup));
        user.setStatus(statusRepository.findById(5));
        userRepository.save(user);
    }

    protected void handleKickUserInput(long chatId, String removedUserName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);
        if (userRepository.findByUserName(removedUserName) == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_DOESNT_EXISTS(removedUserName),
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }
        User removedUser = userRepository.findByUserName(removedUserName);
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == removedUser.getId())) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_IS_NOT_IN_GROUP(removedUserName),
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
        groupRepository.save(group);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_KICK_SUCCESSFUL(removedUserName),
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
        messageHandler.sendMessage(chatId, ENTER_USERNAME);
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
        if (invitedUser.getStatus().getId() != 1) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, ERROR,
                    KeyboardMarkupProvider.inlineContinueButtonToGroupSettingMenu());
            return;
        }
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
                .mapToObj(i -> (i + 1) + ". @" + groupUsers.get(i).getUserName() + isAdmin(selectedGroup, groupUsers.get(i)))
                .collect(Collectors.joining("\n"));
        messageHandler.sendMessageWithKeyboardMarkup(chatId, message,
                KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
    }

    // <--------- Команда /members

    // ---------> Команда /creategroup
    protected void handleCreateGroupCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (groupRepository.findByOwnerId(user.getId()) == null) {
            messageHandler.sendMessage(chatId, GROUP_CREATE);
            user.setStatus(statusRepository.findById(2));
            userRepository.save(user);
        }
        else {
            messageHandler.sendMessage(chatId, TEXT_GROUP_EXISTS(groupRepository.findByOwnerId(user.getId())));
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

        user.setStatus(statusRepository.findById(4));
        userRepository.save(user);

        List<Groupe> groups = user.getGroupUsers().stream().map(GroupUser::getGroup).toList();
        InlineKeyboardMarkup inlineKeyboardMarkup = KeyboardMarkupProvider.addSelectGroupAnswers(groups);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_SELECT, inlineKeyboardMarkup);
    }

    protected void handleGroupSelectInput(long chatId, String stringGroupId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = groupRepository.findById(Long.parseLong(stringGroupId)).get();
        user.setSelectedGroup(selectedGroup);
        user.setStatus(statusRepository.findById(1));
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
                groupUser.setSelectedGroup(null);
                userRepository.save(groupUser);
            }
        }
        String groupName = selectedGroup.getName();
        try {
            FileUtils.deleteDirectory(new File(String.format("src/main/resources/groups/%d",
                    selectedGroup.getId())));
        }
        catch (IOException _) {}
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
