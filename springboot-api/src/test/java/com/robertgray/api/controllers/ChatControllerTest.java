package com.robertgray.api.controllers;

import com.robertgray.api.models.ChatMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Basically this test starts up 2 threads and sends 10 messages concurrently in each thread
//The result is 20 messages in under 1 second which means they all have the same time stamp to the second.

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ChatControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ChatControllerTest.class);

    @Autowired
    private ChatController chatController;

    List<ChatMessage> messages = new ArrayList<ChatMessage>();

    @Before
    public void initialize() {
        for (int i = 1; i <= 10; i++) {
            ChatMessage chatMessage = new ChatMessage(ChatMessage.MessageType.CHAT, "TEST MESSAGE " + i,
                    "Sender " + i, null, new Date(), null, null, null);
            messages.add(chatMessage);
        }
    }


    @Test
    public void sendMessages() {

        Runnable myRunnable1 = new Runnable() {

            public void run(){
                for (ChatMessage message : messages) {
                    chatController.sendMessage(message);
                    logger.info("Sender: " + message.getSender() + " Message: " + message.getContent() + " Sent: " + message.getSent());
                }
            }
        };
        Runnable myRunnable2 = new Runnable() {

            public void run(){
                for (ChatMessage message : messages) {
                    chatController.sendMessage(message);
                    logger.info("Sender: " + message.getSender() + " Message: " + message.getContent() + " Sent: " + message.getSent());
                }
            }
        };

        Thread thread1 = new Thread(myRunnable1);
        Thread thread2 = new Thread(myRunnable2);


        thread1.start();
        thread2.start();


    }

}




