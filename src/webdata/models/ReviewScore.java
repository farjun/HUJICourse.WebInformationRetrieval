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
        return Double.compare(this.score,obj.score);
    }

    public static ReviewScoresIterator getIterator(PriorityQueue<ReviewScore> reviewScores){
        return new ReviewScoresIterator(reviewScores);
    }

    public static class ReviewScoresIterator implements Enumeration<Integer>{
        private PriorityQueue<ReviewScore> reviewScores;

        public ReviewScoresIterator(PriorityQueue<ReviewScore> reviewScores){
            this.reviewScores = reviewScores;
        }

        @Override
        public boolean hasMoreElements() {
            return this.reviewScores.size() > 0;
        }

        @Override
        public Integer nextElement() {
            ReviewScore rv = reviewScores.poll();
            if(rv == null){
                return null;
            }
            return rv.review;
        }
    }
}
