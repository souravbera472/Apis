package com.myproject.search;

import com.myproject.logger.KLogger;
import com.myproject.utill.Utillity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class searchUtility {
    private static String serverUrl;
    static {
        if(Utillity.getWorkbenchValue("USE_SOLR",false)) {
            String protocol = "http://";
            String  sever = Utillity.getWorkbenchValue("SERVER_IP","")+":";
            String port = Utillity.getWorkbenchValue("SOLR_PORT","8983");
            serverUrl = protocol+sever+port+"/solr";
        }
    }
    public static String getServerUrl(){
        return serverUrl;
    }
    public static String getOrCreateCollection(String collectionName) throws IOException {
       // /admin/collections?action=LIST&wt=json
        String query = "/admin/collections?action=LIST&wt=json";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(serverUrl+query);
        HttpResponse response = client.execute(httpGet);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(outputStream);
        String responseString = new String(outputStream.toByteArray());
        JSONObject responseObject = new JSONObject(responseString);
        JSONArray list = responseObject.getJSONArray("collections");
        for(int i=0;i<list.length();i++){
            if(list.get(i).equals(collectionName)){
                KLogger.info("Collection already exits: "+collectionName);
                return collectionName;
            }
        }
        KLogger.info(responseObject.toString());
        return null;
    }
    public static void main(String[] args) throws IOException {
      String collection =  getOrCreateCollection("Demo");
      KLogger.info(collection);
    }
}
