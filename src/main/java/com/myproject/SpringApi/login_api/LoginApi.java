package com.myproject.SpringApi.login_api;

import com.myproject.logger.KLogger;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginApi {

    @PostMapping("/login")
    @ResponseBody
    public Document checkLoginRequest(@RequestParam String userName, @RequestParam String password){
        Document loginResult = LoginUtility.getLoginResult(userName);
        if(!loginResult.containsKey("error")) {
            boolean check = LoginUtility.checkValidation(loginResult.getString("password"), password);
            KLogger.info(check);
            Document resultDoc = new Document();
            if (check) {
                Document result = new Document();
                result.append("userName",loginResult.getString("userName"))
                        .append("fName",loginResult.getString("fName"))
                        .append("lName",loginResult.getString("lName"))
                        .append("id",loginResult.getString("_id"))
                        .append("role",loginResult.getOrDefault("role","user"));
                resultDoc.put("result",result);
                resultDoc.put("user-message", "You have successfully login");
                resultDoc.put("developer-message", "Credential is ok");
            } else {
                resultDoc.put("user-message", "You user name or password is wrong");
                resultDoc.put("developer-message", "Credential is not ok");
            }
            KLogger.info(resultDoc);
            return resultDoc;
        }
        return new Document().append("user-message","User name invalid");
    }
    @GetMapping("/login")
    public Document getData1(){
        return new Document().append("abc","cde");
    }

}
