package com.myproject.SpringApi.user_book;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.result.UpdateResult;
import com.myproject.logger.KLogger;
import com.myproject.mongodb.MongoDbUtility;
import com.myproject.utill.Utillity;
import org.bson.Document;

import javax.print.Doc;
import java.util.*;

public class ApproveBookUtility {

    public static Document getAllReqBook(String filter) {
        try (MongoClient client = MongoDbUtility.getConnection()) {
            List<Document> data = new ArrayList<>();
            Document filterDoc = new Document();
            if (Utillity.stringIsNonEmpty(filter)) {
                Document regex = new Document("$regex", ".*" + filter + ".*").append("$options", "si");
                Document label = new Document("label", regex);
                Document userName = new Document("userName", regex);
                filterDoc.append("$or", Arrays.asList(label, userName));
            }
            FindIterable<Document> notAvalBookData = MongoDbUtility.getDocumentByFilter("book", client, new Document("quantity", new Document("$eq", 0)));
            HashSet<String> hashSet = new HashSet<>();
            for (Document doc : notAvalBookData) {
                hashSet.add(doc.getString("_id"));
            }
            FindIterable<Document> documentByFilter = MongoDbUtility.getDocumentByFilter("user-book-req", client, filterDoc);
            for (Document document : documentByFilter) {
                ArrayList<Document> userBook = document.get("book-info", new ArrayList<>());
                userBook.forEach(e -> {
                    if (hashSet.contains(e.getString("_id"))) {
                        e.put("isAvailable", false);
                    } else e.put("isAvailable", true);
                });
                data.add(document);
            }
            //KLogger.info(data);
            return new Document("req-data", data);
        } catch (Exception e) {
            KLogger.error(e);
        }

        return null;
    }

    /**
     * @param approveId
     * @param approveBy
     * @param approveById
     * @param bookInfo
     * @return
     */
    public static boolean postBooksData(String approveId, String approveBy, String approveById, List<Map<String, String>> bookInfo) {

        try (MongoClient client = MongoDbUtility.getConnection()) {
            Document updateDoc = new Document();
            List<Document> updateList = new ArrayList<>();
            List<String> bookIds = new ArrayList<>();
            for (Map<String, String> document : bookInfo) {
                Document doc = new Document();
                KLogger.info("Doc: " + document);
                String id = document.get("_id");
                String bookId = document.get("bookId");
                String bookName = document.get("bookName");
                String bookAuthor = document.get("bookAuthor");
                bookIds.add(id);
                doc.put("_id", id);
                doc.put("id", bookId);
                doc.put("name", bookName);
                doc.put("author", bookAuthor);
                doc.put("approvedBy", approveBy);
                doc.put("approvedById", approveById);
                doc.put("ct", System.currentTimeMillis());
                doc.put("lu", System.currentTimeMillis());
                updateList.add(doc);
            }
            Document userDoc = MongoDbUtility.getDocumentByID("user-collection", client, approveId);
            Document userBookDoc = MongoDbUtility.getDocumentByID("user-book", client, approveId);
            if (userBookDoc == null || userBookDoc.isEmpty()) {
                updateDoc.append("_id", approveId)
                        .append("userName", userDoc.getString("userName"))
                        .append("label", userDoc.getString("fName") + " " + userDoc.getString("lName"))
                        .append("book-info", updateList);
                MongoDbUtility.insertOneDocument("user-book", client, updateDoc);
            } else {
                updateDoc = MongoDbUtility.prepareDataForArray("book-info", updateList);
                MongoDbUtility.updateOneById("user-book", client, approveId, updateDoc);
            }

            // remove books-info from 'user-book-req' collection.
            Document userReqDoc = MongoDbUtility.getDocumentByID("user-book-req", client, approveId);
            List<String> reqCount = userReqDoc.get("book-info", new ArrayList<>());
            if (bookIds.size() == reqCount.size()) {
                MongoDbUtility.deleteById("user-book-req", client, approveId);
            } else {
                Document removeDataForArray = MongoDbUtility.removeDataForArray("book-info", new Document("_id", new Document("$in", bookIds)));
                MongoDbUtility.updateOneById("user-book-req", client, approveId, removeDataForArray);
            }

            // decrease quantity books from 'book' collection.
            Document removeBookDoc = new Document("$inc", new Document("quantity", -1));
            MongoDbUtility.updateMany("book", client, new Document("_id", new Document("$in", bookIds)), removeBookDoc);
            return true;
        } catch (Exception e) {
            KLogger.error(e);
        }
        return false;
    }

    public static Document getAllRenewalBooks(String filter, String type) {
        try (MongoClient client = MongoDbUtility.getConnection()) {
            String collectionName = "";
            switch (type.toLowerCase()) {
                case "renewal" -> collectionName = "user-request-renewal";
                case "return" -> collectionName = "user-request-return";
                default -> KLogger.warn("Type is not match for return or renewal request");
            }
            Document filterDoc = new Document();
            if (Utillity.stringIsNonEmpty(filter)) {
                Document regex = new Document("$regex", ".*" + filter + ".*").append("$options", "si");
                Document label = new Document("label", regex);
                Document userName = new Document("userName", regex);
                filterDoc.append("$or", Arrays.asList(label, userName));
            }
            FindIterable<Document> documentByFilter = MongoDbUtility.getDocumentByFilter(collectionName, client, filterDoc);
            List<Document> result = new ArrayList<>();
            for (Document document : documentByFilter) {
                result.add(document);
            }
            return new Document("req-data",result);
        } catch (Exception e) {
            KLogger.error(e);
        }
        return null;
    }

    public static boolean checkRenewalOrReturnBooks(String userId, String approveById, String approveBy, String type, List<String> bookIds) {

        try(MongoClient client = MongoDbUtility.getConnection()) {
            String collectionName = "";
            String bookCollection = "user-book";
            switch (type.toLowerCase()) {
                case "renewal" -> collectionName = "user-request-renewal";
                case "return" -> collectionName = "user-request-return";
                default -> KLogger.warn("Type is not match for return or renewal request");
            }
            MongoDbUtility.deleteById(collectionName,client,userId);
            // Renewal part ----------
            if(type.equalsIgnoreCase("renewal")){
                Document filter = new Document("_id",userId).append("book-info._id",new Document("$in",bookIds));
                // updated lu, approvedBy and approveByID in user-book collection..
                Document updateDoc = new Document("$set",new Document("book-info.$.lu",System.currentTimeMillis())
                        .append("book-info.$.approvedBy",approveBy)
                        .append("book-info.$.approvedById",approveById));
                MongoDbUtility.updateOne(bookCollection,client,filter,updateDoc);
                KLogger.info("Books are renewal successfully, bookIds: "+bookIds);
            }

            // Return part ----------
            else{
                // remove data from user-book collection for a particular user.
                Document removeDataForArray = MongoDbUtility.removeDataForArray("book-info", new Document("_id", new Document("$in", bookIds)));
                MongoDbUtility.updateOneById(bookCollection, client, userId, removeDataForArray);

                // increment count in book collection
                Document removeBookDoc = new Document("$inc", new Document("quantity", 1));
                MongoDbUtility.updateMany("book", client, new Document("_id", new Document("$in", bookIds)), removeBookDoc);

                KLogger.info("Books are returned successfully, bookIds: "+bookIds);
            }
            return true;
        }catch (Exception e){
            KLogger.error(e);
        }
        return false;
    }
}
