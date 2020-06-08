package webdata.iterators;

import webdata.models.ProductReview;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class ReviewsIterator implements Iterator<ProductReview> {
    private BufferedReader inputFile;
    private String curLine;
    int reviewIdCounter;

    private static int NUM_OF_FIELDS_IN_REVIEW = 8;
    private boolean isFirstLine;

    public ReviewsIterator(String inputFile){
        try {
            this.inputFile = new BufferedReader(new FileReader(inputFile));
            this.curLine = this.inputFile.readLine();
            this.reviewIdCounter = 0;
            this.isFirstLine = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean hasNext() {
        return this.curLine != null;
    }


//    product/productId: B001E4KFG0
//    review/userId: A3SGXH7AUHU8GW
//    review/profileName: delmartian
//    review/helpfulness: 1/1
//    review/score: 5.0
//    review/time: 1303862400
//    review/summary: Good Quality Dog Food
//    review/text: I have bought several of the Vitality canned dog food products and have found them all to be of good quality. The product looks more like a stew than a processed meat and it smells better. My Labrador is finicky and she appreciates this product better than  most.

//    product/productId: B00813GRG4
//    public String readCurrAttr(String nextAttrName) throws IOException {
//        StringBuilder str = new StringBuilder();
//        str.append(this.curLine);
//        while((this.curLine = this.inputFile.readLine()) != null ){
//            if(this.curLine.startsWith(nextAttrName)) {
//                break;
//            }
//            str.append(this.curLine);
//            str.append("\n");
//        }
//        return str.toString();
//    }

    private boolean ofInterest(String field) {
        return field!=null && (field.equals("product/productId") || field.equals("review/userId") ||
                field.equals("review/profileName") || field.equals("review/helpfulness") ||
                field.equals("review/score") || field.equals("review/time") ||
                field.equals("review/summary") || field.equals("review/text"));
    }

    public HashMap<String, String> readAttrs() throws IOException {
        HashMap<String, String> fields = new HashMap<>();
        StringBuilder content = new StringBuilder();
        String[] fieldNameContent = this.curLine.split(":",2);
        String fieldName = null;
        if(this.isFirstLine&&fieldNameContent.length>1){
            this.isFirstLine = false;
            fieldName = fieldNameContent[0];
            content.append(fieldNameContent[1].substring(1));
        }
        while((this.curLine = this.inputFile.readLine()) != null ){
            fieldNameContent = this.curLine.split(":",2); // e.g review/text: The variety packs taste great!<br /><br />I have them e
            if(fieldNameContent.length>1){
                if(content.length()>0 && fieldName!=null){
                    if(ofInterest(fieldName)) {
                        fields.put(fieldName, content.toString());
                    }
                    if(fields.size() == NUM_OF_FIELDS_IN_REVIEW) {
                        this.isFirstLine = true;
                        return fields;
                    }
                    content = new StringBuilder();
                }
                fieldName = fieldNameContent[0];
                if(fieldNameContent[1].length()>1)
                    content.append(fieldNameContent[1].substring(1)); // the subS for erasing ' ' at the start
            }
            else{
                content.append(this.curLine);
                content.append("\n");
            }
        }
        if(fieldName!=null){
            if(content.length()>0)
                fields.put(fieldName, content.toString());
            else fields.put(fieldName, "");
        }
        return fields;
    }

    @Override
    public ProductReview next() {

        try {
            HashMap<String,String> review = this.readAttrs();
            if(review.size()==NUM_OF_FIELDS_IN_REVIEW){
                String productId = review.get("product/productId");
                String userId = review.get("review/userId");
                String profileName = review.get("review/profileName");
                String helpfulness = review.get("review/helpfulness");
                String score = review.get("review/score");
                String time = review.get("review/time");
                String summary = review.get("review/summary");
                String text = review.get("review/text");
                this.reviewIdCounter++;
                return new ProductReview(this.reviewIdCounter, productId, userId, profileName, helpfulness, score,
                        time, summary, text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
