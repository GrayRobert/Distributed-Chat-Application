package com.robertgray.api.interfaces;

import com.robertgray.api.models.ChatMessage;

public interface ITimeSyncController {

    void checkAndSendTime(ChatMessage chatMessage);

}
