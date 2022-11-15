package com.myproject.SpringApi.user_book;

import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserBookRequest {
    // get book data using user id.
    @GetMapping("/user/{id}/req-book")
    public Document getBookReq(@PathVariable String id) {
        Document document = BookRequestUtility.getBookReqData(id);
        //KLogger.info("Data: " + document);
        if (document == null) {
            return new Document("developer-message", "Internal Error")
                    .append("user-message", "No data available due to server error");
        } else if (document.isEmpty()) {
            return new Document("user-message", "No data available");
        } else {
            return document;
        }
    }

    @GetMapping("/user/{id}/renewal-req-book")
    public Document getRenewalOrReturnBookReq(@PathVariable String id,@RequestParam String type) {
        Document document = BookRequestUtility.getRenewalOrReturnBookReqData(id,type);
        //KLogger.info("Data: " + document);
        if (document == null) {
            return new Document("developer-message", "Internal Error")
                    .append("user-message", "No data available due to server error");
        } else if (document.isEmpty()) {
            return new Document("user-message", "No data available");
        } else {
            return document;
        }
    }

    // add book req data using user id
    @PostMapping("/user/{userId}/req-book")
    public Document bookReq(@PathVariable String userId, @RequestBody List<String> bookIds) {
        boolean check = BookRequestUtility.addOrUpdateBookOnReqCart(userId, bookIds);
        Document message = new Document();
        if (check) {
            message.put("user-message", "Approve for book request submitted successfully");
            message.put("developer-message", "request submitted successfully");
            return message;
        }
        message.put("user-message", "Request failed due to internal error");
        message.put("developer-message", "Request failed due to server down");
        return message;
    }

    @DeleteMapping("/user/{id}/remove-req-book")
    public Document removeBook(@PathVariable String id, @RequestBody List<String> bookIds) {
        boolean check = BookRequestUtility.removeBooks(id, bookIds);
        if (check) {
            int length = bookIds.size();
            return new Document("user-message", length + (length == 1 ? " book " : " books ") + "removed from your cart successfully");
        }

        return new Document("user-message", "books not removed from your cart due to internal error");
    }

}
