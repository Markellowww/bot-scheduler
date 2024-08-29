package io.tgbot.moaishelper.service;

/**
 * @Authors: Markelloww & YDK
 */


import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public class CommandInitializer {
    public static void init(List<BotCommand> listOfCommands) {
        listOfCommands.add(new BotCommand("/start", "Начало работы"));
    }
}
