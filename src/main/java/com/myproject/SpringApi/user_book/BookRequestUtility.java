package com.myproject.SpringApi.user_book;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.result.UpdateResult;
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
            FindIterable<Document> documentByFilter = MongoDbUtility.getDocumentByFilter("book", client, new Document("bookId", new Document("$in", bookIds)));
            for (Document document : documentByFilter) {
                document.remove("quantity");
                bookDoc.add(document);
            }
            Document documentByID = MongoDbUtility.getDocumentByID("user-book-req", client, userId);
            if (documentByID == null || documentByID.isEmpty()) {
                Document userDoc = MongoDbUtility.getDocumentByID("user-collection", client, userId);
                String fName = userDoc.getString("fName");
                String lName = userDoc.getString("lName");
                Document reqDoc = new Document("_id", userId);
                reqDoc.put("label",fName+" "+lName);
                reqDoc.put("userName", userDoc.getString("userName"));
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

    public static boolean removeBooks(String id, List<String> bookIds) {
        try(MongoClient client = MongoDbUtility.getConnection()){
            Document documentByID = MongoDbUtility.getDocumentByID("user-book-req", client, id);
            List<String> bookCounts = documentByID.get("book-info",new ArrayList<>());
            if(bookCounts.size() == bookIds.size()){
                MongoDbUtility.deleteById("user-book-req",client,id);
            }
            else {
                Document updateDoc = MongoDbUtility.removeDataForArray("book-info",new Document("_id",new Document("$in",bookIds)));
                UpdateResult updateResult = MongoDbUtility.updateOneById("user-book-req", client, id, updateDoc);
                KLogger.info(updateResult);
            }
            return true;
        }catch (Exception e){
            KLogger.error(e);
        }
        return false;
    }

    public static Document getRenewalOrReturnBookReqData(String userId, String type) {
        try(MongoClient client = MongoDbUtility.getConnection()){
            String collectionName = "";
            switch (type.toLowerCase()) {
                case "renewal" -> collectionName = "user-request-renewal";
                case "return" -> collectionName = "user-request-return";
                default -> KLogger.warn("Type is not match for return or renewal request");
            }
           return MongoDbUtility.getDocumentByID(collectionName, client, userId);

        }catch (Exception e){
            KLogger.error(e);
        }
        return null;
    }

    public static boolean removeRenewalBooks(String userId, String type, List<String> bookIds) {
        try(MongoClient client = MongoDbUtility.getConnection()){
            String collectionName = "";
            switch (type.toLowerCase()) {
                case "renewal" -> collectionName = "user-request-renewal";
                case "return" -> collectionName = "user-request-return";
                default -> KLogger.warn("Type is not match for return or renewal request");
            }
            Document documentByID = MongoDbUtility.getDocumentByID(collectionName, client, userId);
            List<String> bookCounts = documentByID.get("book-info",new ArrayList<>());
            if(bookCounts.size() == bookIds.size()){
                MongoDbUtility.deleteById(collectionName,client,userId);
            }
            else {
                Document updateDoc = MongoDbUtility.removeDataForArray("book-info",new Document("_id",new Document("$in",bookIds)));
                UpdateResult updateResult = MongoDbUtility.updateOneById(collectionName, client, userId, updateDoc);
                KLogger.info(updateResult);
            }
            return true;

        }catch (Exception e){
            KLogger.error(e);
        }
        return false;
    }
}
