package com.myproject.SpringApi.user_book;

import com.myproject.logger.KLogger;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class ApproveBookByAdmin {

    @GetMapping("/approve-data")
    public Document getApproveData(@RequestParam Map<String, String> mp){
        String filter = mp.getOrDefault("filter","");
       Document document = ApproveBookUtility.getAllReqBook(filter);
        if(document!=null){
            return document;
        }
        return new Document("user-message","No data available");
    }

    /**
     *
     * @param approveId
     * @param data
     * @return
     */
    @PostMapping("approve/{approveId}/books")
    public Document postApproveData(@PathVariable String approveId,@RequestBody Map<String,Object> data){
        try{
            List<Map<String,String>> bookInfo = (List<Map<String, String>>) data.get("bookInfo");
            KLogger.info("Book info: "+bookInfo);
            if(bookInfo.size()==0)
                return new Document("message","No data available");
            String approveById = data.getOrDefault("approveById","").toString();
            String approveBy = data.getOrDefault("approveBy","").toString();
            boolean check = ApproveBookUtility.postBooksData(approveId, approveBy, approveById, bookInfo);

            if(check){
                return new Document("user-message",bookInfo.size()+(bookInfo.size()==1?" book":" books")
                        +" added successfully in your profile");
            }
            else {
                return new Document("user-message","book/books can't be added due to internal error");
            }
        }catch (Exception e){
            KLogger.error(e);
        }
        return null;
    }
}