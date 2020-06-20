package webdata.models;

import java.util.Enumeration;
import java.util.PriorityQueue;

public class ReviewScore implements Comparable{
    private final int review;
    private final double score;

    public ReviewScore(int review, double score){
        this.review = review;
        this.score = score;
    }

    @Override
    public int compareTo(Object o) {
        ReviewScore obj = (ReviewScore)o;
        return Double.compare(obj.score, this.score); // order is reversed to sort from big to small
    }

    public static ReviewScoresIterator getIterator(PriorityQueue<ReviewScore> reviewScores, int k){
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
            return this.reviewScores.size() > 0 && leftOfK > 0;
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
