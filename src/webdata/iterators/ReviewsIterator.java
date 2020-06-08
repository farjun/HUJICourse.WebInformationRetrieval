package webdata.iterators;

import webdata.models.ProductReview;

import java.io.*;
import java.util.Iterator;

public class ReviewsIterator implements Iterator<ProductReview> {
    private BufferedReader inputFile;
    private String curLine;
    int reviewIdCounter;

    public ReviewsIterator(String inputFile){
        try {
            this.inputFile = new BufferedReader(new FileReader(inputFile));
            this.curLine = this.inputFile.readLine();
            this.reviewIdCounter = 0;
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
    public String readCurrAttr(String nextAttrName) throws IOException {
        StringBuilder str = new StringBuilder();
        str.append(this.curLine);
        while((this.curLine = this.inputFile.readLine()) != null ){
            if(this.curLine.startsWith(nextAttrName)) {
                break;
            }
            str.append(this.curLine);
            str.append("\n");
        }
        return str.toString();
    }

    public String readAttr() throws IOException {
        StringBuilder str = new StringBuilder();

        return null;
    }

    @Override
    public ProductReview next() {

        try {


            String productId = this.readCurrAttr("review/userId");
            String userId = this.readCurrAttr("review/profileName");
            String profileName = this.readCurrAttr("review/helpfulness");
            String helpfulness = this.readCurrAttr("review/score");
            String score = this.readCurrAttr("review/time");
            String time = this.readCurrAttr("review/summary");
            String summary = this.readCurrAttr("review/text");
            String text = this.readCurrAttr("product/productId");

            this.reviewIdCounter++;
//            while((this.curLine = this.inputFile.readLine()) != null && this.curLine.equals(""));
            var prodReview =  new ProductReview(this.reviewIdCounter, productId, userId, profileName, helpfulness, score,
                    time, summary, text);

            return prodReview;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
