package webdata.encoders;

import webdata.iostreams.AppOutputStream;
import webdata.models.SymbolTable;

import java.io.*;
import java.util.Objects;

public final class ArithmicEncoder {



    protected final int numStateBits;

    /** The top bit at width numStateBits, which is 0100...000. */
    protected final long halfRange;

    /** The second highest bit at width numStateBits, which is 0010...000. This is zero when numStateBits=1. */
    protected final long quarterRange;

    /** Bit mask of numStateBits ones, which is 0111...111. */
    protected final long stateMask;

    protected long low;
    protected long high;

    private final SymbolTable frequencyTable;
    private AppOutputStream output;

    public ArithmicEncoder(AppOutputStream out) {
        super();
        numStateBits = BitUtils.NUM_OF_BITS_IN_LONG;
        halfRange = BitUtils.getHalfRange();
        quarterRange = BitUtils.getQuarterRange();  // Can be zero
        stateMask = BitUtils.getAllOnes();
        low = 0;
        high = stateMask;
        this.frequencyTable = new SymbolTable();
        output = Objects.requireNonNull(out);
    }

    protected void writeSymbol(int symbol) {
        long range = high - low + 1;
        long total = this.frequencyTable.getTotal();
        long symLow = this.frequencyTable.getLow(symbol);
        long symHigh = this.frequencyTable.getHigh(symbol);

        // Update range
        long newLow  = low + symLow  * range / total;
        long newHigh = low + symHigh * range / total - 1; // Tamer: the minus one because the range is [,) in the algo
        low = newLow;
        high = newHigh;

        try {
            writeExcessBufferBits();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Encoder Failed to write buffer to file");
        }


        this.frequencyTable.increment(symbol);
    }

    private void writeExcessBufferBits() throws IOException{
        while (BitUtils.bytesHaveSameTopBitValue(low, high)) {
            shiftAndWrite();
            low  = BitUtils.shiftLeft(low);
            high = BitUtils.shiftLeft(high) | 1;
        }
    }

    public void finishBatch() throws IOException {
        writeSymbol(BitUtils.BATCH_SEPERATOR);
        output.write(1);
    }

    protected void shiftAndWrite() throws IOException {
        int bit = (int)(low >>> (numStateBits - 1));
        output.write(bit);
    }

    public static void writeEncoded(String toEncode, AppOutputStream out) throws IOException {
        ArithmicEncoder enc = new ArithmicEncoder(out);
        for (int symbol: toEncode.toCharArray()) {
            enc.writeSymbol(symbol);
        }
        enc.finishBatch();  // Flush remaining code bits
    }

}
