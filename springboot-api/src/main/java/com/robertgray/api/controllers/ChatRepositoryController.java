package com.robertgray.api.controllers;

import com.robertgray.api.interfaces.IChatRepository;
import com.robertgray.api.mappers.ChatMessageMapper;
import com.robertgray.api.models.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigInteger;
import java.util.List;

@Controller
public class ChatRepositoryController implements IChatRepository {

    private static final Logger logger = LoggerFactory.getLogger(ChatRepositoryController.class);

    @Autowired
    private ChatMessageMapper chatMessageMapper;


    @Override
    public List<ChatMessage> getAllChatMessages() {
        return chatMessageMapper.findAllMessages();
    }

    @Override
    public ChatMessage getChatMessageById(BigInteger id) {
        return chatMessageMapper.findMessageById(id);
    }

    @Override
    public int insertChatMessage(ChatMessage message) {
        return chatMessageMapper.insertMessage(message);
    }

    @Override
    public int updateChatMessage(ChatMessage message) {
        return chatMessageMapper.updateMessage(message);
    }

    @Override
    public int deleteChatMessage(BigInteger id) {
        return chatMessageMapper.deleteMessageById(id);
    }

}
