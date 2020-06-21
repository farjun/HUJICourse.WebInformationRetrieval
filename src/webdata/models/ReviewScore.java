package webdata.models;

import java.util.Enumeration;
import java.util.PriorityQueue;

public class ReviewScore implements Comparable<ReviewScore>{
    private final int review;
    private final double score;

    public ReviewScore(int review, double score){
        this.review = review;
        this.score = score;
    }

    @Override
    public int compareTo(ReviewScore obj) {
        int res = Double.compare(obj.score, this.score); // order is reversed to sort from big to small
        if( res == 0){ // if equal score sort by id
            return Integer.compare(this.review, obj.review);
        }
        return res;
    }

    public static ReviewScoresIterator getIterator(PriorityQueue<ReviewScore> reviewScores, int k){
        // in case of k<0 return all
        return new ReviewScoresIterator(reviewScores, k);
    }

    public static class ReviewScoresIterator implements Enumeration<Integer>{
        private PriorityQueue<ReviewScore> reviewScores;
        private int leftOfK;

        public ReviewScoresIterator(PriorityQueue<ReviewScore> reviewScores, int k){
            this.reviewScores = reviewScores;
            this.leftOfK = k;
        }

        @Override
        public boolean hasMoreElements() {
            return this.reviewScores.size() > 0 && leftOfK != 0;
        }

        @Override
        public Integer nextElement() {
            ReviewScore rv = reviewScores.poll();
            if(rv == null){
                return null;
            }
            this.leftOfK--;
            return rv.review;
        }
    }
}
