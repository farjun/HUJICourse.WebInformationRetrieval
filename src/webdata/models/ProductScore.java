package webdata.models;

import java.util.Enumeration;
import java.util.Objects;
import java.util.PriorityQueue;

public class ProductScore implements Comparable {

    private final double lambda = 0.5;
    public final String productId;
    public final int  reviewId, score, helpfulnessNum, helpfulnessDenom;

    public ProductScore(String productId){
        this.productId = productId;
        reviewId = score = helpfulnessNum = helpfulnessDenom = -1;
    }

    public ProductScore(String productId, int reviewId, int score, int helpfulnessNum, int helpfulnessDenum){
        this.productId = productId;
        this.reviewId = reviewId;
        this.score = score;
        this.helpfulnessNum = helpfulnessNum;
        this.helpfulnessDenom = (helpfulnessDenum!=0)?helpfulnessDenum:1;
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
    public int compareTo(Object o) {
        ProductScore other = (ProductScore)o;
        if(productId.equals(other.productId))
            return 0; // in case its the same productId
        double selfScore = lambda*score + (1-lambda)*(double)helpfulnessNum/(double)helpfulnessDenom;
        double otherScore = lambda*other.score + (1-lambda)*(double)other.helpfulnessNum/(double)other.helpfulnessDenom;
        if(selfScore > otherScore)
            return 1;
        else if (selfScore < otherScore)
            return -1;
        return 0;
    }

}