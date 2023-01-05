package com.myproject.SpringApi.user_control;


import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.myproject.logger.KLogger;
import com.myproject.mongodb.MongoDbUtility;
import com.myproject.utill.Utillity;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UserControlUtility {
    public static Document getAllUsers(Map<String, String> params) {
        Document result = new Document();
        try (MongoClient client = MongoDbUtility.getConnection()) {
            String filter = params.getOrDefault("filter", "");
            int limit = Integer.parseInt(params.getOrDefault("limit", "10"));
            int offset = Integer.parseInt(params.getOrDefault("offset", "0"));
            String sortBy = params.getOrDefault("sortBy", "userName");
            String sortOrder = params.getOrDefault("sortOrder", "asc");
            Document filterDoc = new Document();
            if (Utillity.stringIsNonEmpty(filter)) {
                Document regex = new Document("$regex", ".*" + filter + ".*").append("$options", "si");
                Document fName = new Document("fName", regex);
                Document lName = new Document("lName", regex);
                Document userName = new Document("userName", regex);
                filterDoc.append("$or", Arrays.asList(fName, lName, userName));
            }
            String collectionName = "user-collection";
            FindIterable<Document> pageQueryResult = MongoDbUtility.getPageQueryResult(collectionName, client, filterDoc, sortBy, sortOrder, limit, offset);
            Document meta = new Document();
            long count = MongoDbUtility.getCount(collectionName, client, filterDoc);
            meta.append("total", count).append("limit", limit)
                    .append("offset", offset)
                    .append("sortBy", sortBy)
                    .append("sortOrder", sortOrder);
            List<Document> arrayData = new ArrayList<>();
            for (Document item : pageQueryResult) {
                item.remove("password");
                item.put("name",item.getString("fName")+" "+item.getString("lName"));
                item.remove("fName");
                item.remove("lName");
                arrayData.add(item);
            }
            result.put("all-user", arrayData);
            result.put("_meta", meta);
            return result;
        } catch (Exception e) {
            KLogger.error(e);
        }
        return null;
    }
}
