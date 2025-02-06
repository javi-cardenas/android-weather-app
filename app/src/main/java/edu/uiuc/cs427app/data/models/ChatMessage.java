package edu.uiuc.cs427app.data.models;

public class ChatMessage {
    public enum MessageType {
        PROMPT,
        USER_MESSAGE,
        AI_MESSAGE
    }

    private final String messageText;
    private final MessageType messageType;

    /**
     * ChatMessage constructor
     */
    public ChatMessage(String messageText, MessageType messageType) {
        this.messageText = messageText;
        this.messageType = messageType;
    }

    /**
     * text Ggtter
     */
    public String getText() {
        return messageText;
    }

    /**
     * messageType getter
     */
    public MessageType getMessageType() {
        return messageType;
    }
}


