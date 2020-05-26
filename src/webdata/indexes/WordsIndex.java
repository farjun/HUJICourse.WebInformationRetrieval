package webdata.indexes;

import webdata.models.ProductReview;
import webdata.models.TokenFreqEnumeration;
import webdata.models.WordIndexTrie;

import java.util.*;


public class WordsIndex extends Index {
    public final WordIndexTrie trie;

    public WordsIndex(){
        this.trie = new WordIndexTrie();
    }

    public WordsIndex(String serializedWordEntry){
        // parse entries "phone|4353|{ 56 : 100 , 79 : 23 };"
        this();
        loadData(serializedWordEntry);
    }

    public void loadData(String rawIndex){
        String[] rows = rawIndex.split(";"); // Assume ";" is the terminal
        for (String row : rows) {
            String[] cols = row.split("\\|");
            var key = cols[0];
            var globFreq = cols[1];
            var freqJSON = cols[2];
            var terminalNode = this.trie.insert(key);
            terminalNode.setTokenGlobalFreq(Integer.parseInt(globFreq));
            terminalNode.setTokenFreq(this.loadJSON(freqJSON));
        }
    }

    public Enumeration<Integer> getReviewsWithToken(String token){
        if(!this.trie.contains(token)) return Collections.enumeration(Collections.emptyList());
        var terminalNode = this.trie.getTerminalNode(token);
        var tokenFreqMap = terminalNode.getTokenFreq();
        return new TokenFreqEnumeration(tokenFreqMap);
    }

    public void insert(ProductReview review) {
        this.trie.insert(review);
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
        trie.commit(); // fills buffer
        return trie.toString();
    }

}


