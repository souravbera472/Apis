package com.myproject.SpringApi.user_book;

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
            Document document = MongoDbUtility.getDocumentByID("user-book", client, userId);
            return document;
        } catch (Exception e) {
            KLogger.error(e);
        }
        return null;
    }

    //todo need to change userId to approve Id
    public static boolean addBooksForUsers(String userId, List<Document> bookInfo) {
        try (MongoClient client = MongoDbUtility.getConnection()) {
            Document updateDoc = new Document();
            List<Document> updateList = new ArrayList<>();
            for (Document document : bookInfo) {
                Document doc = new Document();
                String bookId = document.getString("bookId");
                String bookName = document.getString("bookName");
                String bookAuthor = document.getString("bookAuthor");
                String approvedBy = document.getString("approvedBy");
                doc.put("id", bookId);
                doc.put("name", bookName);
                doc.put("author", bookAuthor);
                doc.put("approvedBy", approvedBy);
                doc.put("ct", System.currentTimeMillis());
                doc.put("lu", System.currentTimeMillis());
                updateList.add(doc);
            }
            Document userDoc = MongoDbUtility.getDocumentByID("user-collection", client, userId);
            Document userBookDoc = MongoDbUtility.getDocumentByID("user-book", client, userId);
            if (userBookDoc == null || userBookDoc.isEmpty()) {
                updateDoc.append("_id", userId)
                        .append("userName", userDoc.getString("userName"))
                        .append("fName", userDoc.getString("fName"))
                        .append("lName", userDoc.getString("lName"))
                        .append("book-info", updateList);
                MongoDbUtility.insertOneDocument("user-book", client, updateDoc);
            } else {
                updateDoc = MongoDbUtility.prepareDataForArray("book-info", updateList);
                MongoDbUtility.updateOneById("user-book", client, userId, updateDoc);
            }
            return true;
        } catch (Exception e) {
            KLogger.error(e);
        }
        return false;
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
}
