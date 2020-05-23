package webdata.encoders;
import webdata.iostreams.AppInputStream;
import webdata.iostreams.OutOfBitsException;
import webdata.models.SymbolTable;

import java.io.*;

public class ArithmeticDecoder {
    private final SymbolTable frequencyTable;
    protected long low;
    protected long high;
    private AppInputStream input;

    // The current raw code bits being buffered.
    private long bitBuffer;

    public ArithmeticDecoder(AppInputStream input) throws IOException {
        low = 0;
        high = BitUtils.getAllOnes();
        this.input = input;
        bitBuffer = 0;
        this.frequencyTable = new SymbolTable(BitUtils.NUM_OF_SYMBOLS);
        for (int i = 0; i < BitUtils.NUM_OF_BITS_IN_LONG; i++)
            bitBuffer = bitBuffer << 1 | readBit();
    }

    protected void updateHighAndLow(int symbol)  throws IOException{
        long range = high - low + 1;

        long total = this.frequencyTable.getTotalNumOfSymbolsFrequencies();
        long newLow  = low + this.frequencyTable.getLow(symbol)  * range / total;
        // Tamer: the minus one because the range is [,) in the algo
        long newHigh = low + this.frequencyTable.getHigh(symbol) * range / total - 1;
        low = newLow;
        high = newHigh;

        readExcessBufferBits();

    }

    private void readExcessBufferBits() throws IOException{
        // to make sure we update our high and low by shifting them (as with the encoding) while shifting and reading more bit's to the buffer
        while (BitUtils.bytesHaveSameTopBitValue(low, high)) {
            bitBuffer = BitUtils.shiftLeft(bitBuffer) | readBit();
            low  =  BitUtils.shiftLeft(low);
            high =  BitUtils.shiftLeft(high) | 1;
        }

        // Now low's top bit must be 0 and high's top bit must be 1
        // While low's top two bits are 01 and high's are 10, delete the second highest bit of both
        while ((low & ~high & BitUtils.getQuarterRange()) != 0) {
            bitBuffer = (bitBuffer & BitUtils.getHalfRange()) | ((bitBuffer << 1) & (BitUtils.getAllOnes() >>> 1)) | readBit();
            low = (low << 1) ^ BitUtils.getHalfRange();
            high = ((high ^ BitUtils.getHalfRange()) << 1) | BitUtils.getHalfRange() | 1;
        }
    }

    /***
     * reads one symbol from the file ( and decodes it )
     */
    public int read() throws IOException{
        // Translate from coding range scale to frequency table scale
        long total = this.frequencyTable.getTotalNumOfSymbolsFrequencies();
        long offset = bitBuffer - low;
        long range =  high - low + 1;

        long value = ((offset + 1) * total - 1) / range;
        int symbol = searchSymbol(value);

        updateHighAndLow(symbol);
        this.frequencyTable.incrementSymbolCounter(symbol);
        return symbol;
    }

    /**
     * search for the symbol by binary search for the low bound (which is always the symbol in our case - better for debug)
     * @param value - the current value to search for it's symbol
     * @return the start value (which is the symbol code) since that's how we chose the value eo encode in the encoder
     */
    private int searchSymbol(long value) {
        int start = 0;
        int end = this.frequencyTable.getNumOfSymbols();
        while (end - start > 1) {
            int middle = (start + end) / 2;
            if (this.frequencyTable.getLow(middle) > value)
                end = middle;
            else
                start = middle;
        }

        return start;
    }

    /**
     * we use 0 to mark the end of the file.
     * A stream of zeroes will eventually make the encoder read all the bits from 'this.bitBuffer' and eventually will be 0
     * when 'this.bitBuffer' = 0 (after enough readBit it will be 0) we stop the iteration
     * @return the next bit from the input file
     * @throws IOException
     */
    private int readBit() throws IOException {
        if(input.hasMoreInput()){
            return input.read();
        }

        throw new OutOfBitsException();
    }

}

