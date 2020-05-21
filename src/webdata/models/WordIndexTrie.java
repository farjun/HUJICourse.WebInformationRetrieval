package webdata.models;

import java.util.TreeMap;

class TrieNode {
    static final int ALPHABET_SIZE = 26;
    static final int NUMBERS = 26;
    static final int SIZE = NUMBERS + ALPHABET_SIZE;
    TrieNode[] children = new TrieNode[SIZE];
    int tokenRank;
    // isEndOfWord is true if the node represents
    // end of a word
    boolean isEndOfWord;
    int tokenGlobalFreq;
    TreeMap<Integer, Integer> tokenFreq;

    TrieNode() {
        tokenRank = -1;
        isEndOfWord = false;
        tokenGlobalFreq = 0;
        tokenFreq = null;
        for (int i = 0; i < SIZE; i++)
            children[i] = null;
    }

    public StringBuilder serialize(String prefixTillNode) {
        //prefixTillNode is the token
        StringBuilder serializedEntry = new StringBuilder();
        serializedEntry.append(prefixTillNode);
        serializedEntry.append("|");
        serializedEntry.append(tokenGlobalFreq);
        serializedEntry.append("|");
        // replace "=" coming from toString of HashMap with ":" to make it JSON-like
        serializedEntry.append(tokenFreq.toString()
                .replace("=",":").replace(" ", ""));
        serializedEntry.append(";"); // terminate line
        return serializedEntry;
    }
}

public class WordIndexTrie {

    public TrieNode getRoot() {
        return root;
    }

    private TrieNode root;
    private int counter; // not sure we need it
    private StringBuilder serialized;

    public WordIndexTrie(){
        this.root = new TrieNode();
        this.counter = 0;
        this.serialized = new StringBuilder();
    }

    public TrieNode insert(String key) {
        int level;
        int length = key.length();
        int index;

        TrieNode crawler = root;

        for (level = 0; level < length; level++) {
            index = toIndex(key.charAt(level));
            if (crawler.children[index] == null)
                crawler.children[index] = new TrieNode();

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
        }
    }

    private char toChar(int index){
        int plusA =  index + 'a';
        if (plusA <= 'z') return (char)plusA;
        //otherwise its a number
        return (char) ('0' + index - TrieNode.ALPHABET_SIZE);
    }

    private int toIndex(char chr){
        if (chr <= 'z' &&  chr >= 'a')
            return (int) chr - 'a';
        //otherwise its a number
        return (chr - '0' + TrieNode.ALPHABET_SIZE);
    }

    public void traversePreorder(){
        traversePreorder(this.root,"");
    }
    private void traversePreorder(TrieNode node, String prefix)
    {
        if (node == null)
        {
            return;
        }
        if (node.isEndOfWord){
            System.out.println("-------------------------------------");
            System.out.println(prefix);
            System.out.println(node.tokenRank);
            System.out.println(node.tokenFreq);
            System.out.println(node.tokenGlobalFreq);
            //serialize..
            this.serialized.append(node.serialize(prefix));
        }
        for (int i = 0; i < node.children.length;i++)
        {
            var chr = toChar(i);
            traversePreorder(node.children[i], prefix + chr);
        }
    }


    @Override
    public String toString() {
        return this.serialized.toString();
    }

    // Returns true if key presents in trie, else false
    boolean contains(String key) {
        var node = getTerminalNode(key);
        return (node != null && node.isEndOfWord);
    }

    public TrieNode getTerminalNode(String key) {
        int level;
        int length = key.length();
        int index;
        TrieNode crawler = root;

        for (level = 0; level < length; level++) {
            var chr = key.charAt(level);
            if (chr >= '0' && chr <= '9'){
                index = chr - '0' + TrieNode.ALPHABET_SIZE;
            }
            else {
                index = chr - 'a';
            }
            if (crawler.children[index] == null)
                return null;

            crawler = crawler.children[index];
        }
        if (crawler != null && crawler.isEndOfWord) return crawler;

        return null;
    }
}