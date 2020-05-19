package webdata.encoders;

import webdata.iostreams.AppOutputStream;
import webdata.models.SymbolTable;

import java.io.*;
import java.util.Objects;

public final class ArithmeticEncoder {

    protected long low;
    protected long high;

    private final SymbolTable frequencyTable;
    private AppOutputStream output;

    public ArithmeticEncoder(AppOutputStream out) {
        super();
        low = 0;
        high = BitUtils.getAllOnes();
        this.frequencyTable = new SymbolTable();
        output = Objects.requireNonNull(out);
    }

    protected void writeSymbol(int symbol) {
        long range = high - low + 1;
        long total = this.frequencyTable.getTotalNumOfSymbolsFrequencies();

        // Update range
        long newLow  = low + this.frequencyTable.getLow(symbol)  * range / total;
        // Tamer: the minus one because the range is [,) in the algo
        long newHigh = low + this.frequencyTable.getHigh(symbol) * range / total - 1;
        low = newLow;
        high = newHigh;

        try {
            writeExcessBufferBits();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Encoder Failed to write buffer to file");
        }


        this.frequencyTable.incrementSymbolCounter(symbol);
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
        int bit = (int)(low >>> (BitUtils.NUM_OF_BITS_IN_LONG - 1));
        output.write(bit);
    }

    public static void writeEncoded(String toEncode, AppOutputStream out) throws IOException {
        ArithmeticEncoder enc = new ArithmeticEncoder(out);
        for (int symbol: toEncode.toCharArray()) {
            enc.writeSymbol(symbol);
        }
        enc.finishBatch();  // Flush remaining code bits
    }

}
