package webdata.models;

import java.util.TreeMap;

public class WordIndexTrie {

    public WordTrieNode getRoot() {
        return root;
    }

    private WordTrieNode root;
    private int counter;
    private StringBuilder serialized;

    public int getGlobalFreqSum() {
        return globalFreqSum;
    }

    private int globalFreqSum;


    public WordIndexTrie(){
        this.root = new WordTrieNode();
        this.globalFreqSum = 0;
        this.counter = 0;
        this.serialized = new StringBuilder();
    }

    public WordTrieNode insert(String key) {
        int level;
        int length = key.length();
        int index;

        WordTrieNode crawler = root;

        for (level = 0; level < length; level++) {
            index = toIndex(key.charAt(level));
            if (crawler.children[index] == null)
                crawler.children[index] = new WordTrieNode();

            crawler = crawler.children[index];
        }

        // mark last node as leaf
        crawler.isEndOfWord = true;
        if (crawler.tokenFreq == null)
            crawler.tokenFreq = new TreeMap<>(); // creating maps only in terminating nodes
        if (crawler.tokenRank < 0) {
            crawler.tokenRank = counter;
            counter++;
        }
//        var rt = Runtime.getRuntime();
//        if(rt.totalMemory() > memoryThreshold)
        return crawler;
    }

    public void insert(ProductReview review){
        var tokenStats = review.getTokenStats();
        for(var entry: tokenStats.entrySet()){
            var token = entry.getKey();
            var countInReview = entry.getValue();
            var reviewId = review.getId();
            var tokenTerminalNode = insert(token);
            tokenTerminalNode.tokenFreq.put(reviewId, countInReview);
            tokenTerminalNode.tokenGlobalFreq += countInReview;
            globalFreqSum += countInReview;
        }
    }

    private char toChar(int index){
        int plusA =  index + 'a';
        if (plusA <= 'z') return (char)plusA;
        //otherwise its a number
        return (char) ('0' + index - WordTrieNode.ALPHABET_SIZE);
    }

    private int toIndex(char chr){
        if (chr <= 'z' &&  chr >= 'a')
            return (int) chr - 'a';
        //otherwise its a number
        return (chr - '0' + WordTrieNode.ALPHABET_SIZE);
    }


    public void commit(){
        commit(this.root,"");
    }

    private void commit(WordTrieNode node, String prefix)
    {
        if (node == null)
        {
            return;
        }
        if (node.isEndOfWord){
//            System.out.println("-------------------------------------");
//            System.out.println(prefix);
//            System.out.println(node.tokenRank);
//            System.out.println(node.tokenFreq);
//            System.out.println(node.tokenGlobalFreq);
            //serialize..
            this.serialized.append(node.serialize(prefix));
        }
        for (int i = 0; i < node.children.length;i++)
        {
            if(node.children[i] != null) {
                var chr = toChar(i);
                commit(node.children[i], prefix + chr);
            }
        }
    }


    @Override
    public String toString() {
        return this.serialized.toString();
    }

    public void flush(){
        for(int i=0;i<root.children.length;i++){
            // pruning children
            this.root.children[i] = null;
        }
        this.serialized = new StringBuilder();
        // explicitly trigger GC
        Runtime.getRuntime().gc();
    }

//     Returns true if key presents in trie, else false
    public boolean contains(String key) {
        var node = getTerminalNode(key);
        return (node != null && node.isEndOfWord);
    }

    public boolean isEmpty(){
        return counter > 0;
    }

    public WordTrieNode getTerminalNode(String key) {
        int level;
        int length = key.length();
        int index;
        WordTrieNode crawler = root;
        for (level = 0; level < length; level++) {
            var chr = key.charAt(level);
            index = toIndex(chr);
            if (crawler.children[index] == null)
                return null;

            crawler = crawler.children[index];
        }
        if (crawler != null && crawler.isEndOfWord) return crawler;

        return null;
    }
}