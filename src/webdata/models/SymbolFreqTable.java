package webdata.models;

import old.FrequencyTable;

public class SymbolFreqTable implements FrequencyTable {

    private int[] frequencies;
    private int[] sumOfFrequencies;
    private int total;

    public SymbolFreqTable(int numSymbols) {
        frequencies = new int[numSymbols];
        total = 0;
        for (int i = 0; i < frequencies.length; i++) {
            frequencies[i] = 1;
            total++;
        }
        initCumulative();
    }


    /**
     * Returns the number of symbols in this frequency table, which is at least 1.
     *
     * @return the number of symbols in this frequency table
     */
    public int getSymbolLimit() {
        return frequencies.length;
    }


    /**
     * Returns the frequency of the specified symbol. The returned value is at least 0.
     *
     * @param symbol the symbol to query
     * @return the frequency of the specified symbol
     * @throws IllegalArgumentException if {@code symbol} &lt; 0 or {@code symbol} &ge; {@code getSymbolLimit()}
     */
    public int get(int symbol) {
        return frequencies[symbol];
    }


    /**
     * Sets the frequency of the specified symbol to the specified value. The frequency value
     * must be at least 0. If an exception is thrown, then the state is left unchanged.
     *
     * @param symbol the symbol to set
     * @param freq   the frequency value to set
     */
    public void set(int symbol, int freq) {
        total = total - frequencies[symbol] + freq;
        frequencies[symbol] = freq;
        initCumulative();
    }


    /**
     * Increments the frequency of the symbol.
     * @param symbol the symbol to increment
     */
    public void increment(int symbol) {
        total++;
        frequencies[symbol]++;
        initCumulative();
    }


    /**
     * Returns the total of all symbol frequencies. The returned value is at
     * least 0 and is always equal to {@code getHigh(getSymbolLimit() - 1)}.
     *
     * @return the total of all symbol frequencies
     */
    public int getTotal() {
        return total;
    }

    /**
     * Returns the sum of the frequencies of all the symbols strictly
     * below the specified symbol value.
     * @param symbol the symbol to calculate its low bound
     */
    public int getLow(int symbol) {
        return sumOfFrequencies[symbol];
    }


    /**
     * @param symbol the symbol to query
     * @return the sum of the frequencies of symbol and all symbols below
     */
    public int getHigh(int symbol) {
        return sumOfFrequencies[symbol + 1];
    }

    private void initCumulative() {
        sumOfFrequencies = new int[frequencies.length + 1];
        int sum = 0;
        for (int i = 0; i < frequencies.length; i++) {
            sum += frequencies[i];
            sumOfFrequencies[i+1] = sum;
        }
    }

}
