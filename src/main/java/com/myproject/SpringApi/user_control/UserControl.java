package com.myproject.SpringApi.user_control;

import com.myproject.utill.Utillity;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class UserControl {
    @GetMapping("/all-users")
    public static ResponseEntity<Document> getAllUsers(@RequestParam Map<String, String> params){
        Document result = UserControlUtility.getAllUsers(params);
        if(Utillity.docIsNonEmpty(result))
            return ResponseEntity.status(HttpStatus.OK).body(result);
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Document("user-message","Internal server error"));
    }
}
