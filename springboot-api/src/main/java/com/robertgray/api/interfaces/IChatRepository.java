package com.robertgray.api.interfaces;

import com.robertgray.api.models.ChatMessage;

import java.math.BigInteger;
import java.util.List;

public interface IChatRepository {

    List<ChatMessage> getAllChatMessages();

    ChatMessage getChatMessageById(BigInteger id);

    int insertChatMessage(ChatMessage message);

    int updateChatMessage(ChatMessage message);

    int deleteChatMessage(BigInteger id);
}
