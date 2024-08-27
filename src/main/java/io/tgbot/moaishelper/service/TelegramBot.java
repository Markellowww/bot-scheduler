package io.tgbot.moaishelper.service;

import com.vdurmont.emoji.EmojiParser;
import io.tgbot.moaishelper.config.BotConfig;
import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.keyboard.KeyboardText;
import io.tgbot.moaishelper.model.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Authors: Markelloww & YDK
 */

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StatusRepository statusRepository;

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        CommandInitializer.init(listOfCommands);
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException _) {}
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            User user = userRepository.findByChatId(chatId);
            String messageText = update.getMessage().getText();

            if (user != null && user.getStatus().getId() != 1) { // ожидается ввод каких-то данных
                switch (user.getStatus().getId()) {
                    case 2: {
                        handleGroupNameInput(chatId, messageText);
                        break;
                    }
                    case 3: {
                        // Проверяем написал ли '@' пользователь, допускается написание с '@' и без
                        messageText = checkForAtInMessage(messageText);
                        handleInviteUserInput(chatId, messageText);
                        break;
                    }
                    case 5: {
                        messageText = checkForAtInMessage(messageText);
                        handleKickUserInput(chatId, messageText);
                        break;
                    }
                    case 6: {
                        messageText = checkForAtInMessage(messageText);
                        handleGiveOwnerInput(chatId, messageText);
                        break;
                    }
                    case 7: {
                        messageText = checkForAtInMessage(messageText);
                        handleSetAdminInput(chatId, messageText);
                        break;
                    }
                    case 8: {
                        messageText = checkForAtInMessage(messageText);
                        handleRemoveAdminInput(chatId, messageText);
                        break;
                    }
                    default:
                        sendMessage(chatId, Info.NOT_SELECTED_ARGUMENT);
                }
            }
            else {
                if (messageText.equals(KeyboardText.CONTACTS)) {
                    sendMessage(chatId, Info.TEXT_SUPPORT);
                }
                else if (messageText.equals(KeyboardText.NOTIFICATION_SETTING)) {
                    sendMessage(chatId, Info.TEXT_IN_DEVELOP);
                }
                else if (messageText.equals(KeyboardText.GUIDE)) {
                    sendMessage(chatId, Info.TEXT_IN_DEVELOP);
                }
                else if (messageText.equals(KeyboardText.GO_TO_GROUPS)) {
                    sendMessage(chatId, Info.TEXT_IN_DEVELOP);
                }
                else
                    switch (messageText) {
                    case "/start": {
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    }
                    case "/creategroup": {
                        handleCreateGroupCommand(chatId);
                        break;
                    }
                    case "/selectgroup": {
                        handleGroupSelectCommand(chatId);
                        break;
                    }
                    case "/leavegroup": {
                        leaveGroup(chatId, userRepository.findByChatId(chatId).getSelectedGroup());
                        break;
                    }
                    case "/deletegroup": {
                        deleteGroup(chatId, userRepository.findByChatId(chatId).getSelectedGroup());
                        break;
                    }
                    case "/invite": {
                        handleInviteCommand(chatId);
                        break;
                    }
                    case "/giveowner": {
                        handleOwnerCommand(chatId);
                        break;
                    }
                    case "/setadmin": {
                        handleSetAdminCommand(chatId);
                        break;
                    }
                    case "/removeadmin": {
                        handleRemoveAdminCommand(chatId);
                        break;
                    }
                    case "/kick": {
                        handleKickCommand(chatId);
                        break;
                    }
                    case "/members": {
                        showMembers(chatId);
                        break;
                    }
                    case "/help": {
                        sendMessage(chatId, Info.TEXT_IN_DEVELOP);
                        break;
                    }
                    default: {
                        sendMessage(chatId, EmojiParser.parseToUnicode("Я такое не знаю :disappointed_relieved:"));
                    }
                }
            }
        }
        else if (update.hasCallbackQuery()) {
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            User user = userRepository.findByChatId(chatId);

            String callbackData = update.getCallbackQuery().getData();

            switch (user.getStatus().getId()) {
                case 4: {
                    handleGroupSelectInput(chatId, callbackData);
                    deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
                    break;
                }
            }
        }
    }

    // ---------> Команда /setadmin
    public void handleSetAdminCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = user.getSelectedGroup();
        if (selectedGroup == null) {
            sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        if (selectedGroup.getOwner().getId() != user.getId()) {
            sendMessage(chatId, Info.NOT_OWNER(selectedGroup));
            return;
        }
        sendMessage(chatId, "Введите @UserName человека, которого вы хотите назначить админом группы:");
        user.setStatus(statusRepository.findById(7));
        userRepository.save(user);
    }

    private void handleSetAdminInput(long chatId, String newOwnerName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        if (userRepository.findByUserName(newOwnerName) == null) {
            sendMessage(chatId, Info.USER_NOT_EXISTS);
            return;
        }
        User newAdmin = userRepository.findByUserName(newOwnerName);
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == newAdmin.getId())) {
            sendMessage(chatId, "Пользователь не состоит в группе");
            return;
        }
        if (group.getAdmins().stream().anyMatch(admin -> admin.getId() == newAdmin.getId())) {
            sendMessage(chatId, "Пользователь уже является админом группы");
            return;
        }
        group.setAdmin(newAdmin);

        groupRepository.save(group);
        sendMessage(chatId, "Пользователь успешно назначен админом группы!");
    }
    // <--------- Команда /setadmin

    // ---------> Команда /removeadmin
    public void handleRemoveAdminCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = user.getSelectedGroup();
        if (selectedGroup == null) {
            sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        if (selectedGroup.getOwner().getId() != user.getId()) {
            sendMessage(chatId, Info.NOT_OWNER(selectedGroup));
            return;
        }
        sendMessage(chatId, "Введите @UserName удаляемого админа:");
        user.setStatus(statusRepository.findById(8));
        userRepository.save(user);
    }

    private void handleRemoveAdminInput(long chatId, String newOwnerName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        if (userRepository.findByUserName(newOwnerName) == null) {
            sendMessage(chatId, Info.USER_NOT_EXISTS);
            return;
        }
        User removedAdmin = userRepository.findByUserName(newOwnerName);
        Groupe group = user.getSelectedGroup();
        if (group.getMembers().stream().noneMatch(u -> u.getId() == removedAdmin.getId())) {
            sendMessage(chatId, "Пользователь не состоит в группе");
            return;
        }
        if (group.getAdmins().stream().noneMatch(admin -> admin.getId() == removedAdmin.getId())) {
            sendMessage(chatId, "Пользователь не является админом группы");
            return;
        }
        else if (group.getOwner().getId() == removedAdmin.getId()) {
            sendMessage(chatId, "Владелец группы всегда является админом");
            return;
        }
        group.removeAdmin(removedAdmin);

        groupRepository.save(group);
        sendMessage(chatId, "Пользователь больше не админ группы!");
    }
    // <--------- Команда /removeadmin

    // ---------> Команда /giveowner (ОТРЕФАКТОРИТЬ)
    public void handleOwnerCommand(long chatId) {
        User owner = userRepository.findByChatId(chatId);
        Groupe selectedGroup = owner.getSelectedGroup();
        // Проверяем выбрана или группа
        if (selectedGroup == null) {
            sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        // Проверяем владелец ли он группы
        if (selectedGroup.getOwner().getId() != owner.getId()) {
            sendMessage(chatId, Info.NOT_OWNER(selectedGroup));
            return;
        }
        sendMessage(chatId, "Введите @UserName человека, которого вы хотите назначить владельцем группы:");
        owner.setStatus(statusRepository.findById(6));
        userRepository.save(owner);
    }

    private void handleGiveOwnerInput(long chatId, String newOwnerName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        // Проверяем есть ли такой человек в боте
        if (userRepository.findByUserName(newOwnerName) != null) {
            User newOwner = userRepository.findByUserName(newOwnerName);
            Groupe group = user.getSelectedGroup();
            //Проверяем состоит ли указанный пользователь в группе
            if (group.getMembers().stream().noneMatch(u -> u.getId() == newOwner.getId())) {
                sendMessage(chatId, "Пользователь не состоит в группе");
                return;
            }
            // Проверяем является ли он владельцев уже каких-либо других групп
            if (newOwner.getGroupUsers().stream().anyMatch(u -> u.getGroup().getOwner().getId() == newOwner.getId())) {
                sendMessage(chatId, "Пользователь уже является владельцем группы, укажите другого");
                return;
            }
            group.setOwner(newOwner);

            groupRepository.save(group);
            sendMessage(chatId, "Пользователь успешно назначен владельцем группы!");
        }
        else
            sendMessage(chatId, Info.USER_NOT_EXISTS);
    }
    // <--------- Команда /giveowner (ОТРЕФАКТОРИТЬ)

    // ---------> Команда /kick (ОТРЕФАКТОРИТЬ)
    private void handleKickCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user.getSelectedGroup() != null) {
            Groupe selectedGroup = user.getSelectedGroup();
            if (selectedGroup.getAdmins().stream().anyMatch(admin -> admin.getId() == user.getId())) {
                sendMessage(chatId, "Введите @UserName исключаемого из группы человека");
                user.setStatus(statusRepository.findById(5));
                userRepository.save(user);
            }
            else
                sendMessage(chatId, Info.NOT_ADMIN(selectedGroup));
        }
        else
            sendMessage(chatId, Info.GROUP_NOT_SELECTED);
    }

    private void handleKickUserInput(long chatId, String removedUserName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        if (userRepository.findByUserName(removedUserName) != null) {
            User removedUser = userRepository.findByUserName(removedUserName);
            Groupe group = user.getSelectedGroup();
            if (group.getMembers().stream().noneMatch(u -> u.getId() == removedUser.getId())) {
                sendMessage(chatId, "Пользователь не состоит в группе");
                return;
            }
            if (group.getOwner().getId() == removedUser.getId()) {
                sendMessage(chatId, "Вы не можете исключить владельца");
                return;
            }
            if (group.getAdmins().stream().anyMatch(a -> a.getId() == removedUser.getId())) {
                sendMessage(chatId, "Вы не можете исключить админа");
                return;
            }
            group.removeMember(removedUser);
            groupRepository.save(group);
            sendMessage(chatId, "Пользователь успешно исключен из группы!");
        }
        else
            sendMessage(chatId, Info.USER_NOT_EXISTS);
    }
    // <--------- Команда /kick (ОТРЕФАКТОРИТЬ)

    // ---------> Команда /invite (ОТРЕФАКТОРИТЬ)
    private void handleInviteCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user.getSelectedGroup() != null) {
            Groupe selectedGroup = user.getSelectedGroup();
            if (selectedGroup.getAdmins().stream().anyMatch(admin -> admin.getId() == user.getId())) {
                sendMessage(chatId, "Введите @UserName приглашаемого в группу человека");
                user.setStatus(statusRepository.findById(3));
                userRepository.save(user);
            }
            else
                sendMessage(chatId, Info.NOT_ADMIN(selectedGroup));
        }
        else
            sendMessage(chatId, Info.GROUP_NOT_SELECTED);
    }

    private void handleInviteUserInput(long chatId, String invitedUserName) {
        User user = userRepository.findByChatId(chatId);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);

        if (userRepository.findByUserName(invitedUserName) != null) {
            User invitedUser = userRepository.findByUserName(invitedUserName);
            Groupe group = user.getSelectedGroup();
            if (group.getMembers().stream().anyMatch(u -> u.getId() == invitedUser.getId())) {
                sendMessage(chatId, "Пользователь уже состоит в группе");
                return;
            }
            group.addMember(invitedUser, false);
            groupRepository.save(group);
            sendMessage(chatId, "Пользователь успешно добавлен в группу!");
        }
        else
            sendMessage(chatId, Info.USER_NOT_EXISTS);
    }
    // <--------- Команда /invite (ОТРЕФАКТОРИТЬ)

    // ---------> Команда /creategroup (ОТРЕФАКТОРИТЬ)
    private void handleCreateGroupCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (groupRepository.findByOwnerId(user.getId()) == null) {
            sendMessage(chatId, "Введите название для группы");
            user.setStatus(statusRepository.findById(2));
            userRepository.save(user);
        }
        else {
            sendMessage(chatId, Info.TEXT_GROUP_EXISTS(groupRepository.findByOwnerId(user.getId())));
        }
    }

    private void handleGroupNameInput(long chatId, String groupName) {
        User creator = userRepository.findByChatId(chatId);
        Groupe groupe = new Groupe(creator, groupName, new Timestamp(System.currentTimeMillis()));
        System.out.println(groupe.getId());
        groupRepository.save(groupe);

        new File(String.format("src/main/resources/groups/%d",
                groupRepository.findByOwnerId(creator.getId()).getId())).mkdirs();

        new File(String.format("src/main/resources/groups/%d/homeworks",
                groupRepository.findByOwnerId(creator.getId()).getId())).mkdirs();

        new File(String.format("src/main/resources/groups/%d/lectures",
                groupRepository.findByOwnerId(creator.getId()).getId())).mkdirs();

        creator.setSelectedGroup(groupe);
        creator.setStatus(statusRepository.findById(1));
        userRepository.save(creator);
        sendMessage(chatId, "Вы успешно создали группу с названием: " + groupe.getName());
    }
    // <--------- Команда /creategroup (ОТРЕФАКТОРИТЬ)

    // ---------> Команда /selectgroup (ОТРЕФАКТОРИТЬ)
    private void handleGroupSelectCommand(long chatId) {
        User user = userRepository.findByChatId(chatId);

        if (!user.getGroupUsers().isEmpty()) {
            user.setStatus(statusRepository.findById(4));
            userRepository.save(user);
            addSelectGroupAnswers(chatId, user.getGroupUsers().stream().map(GroupUser::getGroup).toList());
        }
        else
            sendMessage(chatId, Info.NO_GROUPS);
    }

    private void handleGroupSelectInput(long chatId, String stringGroupId) {
        User user = userRepository.findByChatId(chatId);
        Groupe selectedGroup = groupRepository.findById(Long.parseLong(stringGroupId)).get();
        user.setSelectedGroup(selectedGroup);
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);
        sendMessage(chatId, String.format("Вы выбрали группу \"%s\"", selectedGroup.getName()));
    }

    /**
     * Присылает пользователю кнопки с вариантами групп для выбора (нажатие кнопки возвращает id группы)
     * @param chatId идентификатор чата
     * @param groups список групп для добавления
     */
    private void addSelectGroupAnswers(long chatId, List<Groupe> groups) {
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

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите группу");
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException _) {}
    }
    // <--------- Команда /selectgroup (ОТРЕФАКТОРИТЬ)

    // ---------> Команда /members
    /**
     * Отправляет пользователю список участников выбранной группы с указанием их статуса (владелец или админ).
     *
     * @param chatId идентификатор чата пользователя, для которого отображается список участников
     */
    private void showMembers(long chatId) {
        User user = userRepository.findByChatId(chatId);
        if (user.getSelectedGroup() == null) {
            sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        Groupe selectedGroup = user.getSelectedGroup();
        List<User> groupUsers = selectedGroup.getMembers();
        String message = IntStream.range(0, groupUsers.size())
                    .mapToObj(i -> (i + 1) + ". @" + groupUsers.get(i).getUserName() + isAdmin(selectedGroup, groupUsers.get(i)))
                    .collect(Collectors.joining("\n"));
        sendMessage(chatId, message);
    }

    // <--------- Команда /members

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
    private void leaveGroup(long chatId, Groupe selectedGroup) {
        if (selectedGroup == null) {
            sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        User user = userRepository.findByChatId(chatId);
        if (user.getId() == selectedGroup.getOwner().getId()){
            if (selectedGroup.getMembers().size() == 1) {
                deleteGroup(chatId, selectedGroup);
                sendMessage(chatId, Info.GROUP_EXIT_SUCCESSFUL(selectedGroup));
                return;
            }
            else if ((selectedGroup.getMembers().size() > 1)) {
                sendMessage(chatId, Info.GROUP_EXIT_FAILED);
                return;
            }
        }
        user.setSelectedGroup(null);
        selectedGroup.removeMember(user);
        userRepository.save(user);
        groupRepository.save(selectedGroup);
        sendMessage(chatId, "Вы успешно вышли из группы!");
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
    private void deleteGroup(long chatId, Groupe selectedGroup) {
        if (selectedGroup == null) {
            sendMessage(chatId, Info.GROUP_NOT_SELECTED);
            return;
        }
        User user = userRepository.findByChatId(chatId);
        if (user.getId() != selectedGroup.getOwner().getId()) {
            sendMessage(chatId, Info.NOT_OWNER(selectedGroup));
            return;
        }
        for (User groupUser : selectedGroup.getMembers()) {
            if (user.getSelectedGroup().getId() == selectedGroup.getId()) {
                groupUser.setSelectedGroup(null);
                userRepository.save(groupUser);
            }
        }
        sendMessage(chatId, String.format("Группа \"%s\" успешно удалена", selectedGroup.getName()));
        groupRepository.deleteById(selectedGroup.getId());
        try {
            FileUtils.deleteDirectory(new File(String.format("src/main/resources/groups/%d",
                    selectedGroup.getId())));
        }
        catch (IOException _) {}
    }
    // <--------- Команда /deletegroup

    /**
     * Регистрация или обновление информации о пользователе в базе данных.
     * Если пользователь зарегистрирован в боте, то он обновляется в базе данных.
     * Если пользователь не зарегистрирован в боте, то он добавляется в базу данных.
     *
     * @param msg объект сообщения, содержащий информацию о пользователе и чате
     */
    private void registerUser(Message msg) {
        var chatId = msg.getChatId();
        var chat = msg.getChat();
        if (userRepository.findByChatId(chatId) == null) {
            User user = new User(chatId, chat.getFirstName(), chat.getUserName(),
                    new Timestamp(System.currentTimeMillis()));
            user.setStatus(statusRepository.findById(1));
            userRepository.save(user);
            return;
        }
        User user = userRepository.findByChatId(chatId);
        user.setUserName(chat.getUserName());
        user.setFirstName(chat.getFirstName());
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);
    }

    /**
     * Отравляет начальное сообщение со стартовой клавиатурой.
     *
     * @param chatId идентификатор чата
     * @param firstName имя человека
     */
    private void startCommandReceived(long chatId, String firstName) {
        String answer = EmojiParser.parseToUnicode("Привет:v:, " + firstName + ", это МОАИС-Helper!\n" +
                "Для начала работы ознакомьтесь с руководством:closed_book:");
        sendMessageWithKeyboardMarkup(chatId, answer, KeyboardMarkupProvider.start());
    }

    /**
     * Отправляет текстовое сообщение в указанный чат с клавиатурой.
     *
     * @param chatId идентификатор чата, в который будет отправлено сообщение
     * @param textToSend текст сообщения, которое нужно отправить
     * @param keyboardMarkup клавиатура, прикрепляемая к сообщению
     */
    private void sendMessageWithKeyboardMarkup(long chatId, String textToSend, ReplyKeyboard keyboardMarkup) {
        SendMessage message = createSendMassage(chatId, textToSend);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException _) {}
    }

    /**
     * Создает объект SendMessage с указанными параметрами для отправки сообщения.
     *
     * @param chatId идентификатор чата, в который будет отправлено сообщение
     * @param textToSend текст сообщения, которое нужно отправить
     * @return объект SendMessage, настроенный с указанными параметрами
     */
    private SendMessage createSendMassage(long chatId, String textToSend) {
        SendMessage message =  new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        return message;
    }

    /**
     * Отправляет текстовое сообщение в указанный чат без клавиатуры.
     *
     * @param chatId идентификатор чата, в который будет отправлено сообщение
     * @param textToSend текст сообщения, которое нужно отправить
     */
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = createSendMassage(chatId,textToSend);
        try {
            execute(message);
        } catch (TelegramApiException _) {}
    }

    /**
     * Удаляет сообщение в чате.
     *
     * @param chatId идентификатор чата, где находится сообщение
     * @param messageId идентификатор удаляемого сообщения
     */
    private void deleteMessage(long chatId, Integer messageId) {
        try {
            execute(new DeleteMessage(String.valueOf(chatId), messageId));
        } catch (TelegramApiException _) {}
    }

    /**
     * Проверяет строку на наличие у нее первого символа равного '@'.
     *
     * @param message исходное сообщение, которое необходимо проверить
     * @return строку без начального '@', если он присутствует, иначе исходную строку
     */
    private String checkForAtInMessage(String message) {
        if (message.charAt(0) == '@') {
            return message.substring(1);
        }
        return message;
    }

    /**
     * Определяет, является ли пользователь владельцем или администратором выбранной группы.
     *
     * @param selectedGroup группа, в которой проверяется статус пользователя
     * @param user пользователь, чей статус проверяется
     * @return строка с указанием статуса пользователя в группе Владелец/Админ
     */
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
