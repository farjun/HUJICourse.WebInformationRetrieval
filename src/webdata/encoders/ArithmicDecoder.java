package webdata.encoders;
import webdata.iostreams.AppInputStream;
import webdata.models.SymbolTable;

import java.io.*;

public class ArithmicDecoder {
    protected final int numStateBits;

    /** The top bit at width numStateBits, which is 0100...000. */
    protected final long halfRange;

    /** The second highest bit at width numStateBits, which is 0010...000. This is zero when numStateBits=1. */
    protected final long quarterRange;

    private final SymbolTable frequencyTable;

    protected long low;
    protected long high;
    private AppInputStream input;

    // The current raw code bits being buffered, which is always in the range [low, high].
    private long curValue;

    public ArithmicDecoder(AppInputStream input) throws IOException {
        numStateBits = BitUtils.NUM_OF_BITS_IN_LONG;
        halfRange = BitUtils.getHalfRange();
        quarterRange = BitUtils.getQuarterRange();
        low = 0;
        high = BitUtils.getAllOnes();
        this.input = input;
        curValue = 0;
        this.frequencyTable = new SymbolTable(BitUtils.NUM_OF_SYMBOLS);
        for (int i = 0; i < numStateBits; i++)
            curValue = curValue << 1 | readBit();
    }



    protected void updateHighAndLow(int symbol) throws IOException {
        long range = high - low + 1;

        // Frequency table values check
        long total = this.frequencyTable.getTotal();
        long symLow = this.frequencyTable.getLow(symbol);
        long symHigh = this.frequencyTable.getHigh(symbol);

        // Update range
        long newLow  = low + symLow  * range / total;
        long newHigh = low + symHigh * range / total - 1; // Tamer: the minus one because the range is [,) in the algo
        low = newLow;
        high = newHigh;

        while (BitUtils.bytesHaveSameTopBitValue(low, high)) {
            curValue = BitUtils.shiftLeft(curValue) | readBit();
            low  =  BitUtils.shiftLeft(low);
            high =  BitUtils.shiftLeft(high) | 1;
        }
    }

    private long getRange(){
        return high - low + 1;
    }

    /***
     * reads one symbol from the file ()
     * @return
     * @throws IOException
     */
    public int read() throws IOException {
        // Translate from coding range scale to frequency table scale
        long total = this.frequencyTable.getTotal();
        long range = this.getRange();
        long offset = curValue - low;
        long value = ((offset + 1) * total - 1) / range;
        int symbol = searchSymbol(value);

        updateHighAndLow(symbol);
        this.frequencyTable.increment(symbol);
        return symbol;
    }

    /**
     * search for the symbol by binary search for the low bound (which is always the symbol in our case - better for debug)
     * @param value - the current value to search for it's symbol
     * @return the start value (which is the symbol code) since that's how we chose the value eo encode in the encoder
     */
    private int searchSymbol(long value) {
        int start = 0;
        int end = this.frequencyTable.getSymbolLimit();
        while (end - start > 1) {
            int middle = (start + end) / 2;
            if (this.frequencyTable.getLow(middle) > value)
                end = middle;
            else
                start = middle;
        }

        return start;
    }

    private int readBit() throws IOException {
        int bitAsInt = input.read();
        if (bitAsInt == BitUtils.END_OF_FILE)
            bitAsInt = 0;
        return bitAsInt;
    }

}

