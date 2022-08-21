package com.myproject.SpringApi.book;

import com.myproject.logger.KLogger;
import com.myproject.utill.Utillity;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookData {
    @PostMapping("/add-book")
    public Document addBook(@RequestBody bookType bookDocx) {
        boolean check = BookUtility.addBookInMongo(bookDocx);
        Document document = new Document();
        if (check) {
            String bookName = bookDocx.getBookName();
            document.append("user-message", bookDocx.getQuantity() + " " + bookName + " book(s) added successfully in your library");
            document.append("developer-message", "book docs added successfully");
            return document;
        } else {
            document.append("user-message", "Book addition failed in your library");
            document.append("developer-message", "book docs addition failed due mongo-server down");
        }
        return document;
    }

    @GetMapping("/req-book/{id}")
    public Document reqForBook(@PathVariable String id,@RequestParam boolean doAdd){
        KLogger.info("Request for book "+(doAdd?"add ":"remove ")+ id);
        String bookName = BookUtility.checkAvailability(id,doAdd);
        Document document = new Document();
        if(Utillity.stringIsNonEmpty(bookName) && doAdd){
            if(bookName.contains("book not available")){
                document.put("user-message",bookName);
                document.put("developer-message","book not available");
                return document;
            }
            document.put("user-message",bookName+ " book added successfully in your cart");
            document.put("developer-message","book request granted");
            return document;
        } else if (Utillity.stringIsNonEmpty(bookName) && !doAdd) {
            document.put("user-message",bookName+ " book removed successfully from your cart");
            document.put("developer-message","book remove request granted");
            return document;
        }
        document.put("user-message","book not added in your cart");
        document.put("developer-message","book request failed due to internal server error (Mongo down)");
        return document;
    }

}
