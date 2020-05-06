package webdata.encoders;

import webdata.iostreams.AppOutputStream;
import webdata.models.SymbolFreqTable;

import java.io.*;
import java.util.Objects;

public final class ArithmicEncoder {
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

    protected long low;
    protected long high;

    private final SymbolFreqTable frequencyTable;
    private AppOutputStream output;

    private int numUnderflow;

    public ArithmicEncoder(AppOutputStream out) {
        super();
        numStateBits = NUM_OF_BITS_IN_LONG;
        fullRange = 1L << numStateBits;
        halfRange = fullRange >>> 1;  // Non-zero
        quarterRange = halfRange >>> 1;  // Can be zero
        stateMask = fullRange - 1;
        low = 0;
        high = stateMask;
        this.frequencyTable = new SymbolFreqTable(NUM_OF_SYMBOLS);
        output = Objects.requireNonNull(out);
        numUnderflow = 0;
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
            shiftAndWrite();
            low  = ((low  << 1) & stateMask);
            high = ((high << 1) & stateMask) | 1;
        }

        // Now low's top bit must be 0 and high's top bit must be 1
        // While low's top two bits are 01 and high's are 10, delete the second highest bit of both
        while ((low & ~high & quarterRange) != 0) {
            numUnderflow++;
            low = (low << 1) ^ halfRange;
            high = ((high ^ halfRange) << 1) | halfRange | 1;
        }
        this.frequencyTable.increment(symbol);
    }

    public void finishBatch() throws IOException {
        writeSymbol(BATCH_SEPERATOR);
        output.write(1);
    }

    protected void shiftAndWrite() throws IOException {
        int bit = (int)(low >>> (numStateBits - 1));
        output.write(bit);

        // Write out the saved underflow bits
        for (; numUnderflow > 0; numUnderflow--)
            output.write(bit ^ 1);
    }

    public static void writeEncoded(String toEncode, AppOutputStream out) throws IOException {
        ArithmicEncoder enc = new ArithmicEncoder(out);
        for (int symbol: toEncode.toCharArray()) {
            enc.writeSymbol(symbol);
        }
        enc.finishBatch();  // Flush remaining code bits
    }

}
