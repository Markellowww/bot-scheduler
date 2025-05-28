package io.tgbot.moaishelper.service;

import io.tgbot.moaishelper.keyboard.KeyboardMarkupProvider;
import io.tgbot.moaishelper.model.GroupFile;
import io.tgbot.moaishelper.model.User;
import io.tgbot.moaishelper.repository.GroupFileRepository;
import io.tgbot.moaishelper.repository.StatusRepository;
import io.tgbot.moaishelper.repository.UserRepository;
import io.tgbot.moaishelper.text.Info;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@org.springframework.transaction.annotation.Transactional
public class GroupFileHandler {
    private final TelegramBot bot;
    private final GroupFileRepository groupFileRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final MessageHandler messageHandler;

    public GroupFileHandler(TelegramBot bot, GroupFileRepository groupFileRepository, StatusRepository statusRepository,
                            UserRepository userRepository, MessageHandler messageHandler) {
        this.bot = bot;
        this.groupFileRepository = groupFileRepository;
        this.messageHandler = messageHandler;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void deleteGroupFile(User user, long fileId) {
        long chatId = user.getChatId();
        GroupFile groupFile = groupFileRepository.findById(fileId).orElse(null);

        if (groupFile == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, "Файл был удален",
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }

        File scheduleFile = getFile(groupFile);
        System.out.println(fileId);
        groupFileRepository.deleteById(fileId);

        scheduleFile.delete();

        messageHandler.sendMessageWithKeyboardMarkup(chatId, "Файл успешно удален",
                KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
    }

    public void handleGroupFileInput(User user, Message message) {
        Document document = message.getDocument();
        long chatId = user.getChatId();

        GroupFile groupFile = new GroupFile(user.getSelectedGroup(), user, document.getFileName(), 14);
        groupFile = groupFileRepository.save(groupFile);
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(document.getFileId());
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);
            String filename = groupFile.getId() + "." +
                    Arrays.stream(document.getFileName().split("\\."))
                            .skip(1)
                            .collect(Collectors.joining("."));

            bot.downloadFile(file, new
                    java.io.File(String.format("src/main/resources/groups/%d/%s",
                    user.getSelectedGroup().getId(), filename)));
            user.setStatus(statusRepository.findById(1));
            userRepository.save(user);

        } catch (TelegramApiException _) {
            messageHandler.sendMessage(chatId, Info.ERROR);
            return;
        }

        messageHandler.sendMessageWithKeyboardMarkup(chatId, "Файл успешно сохранен",
                KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
    }

    public void uploadGroupFile(User user, long fileId) {
        long chatId = user.getChatId();
        GroupFile groupFile = groupFileRepository.findById(fileId).orElse(null);

        if (groupFile == null) {
            messageHandler.sendMessageWithKeyboardMarkup(chatId, "Файл был удален",
                    KeyboardMarkupProvider.inlineContinueButtonToGroupMenu());
            return;
        }

        File requiredFile = getFile(groupFile);
        System.out.println(requiredFile.getAbsolutePath());
        SendDocument file = new SendDocument();
        file.setDocument(new InputFile(requiredFile, groupFile.getFileName()));
        file.setChatId(chatId);
        try {
            bot.execute(file);
        } catch (TelegramApiException _) {}
    }

    private File getFile(GroupFile groupFile) {
        String fileName = groupFile.getId() + "." + Arrays.stream(groupFile.getFileName().split("\\."))
                .skip(1)
                .collect(Collectors.joining("."));

        return new File(String.format("src/main/resources/groups/%d/%s", groupFile.getGroup().getId(),
                fileName));
    }
}