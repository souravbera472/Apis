package com.myproject.SpringApi.book;

import com.mongodb.client.MongoClient;
import com.myproject.logger.KLogger;
import com.myproject.mongodb.MongoDbUtility;
import com.myproject.utill.Utillity;
import org.bson.Document;

public class BookUtility {
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

    public static String checkAvailability(String bookId, boolean doAdd) {
        try (MongoClient client = MongoDbUtility.getConnection()) {
            String collectionName = "book";
            Document book = MongoDbUtility.getOneDocumentByFilter(collectionName, client, new Document("bookId", bookId));
            String bookName = book.getString("bookName");
            if (book != null && !book.isEmpty()) {
                if (doAdd) {
                    if (book.getInteger("quantity") > 0) {
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
