package webdata.models;

import webdata.IndexReader;

import java.util.Enumeration;
import java.util.HashMap;

public class Query {

    private Enumeration<String> query;
    public HashMap<String, Integer> queryTermFreq;

    public Query(Enumeration<String> query){
        this.query = query;
        this.queryTermFreq = generateQueryTermFrequency();
    }

    public HashMap<String, Integer> generateQueryTermFrequency(){
        HashMap<String, Integer> termMap = new HashMap<>();
        while (query.hasMoreElements()){
            String term = query.nextElement();
            if(termMap.containsKey(term)){
                termMap.put(term, termMap.get(term) + 1);
            }
            else {
                termMap.put(term, 1);
            }
        }
        return termMap;
    }


    public HashMap<String, Integer> generateCorpusTermFrequency(IndexReader iReader){
        HashMap<String, Integer> termMap = new HashMap<>();
        for (String term: queryTermFreq.keySet()) {
            termMap.put(term,iReader.getTokenFrequency(term));
        }
        return termMap;
    }

    /**
     * returns a map of the form:
     * { reviewId1 : { term1 : freq1, term2 : freq2, }, reviewId2 : { term2 : freq2, term3 : freq3, } ... }
     * where term1, term2, term3 are all in the query
     * @param iReader
     */
    public HashMap<Integer, HashMap<String, Double>> getReviewIdToFreqMapForQueryTokens(IndexReader iReader, boolean logFreq){
        HashMap<Integer, HashMap<String, Double>> reviewIdToFreqMap = new HashMap<>();
        for (String term: queryTermFreq.keySet()) {
            Enumeration<Integer> reviewsWithToken = iReader.getReviewsWithToken(term);
            while (reviewsWithToken.hasMoreElements()){
                int reviewId = reviewsWithToken.nextElement();
                int tokenFreq = reviewsWithToken.nextElement();
                if(reviewIdToFreqMap.containsKey(reviewId)){
                    if(logFreq)
                        reviewIdToFreqMap.get(reviewId).put(term, 1 + Math.log(tokenFreq));
                    else
                        reviewIdToFreqMap.get(reviewId).put(term, (double)tokenFreq);
                }
                else {
                    HashMap<String, Double> termToScoreMap = new HashMap<>();
                    if(logFreq)
                        termToScoreMap.put(term, 1 + Math.log(tokenFreq));
                    else
                        termToScoreMap.put(term, (double) tokenFreq);

                    reviewIdToFreqMap.put(reviewId, termToScoreMap);
                }
            }
        }
        return reviewIdToFreqMap;
    }

    public HashMap<String, Double> getLTCScore(IndexReader iReader, boolean normalize){
        HashMap<String, Integer> stringIntegerHashMap = generateCorpusTermFrequency(iReader);
        HashMap<String, Double> termMap =  new HashMap<>();
        int N = iReader.getTokenSizeOfReviews();
        for (String term: stringIntegerHashMap.keySet()) {
            int freq = stringIntegerHashMap.get(term);
            if(freq == 0){
                termMap.put(term, 0.0);

            }else {
                termMap.put(term, (1 + Math.log(freq)) * Math.log(N / freq));
            }
        }

        if(normalize){
            double ltcNormSum = 0;
            for(double value : termMap.values()){
                ltcNormSum+= value*value;
            }

            for(String term : termMap.keySet()){
                termMap.put(term, termMap.get(term)/ ltcNormSum);
            }
        }

        return termMap;
    }

}
