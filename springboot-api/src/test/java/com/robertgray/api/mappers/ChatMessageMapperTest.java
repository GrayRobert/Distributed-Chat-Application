package com.robertgray.api.mappers;

import com.robertgray.api.controllers.ChatStorageController;
import com.robertgray.api.models.ChatMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatMessageMapperTest {

    @Autowired
    ChatStorageController chatStorageController;

    ChatMessage testMessage1;
    ChatMessage testMessage2;
    ChatMessage testMessage3;

    //Setup some test messages
    @Before
    public void initialize() {
        testMessage1 = new ChatMessage(ChatMessage.MessageType.CHAT, "TEST MESSAGE 1",
                "Sender 1", "Recipient 1", new Date(), null);
        testMessage2 = new ChatMessage(ChatMessage.MessageType.CHAT, "TEST MESSAGE 2",
                "Sender 2", null, new Date(), null);
        testMessage3 = new ChatMessage(ChatMessage.MessageType.CHAT, null,
                "Sender 3", "Recipient 3", new Date(), null);
    }

    public void insertTestMessages() {
        chatStorageController.insertChatMessage(testMessage1);
        chatStorageController.insertChatMessage(testMessage2);
        chatStorageController.insertChatMessage(testMessage3);
    }

    @Test
    public void getAllMessages() {
        insertTestMessages();
        List<ChatMessage> messages = chatStorageController.getAllChatMessages();
        assertNotNull(messages);
        printAllMessages(messages);
    }

    public void printAllMessages(List<ChatMessage> messages) {
        System.out.println( "\n\n===================================\n" +
                "Here is a list of chat messages \n" +
                "===================================\n");
        for (ChatMessage message:messages) {
            System.out.println(message.getSender() + ": " + message.getContent() +  "| " + message.getSent().toString() + "\n");
        }
        System.out.println( "\n\n===================================\n");
    }




}
