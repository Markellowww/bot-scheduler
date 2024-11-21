package io.tgbot.moaishelper.service;

import io.tgbot.moaishelper.config.BotConfig;
import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.model.*;
import io.tgbot.moaishelper.schedule.Day;
import io.tgbot.moaishelper.schedule.ScheduleReader;
import io.tgbot.moaishelper.text.Keyboard;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.tgbot.moaishelper.text.Info.*;

/**
 * @Authors: Markelloww & YDK
 */

@Component
@EnableTransactionManagement
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final UserSettingsRepository settingsRepository;
    private final GroupRepository groupRepository;
    private final StatusRepository statusRepository;
    private final GroupHandler groupHandler;
    private final MessageHandler messageHandler;
    private final SettingsHandler settingsHandler;
    private final BotConfig config;
    private final ScheduleHandler scheduleHandler;

    public TelegramBot(BotConfig config,
                       @Lazy GroupHandler groupHandler,
                       @Lazy MessageHandler messageHandler,
                       @Lazy SettingsHandler settingsHandler,
                       @Lazy ScheduleHandler scheduleHandler,
                       StatusRepository statusRepository,
                       GroupRepository groupRepository,
                       UserRepository userRepository,
                       UserSettingsRepository settingsRepository) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        CommandInitializer.init(listOfCommands);
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException _) {}

        this.groupHandler = groupHandler;
        this.messageHandler = messageHandler;
        this.settingsHandler = settingsHandler;
        this.statusRepository = statusRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
        this.scheduleHandler = scheduleHandler;
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

            if (messageText.equals("/start") || user == null) {
                registerUser(update.getMessage());
                messageHandler.startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                return;
            }
            else if (messageText.equals("/all")) {
                if (chatId == 989138081L || chatId == 5254745148L) {
                    user.setStatus(statusRepository.findById(11));
                    userRepository.save(user);
                    messageHandler.sendMessage(chatId, ENTER_MESSAGE_FOR_ALL);
                    return;
                }
            }

            if (user.getStatus().getId() != 1) {
                switch (user.getStatus().getId()) {
                    case 2: {
                        groupHandler.handleGroupNameInput(chatId, messageText);
                        break;
                    }
                    case 3: {
                        messageText = messageHandler.checkForAtInMessage(messageText);
                        groupHandler.handleInviteUserInput(chatId, messageText);
                        break;
                    }
                    case 9: {
                        groupHandler.handleMessageInput(chatId, messageText);
                        break;
                    }
                    case 11: {
                        if (!messageText.equalsIgnoreCase("ОТМЕНА"))
                            messageHandler.sendMessageForAll(userRepository.findAll(), messageText);
                        user.setStatus(statusRepository.findById(1));
                        userRepository.save(user);
                        break;
                    }
                    default: {
                        user.setStatus(statusRepository.findById(1));
                        userRepository.save(user);
                        messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_ERROR,
                                KeyboardMarkupProvider.inlineGoBackButton());
                    }
                }
            } 
            else {
                if (messageText.equals(Keyboard.NOTIFICATION_SETTING)) {
                    settingsHandler.showNotificationMenu(chatId, settingsRepository.findById(user.getId()));
                }
                else if (messageText.equals(Keyboard.GO_TO_GROUPS)) {
                    boolean chosen = groupHandler.groupChosen(user), created = groupHandler.groupCreated(user);
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_MENU(user),
                            KeyboardMarkupProvider.groupsMenu(chosen, created));
                }
                else if (messageText.equals(Keyboard.CONTACTS)) {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, TEXT_SUPPORT,
                            KeyboardMarkupProvider.inlineGoBackButton());
                }
                else if (messageText.equals(Keyboard.GUIDE)) {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, TEXT_IN_DEVELOP,
                            KeyboardMarkupProvider.inlineGoBackButton());
                }
                else if (messageText.equals(Keyboard.BACK_TO_GROUPS)) {
                    boolean chosen = groupHandler.groupChosen(user), created = groupHandler.groupCreated(user);
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_MENU(user),
                            KeyboardMarkupProvider.groupsMenu(chosen, created));
                }
                else if (messageText.equals(Keyboard.LEAVE_GROUP)) {
                    groupHandler.leaveGroup(chatId, userRepository.findByChatId(chatId).getSelectedGroup());
                }
                else if (messageText.equals(Keyboard.GROUP_HANDLER) ||
                        messageText.equals(Keyboard.BACK_TO_GROUP_SETTINGS)) {
                    boolean isOwner = user.getId() == user.getSelectedGroup().getOwner().getId();
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Управление группой",
                                KeyboardMarkupProvider.groupSettingsMenu(isOwner));
                }
                else if (messageText.equals(Keyboard.BACK_TO_MENU_GROUPS)) {
                    if (user.getSelectedGroup() == null) {
                        messageHandler.sendMessageWithKeyboardMarkup(chatId, ERROR,
                                KeyboardMarkupProvider.inlineContinueButtonToMainMenu());
                        return;
                    }
                    boolean isAdmin = user.getSelectedGroup().getAdmins().stream().anyMatch(u -> u.getId() ==
                            user.getId());
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Меню группы",
                            KeyboardMarkupProvider.showGroupsSettingsMenu(isAdmin));
                }
                else if (messageText.equals(Keyboard.SHOW_MEMBERS)) {
                    groupHandler.showMembers(chatId);
                }
                else if (messageText.equals(Keyboard.DELETE_GROUP)) {
                    groupHandler.deleteGroup(chatId, userRepository.findByChatId(chatId).getSelectedGroup());
                }
                else if (messageText.equals(Keyboard.GIVE_OWNER)) {
                    groupHandler.handleOwnerCommand(chatId);
                }
                else if (messageText.equals(Keyboard.REMOVE_ADMIN)) {
                    groupHandler.handleRemoveAdminCommand(chatId);
                }
                else if (messageText.equals(Keyboard.KICK_USER)) {
                    groupHandler.handleKickCommand(chatId);
                }
                else if (messageText.equals(Keyboard.SET_ADMIN)) {
                    groupHandler.handleSetAdminCommand(chatId);
                }
                else if (messageText.equals(Keyboard.INVITE_USER)) {
                    groupHandler.handleInviteCommand(chatId);
                }
                else if (messageText.equals(Keyboard.SET_SCHEDULE)) {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_OPTION,
                            KeyboardMarkupProvider.setSchedule());
                }
                else if (messageText.equals(Keyboard.SHOW_SCHEDULE)) {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, CHOOSE_SCHEDULE,
                            KeyboardMarkupProvider.scheduleMenu());
                }
                else if (messageText.equals(Keyboard.NOTIFICATION_FOR_ALL)) {
                    groupHandler.handleMessageCommand(chatId);
                }
                else if (messageText.equals(Keyboard.MANUAL_MODIFICATION)) {
                    scheduleHandler.handleScheduleSetCommand(chatId, true);
                }
                else if (messageText.equals(Keyboard.EXCEL_SPREADSHEET)) {
                    scheduleHandler.handleScheduleSetCommand(chatId, false);
                }
                else {
                    user.setStatus(statusRepository.findById(1));
                    userRepository.save(user);
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, USER_ERROR,
                            KeyboardMarkupProvider.inlineGoBackButton());
                }
            }
        }
        else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            User user = userRepository.findByChatId(chatId);
            messageHandler.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());

            ZoneId zone = ZoneId.of(settingsRepository.findById(user.getId()).getTimeZoneId());
            String callbackData = update.getCallbackQuery().getData();

            if (EnumUtils.isValidEnum(Day.class, callbackData)) {
                GroupUser groupUser = user.getSelectedGroup().getGroupUsers()
                        .stream().filter(u -> u.getUser().getChatId() == chatId).findFirst().get();
                AdminScheduleSettings settings = groupUser.getSettings();

                settings.setWeekDay((short) Day.valueOf(callbackData).getOrder());
                groupRepository.save(user.getSelectedGroup());

                messageHandler.sendMessageWithKeyboardMarkup(chatId, "Выберите действие:",
                        KeyboardMarkupProvider.changeScheduleOption());
                return;
            }

            switch (callbackData) {
                case "MANUAL_SCHEDULE_MODIFICATION": {
                    scheduleHandler.handleScheduleSetCommand(chatId, true);
                    return;
                }
                case "CHANGE_WEEK_NUM": {
                    GroupUser groupUser = user.getSelectedGroup().getGroupUsers()
                            .stream().filter(u -> u.getUser().getChatId() == chatId).findFirst().get();
                    AdminScheduleSettings settings = groupUser.getSettings();
                    settings.setWeekNum(!settings.getWeekNum());
                    groupRepository.save(user.getSelectedGroup());
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_MANUAL_WARNING(settings.getWeekNum()),
                            KeyboardMarkupProvider.chooseDayOfWeek());
                    return;
                }
                case "DENY": {
                    user.setStatus(statusRepository.findById(1));
                    userRepository.save(user);
                    messageHandler.sendMessage(chatId, INPUT_DENIED);
                    return;
                }
                case "BACK_TO_SCHEDULE_MENU": {
                    user.setStatus(statusRepository.findById(1));
                    userRepository.save(user);
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_EXCEL_WARNING,
                            KeyboardMarkupProvider.excelScheduleSettings());
                    return;
                }
                case "BACK_TO_SCHEDULE_SETTINGS": {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_OPTION,
                            KeyboardMarkupProvider.setSchedule());
                    return;
                }
                case "TAKE_TEMPLATE": {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_EXCEL_WARNING,
                            KeyboardMarkupProvider.excelScheduleSettings());
                    messageHandler.uploadScheduleTemplate(chatId);
                    return;
                }
                case "SEND_TEMPLATE": {
                    user.setStatus(statusRepository.findById(10));
                    userRepository.save(user);
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, WAITING_FOR_EXCEL_FILE,
                            KeyboardMarkupProvider.inlineGoBackButtonToScheduleMenu());
                    return;
                }
                case "BACK_TO_MAIN_MENU": {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Назад",
                            KeyboardMarkupProvider.startMenu());
                    return;
                }
                case "BACK_TO_MAIN_GROUPS_MENU": {
                    boolean chosen = groupHandler.groupChosen(user), created = groupHandler.groupCreated(user);
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, GROUP_MENU(user),
                            KeyboardMarkupProvider.groupsMenu(chosen, created));
                    return;
                }
                case "BACK_TO_GROUP_MENU": {
                    boolean isAdmin = user.getSelectedGroup().getAdmins().stream().anyMatch(u -> u.getId() ==
                            user.getId());
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Вы вернулись в меню группы",
                            KeyboardMarkupProvider.showGroupsSettingsMenu(isAdmin));
                    return;
                }
                case "BACK_TO_GROUP_SETTING_MENU": {
                    user.setStatus(statusRepository.findById(1));
                    userRepository.save(user);
                    boolean isOwner = user.getSelectedGroup().getOwner().getId() == user.getId();
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Вы вернулись в управление группой",
                            KeyboardMarkupProvider.groupSettingsMenu(isOwner));
                    return;
                }
                case "CREATE_GROUP": {
                    groupHandler.handleCreateGroupCommand(chatId);
                    return;
                }
                case "SELECT_GROUP": {
                    groupHandler.handleGroupSelectCommand(chatId);
                    return;
                }
                case "GROUP_MENU": {
                    Groupe selectedGroup = user.getSelectedGroup();
                    boolean isAdmin = selectedGroup.getAdmins().stream().anyMatch(u -> u.getId() == user.getId());
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, "Меню группы",
                            KeyboardMarkupProvider.showGroupsSettingsMenu(isAdmin));
                    return;
                }
                case "TODAY": {
                    long groupId = user.getSelectedGroup().getId();
                    boolean isAdmin = user.getSelectedGroup().getAdmins().stream().anyMatch(u -> u.getId() ==
                            user.getId());
                    messageHandler.sendMessageWithKeyboardMarkupAndParseMode(chatId,
                            "<b>Расписание на сегодня:</b>\n\n".concat(ScheduleReader.todaySchedule(groupId, zone)),
                            KeyboardMarkupProvider.showGroupsSettingsMenu(isAdmin));
                    return;
                }
                case "TOMORROW": {
                    long groupId = user.getSelectedGroup().getId();
                    boolean isAdmin = user.getSelectedGroup().getAdmins().stream().anyMatch(u -> u.getId() ==
                            user.getId());
                    messageHandler.sendMessageWithKeyboardMarkupAndParseMode(chatId,
                            "<b>Расписание на завтра:</b>\n\n".concat(ScheduleReader.tomorrowSchedule(groupId, zone)),
                            KeyboardMarkupProvider.showGroupsSettingsMenu(isAdmin));
                    return;
                }
                case "THIS_WEEK": {
                    long groupId = user.getSelectedGroup().getId();
                    boolean isAdmin = user.getSelectedGroup().getAdmins().stream().anyMatch(u -> u.getId() ==
                            user.getId());
                    messageHandler.sendMessageWithKeyboardMarkupAndParseMode(chatId,
                            "<b>Расписание на текущую неделю:</b>\n\n".
                            concat(ScheduleReader.thisWeekSchedule(groupId, zone)),
                            KeyboardMarkupProvider.showGroupsSettingsMenu(isAdmin));
                    return;
                }
                case "NEXT_WEEK": {
                    long groupId = user.getSelectedGroup().getId();
                    boolean isAdmin = user.getSelectedGroup().getAdmins().stream().anyMatch(u -> u.getId() ==
                            user.getId());
                    messageHandler.sendMessageWithKeyboardMarkupAndParseMode(chatId,
                            "<b>Расписание на следующую неделю:</b>\n\n".
                                    concat(ScheduleReader.nextWeekSchedule(groupId, zone)),
                            KeyboardMarkupProvider.showGroupsSettingsMenu(isAdmin));
                    return;
                }
                case "CHANGE_NOTIFICATIONS": {
                    settingsHandler.switchNotifications(user);
                    settingsHandler.showNotificationMenu(chatId, settingsRepository.findById(user.getId()));
                    return;
                }
                case "TIMEZONE_SETTINGS": {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, TIME_SELECTION,
                            KeyboardMarkupProvider.timezonesMenu());
                    return;
                }
                case "NOTIFICATIONS_TIME": {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId,
                            CHOOSE_NOTIFICATION_SEND_TIME, KeyboardMarkupProvider.timeMenu());
                    return;
                }
                case "HOUR_SETTINGS": {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, TIME_SELECTION,
                            KeyboardMarkupProvider.hoursMenu());
                    return;
                }
                case "MINUTE_SETTINGS": {
                    messageHandler.sendMessageWithKeyboardMarkup(chatId, TIME_SELECTION,
                            KeyboardMarkupProvider.minutesMenu());
                    return;
                }
                case "BACK_TO_SETTINGS": {
                    settingsHandler.showNotificationMenu(chatId, settingsRepository.findById(user.getId()));
                    return;
                }
            }

            List<Long> data = Arrays.stream(callbackData.split(" ")).mapToLong(Long::parseLong)
                    .boxed().toList(); // данные вида "код <аргументы через пробел>"
            switch (data.get(0).intValue()) {
                case 1: { // принятие приглашения в группу
                    Groupe group = groupRepository.findById(data.get(1)).get();
                    groupHandler.addUserToGroup(user, group, data.get(2));
                    break;
                }
                case 2: { // отклонение приглашения в группу
                    Groupe group = groupRepository.findById(data.get(1)).get();
                    groupHandler.declineInvite(user, group, data.get(2));
                    if (group.getMembers().stream().noneMatch(u -> u.getId() == user.getId()))
                        messageHandler.sendMessage(chatId, INVITE_REQUEST_DENIED(group));
                    break;
                }
                case 3: { // изменение часовой зоны
                    int hourDifferenceWithMoscow = data.get(1).intValue();
                    settingsHandler.handleTimeZoneInput(user, hourDifferenceWithMoscow);
                    settingsHandler.showNotificationMenu(chatId, settingsRepository.findById(user.getId()));
                    break;
                }
                case 4: { // часы уведомлений
                    short hours = data.get(1).shortValue();
                    settingsHandler.handleHoursInput(user, hours);
                    break;
                }
                case 5: { // минуты уведомлений
                    short minutes = data.get(1).shortValue();
                    settingsHandler.handleMinutesInput(user, minutes);
                    break;
                }
                // сделать так, чтобы кнопки имели коды команд
                case 6: { // выбор группы
                    groupHandler.handleGroupSelectInput(chatId, data.get(1));
                    break;
                }
                case 7: { // удаление пользователя из группы
                    groupHandler.handleKickUserInput(chatId, data.get(1));
                    break;
                }
                case 8: { // передача прав владельца
                    groupHandler.handleGiveOwnerInput(chatId, data.get(1));
                    break;
                }
                case 9: { // назначение админа
                    groupHandler.handleSetAdminInput(chatId, data.get(1));
                    break;
                }
                case 10: { // снятие админа
                    groupHandler.handleRemoveAdminInput(chatId, data.get(1));
                    break;
                }
                default: { // что-то невероятное
                    userError(chatId);
                    break;
                }
            }
        }
        else if (update.getMessage().getDocument() != null) {
            long chatId = update.getMessage().getChatId();
            if (userRepository.findByChatId(chatId).getStatus().getId() == 10) {
                Groupe group = userRepository.findByChatId(chatId).getSelectedGroup();
                groupHandler.handleScheduleFileInput(update.getMessage(), chatId, group);
            }
            else {
                userError(chatId);
            }
        }
    }

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
     * Сбрасывает статус пользователя и возвращает стартовую клавиатуру
     * если произошла ошибка.
     *
     * @param chatId идентификатор пользователя, у которого произошла ошибка
     */
    private void userError(long chatId) {
        User user = userRepository.findByChatId(chatId);
        messageHandler.sendMessageWithKeyboardMarkup(chatId, ERROR,
                KeyboardMarkupProvider.inlineGoBackButton());
        user.setStatus(statusRepository.findById(1));
        userRepository.save(user);
    }
}