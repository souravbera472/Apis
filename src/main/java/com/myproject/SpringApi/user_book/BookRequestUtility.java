package com.myproject.SpringApi.user_book;

import com.mongodb.client.MongoClient;
import com.myproject.logger.KLogger;
import com.myproject.mongodb.MongoDbUtility;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class BookRequestUtility {
    public static boolean addOrUpdateBookOnReqCart(String userId, List<String> bookIds) {
        try (MongoClient client = MongoDbUtility.getConnection()) {
            List<Document> bookDoc = new ArrayList<>();
            for (String bookId : bookIds) {
                Document bookData = MongoDbUtility.getOneDocumentByFilter("book", client, new Document("bookId", bookId));
                bookData.remove("quantity");
                bookDoc.add(bookData);
            }
            Document documentByID = MongoDbUtility.getDocumentByID("user-book-req", client, userId);
            if (documentByID == null || documentByID.isEmpty()) {
                Document reqDoc = new Document("_id", userId);
                reqDoc.put("book-info", bookDoc);
                MongoDbUtility.insertOneDocument("user-book-req", client, reqDoc);
                KLogger.info("Approve for book request submitted successfully");
            } else {
                Document updateDoc = new Document().append("$push", new Document("book-info", new Document("$each",bookDoc)));
                MongoDbUtility.updateOneById("user-book-req", client,userId,updateDoc);
                KLogger.info("Approve for book request updated successfully");
            }

            return true;

        } catch (Exception e) {
            KLogger.warn("Due to server error request not submitted");
            KLogger.error(e);
            return false;
        }
    }
}
