package com.flawlessyou.backend.entity.card;

import java.util.Date;

public class Card {

    private String id; 
    private String senderId; 
    private String expertId; 
    private String message; 
    private Date sentDate; 
    private String expertReply; 
    private Date replyDate;

    
    public Card() {
    }

    public Card(String id, String expertId, String message, String expertReply) {
        this.id = id;
        this.expertId = expertId;
        this.message = message;
        this.expertReply = expertReply;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public String getExpertId() {
        return expertId;
    }
    public void setExpertId(String expertId) {
        this.expertId = expertId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Date getSentDate() {
        return sentDate;
    }
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }
    public String getExpertReply() {
        return expertReply;
    }
    public void setExpertReply(String expertReply) {
        this.expertReply = expertReply;
    }
    public Date getReplyDate() {
        return replyDate;
    }
    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    } 








    
}
