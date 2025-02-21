package com.flawlessyou.backend.controllers;


import com.flawlessyou.backend.entity.card.Card;
import com.flawlessyou.backend.entity.card.cardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private cardService cardService;

    // إرسال بطاقة من المستخدم العادي إلى خبير البشرة
    @PostMapping("/send")
    public String sendCard(@RequestBody Card card, HttpServletRequest request) throws Exception {
        return cardService.sendCard(card, request);
    }

    // استرجاع بطاقة بواسطة معرفها
    @GetMapping("/{id}")
    public Card getCardById(@PathVariable String id) throws ExecutionException, InterruptedException {
        return cardService.getCardById(id);
    }

    // إضافة رد خبير البشرة على البطاقة
    @PostMapping("/{id}/reply")
    public String addExpertReply(@PathVariable String id, @RequestParam String expertReply) throws ExecutionException, InterruptedException {
        return cardService.addExpertReply(id, expertReply);
    }

    // استرجاع جميع البطاقات المرسلة إلى خبير معين
    @GetMapping("/expert/{expertId}")
    public List<Card> getCardsByExpertId(@PathVariable String expertId) throws ExecutionException, InterruptedException {
        return cardService.getCardsByExpertId(expertId);
    }

    // استرجاع جميع البطاقات المرسلة من مستخدم معين
    @GetMapping("/user/{userId}")
    public List<Card> getCardsByUserId(@PathVariable String userId) throws ExecutionException, InterruptedException {
        return cardService.getCardsByUserId(userId);
    }
}