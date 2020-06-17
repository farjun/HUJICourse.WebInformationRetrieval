package webdata.models;

import webdata.IndexReader;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Queue;

public class Query {

    private Enumeration<String> query;

    public Query(Enumeration<String> query){
        this.query = query;
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

    public HashMap<String, Integer> generateDocumentTermFrequency(IndexReader iReader){
        HashMap<String, Integer> termMap = new HashMap<>();
        while (query.hasMoreElements()){
            String term = query.nextElement();
            termMap.put(term,iReader.getTokenFrequency(term));
        }
        return termMap;
    }

    public HashMap<String, Double> generateLTC(IndexReader iReader){
        HashMap<String, Integer> stringIntegerHashMap = generateDocumentTermFrequency(iReader);
        HashMap<String, Double> termMap =  new HashMap<>();
        int N = iReader.getTokenSizeOfReviews();
        for (String term: stringIntegerHashMap.keySet()) {
            int freq = stringIntegerHashMap.get(term);
            termMap.put(term,Math.log(freq)*Math.log(N/freq));
        }

        return termMap;
    }

}
