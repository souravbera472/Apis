package com.myproject.utill;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.myproject.logger.KLogger;
import org.bson.Document;

import java.io.File;
import java.io.IOException;

public class IpConfig {
    public static void IpBindForMongo() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        // Parse the YAML file
        ObjectNode root = (ObjectNode) mapper.readTree(new File("bin/MongoDB/Server/5.0/bin/mongod.cfg"));


       // Update the value
        String ip = Utillity.getWorkbenchValue("SERVER_IP","localhost");
        String port = Utillity.getWorkbenchValue("MONGO_PORT","6000");
        String bindIp = "localhost, "+ ip;
        root.putPOJO("net",new Document("bindIp", bindIp).append("port", port));

       // Write changes to the YAML file
        mapper.writer().writeValue(new File("bin/MongoDB/Server/5.0/bin/mongod.cfg"), root);
        KLogger.info("Data successfully write in mongo.cfg file");
    }
}
