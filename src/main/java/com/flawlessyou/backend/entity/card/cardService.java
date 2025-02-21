package com.flawlessyou.backend.entity.card;

import com.flawlessyou.backend.config.GetUser;
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
    private static final String COLLECTION_NAME = "cards";

    // إرسال بطاقة من المستخدم العادي إلى خبير البشرة
    public String sendCard(Card card, HttpServletRequest request) throws Exception {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        card.setSentDate(new Date());
        User user = getUser.userFromToken(request);
        card.setSenderId(user.getUserId());

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

    // إضافة رد خبير البشرة على البطاقة
    public String addExpertReply(String id, String expertReply) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> future = documentReference.update("expertReply", expertReply, "replyDate", new Date());
        return future.get().getUpdateTime().toString();
    }

    // استرجاع جميع البطاقات المرسلة إلى خبير معين
    public List<Card> getCardsByExpertId(String expertId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference cardsCollection = dbFirestore.collection(COLLECTION_NAME);
        Query query = cardsCollection.whereEqualTo("expertId", expertId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Card> cards = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            cards.add(document.toObject(Card.class));
        }
        return cards;
    }



     // استرجاع جميع البطاقات المرسلة إلى خبير معين
     public List<Card> getCardsByUserId(String userId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference cardsCollection = dbFirestore.collection(COLLECTION_NAME);
        Query query = cardsCollection.whereEqualTo("senderId", userId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Card> cards = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            cards.add(document.toObject(Card.class));
        }
        return cards;
    }
}