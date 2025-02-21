package com.flawlessyou.backend.entity.card;

import java.util.Date;
import java.util.UUID;

public class Card {

    private String id; 
    private String senderId; 
    private String expertId; 
    private String message; 
    private Date sentDate; 
    private String expertReply; 
    private Date replyDate;

    
    

    public Card() {
        this.id = UUID.randomUUID().toString(); // توليد معرف فريد
    }






    
    // public Card( String expertId, String message) {
    //    this.id = UUID.randomUUID().toString(); // توليد معرف فريد
    //     this.expertId = expertId;
    //     this.message = message;
        
    // }
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
