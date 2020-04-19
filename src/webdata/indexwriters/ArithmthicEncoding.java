package webdata.indexwriters;

import javafx.util.Pair;
import webdata.models.ProductReview;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import static sun.misc.Version.print;

public class ArithmthicEncoding {

    private final char[] alphabit;
    HashMap<Character, Integer> symbolsCounter;
    private float numbOfSymbols;
    private static final float LOW_BOUND = 0;
    private static final float HIGH_BOUND = 1;

    public ArithmthicEncoding(char[] alphabit) {
        this.alphabit = alphabit;
        this.resetCounter();
    }

    private void resetCounter(){
        this.symbolsCounter = new HashMap<>();
        for (char c: this.alphabit ) {
            this.symbolsCounter.put(c, 1);
        }
        this.numbOfSymbols = alphabit.length;
    }

    public String encodeToken(char[] symbols){
        float low = LOW_BOUND;
        float high = HIGH_BOUND;
        this.resetCounter();

        for (char c : symbols){
            Pair<Float, Float> newValues = this.restrict(low, high, c);
            low = newValues.getKey();
            high = newValues.getValue();
            this.symbolsCounter.put(c, this.symbolsCounter.getOrDefault(c, 1) + 1);
            this.numbOfSymbols++;
        }

        return Float.toString(low + Math.abs(high - low) / 2);
    }

    public String decodeToken(int length, float codedSymbol ){
        float low = LOW_BOUND;
        float high = HIGH_BOUND;
        this.resetCounter();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            for (char symbol : this.alphabit){
                Pair<Float, Float> newValues = this.restrict(low, high, symbol);
                float newLow = newValues.getKey();
                float newHigh = newValues.getValue();
                if( codedSymbol >= newLow && codedSymbol < newHigh ){
                    result.append(symbol);
                    this.symbolsCounter.put(symbol, this.symbolsCounter.getOrDefault(symbol, 1) + 1);
                    this.numbOfSymbols++;
                    low = newLow;
                    high = newHigh;
                    break;
                }
            }
        }

        return result.toString();
    }

    private float computeLowBound(char symbol){
        float lowBound = 0;
        for(Character key : this.alphabit){
            if( key < symbol ){
                lowBound += this.symbolsCounter.get(key) / this.numbOfSymbols;
            }
        }
        return lowBound;
    }

    private Pair<Float, Float> restrict(float low, float high, char symbol){
        float lowBound = this.computeLowBound(symbol);
        float highBound = lowBound + this.symbolsCounter.get(symbol) / this.numbOfSymbols;
        float range = high - low;

        return new Pair<>(low + range * lowBound, low + range*highBound );
    }


    public static void main(String[] args) {
        char[] alphabitABC = new char[]{'b','c','c','b'};
        char[] alphabitNumbers = new char[]{',', '0','1','2','3','4','5','6','7','8','9'};

        ArithmthicEncoding ae = new ArithmthicEncoding(alphabitNumbers);
        char[] a = new char[]{'2',',','5',',','3',',','6','2',',','2',',','1',',','3','6'};
        char[] b = new char[]{'b','c','c','b'};
        String encoded = ae.encodeToken(a);
        String decoded = ae.decodeToken(a.length, Float.parseFloat(encoded));
        System.out.println(encoded);
        System.out.println(decoded);
    }

}
