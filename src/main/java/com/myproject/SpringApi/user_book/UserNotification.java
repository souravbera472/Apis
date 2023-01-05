package com.myproject.SpringApi.user_book;

import com.myproject.logger.KLogger;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserNotification {

    @GetMapping("/user/{id}/notification")
    public ResponseEntity<Document> getNotification(@PathVariable String id) {
        KLogger.info("Id: " + id);
        Document document = UserBookUtility.getNotificationCount(id);
        if (document != null && !document.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(document);
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Document("user-message", "No notification available"));

    }


}

