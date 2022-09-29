package com.myproject.SpringApi.book;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.myproject.logger.KLogger;
import com.myproject.mongodb.MongoDbUtility;
import com.myproject.utill.Utillity;
import org.bson.Document;
import java.util.*;

public class BookUtility {

    public static Map<String, Object> getAllBookFromMongo(int limit, int offset, String filter, String sortBy, String sortOrder) {
        Map<String, Object> result = new HashMap<>();
        String collectionName = "book";
        try (MongoClient client = MongoDbUtility.getConnection()) {
            Document filterDoc = new Document();
            if (Utillity.stringIsNonEmpty(filter)) {
                Document regex = new Document("$regex", ".*" + filter + ".*").append("$options", "si");
                Document bookName = new Document("bookName", regex);
                Document bookAuthor = new Document("bookAuthor", regex);
                Document bookId = new Document("bookId", regex);
                filterDoc.append("$or", Arrays.asList(bookName, bookAuthor, bookId));
            }
            FindIterable<Document> data = MongoDbUtility.getPageQueryResult(collectionName, client, filterDoc, sortBy, sortOrder, limit, offset);
            Document meta = new Document();
            long count = MongoDbUtility.getCount(collectionName, client, filterDoc);
            meta.append("total", count).append("limit", limit)
                    .append("offset", offset)
                    .append("sortBy",sortBy)
                    .append("sortOrder",sortOrder);
            List<Document> arrayData = new ArrayList<>();
            for (Document item : data) {
                arrayData.add(item);
            }
            result.put("all-book", arrayData);
            result.put("_meta", meta);

        } catch (Exception e) {
            KLogger.error(e);
        }
        return result;
    }

    public static boolean addBookInMongo(bookType bookDocx) {
        String collectionName = "book";
        try (MongoClient client = MongoDbUtility.getConnection()) {
            String bookId = bookDocx.getBookId();
            String bookName = bookDocx.getBookName();
            String bookAuthor = bookDocx.getBookAuthor();
            int quantity = bookDocx.getQuantity();
            Document doc = new Document()
                    .append("bookId", bookId)
                    .append("bookName", bookName)
                    .append("bookAuthor", bookAuthor)
                    .append("quantity", quantity);
            Document book = MongoDbUtility.getOneDocumentByFilter(collectionName, client, new Document("bookId", bookId));
            if (book != null && !book.isEmpty()) {
                doc.clear();
                doc.put("$set", new Document("quantity", quantity + book.getInteger("quantity")));
                MongoDbUtility.updateOneById(collectionName, client, book.getString("_id"), doc);
            } else {
                doc.put("_id", Utillity.getUUID());
                MongoDbUtility.insertOneDocument(collectionName, client, doc);
            }
            return true;
        } catch (Exception e) {
            KLogger.error(e);
            return false;
        }
    }

    public static String checkAvailability(String userId, String bookId, boolean doAdd) {
        try (MongoClient client = MongoDbUtility.getConnection()) {
            String collectionName = "book";
            Document book = MongoDbUtility.getOneDocumentByFilter(collectionName, client, new Document("bookId", bookId));
            String bookName = book.getString("bookName");
            if (book != null && !book.isEmpty()) {
                if (doAdd) {
                    if (book.getInteger("quantity") > 0) {
                        book.remove("quantity");
                        Document updateDoc = new Document().append("$push", new Document("book-info", book));
                        MongoDbUtility.updateOne("user-book-req", client, new Document("_id", userId), updateDoc);
                        Document doc = new Document();
                        doc.put("$set", new Document("quantity", book.getInteger("quantity") - 1));
                        MongoDbUtility.updateOneById(collectionName, client, book.getString("_id"), doc);
                        KLogger.info(bookName + " book successfully added in your cart");
                        return bookName;
                    } else {
                        KLogger.warn(bookName + " book is not available");
                        return bookName + " book not available";
                    }
                } else {
                    Document removeDoc = new Document()
                            .append("$pull", new Document("book-info", new Document("bookId", book.getString("bookId"))));
                    MongoDbUtility.updateOne("user-book-req", client, new Document("_id", userId), removeDoc);
                    Document doc = new Document();
                    doc.put("$set", new Document("quantity", book.getInteger("quantity") + 1));
                    MongoDbUtility.updateOneById(collectionName, client, book.getString("_id"), doc);
                    KLogger.info(bookName + " book successfully removed from your cart");
                    return bookName;
                }
            }
        } catch (Exception e) {
            KLogger.error(e);
        }
        return null;
    }

}
