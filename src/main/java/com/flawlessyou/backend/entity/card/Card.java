package com.flawlessyou.backend.entity.card;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.flawlessyou.backend.entity.SkinAnalysis.SkinAnalysis;

public class Card {

    private String id; 
    private String senderName; 
    private String expertId; 
    private String message; 
    private Date sentDate; 
    private List<String> expertReply; 
    private Date replyDate;
    private String expertName; 
    private boolean isEdit;
    private SkinAnalysis SkinAnalysis;

    public SkinAnalysis getSkinAnalysis() {
        return SkinAnalysis;
    }







    public void setSkinAnalysis(SkinAnalysis SkinAnalysis) {
        this.SkinAnalysis = SkinAnalysis;
    }







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
    public String getsenderName() {
        return senderName;
    }
    public void setsenderName(String senderName) {
        this.senderName = senderName;
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
    public List<String> getExpertReply() {
        return expertReply;
    }
    public void setExpertReply(List<String> expertReply) {
        this.expertReply = expertReply;
    }
    public Date getReplyDate() {
        return replyDate;
    }
    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }







    public String getExpertName() {
        return expertName;
    }







    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }







    public boolean isEdit() {
        return isEdit;
    }







    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    } 








    
}
