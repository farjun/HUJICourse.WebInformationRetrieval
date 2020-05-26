package webdata.models;

import java.util.TreeMap;

public class WordTrieNode extends TrieNode {
    static final int ALPHABET_SIZE = 26;
    static final int NUMBERS = 26;
    static final int SIZE = NUMBERS + ALPHABET_SIZE;
    WordTrieNode[] children = new WordTrieNode[SIZE];
    int tokenRank;

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    // isEndOfWord is true if the node represents
    // end of a word
    boolean isEndOfWord;

    public int getTokenGlobalFreq() {
        return tokenGlobalFreq;
    }

    public void setTokenGlobalFreq(int tokenGlobalFreq) {
        this.tokenGlobalFreq = tokenGlobalFreq;
    }

    int tokenGlobalFreq;

    public TreeMap<Integer, Integer> getTokenFreq() {
        return tokenFreq;
    }

    public void setTokenFreq(TreeMap<Integer, Integer> tokenFreq) {
        this.tokenFreq = tokenFreq;
    }

    TreeMap<Integer, Integer> tokenFreq;

    WordTrieNode() {
        tokenRank = -1;
        isEndOfWord = false;
        tokenGlobalFreq = 0;
        tokenFreq = null;
        for (int i = 0; i < SIZE; i++)
            children[i] = null;
    }

    public StringBuilder serialize(String prefixTillNode) {
        //the node doesn't know the token so we pass it as prefixTillNode
        //and this method serialize the frequncy map, the global frequency and the token.
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
