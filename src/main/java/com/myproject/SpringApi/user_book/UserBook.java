package com.myproject.SpringApi.user_book;

import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.List;

@RestController
public class UserBook {

    // add book in user-book collection using user id.
    @PostMapping("/user/{id}/books")
    public Document addBooksForUsers(@PathVariable String id, @RequestBody List<Document> bookInfo){
        boolean check = UserBookUtility.addBooksForUsers(id,bookInfo);
        if(check){
            return new Document("user-message","Books added successfully")
                    .append("developer-message","Book added successfully in mongo");
        }
        else {
            return new Document("user-message","Books not added due to internal error")
                    .append("developer-message","Book insertion failed due server error");
        }
    }

    // get book data from using user id.
    @GetMapping("/user/{id}/books")
    public Document userBooks(@PathVariable String id){
        Document document = UserBookUtility.getUserBooks(id);
        if(document == null){
            return new Document("developer-message","Internal Error")
                    .append("user-message","No data available due to server error");
        }
        else if(document.isEmpty()){
            return new Document("user-message","No data available");
        }
        else{
            return document;
        }
    }
}
