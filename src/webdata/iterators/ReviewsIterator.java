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

    @Override
    public ProductReview next() {

        try {

            String productId = this.curLine;
            String userId = this.inputFile.readLine();
            String profileName = this.inputFile.readLine();
            String helpfulness = this.inputFile.readLine();
            String score = this.inputFile.readLine();
            String time = this.inputFile.readLine();
            String summary = this.inputFile.readLine();
            String text = this.inputFile.readLine();

            this.reviewIdCounter++;
            while((this.curLine = this.inputFile.readLine()) != null && this.curLine.equals(""));
            var prodReview =  new ProductReview(this.reviewIdCounter, productId, userId, profileName, helpfulness, score,
                    time, summary, text);
            return prodReview;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
