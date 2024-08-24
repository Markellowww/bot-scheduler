package io.tgbot.moaishelper.service;

/**
 * @Authors: Markelloww & YDK
 */


import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public class CommandInitializer {
    public static void init(List<BotCommand> listOfCommands) {
        listOfCommands.add(new BotCommand("/start", "Начало работы"));
        listOfCommands.add(new BotCommand("/support", "Контактная информация"));
        listOfCommands.add(new BotCommand("/help", "Руководство по использованию бота"));
        listOfCommands.add(new BotCommand("/creategroup", "Создать группу"));
        listOfCommands.add(new BotCommand("/selectgroup", "Выбрать группу"));
        listOfCommands.add(new BotCommand("/leavegroup", "Покинуть группу"));
        listOfCommands.add(new BotCommand("/deletegroup", "Удалить группу"));
        listOfCommands.add(new BotCommand("/invite", "Добавить пользователя в вашу группу"));
        listOfCommands.add(new BotCommand("/kick", "Исключить пользователя из группы"));
        listOfCommands.add(new BotCommand("/members", "Показать всех пользователей группы"));
    }
}
