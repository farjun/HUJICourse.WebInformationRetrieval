package webdata.encoders;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.BitInputStream;
import webdata.models.SymbolFreqTable;

import java.io.*;

public class ArithmicDecoder {
    public static final int NUM_OF_BITS_IN_LONG = 32;
    public static final int BATCH_SEPERATOR = 256;
    public static final int NUM_OF_SYMBOLS = 257;
    protected final int numStateBits;

    /** Maximum range (high+1-low) during coding (trivial), which is 2^numStateBits = 1000...000. */
    protected final long fullRange;

    /** The top bit at width numStateBits, which is 0100...000. */
    protected final long halfRange;

    /** The second highest bit at width numStateBits, which is 0010...000. This is zero when numStateBits=1. */
    protected final long quarterRange;

    /** Bit mask of numStateBits ones, which is 0111...111. */
    protected final long stateMask;
    private final SymbolFreqTable frequencyTable;

    protected long low;
    protected long high;
    private AppInputStream input;

    // The current raw code bits being buffered, which is always in the range [low, high].
    private long code;

    public ArithmicDecoder(AppInputStream in) throws IOException {
        numStateBits = NUM_OF_BITS_IN_LONG;
        fullRange = 1L << numStateBits;
        halfRange = fullRange >>> 1;  // Non-zero
        quarterRange = halfRange >>> 1;  // Can be zero
        stateMask = fullRange - 1;
        low = 0;
        high = stateMask;
        input = in;
        code = 0;
        this.frequencyTable = new SymbolFreqTable(NUM_OF_SYMBOLS);
        for (int i = 0; i < numStateBits; i++)
            code = code << 1 | readCodeBit();
    }

    protected void writeSymbol(int symbol) throws IOException {
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

        // While low and high have the same top bit value, shift them out
        while (((low ^ high) & halfRange) == 0) {
            code = ((code << 1) & stateMask) | readCodeBit();
            low  = ((low  << 1) & stateMask);
            high = ((high << 1) & stateMask) | 1;
        }

        // Now low's top bit must be 0 and high's top bit must be 1
        // While low's top two bits are 01 and high's are 10, delete the second highest bit of both
        while ((low & ~high & quarterRange) != 0) {
            code = (code & halfRange) | ((code << 1) & (stateMask >>> 1)) | readCodeBit();
            low = (low << 1) ^ halfRange;
            high = ((high ^ halfRange) << 1) | halfRange | 1;
        }
    }

    public int read() throws IOException {
        // Translate from coding range scale to frequency table scale
        long total = this.frequencyTable.getTotal();
        long range = high - low + 1;
        long offset = code - low;
        long value = ((offset + 1) * total - 1) / range;

        // A kind of binary search. Find highest symbol such that freqs.getLow(symbol) <= value.
        int start = 0;
        int end = this.frequencyTable.getSymbolLimit();
        while (end - start > 1) {
            int middle = (start + end) >>> 1;
            if (this.frequencyTable.getLow(middle) > value)
                end = middle;
            else
                start = middle;
        }

        int symbol = start;

        writeSymbol(symbol);
        this.frequencyTable.increment(symbol);
        return symbol;
    }

    private int readCodeBit() throws IOException {
        int temp = input.read();
        if (temp == -1)
            temp = 0;
        return temp;
    }

}

