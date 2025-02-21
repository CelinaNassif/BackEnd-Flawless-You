package com.flawlessyou.backend.controllers;


import com.flawlessyou.backend.entity.card.Card;
import com.flawlessyou.backend.entity.card.cardService;
import com.flawlessyou.backend.entity.user.UserService;

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
    @Autowired
    private UserService userService;
    @PostMapping("/send")
    public String sendCard(@RequestParam String message, HttpServletRequest request, @RequestParam String name) throws Exception {
       Card card = new Card();
       card.setMessage(message);
       
       card.setExpertId(userService.findByUsername(name).get().getUserId());
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
    @GetMapping("/expert")
    public List<Card> getCardsByExpertId(HttpServletRequest request) throws Exception {
        return cardService.getCardsByExpertId(request);
    }

    // استرجاع جميع البطاقات المرسلة من مستخدم معين
    @GetMapping("/user")
    public List<Card> getCardsByUserId(HttpServletRequest request) throws Exception {
     
        return cardService.getCardsByUserId(request);
    }
}