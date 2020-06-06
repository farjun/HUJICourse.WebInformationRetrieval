package webdata.models;

import webdata.encoders.BitUtils;

public class SymbolTable {

    private final int[] frequencies;
    private int[] sumOfFrequencies;
    private int totalNumOfFrequencies;

    public SymbolTable() {
        this(BitUtils.NUM_OF_SYMBOLS);
    }

    public SymbolTable(int numSymbols) {
        frequencies = new int[numSymbols];
        sumOfFrequencies = new int[numSymbols+1];
        totalNumOfFrequencies = 0;
        for (int i = 0; i < frequencies.length; i++) {
            frequencies[i] = 1;
            totalNumOfFrequencies++;
        }
        recalculateSum(0);
    }


    /**
     * total number of symbols in the table
     */
    public int getNumOfSymbols() {
        return frequencies.length;
    }


    /**
     * get the frequency of the given symbol
     */
    public int get(int symbol) {
        return frequencies[symbol];
    }

    public void incrementSymbolCounter(int symbol) {
        totalNumOfFrequencies++;
        frequencies[symbol]++;
        recalculateSum(symbol);
    }

    public int getTotalNumOfSymbolsFrequencies() {
        return totalNumOfFrequencies;
    }

    public int getLow(int symbol) {
        return sumOfFrequencies[symbol];
    }


    /**
     * return the high boundry of the symbol
     * [ 1,2 -> 3,3,4,5,67,8]
     * [ 0, 1, 3,6...]
     */
    public int getHigh(int symbol) {
        return sumOfFrequencies[symbol + 1];
    }

    private void recalculateSum(int startFrom) {
        int sum = sumOfFrequencies[startFrom];
        for (int i = startFrom; i < frequencies.length; i++) {
            sum += frequencies[i];
            sumOfFrequencies[i+1] = sum;
        }
    }

}
