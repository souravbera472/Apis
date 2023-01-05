package com.myproject.SpringApi.login_api;

import com.myproject.logger.KLogger;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationApi {

    /*
      @RequestBody
        emailId : sb123@gmail.com
        fName : sourav
        lName: bera
        password: abc@123
     */
    @PostMapping("/registration")
    @ResponseBody
    public ResponseEntity<Document> registration(@RequestBody registrationType docx) {
        boolean check = LoginUtility.addUserInMongo(docx);
        if (check) {
            Document document = new Document("user-message", "Registration successful")
                    .append("developer-message", "Data insert successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(document);
        }
        Document document = new Document("user-message", "Email Id already exits ")
                .append("developer-message", "userName already present in Db");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(document);
    }
}
