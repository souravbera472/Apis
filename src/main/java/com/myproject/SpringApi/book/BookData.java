package com.myproject.SpringApi.book;

import com.myproject.logger.KLogger;
import com.myproject.utill.Utillity;
import org.bson.Document;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class BookData {

    // return all book data for every user.
    @GetMapping("/all-book")
    public Map<String, Object> getAllBook(@RequestParam Map<String, String> mp) {
        int limit = Integer.parseInt(mp.getOrDefault("limit", "10"));
        int offset = Integer.parseInt(mp.getOrDefault("offset", "0"));
        String filter = mp.getOrDefault("filter", "");
        String sortBy = mp.getOrDefault("sortBy", "bookName");
        String sortOrder = mp.getOrDefault("sortOrder", "asc");
        Map<String, Object> result = BookUtility.getAllBookFromMongo(limit, offset, filter, sortBy, sortOrder);
        return result;
    }

    @PostMapping("/add-book")
    public ResponseEntity<Document> addBook(@RequestBody bookType bookDocx) {
        boolean check = BookUtility.addBookInMongo(bookDocx);
        Document document = new Document();
        if (check) {
            String bookName = bookDocx.getBookName();
            document.append("user-message", bookDocx.getQuantity() + " " + bookName + " book(s) added successfully in your library");
            document.append("developer-message", "book docs added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        } else {
            document.append("user-message", "Book addition failed in your library");
            document.append("developer-message", "book docs addition failed due mongo-server down");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(document);
    }

    @PostMapping("/user/{userId}/req-book/{bookId}")
    public Document reqForBook(@PathVariable String userId, @PathVariable String bookId, @RequestParam boolean doAdd) {
        KLogger.info("Request for book " + (doAdd ? "add " : "remove ") + bookId);
        String bookName = BookUtility.checkAvailability(userId, bookId, doAdd);
        Document document = new Document();
        if (Utillity.stringIsNonEmpty(bookName) && doAdd) {
            if (bookName.contains("book not available")) {
                document.put("user-message", bookName);
                document.put("developer-message", "book not available");
                return document;
            }
            document.put("user-message", bookName + " book added successfully in your cart");
            document.put("developer-message", "book request granted");
            return document;
        } else if (Utillity.stringIsNonEmpty(bookName) && !doAdd) {
            document.put("user-message", bookName + " book removed successfully from your cart");
            document.put("developer-message", "book remove request granted");
            return document;
        }
        document.put("user-message", "book not added in your cart");
        document.put("developer-message", "book request failed due to internal server error (Mongo down)");
        return document;
    }

}
