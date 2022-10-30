package com.myproject.SpringApi.user_book;

import com.myproject.logger.KLogger;
import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class UserNotification {

    @GetMapping("/user/{id}/notification")
    public Document getNotification(@PathVariable String id) {
        KLogger.info("Id: "+id);
        Document document = UserBookUtility.getNotificationCount(id);
        return Objects.requireNonNullElseGet(document, () -> new Document("user-message", "No notification available"));
    }


}

