package com.robertgray.api.models;

import java.math.BigInteger;
import java.util.Date;

public class ChatMessage {
    private BigInteger id;
    private MessageType type;
    private String content;
    private String sender;
    private String recipient;
    private Date sent;
    private Date reSent;
    private Date received;
    private BigInteger lastMessageId;
    private String hash;


    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        MISSED_MESSAGES,
        TIME
    }

    public ChatMessage(){}

    public ChatMessage(MessageType type, String content, String sender, String recipient, Date sent, Date received, BigInteger lastMessageId, String hash) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.recipient = recipient;
        this.sent = sent;
        this.received = received;
        this.lastMessageId = lastMessageId;
        this.hash = hash;
    }

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() { return recipient; }

    public void setRecipient(String recipient) { this.recipient = recipient; }

    public Date getSent() { return sent; }

    public void setSent(Date sent) { this.sent = sent; }

    public Date getReSent() { return reSent; }

    public void setReSent(Date reSent) { this.reSent = reSent; }

    public Date getReceived() { return received; }

    public void setReceived(Date received) { this.received = received; }

    public BigInteger getLastMessageId() { return lastMessageId; }

    public void setLastMessageId(BigInteger lastMessageId) { this.lastMessageId = lastMessageId; }

    public String getHash() { return hash; }

    public void setHash(String hash) { this.hash = hash; }
}
