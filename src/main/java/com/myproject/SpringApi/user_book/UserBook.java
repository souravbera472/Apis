package com.myproject.SpringApi.user_book;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserBook {

    @PostMapping("/user/{id}/renewal-book-request")
    public ResponseEntity<Document> renewalBooksReq(@PathVariable String id, @RequestParam String type, @RequestBody List<String> bookIds) {
        if (type.equalsIgnoreCase("renewal")) {
            boolean check = UserBookUtility.renewalOrReturnBooks(id, bookIds, "user-request-renewal");
            if (check) {
                int length = bookIds.size();
                Document document = new Document("user-message", length + (length == 1 ? " book " : " books ") + "renewal request submitted successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(document);
            } else {
                Document document = new Document("user-message", "Renewal request submission failed due to internal error");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(document);
            }
        } else if (type.equalsIgnoreCase("return")) {
            boolean check = UserBookUtility.renewalOrReturnBooks(id, bookIds, "user-request-return");
            if (check) {
                int length = bookIds.size();
                Document document = new Document("user-message", length + (length == 1 ? " book " : " books ") + "return request submitted successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(document);
            } else {
                Document document = new Document("user-message", "Return request submission failed due to internal error");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(document);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new Document("user-message", "Type is not valid"));
    }


    // get book data from using user id.
    @GetMapping("/user/{id}/books")
    public ResponseEntity<Document> userBooks(@PathVariable String id) {
        Document document = UserBookUtility.getUserBooks(id);
        if (document == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Document("developer-message", "Internal Error")
                            .append("user-message", "No data available due to server error"));
        } else if (document.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Document("user-message", "No data available"));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(document);
        }
    }
}
