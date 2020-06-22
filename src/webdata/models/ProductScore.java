package webdata.models;

import webdata.IndexReader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
import java.util.PriorityQueue;

public class ProductScore implements Comparable<ProductScore> {

    private final double lambda = 0.5;
    public final String productId;
    private final double score;

    public ProductScore(String productId, IndexReader iReader, ArrayList<Integer> reviewIds) {
        this.productId = productId;
        this.score = this.computeScore(iReader, reviewIds);
    }

    private double computeScore(IndexReader iReader, ArrayList<Integer> reviewIds){
        double tempScore = 0;
        for (int reviewId : reviewIds) {
            int score = iReader.getReviewScore(reviewId);
            int helpfulnessEnum = iReader.getReviewHelpfulnessNumerator(reviewId);
            int helpfulnessDenom = iReader.getReviewHelpfulnessDenominator(reviewId);
            tempScore += helpfulnessDenom == 0 ? score : score + 5 * ( helpfulnessEnum/ helpfulnessDenom );
        }

        return tempScore ;
    }


    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString(){
        return ""+productId+"";
    }

    @Override
    public boolean equals(Object obj) {
        var other = (ProductScore) obj;
        return this.productId.equals(other.productId);
    }

    @Override
    public int compareTo(ProductScore o) {
        return Double.compare(o.score, this.score);
//        ProductScore other = (ProductScore)o;
//        if(productId.equals(other.productId))
//            return 0; // in case its the same productId
//        // TODO what defined as place of "creativity" in ex3, should go here
//        double selfScore = lambda*score + (1-lambda)*(double)helpfulnessNum/(double)helpfulnessDenom;
//        double otherScore = lambda*other.score + (1-lambda)*(double)other.helpfulnessNum/(double)other.helpfulnessDenom;
//        if(selfScore > otherScore)
//            return 1;
//        else if (selfScore < otherScore)
//            return -1;
//        return 0;
    }

}