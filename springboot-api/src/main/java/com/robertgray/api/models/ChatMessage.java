package com.robertgray.api.models;

import java.util.Date;

public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String recipient;
    private Date sent;
    private Date received;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public ChatMessage(){}

    public ChatMessage(MessageType type, String content, String sender, String recipient, Date sent, Date received) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.recipient = recipient;
        this.sent = sent;
        this.received = received;
    }

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

    public Date getReceived() { return received; }

    public void setReceived(Date received) { this.received = received; }
}
