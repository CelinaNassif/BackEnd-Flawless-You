package com.flawlessyou.backend.entity.card;

import com.flawlessyou.backend.config.GetUser;
import com.flawlessyou.backend.entity.SkinAnalysis.SkinAnalysis;
import com.flawlessyou.backend.entity.SkinAnalysis.SkinAnalysisService;
import com.flawlessyou.backend.entity.user.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class cardService {
      @Autowired
private GetUser getUser;

@Autowired
private  SkinAnalysisService SkinAnalysisService;
    private static final String COLLECTION_NAME = "cards";

    // إرسال بطاقة من المستخدم العادي إلى خبير البشرة
    public String sendCard(Card card, HttpServletRequest request) throws Exception {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        card.setSentDate(new Date());
        User user = getUser.userFromToken(request);
        card.setsenderName(user.getUserName());
      
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COLLECTION_NAME).document(card.getId()).set(card);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    // استرجاع البطاقة بواسطة معرفها
    public Card getCardById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        Card card = null;
        if (document.exists()) {
            card = document.toObject(Card.class);
        }
        return card;
    }

    public String addExpertReply(String id, String expertReply) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(id);
    
        // Retrieve the current document
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot documentSnapshot = future.get(); // Blocking call to get the document
    
        if (documentSnapshot.exists()) {
            Card card = documentSnapshot.toObject(Card.class);
            if (card != null) {
                List<String> replies = card.getExpertReply();
                if (replies == null) {
                    replies = new ArrayList<>();
                }
                replies.add(expertReply); // Add the new reply
                card.setExpertReply(replies);
                card.setReplyDate(new Date()); // Update the reply date
    
                // Update the document in Firestore
                ApiFuture<WriteResult> updateFuture = documentReference.set(card);
                updateFuture.get(); // Blocking call to ensure the update is complete
                return "Reply added successfully";
            }
        } else {
            return "Document does not exist";
        }
        return "Failed to add reply";
    }


    // استرجاع جميع البطاقات المرسلة إلى خبير معين
    public List<Card> getCardsByExpertId(HttpServletRequest request) throws Exception {
       
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference cardsCollection = dbFirestore.collection(COLLECTION_NAME);
        Query query = cardsCollection.whereEqualTo("expertName", getUser.userFromToken(request).getUserName());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Card> cards = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            cards.add(document.toObject(Card.class));
        }
        return cards;
    }



     // استرجاع جميع البطاقات المرسلة إلى خبير معين
     public List<Card> getCardsByUserId(HttpServletRequest request) throws Exception {
        
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference cardsCollection = dbFirestore.collection(COLLECTION_NAME);
        Query query = cardsCollection.whereEqualTo("senderId", getUser.userFromToken(request).getUserId());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Card> cards = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            cards.add(document.toObject(Card.class));
        }
        return cards;
    }


//     public String createCardWithLatestAnalysis(Card card, HttpServletRequest request) throws Exception {
//     try {
//         // 1. الحصول على بيانات المستخدم
//         User user = getUser.userFromToken(request);
//         card.setSenderId(user.getUserId());
//         card.setSentDate(new Date());
        
//         // 2. الحصول على آخر تحليل للبشرة للمستخدم
//         SkinAnalysis latestAnalysis = SkinAnalysisService.getLatestSkinAnalysisByUserId(user.getUserId());
        
//         if (latestAnalysis != null) {
//             // 3. ربط الكارد بآخر تحليل
//             card.setSkinAnalysiId(latestAnalysis.getId());
            
//             // 4. حفظ الكارد في Firestore
//             Firestore dbFirestore = FirestoreClient.getFirestore();
//             ApiFuture<WriteResult> future = dbFirestore.collection(COLLECTION_NAME)
//                     .document(card.getId())
//                     .set(card);
            
//             return future.get().getUpdateTime().toString();
//         } else {
//             throw new Exception("User has no skin analysis available");
//         }
//     } catch (Exception e) {
//         throw new Exception("Error creating card with latest analysis: " + e.getMessage(), e);
//     }
// }


public String sendCardWithLatestAnalysis(String message, String name, HttpServletRequest request) throws Exception {
    try {
        // 1. إنشاء كارد جديدة وتعيين القيم الأساسية
        Card card = new Card();
        card.setMessage(message);
        card.setExpertName(name);
        
        // 2. الحصول على بيانات المستخدم من التوكن
        User user = getUser.userFromToken(request);
        card.setsenderName(user.getUserName());
        card.setSentDate(new Date());
        
        // 3. الحصول على آخر تحليل للبشرة للمستخدم
        SkinAnalysis latestAnalysis = SkinAnalysisService.getLatestSkinAnalysisByUserId(user.getUserId());
        
        if (latestAnalysis != null) {
            // 4. ربط الكارد بآخر تحليل
            card.setSkinAnalysis(latestAnalysis);
            
            // 5. حفظ الكارد في Firestore
            Firestore dbFirestore = FirestoreClient.getFirestore();
            ApiFuture<WriteResult> future = dbFirestore.collection(COLLECTION_NAME)
                    .document(card.getId())
                    .set(card);
            
            return future.get().getUpdateTime().toString();
        } else {
            throw new Exception("User has no skin analysis available");
        }
    } catch (Exception e) {
        throw new Exception("Error sending card with latest analysis: " + e.getMessage(), e);
    }
}
}