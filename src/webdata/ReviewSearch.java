package webdata;

import webdata.models.Query;
import webdata.models.ReviewScore;
import webdata.models.ProductScore;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;

public class ReviewSearch {
    private IndexReader iReader;

    /**
     * Constructor
     */
    public ReviewSearch(IndexReader iReader) {
        this.iReader = iReader;
    }


    private double computeScore(HashMap<String, Double> queryScore, HashMap<String, Double> documentScore){
        double score = 0;
        for(String term : queryScore.keySet()){
            score += queryScore.get(term)*documentScore.getOrDefault(term, 0.0);
        }
        return score;
    }


    /* computes the LM query score for a review */
    private double computeScoreLM(HashMap<String, Integer>  queryFreq, HashMap<String, Integer> corpusFreq, HashMap<String, Double> revFreq,
                                  double lambda, int corpusSize, int reviewSize){
        if(corpusSize<=0 || lambda < 0 || reviewSize <= 0)
            return 0;
        double score = 1;
        for(var term: queryFreq.keySet()){
            double globalFreq = (double)corpusFreq.getOrDefault(term, 0);
            double inRevFreq = revFreq.getOrDefault(term, 0.0);
            score *= Math.pow((lambda*inRevFreq / (double) reviewSize +
                    (1-lambda)*globalFreq / (double) corpusSize), queryFreq.get(term));
        }
        return score;
    }


    public Enumeration<Integer> languageModelSearchAux(Query queryObj,
                                                    double lambda, int k){
        PriorityQueue<ReviewScore> results = new PriorityQueue<>();
        HashMap<String, Integer> corpusFreq = queryObj.generateCorpusTermFrequency(this.iReader);
        HashMap<Integer, HashMap<String, Double>> reviewFreqs = queryObj.getReviewIdToFreqMapForQueryTokens(iReader, false);
        int corpusSize = iReader.getTokenSizeOfReviews();
        for( int reviewId : reviewFreqs.keySet()){
            double reviewScore = computeScoreLM(queryObj.getQueryTermFreq(), corpusFreq, reviewFreqs.get(reviewId), lambda, corpusSize,
                    iReader.getReviewLength(reviewId));
            ReviewScore rs = new ReviewScore(reviewId, reviewScore);
            results.add(rs);
        }

        return  ReviewScore.getIterator(results, k);
    }


    /**
     * Returns a list of the id-s of the k most highly ranked reviews for the
     * given query, using the vector space ranking function lnn.ltc (using the
     * SMART notation)
     * The list should be sorted by the ranking
     */
    public Enumeration<Integer> vectorSpaceSearch(Enumeration<String> query, int k) {
        if(k<=0) {
            return Collections.emptyEnumeration();
        }
        Query queryObj = new Query(query,false);
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
        if(k<=0) {
            return Collections.emptyEnumeration();
        }
        Query queryObj = new Query(query,false);
        return languageModelSearchAux(queryObj, lambda, k);
    }

    /**
     * Returns a list of the id-s of the k most highly ranked productIds for the
     * given query using a function of your choice
     * The list should be sorted by the ranking
     */
    public Collection<String> productSearch(Enumeration<String> query, int k) {
        PriorityQueue<ProductScore> sortedProducts = new PriorityQueue<>();
        List<String> productIds = new ArrayList<>();
        double lambda = 0.4;
        Query queryObj = new Query(query,true);
        Enumeration<Integer> bestRevs = languageModelSearchAux(queryObj,  lambda, -1);
        HashMap<String, ArrayList<Integer>> productsToReviewsMap = new HashMap<>();
        while(bestRevs.hasMoreElements() && productsToReviewsMap.size() < k) {
            int reviewId = bestRevs.nextElement();
            String productId = iReader.getProductId(reviewId);
            if(productsToReviewsMap.containsKey(productId)){
                productsToReviewsMap.get(iReader.getProductId(reviewId)).add(reviewId);
            }else {
                ArrayList<Integer> reviewIds = new ArrayList<>();
                reviewIds.add(reviewId);
                productsToReviewsMap.put(productId, reviewIds);
            }

        }

        for (String productId : productsToReviewsMap.keySet()) {
            ArrayList<Integer> reviewIds = productsToReviewsMap.get(productId);
            sortedProducts.add(new ProductScore(productId, iReader, reviewIds));
        }
        while(sortedProducts.size()>0){
            var prdId = sortedProducts.poll();
            if(prdId == null) break;
            productIds.add(prdId.productId);
        }
        return productIds;
    }



}
