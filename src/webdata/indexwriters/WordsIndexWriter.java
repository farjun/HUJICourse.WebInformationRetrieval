package webdata.indexwriters;

import webdata.models.ProductReview;

import java.io.BufferedWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class WordsIndexWriter extends IndexWriter {
    // will host map of sort { token : {reviewId:count,...}}
    private HashMap<String, HashMap<Long, Integer>> tokenFreq;
    // will host map of sort { token : globalCounter }
    private HashMap<String, Integer> tokenGlobalFreq;
    private ByteBuffer entryBuffer = ByteBuffer.allocate(

    );

    public WordsIndexWriter(BufferedWriter outputFile) {
        super(outputFile);
        this.tokenFreq = new HashMap<String, HashMap<Long, Integer>>();
        this.tokenGlobalFreq = new HashMap<String, Integer>();
    }

    public WordsIndexWriter(String filePath) {
        super(filePath);
        this.tokenFreq = new HashMap<String, HashMap<Long, Integer>>();
        this.tokenGlobalFreq = new HashMap<String, Integer>();
    }

    @Override
    public void write(ProductReview review) {
        //TODO: update global word stats information with review tokenStats
        var tokenStats = review.getTokenStats();
        for(var entry: tokenStats.entrySet()){
            var token = entry.getKey();
            var countInReview = entry.getValue();
            var reviewId = review.getId();
            if (!this.tokenFreq.containsKey(token)){
                var tokenReviewFreqMap =  new HashMap<Long, Integer>();
                tokenReviewFreqMap.put(reviewId, countInReview);
                this.tokenFreq.put(token, tokenReviewFreqMap);
            }
            else {
                this.tokenFreq.get(token).put(reviewId, countInReview);
            }
            var tokenGlobFreq = this.tokenGlobalFreq.getOrDefault(token, 0);
            this.tokenGlobalFreq.put(token, tokenGlobFreq+countInReview);
        }
        System.out.println("Global Freq Map:" + this.tokenGlobalFreq.toString());
    }
}
