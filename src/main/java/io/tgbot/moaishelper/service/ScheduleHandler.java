package io.tgbot.moaishelper.service;

import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static io.tgbot.moaishelper.text.Info.*;

/**
 * @Authors: Markelloww & YDK
 */
@Component
public class ScheduleHandler {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final MessageHandler messageHandler;
    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public ScheduleHandler(UserRepository userRepository, GroupRepository groupRepository, MessageHandler messageHandler, UserSettingsRepository userSettingsRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.messageHandler = messageHandler;
        this.userSettingsRepository = userSettingsRepository;
    }

    protected void handleScheduleSetCommand(final long chatId, final boolean isManual) {
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

        if (isManual) {
            GroupUser groupUser =
                    selectedGroup.getGroupUsers().stream().filter(u -> u.getUser().getChatId() == chatId).findFirst().get();
            AdminScheduleSettings settings = groupUser.getSettings();
            messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_MANUAL_WARNING(settings.getWeekNum()),
                    KeyboardMarkupProvider.chooseDayOfWeek());
        }
        else {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, SCHEDULE_EXCEL_WARNING,
                    KeyboardMarkupProvider.excelScheduleSettings());
        }
    }
}
