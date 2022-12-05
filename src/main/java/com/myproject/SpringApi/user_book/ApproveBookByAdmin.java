package com.myproject.SpringApi.user_book;

import com.myproject.logger.KLogger;
import com.myproject.utill.Utillity;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class ApproveBookByAdmin {

    @GetMapping("/approve-data")
    public ResponseEntity<Document> getApproveData(@RequestParam Map<String, String> mp){
        String filter = mp.getOrDefault("filter","");
       Document document = ApproveBookUtility.getAllReqBook(filter);
        if(document!=null){
            return ResponseEntity.status(HttpStatus.OK).body(document);
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Document("user-message","Server not responding due some internal error"));
    }


    @GetMapping("/req-renewal-books")
    //@ResponseStatus(code = HttpStatus.OK, reason = "OK")
    public ResponseEntity<Document> getAllRenewalBooks(@RequestParam Map<String, String> reqMap){
        String filter = reqMap.getOrDefault("filter","");
        String type = reqMap.get("type");
        Document document = ApproveBookUtility.getAllRenewalBooks(filter,type);
        if(Utillity.docIsNonEmpty(document))
            return ResponseEntity.status(HttpStatus.OK).body(document);

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Document("user-message","Server not responding due some internal error"));
    }

    /**
     *
     * @param approveId
     * @param data
     * {
     *     "approveById":"752da97d-51d7-4441-a7ff-c02b6fa33c7b",
     *     "approveBy":"Sourav Bera",
     *     "bookInfo":[
     *         {
     *             "id" : "b293026f-fc39-48a0-925e-ac308aeedba3",
     *             "bookId" : "cp1222553",
     *             "bookName" : "Cpp Advance",
     *             "bookAuthor" : "John brown"
     *         },
     *         {
     *             "id" : "d71385e1-7f34-4ff5-ab6d-11f84ef3b522",
     *             "bookId" : "cp12226544",
     *             "bookName" : "Cpp Basic",
     *             "bookAuthor" : "John brown"
     *         }
     *     ]
     * }
     *
     * @return
     */
    @PostMapping("approve/{approveId}/books")
    public Document postApproveData(@PathVariable String approveId,@RequestBody Map<String,Object> data){
        try{
            List<Map<String,String>> bookInfo = (List<Map<String, String>>) data.get("bookInfo");
            //KLogger.info("Book info: "+bookInfo);
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

    @PostMapping("/renewal/{id}/books")
    public ResponseEntity<Document> updateRenewalOrReturnBooks(@PathVariable String id,@RequestParam String type,@RequestBody Map<String,Object> reqMap){
        List<String>bookIds = (List<String>) reqMap.get("bookIds");
        String approveById = reqMap.getOrDefault("approveById","").toString();
        String approveBy = reqMap.getOrDefault("approveBy","").toString();
        boolean check = ApproveBookUtility.checkRenewalOrReturnBooks(id,approveById,approveBy,type,bookIds);
        if(check){
            return ResponseEntity.status(HttpStatus.CREATED).body((type.equalsIgnoreCase("renewal"))?
                    new Document("user-message","Books Renewal Successfully"): new Document("user-message","Books Returned Successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Document("user-message","Internal Error"));
    }

    @DeleteMapping("approve/{approveId}/books")
    public Document removeBooksFromUsers(@PathVariable String approveId,List<String> bookIds){

        return null;
    }


}