package com.myproject.SpringApi.user_book;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.myproject.logger.KLogger;
import com.myproject.mongodb.MongoDbUtility;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public class UserBookUtility {
    public static Document getUserBooks(String userId) {
        try (MongoClient client = MongoDbUtility.getConnection()) {
            return MongoDbUtility.getDocumentByID("user-book", client, userId);
        } catch (Exception e) {
            KLogger.error(e);
        }
        return null;
    }

    public static Document getNotificationCount(String id) {
        try (MongoClient client = MongoDbUtility.getConnection()) {
            Document result = new Document();
            long totalReqCount = MongoDbUtility.getCountWithOutFilter("user-book-req", client);
            result.put("totalReqCount", totalReqCount);
            Document userReqDoc = MongoDbUtility.getDocumentByID("user-book-req", client, id);
            if(userReqDoc!=null && !userReqDoc.isEmpty()) {
                List<String> bookLen = userReqDoc.get("book-info", new ArrayList<>());
                result.put("userReqCount",bookLen.size());
            }
            else
                result.put("userReqCount",0L);

            Document userBook = MongoDbUtility.getDocumentByID("user-book",client,id);
            if(userBook!=null && !userBook.isEmpty()) {
                List<String> bookLen = userBook.get("book-info", new ArrayList<>());
                result.put("userBookCount",bookLen.size());
            }
            else
                result.put("userBookCount",0L);
            return result;
        } catch (Exception e) {
            KLogger.error(e);
        }
        return null;
    }


    /**
     *
     * collection name: "user-request-renewal"
     * @param userId
     * @param bookIds
     * @return
     */
    public static boolean renewalOrReturnBooks(String userId, List<String> bookIds, String collectionName) {

        try (MongoClient client = MongoDbUtility.getConnection()) {
            List<Document> bookDoc = new ArrayList<>();
            FindIterable<Document> documentByFilter = MongoDbUtility.getDocumentByFilter("book", client, new Document("_id", new Document("$in", bookIds)));
            for (Document document : documentByFilter) {
                document.remove("quantity");
                document.put("ct",System.currentTimeMillis());
                bookDoc.add(document);
            }
            Document documentByID = MongoDbUtility.getDocumentByID(collectionName, client, userId);
            if (documentByID == null || documentByID.isEmpty()) {
                Document userDoc = MongoDbUtility.getDocumentByID("user-collection", client, userId);
                String fName = userDoc.getString("fName");
                String lName = userDoc.getString("lName");
                Document reqDoc = new Document("_id", userId);
                reqDoc.put("label",fName+" "+lName);
                reqDoc.put("userName", userDoc.getString("userName"));
                reqDoc.put("book-info", bookDoc);
                MongoDbUtility.insertOneDocument(collectionName, client, reqDoc);
                KLogger.info("Renewal or Return for book request submitted successfully");
            } else {
                Document updateDoc = MongoDbUtility.prepareDataForArray("book-info", bookDoc);
                MongoDbUtility.updateOneById(collectionName, client, userId, updateDoc);
                KLogger.info("Renewal or Return for book request updated successfully");
            }

            return true;

        } catch (Exception e) {
            KLogger.warn("Due to mongo server error request not submitted");
            KLogger.error(e);
            return false;
        }
    }
}
