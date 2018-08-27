package com.robertgray.api.controllers;

import com.robertgray.api.models.ChatMessage;
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

import java.util.List;

@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8090" }, maxAge = 6000)
@Service
@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    ChatStorageController chatStorageController;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8090" }, maxAge = 6000)
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        logger.info("Persisting chat message: " + chatMessage.getSender() + " - " + chatMessage.getContent() + "\n");
        chatStorageController.insertChatMessage(chatMessage);
        return chatMessage;
    }

    @CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8090" }, maxAge = 6000)
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

        //Resend old messages TODO: Move to seperate queue for new connections
        //List<ChatMessage> oldMessages = chatStorageController.getAllChatMessages();
        //for (ChatMessage message:oldMessages) {
        //    messagingTemplate.convertAndSend("/topic/public", message);
        //}
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



}
