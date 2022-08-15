package com.myproject.SpringApi.login_api;

import com.myproject.logger.KLogger;
import org.bson.Document;
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
    public Document registration(@RequestBody registrationType docx) {
        boolean check = LoginUtility.addUserInMongo(docx);
        if (check) {
            return new Document("user-message", "Registration successful")
                    .append("developer-message", "Data insert successfully");
        }
        return new Document("user-message", "Email Id already exits ")
                .append("developer-message","userName already present in Db");
    }
}
