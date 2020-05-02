package webdata.encoders;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.BitInputStream;
import webdata.models.SymbolFreqTable;

import java.io.*;

public final class ArithmicDecoder {
    public static final int NUM_OF_BITS_IN_INT = 32;

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
    private AppInputStream input;

    // The current raw code bits being buffered, which is always in the range [low, high].
    private long code;

    public ArithmicDecoder(AppInputStream in) throws IOException {
        super();
        numStateBits = NUM_OF_BITS_IN_INT;
        fullRange = 1L << numStateBits;
        halfRange = fullRange >>> 1;  // Non-zero
        quarterRange = halfRange >>> 1;  // Can be zero
        stateMask = fullRange - 1;
        low = 0;
        high = stateMask;
        input = in;
        code = 0;
        for (int i = 0; i < numStateBits; i++)
            code = code << 1 | readCodeBit();
    }

    protected void writeSymbol(SymbolFreqTable freqs, int symbol) throws IOException {
        long range = high - low + 1;


        // Frequency table values check
        long total = freqs.getTotal();
        long symLow = freqs.getLow(symbol);
        long symHigh = freqs.getHigh(symbol);

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
            underflow();
            low = (low << 1) ^ halfRange;
            high = ((high ^ halfRange) << 1) | halfRange | 1;
        }
    }

    public int read(SymbolFreqTable freqs) throws IOException {
        // Translate from coding range scale to frequency table scale
        long total = freqs.getTotal();
        long range = high - low + 1;
        long offset = code - low;
        long value = ((offset + 1) * total - 1) / range;

        // A kind of binary search. Find highest symbol such that freqs.getLow(symbol) <= value.
        int start = 0;
        int end = freqs.getSymbolLimit();
        while (end - start > 1) {
            int middle = (start + end) >>> 1;
            if (freqs.getLow(middle) > value)
                end = middle;
            else
                start = middle;
        }

        int symbol = start;

        writeSymbol(freqs, symbol);
        return symbol;
    }


    protected void shiftAndWrite() throws IOException {
        code = ((code << 1) & stateMask) | readCodeBit();
    }


    protected void underflow() throws IOException {
        code = (code & halfRange) | ((code << 1) & (stateMask >>> 1)) | readCodeBit();
    }

    private int readCodeBit() throws IOException {
        int temp = input.read();
        if (temp == -1)
            temp = 0;
        return temp;
    }



    // To allow unit testing, this method is package-private instead of private.
    public static void decompress(AppInputStream in, OutputStream out) throws IOException {
        SymbolFreqTable freqs = new SymbolFreqTable(257);
        ArithmicDecoder dec = new ArithmicDecoder(in);
        while (true) {
            // Decode and write one byte
            int symbol = dec.read(freqs);
            if (symbol == 256)  // EOF symbol
                break;
            out.write(symbol);
            freqs.increment(symbol);
        }
    }

    public static void main(String[] args) throws IOException{
        // Perform file decompression
        try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream("src\\index\\products.txt")));
             OutputStream out = new BufferedOutputStream(new FileOutputStream("src\\index\\products-decoded.txt"))) {
            ArithmicDecoder.decompress(in, out);
        }
    }

}

