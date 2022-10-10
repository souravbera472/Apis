package com.myproject.mongodb;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.myproject.logger.KLogger;
import org.bson.Document;
import com.myproject.utill.Utillity;

import java.util.ArrayList;
import java.util.List;


public class MongoDbUtility {
    private static final String mongoStr = "mongodb://" + getMongoUserPass() + getIpAndPort() + "/?authSource=" + getDb() + "&authMechanism=SCRAM-SHA-256";
    private static final String mongoDatabase = getDb();
    private static final String authDb = Utillity.getWorkbenchValue("AUTH_DB", "MyDatabase");

    private static String getMongoUserPass() {
        String user = Utillity.getWorkbenchValue("MONGO_USERNAME", "sourav");
        return user + ":" + "sourav@";
    }

    private static String getIpAndPort() {
        try {
            String ip = Utillity.getWorkbenchValue("SERVER_IP", "127.0.0.1");
            String port = Utillity.getWorkbenchValue("MONGO_PORT", "6000");
            return ip + ":" + port;
        } catch (Exception e) {
            KLogger.error(e);
            return null;
        }
    }

    private static String getDb() {
        return Utillity.stringIsNonEmpty(authDb) ? authDb : "MyDatabase";
    }

    public static MongoClient getConnection() {
        return MongoClients.create(mongoStr);
    }

    private static MongoCollection<Document> getMongoCollection(String collectionName, MongoClient client) {
        MongoDatabase database = client.getDatabase(mongoDatabase);
        return database.getCollection(collectionName);
    }

    public static long getCountWithFilter(String collectionName, MongoClient client, Document filter) {
        MongoCollection<Document> document = getMongoCollection(collectionName, client);
        return document.countDocuments(filter);
    }

    public static long getCountWithOutFilter(String collectionName, MongoClient client) {
        MongoCollection<Document> document = getMongoCollection(collectionName, client);
        return document.countDocuments();
    }

    public static long getCount(String collectionName, MongoClient client, Document filter) {
        return getCountWithFilter(collectionName, client, filter);
    }

    public static FindIterable<Document> getAllResult(String collectionName, MongoClient client) {
        MongoCollection<Document> documents = getMongoCollection(collectionName, client);
        return documents.find();
    }

    public static FindIterable<Document> getPageQueryResult(String collectionName, MongoClient client, Document filter,
                                                            String sortBy, String sortOrder, int limit, int offset) {
        MongoCollection<Document> documents = getMongoCollection(collectionName, client);
        limit = Utillity.getOrDefault(limit, 10);
        offset = Utillity.getOrDefault(offset, 0);
        return documents.find(filter).limit(limit).skip(offset).sort(new Document(sortBy, sortOrder.equals("asc")?1:-1));
    }

    public static FindIterable<Document> getDocumentByFilter(String collectionName, MongoClient client, Document filter) {
        MongoCollection<Document> documents = getMongoCollection(collectionName, client);
        return documents.find(filter);
    }

    public static Document getDocumentByID(String collectionName, MongoClient client, String id) {
        MongoCollection<Document> documents = getMongoCollection(collectionName, client);
        FindIterable<Document> iterable = documents.find(new Document("_id", id));
        Document result = new Document();
        for (Document document : iterable) {
            result = document;
        }
        return result;
    }

    public static Document getOneDocumentByFilter(String collectionName, MongoClient client, Document filter) {
        MongoCollection<Document> documents = getMongoCollection(collectionName, client);
        FindIterable<Document> iterable = documents.find(filter);
        Document result = new Document();
        for (Document document : iterable) {
            result = document;
        }
        return result;
    }

    public static void insertOneDocument(String collectionName, MongoClient client, Document doc) {
        MongoCollection<Document> document = getMongoCollection(collectionName, client);
        if (doc != null && !doc.isEmpty()) {
            document.insertOne(doc);
            KLogger.info("One documents insert successfully: " + doc);
        } else {
            KLogger.info("Document is empty or null: " + doc);
        }
    }

    public static void insertMany(String collectionName, MongoClient client, List<Document> list) {
        MongoCollection<Document> collection = getMongoCollection(collectionName, client);
        if (list != null && !list.isEmpty()) {
            collection.insertMany(list);
            KLogger.info("Data inserted successfully");
        }
    }

    public static void updateOne(String collectionName, MongoClient client, Document filter, Document update) {
        MongoCollection<Document> collection = getMongoCollection(collectionName, client);
        collection.updateOne(filter, update);

    }

    public static void updateOneById(String collectionName, MongoClient client, String id, Document update) {
        MongoCollection<Document> collection = getMongoCollection(collectionName, client);
        collection.updateOne(new Document("_id", id), update);
    }

    public static void deleteById(String collectionName, MongoClient client, String id) {
        MongoCollection<Document> collection = getMongoCollection(collectionName, client);
        DeleteResult deleteOne = collection.deleteOne(new Document("_id", id));
        KLogger.info(deleteOne.toString());

    }

    public static void deleteMany(String collectionName, MongoClient client, Document filter) {
        MongoCollection<Document> collection = getMongoCollection(collectionName, client);
        DeleteResult deleteResult = collection.deleteMany(filter);
        KLogger.info(deleteResult.getDeletedCount());
    }

}
