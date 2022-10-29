package com.myproject.SpringApi.user_book;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.myproject.logger.KLogger;
import com.myproject.mongodb.MongoDbUtility;
import org.bson.Document;

import javax.print.Doc;
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
                Document updateDoc = MongoDbUtility.prepareDataForArray("book-info", bookDoc);
                MongoDbUtility.updateOneById("user-book-req", client, userId, updateDoc);
                KLogger.info("Approve for book request updated successfully");
            }

            return true;

        } catch (Exception e) {
            KLogger.warn("Due to server error request not submitted");
            KLogger.error(e);
            return false;
        }
    }

    public static Document getBookReqData(String id) {
        try (MongoClient client = MongoDbUtility.getConnection()) {
            Document reqDoc = MongoDbUtility.getDocumentByID("user-book-req", client, id);
            List<Document> bookInfo = reqDoc.get("book-info", new ArrayList<>());
            List<String> bookIds = new ArrayList<>();
            for (Document document : bookInfo) {
                bookIds.add(document.getString("_id"));
            }
            Document filter = new Document("_id", new Document("$in", bookIds));
            FindIterable<Document> bookDoc = MongoDbUtility.getDocumentByFilter("book", client, filter);
            List<Document> result = new ArrayList<>();
            for (Document document : bookDoc) {
                KLogger.info("BookDoc: " + document);
                if (document.getInteger("quantity") > 0)
                    document.put("isAvailable", true);
                else
                    document.put("isAvailable", false);

                document.remove("quantity");
                result.add(document);
            }

            return new Document("_id", id).append("book-info", result);

        } catch (Exception e) {
            KLogger.error(e);
        }
        return null;
    }
}
