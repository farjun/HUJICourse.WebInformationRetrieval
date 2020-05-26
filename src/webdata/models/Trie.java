package webdata.models;

import java.util.TreeMap;


public class Trie {


    private int counter;
    private StringBuilder serialized;


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


    @Override
    public String toString() {
        return this.serialized.toString();
    }



    public boolean isEmpty(){
        return counter > 0;
    }


}