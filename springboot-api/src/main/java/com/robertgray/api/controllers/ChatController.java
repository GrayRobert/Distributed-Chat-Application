package com.robertgray.api.controllers;

import com.robertgray.api.models.ChatMessage;
import org.joda.time.DateTime;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8090" }, maxAge = 6000)
@Service
@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatStorageController chatStorageController;

    @Autowired
    private TimeSyncController timeSyncController;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    // Mutual exclusion lock
    ReentrantLock lock = new ReentrantLock();

    @CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8090" }, maxAge = 6000)
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {

        timeSyncController.checkAndSendTime(chatMessage);

        int success = 0;
        ChatMessage storedMessage;

        lock.lock(); //obtain the lock
        try {
            //Store the message
            logger.info("Persisting chat message: " + chatMessage.getSender() + " - " + chatMessage.getContent() + "\n");
            success = chatStorageController.insertChatMessage(chatMessage);
            storedMessage = chatStorageController.getChatMessageByHash(chatMessage.getHash());

            //Only broadcast the message if we successfully store it.
            if (success > 0) {
                return storedMessage; //return the stored message as want the ID and to ensure consistency
            } else {
                return getFailureMessage(chatMessage); //return failure message
            }
        } finally {
            lock.unlock(); //release the lock
        }



    }

    @CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8090" }, maxAge = 6000)
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

        return chatMessage;
    }

    @CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8090" }, maxAge = 6000)
    @MessageMapping("/chat.getMissedMessages")
    @SendTo("/topic/public")
    public ChatMessage getMissedMessages(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {

        String sender = chatMessage.getSender();

        if(sender !=null) {
            // Add username in web socket session
            headerAccessor.getSessionAttributes().put("username", sender);

            //Resend missed messages to sender
            List<ChatMessage> oldMessages = chatStorageController.getAllChatMessages();
            for (ChatMessage message:oldMessages) {
                message.setRecipient(chatMessage.getSender());
                messagingTemplate.convertAndSend("/topic/public", message);
            }
        }
        return chatMessage;
    }

    private ChatMessage getFailureMessage(ChatMessage originalMessage) {
        ChatMessage failMessage = new ChatMessage();
        failMessage.setSent(new Date());
        failMessage.setRecipient(originalMessage.getSender());
        failMessage.setType(ChatMessage.MessageType.CHAT);
        failMessage.setContent("Failed to broadcast message due to server error. Please try resend");
        return failMessage;
    }
}
