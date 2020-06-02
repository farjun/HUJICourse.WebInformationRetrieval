package webdata.indexes;

import webdata.models.ProductReview;
import webdata.models.TokenFreqEnumeration;
import java.util.*;


public class WordsIndex extends Index {
    // will host map of sort { token : {reviewId:count,...}} // TreeMap<String, TreeMap<Long, Integer>>
    public final TreeMap<String, TreeMap<Integer, Integer>> tokenFreq;
    // will host map of sort { token : globalCounter }
    public final TreeMap<String, Integer> tokenGlobalFreq;

    private StringBuilder leftOverFromLastBlock;
    public final int NUM_OF_ENTRIES_IN_BLOCK = 50; //TODO : increase
    private int mockedCounter;

    public WordsIndex(){
        this.tokenFreq = new TreeMap<>();
        this.tokenGlobalFreq = new TreeMap<>();
        this.leftOverFromLastBlock = new StringBuilder();
        this.mockedCounter = 0;
    }

    public WordsIndex(String serializedWordEntry){
        // parse entries "phone|4353|{ 56 : 100 , 79 : 23 };"
        this();
        this.loadData(serializedWordEntry);
    }


    public int getBlockNum(String token){
        return mockedCounter++; //TODO
    }

    @Override
    public void loadData(String serializedWordEntries){
        // parse entries "phone|4353|{56:100,79:23};"
        this.tokenFreq.clear();
        this.tokenGlobalFreq.clear();
        String[] rows = serializedWordEntries.split(";"); // Assume ";" is the terminal
        for (String row : rows) {
            if(row.length()<=0) continue;
            if(leftOverFromLastBlock.length()!=0){
                row = leftOverFromLastBlock.toString().concat(row);
                leftOverFromLastBlock.setLength(0);//empty
            }
            if(row.lastIndexOf('}') == -1){
                leftOverFromLastBlock.append(row);
                continue;
            }
            String[] cols = row.split("\\|");
            var key = cols[0];
            var globFreq = cols[1];
            var freqJSON = cols[2];
            this.tokenGlobalFreq.put(key, Integer.parseInt(globFreq));
            this.tokenFreq.put(key, this.loadJSON(freqJSON));
        }
    }

    public Enumeration<Integer> getReviewsWithToken(String token){
        if(!this.tokenGlobalFreq.containsKey(token)) return Collections.enumeration(Collections.emptyList());
        var tokenFreqMap = this.tokenFreq.get(token);
        return new TokenFreqEnumeration(tokenFreqMap);
    }

    public void insert(ProductReview review) {
        var tokenStats = review.getTokenStats();
        for(var entry: tokenStats.entrySet()){
            var token = entry.getKey();
            var countInReview = entry.getValue();
            var reviewId = review.getId();
            if (!this.tokenFreq.containsKey(token)){
                var tokenReviewFreqMap =  new TreeMap<Integer, Integer>();
                tokenReviewFreqMap.put(reviewId, countInReview);
                this.tokenFreq.put(token, tokenReviewFreqMap);
            }
            else {
                this.tokenFreq.get(token).put(reviewId, countInReview);
            }
            var tokenGlobFreq = this.tokenGlobalFreq.getOrDefault(token, 0);
            this.tokenGlobalFreq.put(token, tokenGlobFreq+countInReview);
        }
//        System.out.println("Global Freq Map:" + this.tokenGlobalFreq.toString());
    }

    /* parses JSON from "{a:b,c:d}" to ["a:b","c:d"] */
    private String[] jsonLoadAux(String strJSON){
        if (strJSON.length() < 2)
            return null;
        StringBuilder jsonItems = new StringBuilder(strJSON);
        jsonItems.deleteCharAt(strJSON.length() - 1).deleteCharAt(0); // drop {}
        return jsonItems.toString().split(",");
    }

    private TreeMap<Integer, Integer> loadJSON(String strJSON) {
        var result = new TreeMap<Integer, Integer>();
        var jsonItemsClean = this.jsonLoadAux(strJSON);
        if(jsonItemsClean == null) return result;
        for (var keyValStr : jsonItemsClean) {
            String[] keyValPair = keyValStr.split(":");
            Integer key = Integer.parseInt(keyValPair[0]);
            Integer value = Integer.parseInt(keyValPair[1]);
            result.put(key, value);
        }
        return result;
    }


    @Override
    public String toString() {
        StringBuilder serialized = new StringBuilder();
        for(var entry: this.tokenGlobalFreq.entrySet()){
            var token = entry.getKey();
            var globalFreq = entry.getValue();
            var freqMap = this.tokenFreq.get(token);
            var serializedEntry = token + "|";
            serializedEntry += globalFreq.toString() + "|";
            // replace "=" coming from toString of TreeMap with ":" to make it JSON-like
            serializedEntry += freqMap.toString().replace("=",":").replace(" ", "");
            serializedEntry += ";"; // terminate line
            serialized.append(serializedEntry);
        }
        return serialized.toString();
    }


    public String[] toStringBlocks(boolean lastBatch) {
        StringBuilder serialized = new StringBuilder();
        int numOfBlocks = (this.tokenGlobalFreq.size() / NUM_OF_ENTRIES_IN_BLOCK);
        var floored = Math.floor((double)this.tokenGlobalFreq.size() / NUM_OF_ENTRIES_IN_BLOCK);
        if(lastBatch &&  floored < (double)this.tokenGlobalFreq.size() / NUM_OF_ENTRIES_IN_BLOCK )
            numOfBlocks++;

        String[] wordsBlocks = new String[numOfBlocks];
        int curNumOfEntries = 0;
        int curBlock = 0;

        for(var entry: this.tokenGlobalFreq.entrySet()){
            String token = entry.getKey();
            Integer globalFreq = entry.getValue();
            TreeMap freqMap = this.tokenFreq.get(token);
            StringBuilder serializedEntry = new StringBuilder();
            serializedEntry.append(token).append('|');
            serializedEntry.append(globalFreq.toString()).append('|');
            // replace "=" coming from toString of TreeMap with ":" to make it JSON-like
            serializedEntry.append(freqMap.toString().
                    replace("=",":").
                    replace(" ", ""));
            serializedEntry.append(';'); // terminate line
            serialized.append(serializedEntry);
            curNumOfEntries++;
            if( curNumOfEntries >= NUM_OF_ENTRIES_IN_BLOCK){
                wordsBlocks[curBlock] = serialized.toString();
                serialized = new StringBuilder();
                curNumOfEntries = 0;
                curBlock++;
            }
        }

        if(lastBatch) {
            String lastBlock = serialized.toString();
            if (!lastBlock.equals("")) {
                wordsBlocks[curBlock] = lastBlock;
            }
        }else{
            this.loadData(serialized.toString());
        }

        return wordsBlocks;
    }





}


