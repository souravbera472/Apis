package com.myproject.SpringApi.login_api;

import com.mongodb.client.MongoClient;
import com.myproject.logger.KLogger;
import com.myproject.mongodb.MongoDbUtility;
import com.myproject.utill.Utillity;
import org.bson.Document;

import java.security.MessageDigest;

import static com.myproject.utill.Utillity.getUUID;

public class LoginUtility {

    // get login result from mongodb using userName.
    // @collectionName: "user-collection"
    public static Document getLoginResult(String userName) {
        Document response = new Document().append("error", "user name not found");
        try (MongoClient client = MongoDbUtility.getConnection()) {
            Document user = MongoDbUtility.getOneDocumentByFilter("user-collection", client, new Document("userName", userName));
            if (user != null && !user.isEmpty()) {
                //KLogger.info(user);
                return user;
            }
        } catch (Exception e) {
            KLogger.error(e);
        }
        return response;
    }

    // check login validation
    public static boolean checkValidation(String getPassword, String providedPassword) {
        return Utillity.stringIsNonEmpty(providedPassword) && getPassword.equals(encryptPassword(providedPassword));
    }

    // encrypt password for security
    private static String encryptPassword(String password) {
        String encryptPass = null;
        String algorithm = "SHA";
        if (Utillity.stringIsNonEmpty(password)) {
            byte[] plainText = password.getBytes();
            try {
                MessageDigest digest = MessageDigest.getInstance(algorithm);
                digest.reset();
                digest.update(plainText);
                byte[] encodedPassword = digest.digest();

                StringBuilder builder = new StringBuilder();
                for (byte b : encodedPassword) {
                    if ((b & 0xff) < 0x10) {
                        builder.append("0");
                    }
                    builder.append(Long.toString(b & 0xff, 16));
                }
                encryptPass = builder.toString();
            } catch (Exception e) {
                KLogger.error(e);
            }
        }
        //KLogger.info("Password: "+encryptPass);
        return encryptPass;
    }

    public static boolean addUserInMongo(registrationType docx) {
        String emailId = docx.getEmailId();
        String password = docx.getPassword();
        String fName = docx.getFName();
        String lName = docx.getLName();
        Document document = new Document("_id", getUUID())
                .append("userName", emailId)
                .append("fName", fName)
                .append("lName", lName)
                .append("role", "user")
                .append("password", encryptPassword(password));
        try (MongoClient client = MongoDbUtility.getConnection()) {
            MongoDbUtility.insertOneDocument("user-collection", client, document);
            KLogger.info("Data insert successfully for user registration: " + document);
            return true;
        } catch (Exception e) {
            KLogger.error(e);
            return false;
        }
    }
}
