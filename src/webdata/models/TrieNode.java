package webdata.models;

public abstract class TrieNode {
    static final int ALPHABET_SIZE = 26;
    static final int NUMBERS = 26;
    static final int SIZE = NUMBERS + ALPHABET_SIZE;
    WordTrieNode[] children = new WordTrieNode[SIZE];
    // isEndOfWord is true if the node represents
    // end of a word
    boolean isEndOfWord;
    TrieNode() {
        isEndOfWord = false;
        for (int i = 0; i < SIZE; i++)
            children[i] = null;
    }

    public abstract StringBuilder serialize(String prefixTillNode);

}
