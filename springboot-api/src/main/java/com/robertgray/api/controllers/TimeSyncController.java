package com.robertgray.api.controllers;

import com.robertgray.api.interfaces.ITimeSyncController;
import com.robertgray.api.models.ChatMessage;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class TimeSyncController implements ITimeSyncController {

    private static final Logger logger = LoggerFactory.getLogger(TimeSyncController.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Override
    public void checkAndSendTime(ChatMessage chatMessage) {

        DateTime sentTime = new DateTime(chatMessage.getSent());
        DateTime nowTime = new DateTime();

        // Bit of a hack but want to make sure time sync issues are not flagged on messages that are resent without messing with ordering.
        DateTime reSentTime = new DateTime(chatMessage.getReSent());
        if (reSentTime != null) {sentTime = reSentTime;}

        long diffInMillis = nowTime.getMillis() - sentTime.getMillis();

        logger.info("Time difference is " + diffInMillis);

        // Warn client if time different is more than 5 seconds
        if(diffInMillis > 5000 || diffInMillis < -5000) {
            ChatMessage timeMessage = new ChatMessage();
            timeMessage.setSender("Moderator");
            timeMessage.setSent(new Date());
            timeMessage.setRecipient(chatMessage.getSender());
            timeMessage.setType(ChatMessage.MessageType.TIME);
            timeMessage.setContent("Your clock appears to be out of sync by " + diffInMillis + " milliseconds. Please update your devices time or use NTP.");

            messagingTemplate.convertAndSend("/topic/public", timeMessage);
        }

    }
}
