package webdata;

import webdata.models.Query;
import webdata.models.ReviewScore;

import java.util.*;

public class ReviewSearch {
    private IndexReader iReader;

    /**
     * Constructor
     */
    public ReviewSearch(IndexReader iReader) {
        this.iReader = iReader;
    }


    private double computeScore(HashMap<String, Double> queryScore, HashMap<String, Double> documnetScore){
        double score = 0;
        for(String term : queryScore.keySet()){
            score += queryScore.get(term)*documnetScore.getOrDefault(term, 0.0);
        }
        return score;
    }

    /* computes the LM query score for a review */
    private double computeScoreLM(HashMap<String, Integer> corpusFreq, HashMap<String, Double> revFreq,
                                  double lambda){
        double score = 1;
        for(String term : revFreq.keySet()){
            double globalFreq = (double)corpusFreq.getOrDefault(term, 0);
            double inRevFreq = revFreq.getOrDefault(term, 0.0);
            score *= lambda*revFreq.getOrDefault(term, 0.0) + (1-lambda)*(double)corpusFreq.getOrDefault(term, 0);
        }
        return score;
    }



    /**
     * Returns a list of the id-s of the k most highly ranked reviews for the
     * given query, using the vector space ranking function lnn.ltc (using the
     * SMART notation)
     * The list should be sorted by the ranking
     */
    public Enumeration<Integer> vectorSpaceSearch(Enumeration<String> query, int k) {
        Query queryObj = new Query(query);
        PriorityQueue<ReviewScore> results = new PriorityQueue<>();
        HashMap<String, Double> LTCscore = queryObj.getLTCScore(this.iReader, true);
        HashMap<Integer, HashMap<String, Double>> reviewIdToFreqMapForQueryTokens = queryObj.getReviewIdToFreqMapForQueryTokens(iReader, true);
        for( int reviewId : reviewIdToFreqMapForQueryTokens.keySet()){
            double reviewScore = computeScore(LTCscore, reviewIdToFreqMapForQueryTokens.get(reviewId));
            ReviewScore rs = new ReviewScore(reviewId, reviewScore);
            results.add(rs);
        }

        return  ReviewScore.getIterator(results, k);
    }

    /**
     * Returns a list of the id-s of the k most highly ranked reviews for the
     * given query, using the language model ranking function, smoothed using a
     * mixture model with the given value of lambda
     * The list should be sorted by the ranking
     */
    public Enumeration<Integer> languageModelSearch(Enumeration<String> query,
                                                    double lambda, int k) {
        Query queryObj = new Query(query);
        PriorityQueue<ReviewScore> results = new PriorityQueue<>();
        HashMap<String, Integer> corpusFreq = queryObj.generateCorpusTermFrequency(this.iReader);
        HashMap<Integer, HashMap<String, Double>> reviewFreqs = queryObj.getReviewIdToFreqMapForQueryTokens(iReader, false);
        for( int reviewId : reviewFreqs.keySet()){
            double reviewScore = computeScoreLM(corpusFreq, reviewFreqs.get(reviewId), lambda); ////
            ReviewScore rs = new ReviewScore(reviewId, reviewScore);
            results.add(rs);
        }

        return  ReviewScore.getIterator(results, k);
    }

    /**
     * Returns a list of the id-s of the k most highly ranked productIds for the
     * given query using a function of your choice
     * The list should be sorted by the ranking
     */
    public Collection<String> productSearch(Enumeration<String> query, int k) {
        return null;
    }



}
