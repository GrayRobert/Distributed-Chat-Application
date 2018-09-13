package com.robertgray.api.mappers;

import com.robertgray.api.models.ChatMessage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
public interface ChatMessageMapper {

    @Select("SELECT id,type,content,sender,recipient,sent,received,lastMessageId,hash FROM chat_message ORDER BY sent ASC, id")
    public List<ChatMessage> findAllMessages();

    @Select("SELECT id,type,content,sender,recipient,sent,received,lastMessageId,hash FROM chat_message WHERE id = #{id}")
    public ChatMessage findMessageById(BigInteger id);

    @Select("SELECT id,type,content,sender,recipient,sent,received,lastMessageId,hash FROM chat_message WHERE hash = #{hash}")
    public ChatMessage findMessageByHash(String hash);

    @Insert("INSERT INTO chat_message " +
            "(type,content,sender,recipient,sent,received,lastMessageId,hash)" +
            "VALUES(#{type}, #{content}, #{sender}, #{recipient}, #{sent}, #{received}, #{lastMessageId}, #{hash});")
    public int insertMessage(ChatMessage message);

    @Update("UPDATE chat_message " +
            "SET type=#{type}," +
            "content=#{content}," +
            "sender=#{sender}," +
            "recipient=#{recipient}," +
            "sent=#{sent}," +
            "received=#{received}," +
            "lastMessageId=#{lastMessageId}," +
            "hash=#{hash}" +
            "WHERE id=#{id};")
    public int updateMessage(ChatMessage message);

    @Delete("DELETE FROM chat_message WHERE id = #{id}")
    public int deleteMessageById(BigInteger id);

    @Delete("DELETE FROM chat_message WHERE id <> 1")
    public int deleteAllMessages();



}
