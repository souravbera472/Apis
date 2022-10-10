package com.myproject.SpringApi.user_book;

import org.bson.Document;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class UserBookRequest {
    @PostMapping("/user/{userId}/req-book")
    public Document bookReq(@PathVariable String userId, @RequestBody List<String> bookIds) {
        boolean check = BookRequestUtility.addOrUpdateBookOnReqCart(userId, bookIds);
        Document message = new Document();
        if(check){
            message.put("user-message","Approve for book request submitted successfully");
            message.put("developer-message","request submitted successfully");
            return message;
        }
        message.put("user-message","Request failed due to internal error");
        message.put("developer-message","Request failed due to server down");
        return message;
    }

}
